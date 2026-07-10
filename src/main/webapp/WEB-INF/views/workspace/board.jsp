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
  <!-- ========== 업무 보드 ========== -->
  <section id="v-tasks">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>AI 헬스케어 웹서비스</em></h1>
    <p class="sub">모집이 끝난 팀의 작업 공간이에요. 업무·일정·회의를 한곳에서 관리해요.</p>

    <div class="ws-nav">
      <button onclick="go('overview')">개요</button>
      <button class="on" onclick="go('tasks')">업무 보드</button>
      <button onclick="go('schedule')">일정</button>
      <button onclick="go('meetings')">회의</button>
      <button onclick="go('org')">팀원 관리</button>
      <button onclick="go('results')">결과물</button>
      <button onclick="go('complete')">완료</button>
      <div class="sp"><span class="team-need">팀원 4명 · 마감 D-24</span></div>
    </div>

    <div class="seg-toggle" id="taskToggle">
      <button class="on" data-p="all">전체 업무</button>
      <button data-p="mine">내 업무</button>
      <button data-p="team">팀 현황</button>
    </div>

    <div class="proj-switch" id="boardTools" style="gap:10px">
      <input type="text" id="taskSearch" class="board-search" placeholder="업무 검색 (제목·담당자)" oninput="onTaskSearch(this.value)">
      <button class="btn pri sm" style="margin-left:auto" onclick="go('taskform')">+ 업무 등록</button>
    </div>

    <div id="boardPanel">
      <div class="kanban" id="kanban"></div>
    </div>

    <!-- 팀 현황 (팀장) -->
    <div id="teamPanel" style="display:none">
      <div class="mp-stats">
        <div class="mp-stat"><div class="n mono">12</div><div class="k">전체 업무</div></div>
        <div class="mp-stat"><div class="n mono" style="color:var(--accent)">4</div><div class="k">진행중</div></div>
        <div class="mp-stat"><div class="n mono" style="color:var(--wait)">1</div><div class="k">검수 대기</div></div>
        <div class="mp-stat"><div class="n mono" style="color:var(--ok)">6</div><div class="k">승인 완료</div></div>
      </div>
      <div class="sec-label">팀원별 업무 현황 <span style="font-family:var(--sans);font-weight:500;color:var(--ink-soft);font-size:12px;text-transform:none;letter-spacing:0">· 이름을 누르면 담당 업무를 볼 수 있어요</span></div>
      <div class="member-row clickable" onclick="openMember('이도현')" tabindex="0" onkeydown="if(event.key==='Enter')openMember('이도현')">
        <span class="pic" style="background:#2b46c8">이</span>
        <div class="mr-info"><div class="nm">이도현 <span class="role">· 백엔드</span></div></div>
        <div class="mr-stat">완료 <b>3</b> / 배정 5</div>
        <svg class="chev" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>
      <div class="member-row clickable" onclick="openMember('김민재')" tabindex="0" onkeydown="if(event.key==='Enter')openMember('김민재')">
        <span class="pic" style="background:#2b46c8">민</span>
        <div class="mr-info"><div class="nm">김민재 <span class="role">· 백엔드</span></div></div>
        <div class="mr-stat">완료 <b>2</b> / 배정 4</div>
        <svg class="chev" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>
      <div class="member-row clickable" onclick="openMember('서지우')" tabindex="0" onkeydown="if(event.key==='Enter')openMember('서지우')">
        <span class="pic" style="background:#0f9d8c">서</span>
        <div class="mr-info"><div class="nm">서지우 <span class="role">· 기획/디자인</span></div></div>
        <div class="mr-stat">완료 <b>3</b> / 배정 3</div>
        <svg class="chev" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>