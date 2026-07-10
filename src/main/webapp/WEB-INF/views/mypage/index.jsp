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
      <button class="btn ghost edit" onclick="openModal('profileModal')">프로필 수정</button>
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
  <div class="modal-overlay" id="profileModal" onclick="if(event.target===this)closeModal('profileModal')">
  <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="profileTitle">
    <div class="modal-head">
      <div class="mh-info"><h3 id="profileTitle">프로필 수정</h3><div class="role">다른 사람에게 보이는 내 정보예요</div></div>
      <button class="modal-close" onclick="closeModal('profileModal')" aria-label="닫기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>
    <div class="modal-body" id="profileBody">
      <div style="display:flex;align-items:center;gap:14px;margin-bottom:18px">
        <span class="big" style="width:56px;height:56px;border-radius:16px;background:linear-gradient(135deg,#2b46c8,#5b45c8);color:#fff;display:grid;place-items:center;font-size:22px;font-weight:800;flex:none">민</span>
        <button class="btn ghost sm">사진 변경</button>
      </div>
      <div class="fld one"><label>이름</label><input type="text" id="pfName" value="김민재"></div>
      <div class="frow">
        <div class="fld"><label>학과</label>
          <select id="pfDept"><option selected>컴퓨터공학과</option><option>소프트웨어학과</option><option>산업경영공학과</option><option>게임공학과</option></select>
        </div>
        <div class="fld"><label>학년</label>
          <select id="pfYear"><option>1학년</option><option>2학년</option><option>3학년</option><option selected>4학년</option><option>5학년 이상</option></select>
        </div>
      </div>
      <div class="frow">
        <div class="fld"><label>주전공</label><input type="text" id="pfMajor" value="컴퓨터공학"></div>
        <div class="fld"><label>복수전공 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><input type="text" id="pfMinor" placeholder="없으면 비워두세요"></div>
      </div>
      <div class="fld one">
        <label>관심 분야</label>
        <div class="picker" id="pfInterests">
          <input type="checkbox" id="pf1" checked><label for="pf1">백엔드</label>
          <input type="checkbox" id="pf2"><label for="pf2">프론트엔드</label>
          <input type="checkbox" id="pf3" checked><label for="pf3">데이터</label>
          <input type="checkbox" id="pf4"><label for="pf4">AI</label>
          <input type="checkbox" id="pf5"><label for="pf5">기획</label>
          <input type="checkbox" id="pf6"><label for="pf6">디자인</label>
          <input type="checkbox" id="pf7"><label for="pf7">게임</label>
        </div>
      </div>
      <div class="fld one"><label>한 줄 소개 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><input type="text" id="pfBio" placeholder="예: Spring 백엔드에 관심 많은 4학년"></div>
      <div class="form-foot">
        <button class="btn ghost" onclick="closeProfile()">취소</button>
        <button class="btn pri" onclick="saveProfile()">저장하기</button>
      </div>
    </div>
  </div>
</div>
  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/common.js"></script>
</body>
</html>