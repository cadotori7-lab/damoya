<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
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
  <!-- ========== 조직도 ========== -->
  <section id="v-org">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>AI 헬스케어 웹서비스</em></h1>
    <p class="sub">모집이 끝난 팀의 작업 공간이에요. 업무·일정·회의를 한곳에서 관리해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <div class="org">
      <div class="org-mentor">
        <div class="org-node">
          <div class="pic" style="background:linear-gradient(135deg,#e07a45,#c98a12)">박</div>
          <div class="nm">박준호</div>
          <div class="role">멘토 · 초대</div>
          <div class="dept">컴퓨터공학 교수</div>
        </div>
      </div>
      <div class="conn-v dash"></div>

      <div class="org-node lead">
        <div class="pic" style="background:linear-gradient(135deg,#c98a12,#e07a45)">최</div>
        <div class="nm">최윤서</div>
        <div class="role">팀장 · 프론트엔드</div>
        <div class="dept">컴퓨터공학 · 4학년</div>
      </div>
      <div class="conn-v"></div>

      <div class="org-row" id="orgRow"></div>

      <div class="org-legend">
        <span><span class="succ" style="position:static;width:20px;height:20px;font-size:11px">n</span>승계 우선순위</span>
        <span><i style="width:12px;height:12px;border:1.5px dashed var(--cat-liberal);border-radius:3px;display:inline-block"></i>멘토(초대·자문)</span>
      </div>
    </div>

    <div class="tm-section">
      <div class="sec-label" style="justify-content:center">지원자 관리</div>
      <a class="tm-row" style="cursor:pointer" href="${ctx}/workspace/applicants">
        <span class="pic" style="background:var(--accent-soft);color:var(--accent)">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </span>
        <div class="tm-info"><div class="nm">지원자 확인 · 면접 · 승인</div><div class="role">모집 2/4명 확정 · 대기 중인 지원 <b style="color:var(--reject)">2명</b></div></div>
        <span class="btn sm pri" style="pointer-events:none">지원자 관리 →</span>
      </a>
      <div class="tm-note" style="margin-top:12px">지원–면접–승인을 거쳐 팀원을 확정해요. 승인하면 아래 팀 구성에 자동으로 추가돼요.</div>
    </div>

    <div class="tm-section" style="margin-top:24px">
      <div class="sec-label" style="justify-content:center">보낸 제의 <span style="font-family:var(--sans);font-weight:500;color:var(--ink-soft);font-size:12px;text-transform:none;letter-spacing:0">· 인재풀에서 초대한 사람들</span></div>
      <div id="sentOffers"></div>
      <div class="tm-note" style="margin-top:12px">인재풀에서 <b>함께하기 제의</b>를 보낸 목록이에요. 상대가 수락하면 아래 팀 구성에 추가돼요. 대기 중인 제의는 취소할 수 있어요.</div>
    </div>

    <div class="tm-section" style="margin-top:24px">
      <div class="sec-label" style="justify-content:center">팀 구성 · 승계 순위</div>
      <div id="tmList"></div>
      <div class="tm-note">팀장이 탈퇴하거나 계정이 삭제되면 <b>승계 우선순위</b> 순번대로 다음 팀원이 자동으로 팀장이 돼요. 화살표로 순위를 조정하거나, 규정을 위반한 팀원을 강퇴할 수 있어요.</div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script src="../resources/js/workspace/members.js"></script>
</body>
</html>