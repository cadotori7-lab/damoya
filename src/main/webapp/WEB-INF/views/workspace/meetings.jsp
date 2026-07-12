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
    <link rel="stylesheet" href="../resources/css/style.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
  <main>
  <!-- ========== 회의 ========== -->
  <section id="v-meetings">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>AI 헬스케어 웹서비스</em></h1>
    <p class="sub">모집이 끝난 팀의 작업 공간이에요. 업무·일정·회의를 한곳에서 관리해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <div class="meet-wrap">
      <div class="meet-list" id="meetList"></div>
      <div class="panel minutes" id="minutes"></div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script src="../resources/js/workspace/meetings.js"></script>
</body>
</html>