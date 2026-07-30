package com.soldesk.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.soldesk.service.TaskService;
import com.soldesk.service.ParticipationService;
import com.soldesk.vo.TaskVO;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk.service.MemberService;
import com.soldesk.vo.MemberVO;

@Controller
@RequestMapping("/workspace/{project_id}")
public class TaskBoardController {

    private static final long MAX_SUBMIT_FILE_SIZE = 20L * 1024L * 1024L;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "pdf", "doc", "docx", "ppt", "pptx",
        "xls", "xlsx", "hwp", "hwpx",
        "txt", "zip", "png", "jpg", "jpeg"
    );

    @Autowired
    private TaskService taskService;

    @Autowired
    private ParticipationService participationService;

    @Autowired
    private MemberService memberService;

    /*
     * webapps 내부가 아닌 Tomcat 외부 폴더에 저장.
     * WAR를 다시 배포해도 제출 파일이 삭제 방지.
     */
    private Path getTaskUploadDirectory() {
        String catalinaBase = System.getProperty("catalina.base");

        if (catalinaBase == null || catalinaBase.trim().isEmpty()) {
            catalinaBase = System.getProperty("java.io.tmpdir");
        }

        return Paths.get(catalinaBase, "uploads", "tasks")
                .toAbsolutePath()
                .normalize();
    }

    // 업무 보드
    @GetMapping("/board")
    public String board(
        @PathVariable("project_id") long project_id,
        Principal principal,
        Model model) {

        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        boolean isLeader = loginMember != null
            && participationService.isLeader(
                project_id,
                loginMember.getMember_id()
        );

        model.addAttribute("project_id", project_id);
        model.addAttribute("isLeader", isLeader);

        model.addAttribute(
            "taskList",
            taskService.selectTaskList(project_id)
        );

        model.addAttribute(
            "currentMemberId",
            loginMember != null
            ? loginMember.getMember_id()
            : 0
        );

        return "workspace/board";
    }

    // 업무 등록 화면
    @GetMapping("/taskform")
    public String taskForm(
        @PathVariable("project_id") long project_id,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes) {

        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        if (loginMember == null
            || !participationService.isLeader(
                    project_id,
                    loginMember.getMember_id())) {

            redirectAttributes.addFlashAttribute(
                "taskError",
                "프로젝트 팀장만 업무를 등록할 수 있습니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        TaskVO task = new TaskVO();
        task.setProject_id(project_id);

        model.addAttribute("project_id", project_id);
        model.addAttribute("task", task);

        model.addAttribute(
            "projectMembers",
            participationService.selectTaskMembers(project_id)
        );

        return "workspace/taskform";
    }

    // 업무 등록
    @PostMapping("/tasks")
    public String insertTask(
        @PathVariable("project_id") long project_id,
        @ModelAttribute("task") TaskVO task,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes) {

        task.setProject_id(project_id);

        // 현재 로그인 사용자 조회
        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        // 로그인 정보가 없거나 프로젝트 팀장이 아닌 경우
        if (loginMember == null
            || !participationService.isLeader(
                    project_id,
                    loginMember.getMember_id())) {

            redirectAttributes.addFlashAttribute(
                "taskError",
                "프로젝트 팀장만 업무를 등록할 수 있습니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        // 선택한 담당자가 해당 프로젝트 참여자인지 검사
        Long assigneeId = task.getAssignee_id();

        if (assigneeId == null
            || !participationService.isTaskMember(
                    project_id,
                    assigneeId)) {

            model.addAttribute("project_id", project_id);

            model.addAttribute(
                "projectMembers",
                participationService.selectTaskMembers(project_id)
            );

            model.addAttribute(
                "assigneeError",
                "해당 프로젝트의 참여자만 담당자로 지정할 수 있습니다."
            );

            return "workspace/taskform";
        }

        taskService.insertTask(project_id, task);

        return "redirect:/workspace/"
            + project_id
            + "/board";
    }

    @GetMapping("/tasks/{task_id}") //업무 상세 조회
    public String taskDetail(
        @PathVariable("project_id") long project_id,
        @PathVariable("task_id") long task_id,
        Model model) {

        TaskVO task = taskService.selectTaskById(
            project_id,
            task_id
        );

        if (task == null) {
            return "redirect:/workspace/"
                    + project_id
                    + "/board";
        }

        model.addAttribute("task", task);
        model.addAttribute("project_id", project_id);

        return "workspace/taskdetail";
    }

    //업무제출
    @PostMapping("/tasks/{task_id}/submit")
    public String submitTask(
        @PathVariable("project_id") long project_id,
        @PathVariable("task_id") long task_id,
        @ModelAttribute TaskVO task,
        @RequestParam(value = "submitFile", required = false)
        MultipartFile submitFile,
        Principal principal,
        RedirectAttributes redirectAttributes) {

        MemberVO loginMember = null;

        if (principal != null) {
            loginMember = memberService.findByLoginId(
                principal.getName()
            );
        }

        if (loginMember == null) {
            redirectAttributes.addFlashAttribute(
                "taskError",
                "로그인이 필요합니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        String submitTitle = task.getSubmit_title();
        String submitContent = task.getSubmit_content();

        if (submitTitle == null || submitTitle.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "taskError",
                "제출 제목을 입력해주세요."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        if (submitContent == null || submitContent.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "taskError",
                "제출 내용을 입력해주세요."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        // URL과 로그인 정보로 서버에서 값을 고정
        task.setTask_id(task_id);
        task.setProject_id(project_id);
        task.setAssignee_id(
            (long) loginMember.getMember_id()
        );

        task.setSubmit_title(submitTitle.trim());
        task.setSubmit_content(submitContent.trim());

        String savedFileName = null;
        TaskVO previousTask = taskService.selectTaskById(
            project_id,
            task_id
        );

        try {
            if (submitFile != null && !submitFile.isEmpty()) {
                savedFileName = saveSubmitFile(submitFile);
                task.setSubmit_file(savedFileName);
            }

            boolean submitted = taskService.submitTask(task);

            if (!submitted) {
                deleteSubmitFile(savedFileName);

                redirectAttributes.addFlashAttribute(
                    "taskError",
                    "본인에게 배정된 진행 중 또는 반려된 업무만 제출할 수 있습니다."
                );

                return "redirect:/workspace/"
                    + project_id
                    + "/board";
            }

            /*
             * 재제출하면서 새 파일을 올린 경우에만 기존 파일을 삭제한다.
             * 새 파일을 선택하지 않았다면 SQL의 COALESCE로 기존 파일을 유지한다.
             */
            if (savedFileName != null
                    && previousTask != null
                    && previousTask.getSubmit_file() != null
                    && !savedFileName.equals(previousTask.getSubmit_file())) {

                deleteSubmitFile(previousTask.getSubmit_file());
            }
        } catch (IllegalArgumentException e) {
            deleteSubmitFile(savedFileName);

            redirectAttributes.addFlashAttribute(
                "taskError",
                e.getMessage()
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        } catch (IOException e) {
            deleteSubmitFile(savedFileName);

            redirectAttributes.addFlashAttribute(
                "taskError",
                "파일 저장 중 오류가 발생했습니다."
            );

            return "redirect:/workspace/"
                + project_id
                + "/board";
        }

        redirectAttributes.addFlashAttribute(
            "taskMessage",
            "업무 결과물을 제출했습니다. 팀장의 검수를 기다려주세요."
        );

        return "redirect:/workspace/"
            + project_id
            + "/board";
    }

    // 제출 파일 다운로드
    @GetMapping("/tasks/{task_id}/file")
    public ResponseEntity<Resource> downloadSubmitFile(
        @PathVariable("project_id") long project_id,
        @PathVariable("task_id") long task_id,
        Principal principal) throws IOException {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        MemberVO loginMember = memberService.findByLoginId(
            principal.getName()
        );

        if (loginMember == null
                || !participationService.isTaskMember(
                    project_id,
                    (long) loginMember.getMember_id())) {

            return ResponseEntity.status(403).build();
        }

        TaskVO task = taskService.selectTaskById(
            project_id,
            task_id
        );

        if (task == null
                || task.getSubmit_file() == null
                || task.getSubmit_file().trim().isEmpty()) {

            return ResponseEntity.notFound().build();
        }

        Path filePath = resolveStoredFile(task.getSubmit_file());

        if (!Files.isRegularFile(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());
        String originalFileName = extractOriginalFileName(
            task.getSubmit_file()
        );
        String encodedFileName = URLEncoder.encode(
            originalFileName,
            StandardCharsets.UTF_8.name()
        ).replace("+", "%20");

        String contentType = Files.probeContentType(filePath);

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedFileName
            )
            .contentLength(Files.size(filePath))
            .body(resource);
    }

    private String saveSubmitFile(MultipartFile file)
            throws IOException {

        if (file.getSize() > MAX_SUBMIT_FILE_SIZE) {
            throw new IllegalArgumentException(
                "첨부 파일은 20MB 이하만 업로드할 수 있습니다."
            );
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "파일 이름을 확인할 수 없습니다."
            );
        }

        originalFileName = Paths.get(originalFileName)
                .getFileName()
                .toString()
                .replaceAll("[\\r\\n]", "")
                .replaceAll("[^0-9A-Za-z가-힣._-]", "_");

        if (originalFileName.length() > 180) {
            throw new IllegalArgumentException(
                "파일 이름은 180자 이하로 변경해주세요."
            );
        }

        String extension = getExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                "허용되지 않는 파일 형식입니다."
            );
        }

        String savedFileName =
            UUID.randomUUID().toString() + "_" + originalFileName;

        Path uploadDirectory = getTaskUploadDirectory();
        Files.createDirectories(uploadDirectory);

        Path target = uploadDirectory.resolve(savedFileName).normalize();

        if (!target.getParent().equals(uploadDirectory)) {
            throw new IllegalArgumentException(
                "올바르지 않은 파일 이름입니다."
            );
        }

        file.transferTo(target.toFile());

        return savedFileName;
    }

    private Path resolveStoredFile(String savedFileName) {
        Path uploadDirectory = getTaskUploadDirectory();
        Path filePath = uploadDirectory
                .resolve(savedFileName)
                .normalize();

        if (!filePath.getParent().equals(uploadDirectory)) {
            throw new IllegalArgumentException(
                "올바르지 않은 파일 경로입니다."
            );
        }

        return filePath;
    }

    private void deleteSubmitFile(String savedFileName) {
        if (savedFileName == null || savedFileName.trim().isEmpty()) {
            return;
        }

        try {
            Files.deleteIfExists(resolveStoredFile(savedFileName));
        } catch (IOException | IllegalArgumentException ignored) {
            // DB 제출 성공 여부가 파일 정리 실패 때문에 바뀌면 안 된다.
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private String extractOriginalFileName(String savedFileName) {
        int separatorIndex = savedFileName.indexOf('_');

        if (separatorIndex < 0
                || separatorIndex == savedFileName.length() - 1) {
            return savedFileName;
        }

        return savedFileName.substring(separatorIndex + 1);
    }
}