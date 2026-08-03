<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시물 관리 | 다모여</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
    <!-- ========== 게시물 관리 (관리자) ========== -->
  <section id="v-posts">
    <a class="back" href="${ctx}/admin/dashboard">← 관리자 대시보드</a>
    <div class="eyebrow">Post management</div>
    <h1 class="page"><em>게시물 관리</em></h1>
    <p class="sub">공모 게시물을 확인하고, 신고·규정 위반 게시물을 숨기거나 삭제할 수 있어요.</p>

    <div class="acc-toolbar">
      <input type="text" id="postSearch" placeholder="제목·작성자로 검색">
    </div>
    <div class="appl-tabs" id="postTabs">
      <button class="on" data-f="all">전체 <span class="mono">0</span></button>
      <button data-f="reported">신고 있음 <span class="mono">0</span></button>
      <button data-f="hidden">숨김·삭제 <span class="mono">0</span></button>
    </div>

    <div class="tbl-wrap">
      <table id="postTable"></table>
    </div>

    <nav aria-label="page" style="margin-top: 32px;">
      <ul class="pagination" id="postPagination"
          style="display: flex; justify-content: center; gap: 8px; list-style: none; padding: 0;"></ul>
    </nav>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script type="application/json" id="reports-data"><c:out value="${reports}" escapeXml="false"/></script>
  <script>
    const reports = JSON.parse(document.getElementById('reports-data').textContent || '[]');
    const ctx = '${pageContext.request.contextPath}';
    const csrfParameter = '${_csrf.parameterName}';
    const csrfToken = '${_csrf.token}';
    <c:if test="${not empty msg}">
      alert('<c:out value="${msg}"/>');
    </c:if>
  </script>
  <script src="${ctx}/resources/js/common.js"></script>
  <script src="${ctx}/resources/js/adminPosts.js"></script>
</body>
</html>
