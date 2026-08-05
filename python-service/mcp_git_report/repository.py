import re
import shutil
import stat
import zipfile
from collections import Counter
from pathlib import Path, PurePosixPath
from tempfile import TemporaryDirectory
from urllib.error import HTTPError, URLError
from urllib.parse import quote, urlparse
from urllib.request import Request, urlopen

import config

from mcp_git_report.schemas import (
    LanguageSummary,
    RepositoryFile,
    RepositoryInspection,
    RepositorySnapshot,
)

_REF_PATTERN = re.compile(r"^[A-Za-z0-9._/-]{1,200}$")
_PATH_SEGMENT_PATTERN = re.compile(r"^[A-Za-z0-9._-]+$")

_LANGUAGE_BY_SUFFIX = {
    ".java": "Java",
    ".jsp": "JSP",
    ".kt": "Kotlin",
    ".kts": "Kotlin",
    ".py": "Python",
    ".js": "JavaScript",
    ".mjs": "JavaScript",
    ".cjs": "JavaScript",
    ".ts": "TypeScript",
    ".tsx": "TypeScript",
    ".jsx": "JavaScript",
    ".html": "HTML",
    ".htm": "HTML",
    ".css": "CSS",
    ".scss": "SCSS",
    ".sass": "Sass",
    ".sql": "SQL",
    ".xml": "XML",
    ".json": "JSON",
    ".yaml": "YAML",
    ".yml": "YAML",
    ".md": "Markdown",
    ".go": "Go",
    ".rs": "Rust",
    ".c": "C",
    ".h": "C/C++ Header",
    ".cc": "C++",
    ".cpp": "C++",
    ".cs": "C#",
    ".php": "PHP",
    ".rb": "Ruby",
    ".swift": "Swift",
    ".vue": "Vue",
    ".svelte": "Svelte",
    ".sh": "Shell",
    ".ps1": "PowerShell",
    ".bat": "Batch",
    ".gradle": "Gradle",
    ".properties": "Properties",
}

_SOURCE_SUFFIXES = {
    ".java",
    ".jsp",
    ".kt",
    ".py",
    ".js",
    ".ts",
    ".tsx",
    ".jsx",
    ".go",
    ".rs",
    ".cs",
    ".php",
    ".rb",
    ".vue",
    ".svelte",
}

_KEY_FILE_NAMES = {
    "pom.xml",
    "build.gradle",
    "build.gradle.kts",
    "settings.gradle",
    "settings.gradle.kts",
    "package.json",
    "package-lock.json",
    "pnpm-lock.yaml",
    "yarn.lock",
    "requirements.txt",
    "pyproject.toml",
    "poetry.lock",
    "pipfile",
    "dockerfile",
    "docker-compose.yml",
    "docker-compose.yaml",
    "compose.yml",
    "compose.yaml",
    "go.mod",
    "cargo.toml",
    "gemfile",
    "application.properties",
    "application.yml",
    "application.yaml",
}

_LOW_VALUE_FILE_NAMES = {
    "package-lock.json",
    "pnpm-lock.yaml",
    "yarn.lock",
    "poetry.lock",
}

_SENSITIVE_FILE_NAMES = {
    ".env",
    ".npmrc",
    ".pypirc",
    ".netrc",
    "id_rsa",
    "id_ed25519",
    "credentials",
    "credentials.json",
}
_SENSITIVE_SUFFIXES = {".pem", ".key", ".p12", ".pfx", ".keystore", ".jks"}

_SECRET_PATTERNS = (
    re.compile(r"\bsk-[A-Za-z0-9_-]{20,}\b"),
    re.compile(r"\bgh[pousr]_[A-Za-z0-9]{20,}\b"),
    re.compile(r"\bAKIA[A-Z0-9]{16}\b"),
    re.compile(
        r"(?im)^(\s*(?:api[_-]?key|secret|access[_-]?token|password)"
        r"\s*[:=]\s*)[^\r\n]+$"
    ),
)

# 저장소 검사 과정에서 발생한 안전한 사용자 오류
class GitRepositoryError(RuntimeError):
    """저장소 검사 과정에서 발생한 안전한 사용자 오류."""


# GitHub ZIP에서 LLM에 전달할 대표 파일만 추리는 클래스
class GitRepositoryManager:
    def __init__(self) -> None:
        self.work_root = config.GIT_REPORT_WORK_DIR.resolve()
        self.work_root.mkdir(parents=True, exist_ok=True)

    def inspect(
        self,
        repository_url: str,
        ref: str | None = None,
        access_token: str | None = None,
        tree_limit: int = 80,
        max_files: int = 5,
        max_chars_per_file: int = 2_500,
    ) -> RepositoryInspection:
        """ZIP을 임시로 내려받고 대표 파일을 읽은 뒤 즉시 정리한다."""
        normalized_url = self.validate_repository_url(repository_url)
        normalized_ref = self._validate_ref(ref)
        tree_limit = min(max(tree_limit, 20), 1_000)
        max_files = min(
            max(max_files, 1),
            config.GIT_REPORT_MAX_FILES_PER_READ,
        )
        max_chars_per_file = min(max(max_chars_per_file, 1_000), 120_000)

        with TemporaryDirectory(
            prefix="git-report-",
            dir=self.work_root,
        ) as workspace:
            workspace_dir = Path(workspace)
            archive_path = workspace_dir / "repository.zip"
            repository_dir = workspace_dir / "repository"

            self._download_archive(
                self._build_archive_url(normalized_url, normalized_ref),
                archive_path,
                access_token,
            )
            tracked_paths = self._extract_archive(archive_path, repository_dir)

            total_bytes, languages, key_files, tree = self._summarize_files(
                repository_dir,
                tracked_paths,
                tree_limit,
            )
            selected_paths = self._select_analysis_files(
                key_files,
                tree,
                max_files,
            )
            files, skipped = self._read_selected_files(
                repository_dir,
                tracked_paths,
                selected_paths,
                max_chars_per_file,
            )

            return RepositoryInspection(
                snapshot=RepositorySnapshot(
                    repository_url=normalized_url,
                    branch=normalized_ref or "default branch",
                    commit="ZIP snapshot",
                    short_commit="ZIP",
                    file_count=len(tracked_paths),
                    total_tracked_bytes=total_bytes,
                    languages=[
                        LanguageSummary(language=name, file_count=count)
                        for name, count in languages.most_common()
                    ],
                    key_files=key_files,
                    tree=tree,
                ),
                files=files,
                skipped=skipped,
            )

    @staticmethod
    def validate_repository_url(repository_url: str) -> str:
        candidate = repository_url.strip()
        if not candidate or len(candidate) > 500:
            raise GitRepositoryError("올바른 Git 저장소 URL을 입력하세요.")

        parsed = urlparse(candidate)
        try:
            port = parsed.port
        except ValueError as error:
            raise GitRepositoryError("Git 저장소 포트가 올바르지 않습니다.") from error

        if (
            parsed.scheme.lower() != "https"
            or not parsed.hostname
            or parsed.username
            or parsed.password
            or port is not None
            or parsed.query
            or parsed.fragment
        ):
            raise GitRepositoryError(
                "인증 정보나 추가 옵션이 없는 HTTPS URL을 사용하세요."
            )

        host = parsed.hostname.lower().rstrip(".")
        if host not in config.GIT_REPORT_ALLOWED_HOSTS:
            raise GitRepositoryError("허용된 Git 호스트가 아닙니다.")

        if "%" in parsed.path or "\\" in parsed.path:
            raise GitRepositoryError("저장소 경로 형식이 올바르지 않습니다.")
        path = parsed.path.strip("/")
        if path.endswith(".git"):
            path = path[:-4]
        segments = path.split("/")
        if len(segments) != 2 or any(
            not _PATH_SEGMENT_PATTERN.fullmatch(segment)
            for segment in segments
        ):
            raise GitRepositoryError("owner/repository 형식의 URL을 사용하세요.")
        return f"https://{host}/{'/'.join(segments)}"

    @staticmethod
    def _validate_ref(ref: str | None) -> str | None:
        if ref is None or not ref.strip():
            return None
        candidate = ref.strip()
        if (
            not _REF_PATTERN.fullmatch(candidate)
            or candidate.startswith(("-", "/"))
            or candidate.endswith("/")
            or ".." in candidate
            or "//" in candidate
        ):
            raise GitRepositoryError("브랜치 또는 태그 이름이 올바르지 않습니다.")
        return candidate

    @staticmethod
    def _build_archive_url(repository_url: str, ref: str | None) -> str:
        owner, repository = urlparse(repository_url).path.strip("/").split("/")
        url = (
            f"https://api.github.com/repos/{quote(owner)}/"
            f"{quote(repository)}/zipball"
        )
        return f"{url}/{quote(ref, safe='')}" if ref else url

    @staticmethod
    def _download_archive(
        archive_url: str,
        destination: Path,
        access_token: str | None = None,
    ) -> None:
        request = Request(
            archive_url,
            headers={
                "Accept": "application/vnd.github+json",
                "User-Agent": "damoya-git-report",
            },
        )
        if access_token:
            token = access_token.strip()
            if len(token) > 500 or "\r" in token or "\n" in token:
                raise GitRepositoryError("GitHub 인증 정보가 올바르지 않습니다.")
            request.add_unredirected_header("Authorization", f"Bearer {token}")
        try:
            with urlopen(
                request,
                timeout=config.GIT_REPORT_DOWNLOAD_TIMEOUT_SECONDS,
            ) as response:
                final_host = (urlparse(response.geturl()).hostname or "").lower()
                if final_host not in {
                    "api.github.com",
                    "github.com",
                    "codeload.github.com",
                }:
                    raise GitRepositoryError("GitHub ZIP 응답이 올바르지 않습니다.")

                content_length = response.headers.get("Content-Length")
                if content_length and int(content_length) > (
                    config.GIT_REPORT_MAX_ARCHIVE_BYTES
                ):
                    raise GitRepositoryError("저장소 ZIP 파일이 너무 큽니다.")

                downloaded = 0
                with destination.open("wb") as output:
                    while chunk := response.read(1024 * 1024):
                        downloaded += len(chunk)
                        if downloaded > config.GIT_REPORT_MAX_ARCHIVE_BYTES:
                            raise GitRepositoryError("저장소 ZIP 파일이 너무 큽니다.")
                        output.write(chunk)
        except HTTPError as error:
            if error.code == 404:
                message = "저장소 또는 요청한 브랜치를 찾을 수 없습니다. 비공개 저장소라면 GitHub에 로그인해 주세요."
            elif error.code == 401:
                message = "GitHub 로그인이 만료되었습니다. 다시 로그인해 주세요."
            elif error.code == 403:
                message = "GitHub 다운로드 한도를 초과했습니다."
            else:
                message = f"GitHub ZIP 다운로드에 실패했습니다. ({error.code})"
            raise GitRepositoryError(message) from error
        except (URLError, TimeoutError, OSError) as error:
            raise GitRepositoryError("GitHub ZIP 다운로드 연결에 실패했습니다.") from error

    def _extract_archive(
        self,
        archive_path: Path,
        repository_dir: Path,
    ) -> list[str]:
        try:
            with zipfile.ZipFile(archive_path) as archive:
                entries = [entry for entry in archive.infolist() if not entry.is_dir()]
                if not entries:
                    raise GitRepositoryError("저장소 ZIP 파일이 비어 있습니다.")
                if len(entries) > config.GIT_REPORT_MAX_TRACKED_FILES:
                    raise GitRepositoryError("저장소 파일 수가 너무 많습니다.")
                if sum(entry.file_size for entry in entries) > (
                    config.GIT_REPORT_MAX_REPOSITORY_BYTES
                ):
                    raise GitRepositoryError("압축 해제된 저장소가 너무 큽니다.")

                root_name: str | None = None
                safe_entries: list[tuple[zipfile.ZipInfo, str]] = []
                for entry in entries:
                    path = PurePosixPath(entry.filename)
                    if (
                        entry.flag_bits & 0x1
                        or "\\" in entry.filename
                        or path.is_absolute()
                        or len(path.parts) < 2
                        or any(part in {"", ".", ".."} for part in path.parts)
                    ):
                        raise GitRepositoryError("안전하지 않은 ZIP 경로가 있습니다.")

                    if root_name is None:
                        root_name = path.parts[0]
                    elif path.parts[0] != root_name:
                        raise GitRepositoryError("ZIP 최상위 경로가 올바르지 않습니다.")

                    if stat.S_ISLNK(entry.external_attr >> 16):
                        continue
                    relative_path = PurePosixPath(*path.parts[1:]).as_posix()
                    if not self._is_sensitive_path(relative_path):
                        safe_entries.append((entry, relative_path))

                if not safe_entries:
                    raise GitRepositoryError("분석할 저장소 파일이 없습니다.")

                repository_dir.mkdir()
                extracted: list[str] = []
                for entry, relative_path in safe_entries:
                    target = self._safe_repository_file(repository_dir, relative_path)
                    target.parent.mkdir(parents=True, exist_ok=True)
                    with archive.open(entry) as source, target.open("wb") as output:
                        shutil.copyfileobj(source, output, length=1024 * 1024)
                    extracted.append(relative_path)
                return sorted(extracted)
        except zipfile.BadZipFile as error:
            raise GitRepositoryError("GitHub가 올바른 ZIP 파일을 반환하지 않았습니다.") from error

    def _summarize_files(
        self,
        repository_dir: Path,
        tracked_paths: list[str],
        tree_limit: int,
    ) -> tuple[int, Counter[str], list[str], list[str]]:
        total_bytes = 0
        languages: Counter[str] = Counter()
        key_files: list[str] = []

        for relative_path in tracked_paths:
            file_path = self._safe_repository_file(repository_dir, relative_path)
            total_bytes += file_path.stat().st_size
            language = self._language_for(relative_path)
            if language:
                languages[language] += 1
            if self._is_key_file(relative_path):
                key_files.append(relative_path)

        key_files.sort(key=self._key_file_priority)
        return total_bytes, languages, key_files[:80], tracked_paths[:tree_limit]

    @staticmethod
    def _select_analysis_files(
        key_files: list[str],
        tree: list[str],
        max_files: int,
    ) -> list[str]:
        selected: list[str] = []

        def add(path: str) -> None:
            if path and path not in selected:
                selected.append(path)

        for path in key_files:
            if PurePosixPath(path).name.lower() not in _LOW_VALUE_FILE_NAMES:
                add(path)
            if len(selected) >= min(3, max_files):
                break

        candidates: list[tuple[int, int, str]] = []
        for path in tree:
            pure_path = PurePosixPath(path)
            if pure_path.suffix.lower() not in _SOURCE_SUFFIXES:
                continue
            lowered = f"/{path.lower()}/"
            if "/test/" in lowered or "/target/" in lowered:
                continue
            priority = 0 if any(
                marker in lowered
                for marker in (
                    "controller",
                    "service",
                    "application",
                    "app.py",
                    "main.py",
                )
            ) else 1
            candidates.append((priority, len(pure_path.parts), path))

        for _, _, path in sorted(candidates):
            add(path)
            if len(selected) >= max_files:
                break

        if not selected:
            selected.extend(tree[:max_files])
        return selected

    def _read_selected_files(
        self,
        repository_dir: Path,
        tracked_paths: list[str],
        selected_paths: list[str],
        max_chars_per_file: int,
    ) -> tuple[list[RepositoryFile], list[str]]:
        tracked = set(tracked_paths)
        files: list[RepositoryFile] = []
        skipped: list[str] = []
        returned_chars = 0

        for relative_path in selected_paths:
            if relative_path not in tracked:
                skipped.append(f"{relative_path}: 저장소 파일이 아닙니다.")
                continue

            file_path = self._safe_repository_file(repository_dir, relative_path)
            read_limit = min(
                config.GIT_REPORT_MAX_FILE_BYTES,
                max_chars_per_file * 4,
            )
            raw = file_path.read_bytes()[: read_limit + 1]
            truncated = file_path.stat().st_size > read_limit or len(raw) > read_limit
            raw = raw[:read_limit]
            if b"\x00" in raw:
                skipped.append(f"{relative_path}: 바이너리 파일입니다.")
                continue

            content = raw.decode("utf-8", errors="replace")
            if len(content) > max_chars_per_file:
                content = content[:max_chars_per_file]
                truncated = True

            remaining = config.GIT_REPORT_MAX_RETURNED_CHARS - returned_chars
            if remaining <= 0:
                skipped.append("전체 문자 한도에 도달했습니다.")
                break
            if len(content) > remaining:
                content = content[:remaining]
                truncated = True

            content, redacted = self._redact_secrets(content)
            returned_chars += len(content)
            files.append(
                RepositoryFile(
                    path=relative_path,
                    content=content,
                    size_bytes=file_path.stat().st_size,
                    truncated=truncated,
                    redacted=redacted,
                )
            )
        return files, skipped

    @staticmethod
    def _safe_repository_file(repository_dir: Path, relative_path: str) -> Path:
        path = PurePosixPath(relative_path)
        if (
            not relative_path
            or path.is_absolute()
            or any(part in {"", ".", ".."} for part in path.parts)
        ):
            raise GitRepositoryError("파일 경로가 올바르지 않습니다.")

        root = repository_dir.resolve()
        file_path = root.joinpath(*path.parts)
        if not file_path.resolve().is_relative_to(root):
            raise GitRepositoryError("저장소 밖의 파일은 읽을 수 없습니다.")
        return file_path

    @staticmethod
    def _language_for(relative_path: str) -> str | None:
        return _LANGUAGE_BY_SUFFIX.get(PurePosixPath(relative_path).suffix.lower())

    @staticmethod
    def _is_key_file(relative_path: str) -> bool:
        path = PurePosixPath(relative_path)
        name = path.name.lower()
        return (
            name.startswith("readme")
            or name in _KEY_FILE_NAMES
            or (
                len(path.parts) >= 3
                and path.parts[0] == ".github"
                and path.parts[1] == "workflows"
                and path.suffix.lower() in {".yml", ".yaml"}
            )
        )

    @staticmethod
    def _key_file_priority(relative_path: str) -> tuple[int, int, str]:
        name = PurePosixPath(relative_path).name.lower()
        if name.startswith("readme"):
            priority = 0
        elif name in {
            "pom.xml",
            "build.gradle",
            "build.gradle.kts",
            "package.json",
            "requirements.txt",
            "pyproject.toml",
        }:
            priority = 1
        else:
            priority = 2
        return priority, len(PurePosixPath(relative_path).parts), relative_path

    @staticmethod
    def _is_sensitive_path(relative_path: str) -> bool:
        path = PurePosixPath(relative_path)
        name = path.name.lower()
        return (
            name in _SENSITIVE_FILE_NAMES
            or name.startswith(".env.")
            or path.suffix.lower() in _SENSITIVE_SUFFIXES
        )

    @staticmethod
    def _redact_secrets(content: str) -> tuple[str, bool]:
        redacted = False
        result = content
        for pattern in _SECRET_PATTERNS:
            if pattern.search(result):
                redacted = True
                result = pattern.sub(
                    r"\1[REDACTED]" if pattern.groups else "[REDACTED]",
                    result,
                )
        return result, redacted
