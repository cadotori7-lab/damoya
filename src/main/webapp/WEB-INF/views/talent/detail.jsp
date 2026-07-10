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
  <!-- ========== 인재 상세 ========== -->
  <section id="v-talentdetail">
    <button class="back" onclick="go('talent')">← 인재풀</button>
    <div class="detail">
      <div>
        <div class="panel">
          <div class="tc-head" style="margin-bottom:16px">
            <span class="pic" id="tdPic" style="width:60px;height:60px;font-size:22px"></span>
            <div class="who"><div class="nm" id="tdName" style="font-size:20px;font-weight:800"></div><div class="dept" id="tdDept" style="font-size:14px"></div></div>
            <span class="tc-kind" id="tdKind"></span>
          </div>
          <div class="d-meta" style="margin-top:0">
            <div><div class="k">희망 분야</div><div class="v" id="tdField"></div></div>
            <div><div class="k">가능 시간</div><div class="v" id="tdTime"></div></div>
            <div><div class="k">관심 카테고리</div><div class="v" id="tdCat"></div></div>
          </div>
          <div class="prose" id="tdIntro"></div>
          <div class="tc-tags" id="tdTags" style="margin-top:16px"></div>
        </div>
      </div>
      <div class="side">
        <div class="apply-card" style="position:sticky;top:86px">
          <div style="font-family:var(--mono);font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:var(--ink-soft);margin-bottom:8px">팀장이라면</div>
          <p style="font-size:13px;color:var(--ink-soft);line-height:1.6;margin-bottom:14px">내 프로젝트에 이 분을 초대할 수 있어요. 제의를 보내면 상대가 수락/거절해요.</p>
          <button class="btn pri" style="width:100%;justify-content:center" onclick="openOffer()">함께하기 제의</button>
          <button class="btn ghost" style="width:100%;justify-content:center;margin-top:9px">♥ 관심 표시</button>
        </div>
      </div>
    </div>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>