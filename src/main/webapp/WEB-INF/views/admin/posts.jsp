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
    <!-- ========== 게시물 관리 (관리자) ========== -->
  <section id="v-posts">
    <a class="back" href="${ctx}/admin/dashboard">← 관리자 대시보드</a>
    <div class="eyebrow">Post management</div>
    <h1 class="page"><em>게시물 관리</em></h1>
    <p class="sub">공모 게시물을 확인하고, 신고·규정 위반 게시물을 숨기거나 삭제할 수 있어요.</p>

    <div class="acc-toolbar">
      <input type="text" placeholder="제목·작성자로 검색">
    </div>
    <div class="appl-tabs" id="postTabs">
      <button class="on" data-f="all">전체 <span class="mono">6</span></button>
      <button data-f="reported">신고 있음 <span class="mono">2</span></button>
      <button data-f="hidden">숨김·삭제 <span class="mono">1</span></button>
    </div>

    <div class="tbl-wrap">
      <table id="postTable"></table>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/common.js"></script>
  <script src="${ctx}/resources/js/adminPosts.js"></script>
</body>
</html>