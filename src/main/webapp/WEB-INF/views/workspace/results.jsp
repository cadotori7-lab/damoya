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
  <!-- ========== 결과물 (승인 완료 산출물) ========== -->
  <section id="v-results">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>AI 헬스케어 웹서비스</em></h1>
    <p class="sub">모집이 끝난 팀의 작업 공간이에요. 업무·일정·회의를 한곳에서 관리해요.</p>

    <div class="ws-nav">
      <button onclick="go('overview')">개요</button>
      <button onclick="go('tasks')">업무 보드</button>
      <button onclick="go('schedule')">일정</button>
      <button onclick="go('meetings')">회의</button>
      <button onclick="go('org')">팀원 관리</button>
      <button class="on" onclick="go('results')">결과물</button>
      <button onclick="go('complete')">완료</button>
    </div>

    <div class="res-summary">승인 완료된 결과물 <b id="resCount">7</b>건 · 팀이 만들어낸 산출물을 모아봤어요.</div>

    <div class="res-filter" id="resFilter">
      <button class="on" data-f="all">전체</button>
      <button data-f="이도현">이도현</button>
      <button data-f="김민재">김민재</button>
      <button data-f="서지우">서지우</button>
    </div>

    <div class="results-grid" id="resGrid"></div>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>