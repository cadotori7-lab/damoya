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
  <section id="v-talentpost">
    <div class="form-wrap">
      <button class="back" onclick="go('talent')">← 인재풀</button>
      <div class="eyebrow">New profile post</div>
      <h1 class="page"><em>내 소개</em> 올리기</h1>
      <p class="sub">프로젝트를 찾고 있다는 걸 알려요. 팀장들이 보고 함께하기를 제의할 수 있어요.</p>

      <div class="form-card">
        <div class="fld one">
          <label>글 유형<span class="req">*</span></label>
          <div class="picker">
            <input type="radio" name="tkind" id="tk1" checked><label for="tk1">팀원으로 지원</label>
            <input type="radio" name="tkind" id="tk2"><label for="tk2">멘토로 참여</label>
          </div>
        </div>
        <div class="fld one"><label>한 줄 소개<span class="req">*</span></label><input type="text" placeholder="예: Spring 백엔드로 함께할 팀을 찾고 있어요"></div>
        <div class="frow">
          <div class="fld"><label>희망 분야</label><input type="text" placeholder="예: 백엔드 / 기획 / 멘토링"></div>
          <div class="fld"><label>가능 시간</label><input type="text" placeholder="예: 평일 저녁, 주말"></div>
        </div>
        <div class="fld one">
          <label>관심 카테고리</label>
          <div class="picker">
            <input type="checkbox" id="tpc1" checked><label for="tpc1">공모전</label>
            <input type="checkbox" id="tpc2"><label for="tpc2">학과</label>
            <input type="checkbox" id="tpc3"><label for="tpc3">교양</label>
            <input type="checkbox" id="tpc4"><label for="tpc4">교내활동</label>
          </div>
        </div>
        <div class="fld one"><label>자기소개<span class="req">*</span></label><textarea placeholder="경험, 다뤄본 기술, 어떤 팀과 함께하고 싶은지 자유롭게 적어주세요." style="min-height:120px"></textarea></div>
        <div class="fld one"><label>포트폴리오 링크 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><input type="text" placeholder="GitHub, 노션, 블로그 등"></div>
        <div class="form-foot">
          <button class="btn ghost" onclick="go('talent')">취소</button>
          <button class="btn pri" onclick="go('talent')">등록하기</button>
        </div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>