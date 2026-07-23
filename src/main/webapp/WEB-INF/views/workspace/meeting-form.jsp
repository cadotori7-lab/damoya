<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
  <main>
  <!-- ========== 회의 등록 ========== -->
  <section id="v-meetingform">
    <div class="form-wrap">
      <button class="back" onclick="go('meetings')">← 회의 목록</button>
      <div class="eyebrow" id="mfEyebrow">New meeting</div>
      <h1 class="page"><em id="mfTitle">회의 등록</em></h1>
      <p class="sub">AI 헬스케어 웹서비스 · 회의 일정을 잡고 회의록을 남겨요.</p>

      <div class="form-card">
        <div class="fld one"><label>회의명<span class="req">*</span></label><input type="text" id="mfName" placeholder="예: 2차 스프린트 회의"></div>
        <div class="frow">
          <div class="fld"><label>날짜<span class="req">*</span></label><input type="date" id="mfDate" value="2026-08-24"></div>
          <div class="fld"><label>시간</label><input type="time" id="mfTime" value="19:00"></div>
        </div>
        <div class="fld one"><label>안건 (한 줄 요약)</label><input type="text" id="mfAgenda" placeholder="예: 관리자·통계 기능 분담"></div>
        <div class="fld one">
          <label>회의록 / 내용</label>
          <textarea id="mfContent" placeholder="[안건]&#10;- 논의할 항목&#10;&#10;[결정 사항]&#10;- 결정된 내용" style="min-height:150px"></textarea>
          <div class="hint">[대괄호]는 소제목, "- "로 시작하면 목록으로 표시돼요.</div>
        </div>
        <div class="form-foot">
          <button class="btn ghost" onclick="go('meetings')">취소</button>
          <button class="btn pri" id="mfSave" onclick="saveMeeting()">회의 등록</button>
        </div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="${ctx}/includes/footer.jsp" />
</body>
</html>