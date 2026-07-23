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
  <!-- ========== 모집 관리 (지원자 승인) ========== -->
  <section id="v-applicants">
    <a class="back" href="${ctx}/workspace/members">← 팀원 관리</a>
    <div class="eyebrow">Recruitment · 팀장</div>
    <h1 class="page"><em>지원자 관리</em></h1>
    <p class="sub">AI 헬스케어 웹서비스 · 지원자를 확인하고 면접·승인을 진행해요.</p>

    <div class="recruit-bar">
      <div>
        <div class="rb-num" id="recruitNum">2<small> / 4명 확정</small></div>
      </div>
      <div class="rb-bar">
        <div class="bar" style="margin-top:0"><span id="recruitFill" style="width:50%"></span></div>
        <div class="team-need mono" style="font-size:11.5px;color:var(--ink-soft);margin-top:6px">모집 마감 D-9 · 지원 5명</div>
      </div>
      <div class="rb-fields">
        <span class="field-pill">백엔드 <b>1/2</b></span>
        <span class="field-pill">프론트엔드 <b>0/1</b></span>
        <span class="field-pill">기획·디자인 <b>1/1</b> ✓</span>
      </div>
    </div>

    <div class="appl-tabs" id="applTabs">
      <button class="on" data-f="all">전체 <span class="mono">5</span></button>
      <button data-f="대기">대기 <span class="mono">2</span></button>
      <button data-f="면접">면접 <span class="mono">1</span></button>
      <button data-f="승인">승인 <span class="mono">1</span></button>
      <button data-f="거절">거절 <span class="mono">1</span></button>
    </div>

    <div id="applList"></div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/workspace/applicants.js"></script>
</body>
</html>