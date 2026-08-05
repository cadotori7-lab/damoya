# 간단한 GitHub 프로젝트 요약 설정

`/gitTest/` 화면은 GitHub 로그인 후 자신의 공개·비공개 저장소를 선택해 요약 PDF를 만든다. 로그인하지 않으면 분석 폼과 PDF 생성 요청을 모두 차단한다.

## 처리 방식

1. GitHub의 ZIP archive API로 기본 브랜치의 최신 파일 스냅샷을 받는다.
2. `inspect_repository` MCP Tool이 저장소 구조 80개와 대표 텍스트 파일 최대 5개를 고른다.
3. 파일당 최대 2,500자만 LLM에 전달한다.
4. LLM은 주제, 요약, 기술 스택, 개선점만 반환한다.
5. ReportLab으로 간단한 A4 PDF를 만든 뒤 압축 파일과 임시 저장소를 삭제한다.

Git clone, 커밋 이력, 기여자, 점수, 빌드 및 테스트 실행은 사용하지 않는다.

## Python 서비스

`python-service/.env`에 OpenAI 설정을 넣는다.

```text
OPENAI_API_KEY=...
OPENAI_MODEL=gpt-5.4-mini
FASTAPI_HOST=0.0.0.0
FASTAPI_PORT=8501
```

의존성을 설치하고 실행한다.

```powershell
cd python-service
python -m pip install -r requirements.txt
python app.py
```

Windows에서는 Noto Sans KR 또는 맑은 고딕을 자동으로 사용한다. 다른 환경에서는 `GIT_REPORT_PDF_FONT_PATH`와 선택적으로 `GIT_REPORT_PDF_BOLD_FONT_PATH`에 한글 TTF 또는 TTC 파일 경로를 지정한다.

## Spring MVC

FastAPI 주소가 기본값과 다르면 환경 변수 또는 Spring 속성으로 지정한다.

```text
FASTAPI_BASE_URL=http://localhost:8501
```

비공개 저장소도 사용할 경우 GitHub의 `Settings > Developer settings > OAuth Apps`에서 OAuth App을 하나 만든다.

```text
Homepage URL:               http://localhost:8080/gitTest/
Authorization callback URL: http://localhost:8080/gitTest/github/callback
```

발급받은 값을 서버 환경 변수로 설정한다. Client Secret은 Git에 커밋하지 않는다.

```text
GITHUB_CLIENT_ID=...
GITHUB_CLIENT_SECRET=...
GITHUB_CALLBACK_URL=http://localhost:8080/gitTest/github/callback
```

로그인 토큰은 DB가 아니라 현재 HTTP 세션에만 보관된다. 로그아웃하거나 세션이 만료되면 다시 로그인해야 한다.

Tomcat에 WAR를 배포한 뒤 다음 화면을 연다.

```text
http://localhost:8080/gitTest/
```

## 제한 사항

- GitHub OAuth 로그인과 저장소 선택이 필수다.
- OAuth App 방식은 저장소 접근을 위해 넓은 `repo` 권한을 요청한다. 운영 서비스에서는 GitHub App 전환을 검토한다.
- 파일을 텍스트로만 읽으며 저장소 안의 명령이나 스크립트를 실행하지 않는다.
- 대형 저장소도 전체 파일 내용이 아니라 대표 파일 최대 5개만 분석한다.
