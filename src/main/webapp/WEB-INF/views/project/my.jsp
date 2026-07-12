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
  <!-- ========== 내 프로젝트 목록 ========== -->
  <section id="v-myprojects">
    <div class="eyebrow">My projects</div>
    <h1 class="page"><em>내 프로젝트</em></h1>
    <p class="sub">참여 중인 프로젝트예요. 눌러서 들어가면 개요·업무·회의를 관리할 수 있어요.</p>

    <div class="psel-list">
      <a class="psel-card" style="--c:var(--cat-contest)" href="${ctx}/workspace/overview">
        <div class="psel-main">
          <div class="psel-top"><span class="psel-cat">공모전 · 교내</span><span class="psel-role lead">팀장</span><span class="chip ing">진행중</span></div>
          <h3>AI 헬스케어 웹서비스</h3>
          <div class="psel-meta"><span>팀원 4명</span><span>D-24</span><span>내 업무 4건</span></div>
        </div>
        <svg class="psel-enter" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </a>

      <div class="psel-card" style="--c:var(--cat-major)" onclick="go('overview')" tabindex="0" onkeydown="if(event.key==='Enter')go('overview')">
        <div class="psel-main">
          <div class="psel-top"><span class="psel-cat">학과 · 교내</span><span class="psel-role member">팀원</span><span class="chip ing">진행중</span></div>
          <h3>데이터베이스 텀 프로젝트</h3>
          <div class="psel-meta"><span>팀원 3명</span><span>D-11</span><span>내 업무 2건</span></div>
        </div>
        <svg class="psel-enter" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>

      <div class="psel-card" style="--c:var(--cat-liberal)" onclick="go('overview')" tabindex="0" onkeydown="if(event.key==='Enter')go('overview')">
        <div class="psel-main">
          <div class="psel-top"><span class="psel-cat">교양 · 교내</span><span class="psel-role member">팀원</span><span class="chip ing">진행중</span></div>
          <h3>〈창업과 경영〉 발표 팀플</h3>
          <div class="psel-meta"><span>팀원 5명</span><span>D-6</span><span>내 업무 1건</span></div>
        </div>
        <svg class="psel-enter" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>
    </div>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>