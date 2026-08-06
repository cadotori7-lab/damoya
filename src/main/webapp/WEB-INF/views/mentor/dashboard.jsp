<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
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
      <div class="hstat active" id="stat-teams" onclick="selectView('teams')">
        <div class="ic a">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
        </div>
        <div><div class="n"><c:out value="${stats.mentoringTeamCount}"/></div><div class="k">멘토링 중인 팀</div></div>
      </div>
      <div class="hstat" id="stat-active" onclick="selectView('active')">
        <div class="ic c">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 10 10"/><path d="M12 6v6l4 2"/></svg>
        </div>
        <div><div class="n"><c:out value="${stats.activeProjectCount}"/></div><div class="k">진행 중 프로젝트</div></div>
      </div>
      <div class="hstat" id="stat-feedback" onclick="selectView('feedback')">
        <div class="ic b">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/><path d="M9 15h6"/></svg>
        </div>
        <div><div class="n"><c:out value="${stats.feedbackCount}"/></div><div class="k">내가 남긴 피드백</div></div>
      </div>
      <div class="hstat" id="stat-offers" onclick="selectView('offers')">
        <div class="ic d">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/></svg>
        </div>
        <div><div class="n"><c:out value="${stats.pendingOfferCount}"/></div><div class="k">제안받은 프로젝트</div></div>
      </div>
    </div>

    <div class="panel" style="margin-top:22px">
      <div class="panel-head">
        <h3 id="panelTitle">멘토링 중인 팀</h3>
      </div>

      <!-- 멘토링 중인 팀 / 진행 중 프로젝트 (같은 목록, '진행 중'은 완료 팀 숨김) -->
      <div data-view="teams">
        <c:choose>
          <c:when test="${empty teams}">
            <div class="team-member-empty">아직 멘토링 중인 팀이 없어요. 프로젝트에서 멘토로 참여하면 여기에 표시돼요.</div>
          </c:when>
          <c:otherwise>
            <div class="mentor-teams" id="teamGrid">
              <c:forEach var="team" items="${teams}">
                <c:choose>
                  <c:when test="${team.category eq '공모전' or team.category eq 'CONTEST'}">
                    <c:set var="catColor" value="--cat-contest"/><c:set var="catName" value="공모전"/>
                  </c:when>
                  <c:when test="${team.category eq '학과' or team.category eq 'DEPARTMENT'}">
                    <c:set var="catColor" value="--cat-major"/><c:set var="catName" value="학과"/>
                  </c:when>
                  <c:when test="${team.category eq '교양' or team.category eq 'LIBERAL'}">
                    <c:set var="catColor" value="--cat-liberal"/><c:set var="catName" value="교양"/>
                  </c:when>
                  <c:otherwise>
                    <c:set var="catColor" value="--cat-club"/><c:set var="catName" value="${team.category}"/>
                  </c:otherwise>
                </c:choose>
                <div class="mteam" style="--c:var(${catColor})" data-status="${team.status}">
                  <div class="mteam-top">
                    <span class="cat-badge"><c:out value="${catName}"/></span>
                    <c:choose>
                      <c:when test="${team.status eq 'RECRUITING'}"><span class="chip recruit">모집중</span></c:when>
                      <c:when test="${team.status eq 'DONE'}"><span class="chip done">완료</span></c:when>
                      <c:otherwise><span class="chip ing">진행중</span></c:otherwise>
                    </c:choose>
                  </div>
                  <h4><c:out value="${team.title}"/></h4>
                  <div class="mteam-meta">
                    <span>팀원 ${team.memberCount}명</span>
                    <span>업무 ${team.taskDone}/${team.taskTotal} 완료</span>
                    <c:if test="${not empty team.nextMeetingDisplay}">
                      <span>다음 회의 ${team.nextMeetingDisplay}</span>
                    </c:if>
                  </div>
                  <div class="mteam-bar"><div class="fill" style="width:${team.progressPercent}%"></div></div>
                  <div class="mteam-foot">
                    <a class="btn ghost sm" href="${ctx}/workspace/${team.projectId}/overview">진행 상황 보기</a>
                    <a class="btn pri sm" href="${ctx}/feedback/${team.projectId}">피드백 남기기</a>
                  </div>
                </div>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- 내가 남긴 피드백 -->
      <div data-view="feedback" style="display:none">
        <c:choose>
          <c:when test="${empty myFeedbacks}">
            <div class="team-member-empty">아직 남긴 피드백이 없어요. 팀 카드의 '피드백 남기기'에서 작성할 수 있어요.</div>
          </c:when>
          <c:otherwise>
            <div class="mp-list">
              <c:forEach var="f" items="${myFeedbacks}">
                <div class="mp-item" onclick="location.href='${ctx}/feedback/${f.projectId}'" style="cursor:pointer">
                  <div class="m-main">
                    <div class="m-cat"><c:out value="${f.projectTitle}"/></div>
                    <h4 style="font-weight:500;font-size:14px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis"><c:out value="${f.content}"/></h4>
                    <div class="m-meta"><span>${f.createdAtDisplay}</span></div>
                  </div>
                  <div class="m-right">
                    <span class="chip stage"><c:out value="${f.stageLabel}"/></span>
                  </div>
                </div>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- 제안받은 프로젝트 -->
      <div data-view="offers" style="display:none">
        <c:choose>
          <c:when test="${empty offeredProjects}">
            <div class="team-member-empty">제안받은 프로젝트가 없어요. 팀장이 멘토 제안을 보내면 여기에 표시돼요.</div>
          </c:when>
          <c:otherwise>
            <div class="mp-list">
              <c:forEach var="o" items="${offeredProjects}">
                <form action="${ctx}/mypage/accept-offer" method="post">
                  <input type="hidden" name="projectId" value="${o.projectId}" />
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <div class="mp-item" style="--c:var(--cat-${o.category})">
                    <div class="m-main" onclick="location.href='${ctx}/project/detail?id=${o.projectId}'" style="cursor:pointer">
                      <div class="m-cat"><c:out value="${o.category}"/></div>
                      <h4><c:out value="${o.title}"/></h4>
                      <div class="m-meta">
                        <span>제안일 ${fn:substring(o.appliedAt, 0, 10)}</span>
                        <span>${o.status eq 'RECRUITING' ? '모집중' : '진행중'}</span>
                      </div>
                    </div>
                    <div class="m-right">
                      <span class="role-tag mentor">멘토</span>
                      <button type="submit" formaction="${ctx}/mypage/reject-offer" class="btn sm ghost" onclick="return confirm('이 프로젝트의 멘토 제안을 거절하시겠습니까?')">거절</button>
                      <button type="submit" class="btn sm pri" onclick="return confirm('이 프로젝트의 멘토 제안을 수락하시겠습니까?')">수락</button>
                    </div>
                  </div>
                </form>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </main>

  <jsp:include page="../includes/footer.jsp" />

  <script>
    var viewTitles = {
      teams: '멘토링 중인 팀',
      active: '진행 중 프로젝트',
      feedback: '내가 남긴 피드백',
      offers: '제안받은 프로젝트'
    };

    function selectView(view) {
      document.getElementById('panelTitle').textContent = viewTitles[view];

      // '진행 중 프로젝트'는 팀 목록을 재사용하되 완료(DONE) 팀만 숨긴다
      var sectionName = (view === 'active') ? 'teams' : view;
      document.querySelectorAll('[data-view]').forEach(function (el) {
        el.style.display = (el.dataset.view === sectionName) ? '' : 'none';
      });
      var grid = document.getElementById('teamGrid');
      if (grid) grid.classList.toggle('only-active', view === 'active');

      document.querySelectorAll('.home-stats .hstat').forEach(function (card) {
        card.classList.remove('active');
      });
      document.getElementById('stat-' + view).classList.add('active');
    }
  </script>
</body>
</html>
