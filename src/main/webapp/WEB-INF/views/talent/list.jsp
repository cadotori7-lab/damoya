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
  <!-- ========== 인재풀 목록 ========== -->
  <section id="v-talent">
    <div class="eyebrow">Talent pool</div>
    <h1 class="page"><em>함께할 사람</em> 찾기</h1>
    <p class="sub">팀원·멘토가 올린 자기소개 글이에요. 마음에 드는 분에게 팀장이 함께하기를 제의할 수 있어요.</p>

    <div class="searchbar">
      <input type="text" placeholder="관심 분야·기술·소개로 검색  (예: 백엔드, Spring, 디자인)">
      <button class="btn pri" onclick="go('talentpost')">+ 내 소개 올리기</button>
    </div>

    <div class="appl-tabs" id="talentTabs">
      <button class="on" data-f="all">전체</button>
      <button data-f="member">팀원 지원</button>
      <button data-f="mentor">멘토</button>
    </div>

    <div class="talent-grid" id="talentGrid"></div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>