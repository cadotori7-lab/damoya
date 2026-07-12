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
    <a class="back" href="${ctx}/talent/list">← 인재풀</a>
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
  
<!-- ===== 함께하기 제의 모달 ===== -->
<div class="modal-overlay" id="offerModal" onclick="if(event.target===this)closeOffer()">
  <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="offerTitle">
    <div class="modal-head">
      <div class="mh-info"><h3 id="offerTitle">함께하기 제의</h3><div class="role" id="offerWho">상대에게 초대를 보내요</div></div>
      <button class="modal-close" onclick="closeOffer()" aria-label="닫기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>
    <div class="modal-body" id="offerBody">
      <div class="offer-proj">
        <div class="k">초대할 프로젝트</div>
        <select id="offerProj">
          <option>AI 헬스케어 웹서비스 (팀장)</option>
          <option>데이터베이스 텀 프로젝트</option>
        </select>
      </div>
      <div class="fld one"><label>맡아줬으면 하는 역할</label><input type="text" id="offerRole" placeholder="예: 백엔드 · 인증/권한"></div>
      <div class="fld one"><label>제의 메시지</label><textarea placeholder="왜 함께하고 싶은지, 어떤 점이 좋았는지 적어주세요." style="min-height:90px"></textarea></div>
      <div class="form-foot">
        <button class="btn ghost" onclick="closeOffer()">취소</button>
        <button class="btn pri" onclick="sendOffer()">제의 보내기</button>
      </div>
    </div>
  </div>
</div>
<jsp:include page="../includes/footer.jsp" />
<script src="${ctx}/resources/js/common.js"></script>
<script src="${ctx}/resources/js/talent.js"></script>
</body>
</html>