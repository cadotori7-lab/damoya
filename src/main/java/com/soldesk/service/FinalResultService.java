package com.soldesk.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk.mapper.ProjectMapper;
import com.soldesk.mapper.TaskMapper;
import com.soldesk.vo.IncompleteTaskVO;
import com.soldesk.vo.ProjectCompletionVO;
import com.soldesk.vo.TaskVO;

@Service
public class FinalResultService {

    private static final long MAX_FINAL_FILE_SIZE = 20L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "ppt", "pptx",
            "xls", "xlsx", "hwp", "hwpx",
            "txt", "md", "zip", "png", "jpg", "jpeg");

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public ProjectCompletionVO getCompletionOverview(long projectId) {
        ProjectCompletionVO overview = new ProjectCompletionVO();
        overview.setTotalTaskCount(taskMapper.countRegularTasks(projectId));
        overview.setCompletedTaskCount(
                taskMapper.countCompletedRegularTasks(projectId));
        overview.setIncompleteTasks(
                taskMapper.selectIncompleteTasks(projectId));
        overview.setFinalResult(taskMapper.selectFinalResult(projectId));
        return overview;
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitFinalResult(
            long projectId,
            long leaderId,
            String title,
            String description,
            String reflection,
            MultipartFile finalFile) throws IOException {

        String normalizedTitle = requireText(title, "최종 결과물 제목", 200);
        String normalizedDescription = requireText(
                description, "최종 결과물 설명", 5000);
        String normalizedReflection = requireText(
                reflection, "프로젝트 소감", 5000);
        validateFinalFile(finalFile);

        if (projectMapper.getProjectByIdForUpdate(projectId) == null) {
            throw new IllegalStateException("프로젝트를 찾을 수 없습니다.");
        }

        if (taskMapper.countFinalResult(projectId) > 0) {
            throw new IllegalStateException("최종 결과물이 이미 제출되었습니다.");
        }

        List<IncompleteTaskVO> incompleteTasks =
                taskMapper.selectIncompleteTasks(projectId);
        if (!incompleteTasks.isEmpty()) {
            throw new IllegalStateException(
                    "완료되지 않은 업무가 있어 최종 결과물을 제출할 수 없습니다.");
        }

        String savedFileName = saveFinalFile(finalFile);
        try {
            TaskVO finalResult = new TaskVO();
            finalResult.setProject_id(projectId);
            finalResult.setAssignee_id(leaderId);
            finalResult.setSubmit_title(normalizedTitle);
            finalResult.setSubmit_content(
                    "[최종 결과물 설명]\n" + normalizedDescription
                    + "\n\n[프로젝트 소감]\n" + normalizedReflection);
            finalResult.setSubmit_file(savedFileName);

            if (taskMapper.insertFinalResult(finalResult) != 1) {
                throw new IllegalStateException(
                        "최종 결과물을 저장하지 못했습니다.");
            }

            projectMapper.markProjectDone(projectId);
        } catch (RuntimeException e) {
            deleteSavedFile(savedFileName);
            throw e;
        }
    }

    private String requireText(String value, String label, int maxLength) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + "을(를) 입력해주세요.");
        }
        if (normalized.length() > maxLength) {
            throw new IllegalArgumentException(
                    label + "은(는) " + maxLength + "자 이하로 입력해주세요.");
        }
        return normalized;
    }

    private void validateFinalFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "최종 결과물 파일을 선택해주세요.");
        }
        if (file.getSize() > MAX_FINAL_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "최종 결과물 파일은 20MB 이하만 업로드할 수 있습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일 이름을 확인할 수 없습니다.");
        }

        String extension = getExtension(originalFileName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "허용되지 않는 최종 결과물 파일 형식입니다.");
        }
    }

    private String saveFinalFile(MultipartFile file) throws IOException {
        String originalFileName = Paths.get(file.getOriginalFilename())
                .getFileName()
                .toString()
                .replaceAll("[\\r\\n]", "")
                .replaceAll("[^0-9A-Za-z가-힣._-]", "_");

        if (originalFileName.length() > 180) {
            throw new IllegalArgumentException(
                    "파일 이름은 180자 이하로 변경해주세요.");
        }

        String savedFileName = UUID.randomUUID() + "_" + originalFileName;
        Path directory = getTaskUploadDirectory();
        Files.createDirectories(directory);
        Path target = directory.resolve(savedFileName).normalize();

        if (!target.getParent().equals(directory)) {
            throw new IllegalArgumentException("올바르지 않은 파일 이름입니다.");
        }

        try {
            file.transferTo(target.toFile());
            return savedFileName;
        } catch (IOException e) {
            Files.deleteIfExists(target);
            throw e;
        }
    }

    private Path getTaskUploadDirectory() {
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase == null || catalinaBase.trim().isEmpty()) {
            catalinaBase = System.getProperty("java.io.tmpdir");
        }
        return Paths.get(catalinaBase, "uploads", "tasks")
                .toAbsolutePath()
                .normalize();
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 || dot == fileName.length() - 1
                ? ""
                : fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private void deleteSavedFile(String savedFileName) {
        if (savedFileName == null || savedFileName.trim().isEmpty()) {
            return;
        }
        try {
            Files.deleteIfExists(
                    getTaskUploadDirectory().resolve(savedFileName).normalize());
        } catch (IOException ignored) {
            // DB 롤백이 파일 정리 실패 때문에 가려지지 않도록 한다.
        }
    }
}
