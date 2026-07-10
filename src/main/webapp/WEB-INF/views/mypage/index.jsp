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
  <!-- ========== 마이페이지 ========== -->
  <section id="v-mypage">
    <div class="eyebrow">My page</div>
    <h1 class="page"><em>마이페이지</em></h1>
    <p class="sub" style="margin-bottom:22px">내 프로젝트, 지원 현황, 관심 목록을 한곳에서 확인해요.</p>

    <div class="mp-head">
      <div class="big">민</div>
      <div class="info">
        <h2>김민재</h2>
        <div class="line">대진대학교 · 컴퓨터공학과 · 4학년</div>
        <div class="badges"><span class="b">백엔드</span><span class="b">데이터</span><span class="b">✓ 학교 인증됨</span></div>
      </div>
      <button class="btn ghost edit" onclick="openProfile()">프로필 수정</button>
    </div>

    <div class="mp-stats">
      <div class="mp-stat"><div class="n">3</div><div class="k">진행 중</div></div>
      <div class="mp-stat"><div class="n">5</div><div class="k">완료</div></div>
      <div class="mp-stat"><div class="n">2</div><div class="k">지원 대기</div></div>
      <div class="mp-stat"><div class="n">7</div><div class="k">관심</div></div>
    </div>

    <div class="mp-tabs" id="mpTabs">
      <button class="on" data-t="joined">참여 중인 프로젝트</button>
      <button data-t="applied">내 지원 현황</button>
      <button data-t="offers">받은 제의</button>
      <button data-t="liked">관심 목록</button>
    </div>
    <div class="mp-list" id="mpList"></div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>