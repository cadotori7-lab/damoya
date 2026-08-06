<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="p" value="${overview.project}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${p.title}"/> 개요</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
<jsp:include page="../includes/header.jsp" />
<main>
  <section id="v-overview">
    <a class="back" href="${ctx}/project/my">← 내 프로젝트</a>
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em><c:out value="${p.title}"/></em></h1>
    <p class="sub">프로젝트의 핵심 정보와 최근 현황을 한눈에 확인해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <div class="ov-hero">
      <div class="oh-main">
        <div class="oh-title">
          <h2><c:out value="${p.title}"/></h2>
          <span class="chip ${overview.projectStatusClass}"><c:out value="${overview.projectStatusLabel}"/></span>
          <c:if test="${not empty p.category}">
            <span class="tag"><c:out value="${p.category}"/></span>
          </c:if>
        </div>
        <div class="oh-meta">
          <span>팀원 <b><c:out value="${overview.teamCountLabel}"/></b></span>
          <span>시작일 <b><c:out value="${overview.startDateLabel}"/></b></span>
          <c:if test="${overview.recruiting}">
            <span>모집 종료일 <b><c:out value="${overview.endDateLabel}"/></b></span>
          </c:if>
        </div>
      </div>
      <div class="oh-prog">
        <div class="k">완료 업무</div>
        <div class="big">
          <c:out value="${overview.taskStats.approved}"/>
          <span class="ov-total">/ <c:out value="${overview.taskStats.total}"/></span>
        </div>
        <div class="mono ov-progress-detail">
          진행 <c:out value="${overview.taskStats.ongoing}"/> · 검수 대기 <c:out value="${overview.taskStats.review}"/>
        </div>
      </div>
    </div>

    <div class="ov-grid overview-top-grid">
      <section class="ocard overview-summary-card">
        <div class="oc-head"><div class="t">프로젝트 요약</div></div>
        <c:choose>
          <c:when test="${not empty p.summary}">
            <p class="oc-desc"><c:out value="${p.summary}"/></p>
          </c:when>
          <c:otherwise>
            <p class="oc-desc ov-empty-text">등록된 프로젝트 요약이 없어요.</p>
          </c:otherwise>
        </c:choose>

        <div class="oc-tags">
          <c:forEach var="tag" items="${overview.tags}">
            <span class="tag">#<c:out value="${tag}"/></span>
          </c:forEach>
          <c:if test="${empty overview.tags}">
            <span class="ov-empty-text">등록된 태그가 없어요.</span>
          </c:if>
        </div>
      </section>

      <section class="ocard overview-meetings-card">
        <div class="oc-head">
          <div class="t">회의</div>
          <a class="oc-more" href="${ctx}/workspace/${project_id}/meetings">회의 전체 보기</a>
        </div>

        <div class="overview-meeting-list">
          <c:forEach var="meeting" items="${overview.nearestMeetings}">
            <a class="overview-meeting-card"
               href="${ctx}/workspace/${project_id}/meetings/${meeting.meeting_id}">
              <div class="meeting-date"><c:out value="${meeting.meetDateDisplay}"/></div>
              <div class="meeting-copy">
                <strong><c:out value="${meeting.title}"/></strong>
                <span>
                  <c:choose>
                    <c:when test="${not empty meeting.summary}"><c:out value="${meeting.summary}"/></c:when>
                    <c:otherwise>등록된 회의 요약이 없어요.</c:otherwise>
                  </c:choose>
                </span>
              </div>
              <span class="meeting-arrow" aria-hidden="true">→</span>
            </a>
          </c:forEach>

          <c:if test="${empty overview.nearestMeetings}">
            <div class="overview-empty-state">
              <strong>등록된 회의가 아직 없어요.</strong>
              <span>회의를 등록하면 가까운 시간 순으로 표시됩니다.</span>
            </div>
          </c:if>
        </div>
      </section>
    </div>

    <section class="ocard overview-team-card">
      <div class="oc-head">
        <div class="t">팀원별 업무 현황</div>
        <a class="oc-more" href="${ctx}/workspace/${project_id}/board">업무 보드</a>
      </div>

      <div class="mp-stats overview-task-stats">
        <div class="mp-stat"><div class="n mono"><c:out value="${overview.taskStats.total}"/></div><div class="k">전체 업무</div></div>
        <div class="mp-stat"><div class="n mono status-ongoing"><c:out value="${overview.taskStats.ongoing}"/></div><div class="k">진행 중</div></div>
        <div class="mp-stat"><div class="n mono status-review"><c:out value="${overview.taskStats.review}"/></div><div class="k">검수 대기</div></div>
        <div class="mp-stat"><div class="n mono status-rejected"><c:out value="${overview.taskStats.rejected}"/></div><div class="k">반려</div></div>
        <div class="mp-stat"><div class="n mono status-approved"><c:out value="${overview.taskStats.approved}"/></div><div class="k">완료</div></div>
      </div>

      <div class="overview-table-scroll">
        <table class="ov-tbl overview-team-table">
          <thead>
            <tr>
              <th>팀원</th>
              <th>역할</th>
              <th>가장 가까운 업무</th>
              <th>마감일</th>
              <th>상태</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="memberTask" items="${overview.memberTasks}">
              <tr>
                <td>
                  <div class="u">
                    <span class="pic ${memberTask.roleLabel == '팀장' ? 'leader' : ''}"><c:out value="${memberTask.memberInitial}"/></span>
                    <c:out value="${memberTask.member.memberName}"/>
                  </div>
                </td>
                <td><span class="overview-role"><c:out value="${memberTask.roleLabel}"/></span></td>
                <td class="tk">
                  <c:choose>
                    <c:when test="${not empty memberTask.nearestTask}">
                      <a class="overview-task-link" href="${ctx}/workspace/${project_id}/board?task=${memberTask.nearestTask.task_id}">
                        <c:out value="${memberTask.nearestTask.task_name}"/>
                      </a>
                    </c:when>
                    <c:otherwise><span class="ov-empty-text">담당 업무 없음</span></c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <span class="mono overview-due ${memberTask.overdue ? 'overdue' : ''}">
                    <c:out value="${memberTask.dueDateLabel}"/>
                    <c:if test="${memberTask.overdue}"><small>지연</small></c:if>
                  </span>
                </td>
                <td><span class="chip ${memberTask.taskStatusClass}"><c:out value="${memberTask.taskStatusLabel}"/></span></td>
              </tr>
            </c:forEach>

            <c:if test="${empty overview.memberTasks}">
              <tr><td colspan="5" class="overview-table-empty">현재 참여 중인 팀원이 없어요.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </section>
</main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>
