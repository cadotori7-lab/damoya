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
    <!-- ========== 프로젝트 상세 ========== -->
  <section id="v-detail">
    <button class="back" onclick="go('match')">← 목록으로</button>
    <div class="detail">
      <div>
        <div class="panel d-head">
          <div class="cat">공모전 · 교내</div>
          <h2>2026 캡스톤 경진대회 — AI 헬스케어 웹서비스 팀원 모집</h2>
          <div style="display:flex;gap:8px;flex-wrap:wrap">
            <span class="chip recruit">모집중</span>
            <span class="tag">Spring</span><span class="tag">React</span><span class="tag">MySQL</span><span class="tag">캡스톤</span>
          </div>
          <div class="d-meta">
            <div><div class="k">카테고리</div><div class="v">공모전</div></div>
            <div><div class="k">대상 학년</div><div class="v">3–4학년</div></div>
            <div><div class="k">모집 인원</div><div class="v">4명 (2/4)</div></div>
            <div><div class="k">모집 기간</div><div class="v mono">~ 08.14</div></div>
            <div><div class="k">대학 / 학과</div><div class="v">대진대 · 컴퓨터공학</div></div>
          </div>
          <div class="prose">
            <p>교내 캡스톤 경진대회 출품을 목표로, 학생 건강 관리를 돕는 AI 기반 웹서비스를 함께 만들 팀원을 찾습니다. 방학 동안 집중해서 완성도 있는 결과물을 만드는 것이 목표예요.</p>
            <h5>이런 분을 찾아요</h5>
            <ul>
              <li>Spring 백엔드 또는 React 프론트를 다뤄봤거나, 배우며 참여할 의지가 있는 분</li>
              <li>주 2회 오프라인 회의에 참여 가능한 분 (대진대 캠퍼스)</li>
              <li>끝까지 완주할 책임감이 있는 분</li>
            </ul>
            <h5>모집 분야</h5>
            <ul>
              <li>백엔드 2명 · 프론트엔드 1명 · 기획/디자인 1명</li>
            </ul>
          </div>
        </div>

        <div class="panel">
          <h5 style="font-size:16px;font-weight:800;margin-bottom:4px">댓글 <span class="mono" style="color:var(--ink-soft);font-size:14px">3</span></h5>
          <div class="cmt">
            <div class="pic" style="background:#0f9d8c">서</div>
            <div class="body"><div class="nm">서지우 <span>경영학 · 3학년</span></div><p>혹시 디자인만 담당해도 지원 가능할까요? 피그마 사용 가능합니다!</p></div>
          </div>
          <div class="cmt">
            <div class="pic" style="background:#e07a45">이</div>
            <div class="body"><div class="nm">이도현 <span>컴퓨터공학 · 4학년</span></div><p>백엔드 지원하고 싶습니다. Spring, MyBatis 경험 있어요.</p></div>
          </div>
          <div class="cmt">
            <div class="pic" style="background:#2b46c8">팀</div>
            <div class="body"><div class="nm">팀장 <span>답글</span></div><p>두 분 다 환영합니다 🙌 지원서 넣어주시면 면접 일정 잡을게요.</p></div>
          </div>

          <div class="cmt-form">
            <div class="pic">민</div>
            <div class="cf-input">
              <textarea id="cmtInput" placeholder="궁금한 점이나 지원 관련 문의를 남겨보세요."></textarea>
              <div class="cf-foot"><button class="btn pri sm" onclick="addComment()">댓글 등록</button></div>
            </div>
          </div>
        </div>
      </div>

      <div class="side">
        <div class="apply-card">
          <div class="num">2<small> / 4명 모집</small></div>
          <div class="bar"><span style="width:50%"></span></div>
          <div class="team-need mono" style="font-size:12px;color:var(--ink-soft)">백엔드 1 · 프론트 1 · 기획 1 남음</div>
          <div class="lead">
            <div class="pic">최</div>
            <div class="nm">최윤서 <small>팀장 · 컴퓨터공학 4학년</small></div>
          </div>
          <div style="font-family:var(--mono);font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:var(--ink-soft);margin-bottom:6px">지원 절차</div>
          <div class="stepline">
            <div class="act">지원</div><div>면접</div><div>승인</div>
          </div>
          <button class="btn pri" style="width:100%;justify-content:center;margin-top:18px">지원하기</button>
          <button class="btn ghost" style="width:100%;justify-content:center;margin-top:9px">♥ 관심 등록</button>
        </div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script src="../resources/js/detail.js"></script>
</body>
</html>