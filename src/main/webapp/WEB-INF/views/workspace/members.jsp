<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 팀원 관리</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
  <section id="v-org">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em><c:out value="${project.title}"/></em></h1>
    <p class="sub">현재 프로젝트의 팀 구성과 역할을 확인해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <c:if test="${not empty teamMessage}">
      <div class="panel" style="margin-bottom:16px;color:var(--ok);font-weight:700"><c:out value="${teamMessage}"/></div>
    </c:if>
    <c:if test="${not empty teamError}">
      <div class="panel" style="margin-bottom:16px;color:var(--reject);font-weight:700"><c:out value="${teamError}"/></div>
    </c:if>

    <c:if test="${canManageTeam}">
      <div class="tm-section">
        <div class="sec-label" style="justify-content:center">지원자 관리</div>
        <a class="tm-row" style="cursor:pointer" href="${ctx}/workspace/${project_id}/applicants">
          <span class="pic" style="background:var(--accent-soft);color:var(--accent)">지원</span>
          <div class="tm-info">
            <div class="nm">대기 중인 지원자 확인</div>
            <div class="role">대기 중인 지원자 <b style="color:var(--reject)">${waitingApplicantCount}명</b></div>
          </div>
          <span class="btn sm pri" style="pointer-events:none">지원자 관리 →</span>
        </a>
      </div>
    </c:if>

    <div class="tm-section" style="margin-top:24px">
      <div class="sec-label" style="justify-content:center">
        팀 구성
        <span style="font-family:var(--sans);font-weight:500;color:var(--ink-soft);font-size:12px;text-transform:none;letter-spacing:0">
          · 현재 ${fn:length(teamMembers)}명
        </span>
      </div>

      <c:forEach var="teamMember" items="${teamMembers}">
        <div class="tm-row">
          <span class="pic" style="background:var(--accent-soft);color:var(--accent)">
            <c:out value="${fn:substring(teamMember.memberName, 0, 1)}"/>
          </span>
          <div class="tm-info">
            <div class="nm"><c:out value="${teamMember.memberName}"/></div>
            <div class="role">
              <c:choose>
                <c:when test="${teamMember.projectRole == 'LEADER'}">팀장</c:when>
                <c:when test="${teamMember.projectRole == 'MENTOR'}">멘토 · 읽기 전용</c:when>
                <c:otherwise>팀원</c:otherwise>
              </c:choose>
              <c:if test="${not empty teamMember.wantPosition}"> · <c:out value="${teamMember.wantPosition}"/></c:if>
              <c:if test="${not empty teamMember.memberMajor}"> · <c:out value="${teamMember.memberMajor}"/></c:if>
              <c:if test="${teamMember.memberGrade > 0}"> ${teamMember.memberGrade}학년</c:if>
            </div>
          </div>

          <c:if test="${teamMember.projectRole == 'MEMBER'}">
            <div class="tm-succ">
              <span class="lbl">승계</span>
              <span class="rank">${teamMember.successionOrder}</span>
              <c:if test="${canManageTeam}">
                <div class="arrows">
                  <form method="post" style="display:inline" action="${ctx}/workspace/${project_id}/members/${teamMember.memberId}/succession">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="direction" value="UP"/>
                    <button type="submit" aria-label="승계 순위 올리기">▲</button>
                  </form>
                  <form method="post" style="display:inline" action="${ctx}/workspace/${project_id}/members/${teamMember.memberId}/succession">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="direction" value="DOWN"/>
                    <button type="submit" aria-label="승계 순위 내리기">▼</button>
                  </form>
                </div>
              </c:if>
            </div>
            <c:if test="${canManageTeam}">
              <div class="tm-kick">
                <form method="post" action="${ctx}/workspace/${project_id}/members/${teamMember.memberId}/kick"
                      onsubmit="return confirm('이 팀원을 프로젝트에서 강퇴할까요?');">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <button type="submit" class="btn sm danger">강퇴</button>
                </form>
              </div>
            </c:if>
          </c:if>
        </div>
      </c:forEach>

      <c:if test="${empty teamMembers}">
        <div class="panel" style="text-align:center;color:var(--ink-soft)">표시할 참여자가 없습니다.</div>
      </c:if>

      <c:choose>
        <c:when test="${canManageTeam}">
          <div class="tm-note">팀원 순위를 조정하면 팀장 승계 시 해당 순서가 사용됩니다.</div>
          <div class="tm-row" style="margin-top:16px">
            <div class="tm-info">
              <div class="nm">프로젝트 나가기</div>
              <div class="role">
                <c:choose>
                  <c:when test="${canLeaderLeave}">승계 1순위 팀원에게 팀장 권한을 넘기고 프로젝트에서 나갑니다.</c:when>
                  <c:otherwise>승계할 팀원이 없어 현재는 프로젝트를 나갈 수 없습니다.</c:otherwise>
                </c:choose>
              </div>
            </div>
            <div class="tm-kick">
              <c:choose>
                <c:when test="${canLeaderLeave}">
                  <form method="post" action="${ctx}/workspace/${project_id}/members/leave"
                        onsubmit="return confirm('승계 1순위 팀원에게 팀장을 넘기고 프로젝트에서 나갈까요?');">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn sm danger">나가기</button>
                  </form>
                </c:when>
                <c:otherwise>
                  <button type="button" class="btn sm danger" disabled>나가기</button>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="tm-note">멘토는 피드백 기능을 제외한 워크스페이스 정보를 읽기만 할 수 있습니다.</div>
        </c:otherwise>
      </c:choose>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>
