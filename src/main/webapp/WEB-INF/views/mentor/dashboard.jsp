<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 멘토 대시보드</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />

  <main>
    <div class="eyebrow">Mentor</div>
    <h1 class="page"><em><c:out value="${member.name}"/></em> 멘토님, 안녕하세요</h1>
    <p class="sub">맡고 계신 팀들의 진행 상황과 피드백 요청을 한곳에서 확인해요.</p>

    <div class="home-stats">
      <div class="hstat">
        <div class="ic a">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
        </div>
        <div><div class="n"><c:out value="${stats.mentoringTeamCount}"/></div><div class="k">멘토링 중인 팀</div></div>
      </div>
      <div class="hstat">
        <div class="ic c">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 10 10"/><path d="M12 6v6l4 2"/></svg>
        </div>
        <div><div class="n"><c:out value="${stats.activeProjectCount}"/></div><div class="k">진행 중 프로젝트</div></div>
      </div>
      <div class="hstat">
        <div class="ic b">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/><path d="M9 15h6"/></svg>
        </div>
        <div><div class="n"><c:out value="${stats.feedbackCount}"/></div><div class="k">내가 남긴 피드백</div></div>
      </div>
    </div>

    <div class="panel" style="margin-top:22px">
      <div class="panel-head">
        <h3>멘토링 중인 팀</h3>
      </div>
      <div class="mentor-teams">
        <div class="mteam">
          <div class="mteam-top">
            <span class="cat-badge" style="--c:#c98a12">공모전</span>
            <span class="chip approve">진행중</span>
          </div>
          <h4>AI 헬스케어 웹서비스</h4>
          <div class="mteam-meta">
            <span>팀원 4명</span>
            <span>업무 6/9 완료</span>
            <span>다음 회의 8.24(월)</span>
          </div>
          <div class="mteam-bar"><div class="fill" style="width:67%"></div></div>
          <div class="mteam-foot">
            <a class="btn ghost sm" href="#">진행 상황 보기</a>
            <a class="btn pri sm" href="#">피드백 남기기</a>
          </div>
        </div>
      </div>
    </div>
  </main>

  <jsp:include page="../includes/footer.jsp" />
</body>
</html>
