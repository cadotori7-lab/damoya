<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 일정</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />

  <main>
  <section id="v-schedule">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>${project.title}</em></h1>
    <p class="sub">업무 마감·회의·피드백 일정을 한 달 단위로 모아 봐요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <%-- ===== 달력 헤더: 월 이동 + 범례 ===== --%>
    <div class="cal-head">
      <div class="navs">
        <a href="${ctx}/workspace/${project_id}/schedule?ym=${prevYm}" aria-label="이전 달">◀</a>
        <a href="${ctx}/workspace/${project_id}/schedule?ym=${nextYm}" aria-label="다음 달">▶</a>
      </div>
      <h3>${year}년 ${month}월</h3>
      <a class="btn ghost sm" href="${ctx}/workspace/${project_id}/schedule?ym=${thisYm}">오늘</a>

      <div class="cal-legend">
        <span><i style="background:var(--accent-soft)"></i>회의</span>
        <span><i style="background:var(--wait-bg)"></i>업무 마감</span>
        <span><i style="background:var(--interview-bg)"></i>피드백</span>
      </div>
    </div>

    <%-- ===== 달력 격자 ===== --%>
    <div class="cal">
      <div class="dow">
        <div>일</div><div>월</div><div>화</div><div>수</div><div>목</div><div>금</div><div>토</div>
      </div>

      <div class="grid7">
        <c:forEach var="week" items="${weeks}">
          <c:forEach var="cell" items="${week}">
            <div class="cell ${cell.outside ? 'out' : ''} ${cell.today ? 'today' : ''}">
              <div class="dn">${cell.day}</div>

              <c:forEach var="e" items="${cell.events}">
                <c:choose>
                  <%-- 회의는 회의록으로 이동 --%>
                  <c:when test="${e.type == 'meet'}">
                    <a class="ev meet" title="${fn:escapeXml(e.title)}"
                       href="${ctx}/workspace/${project_id}/meetings/${e.refId}">
                      <c:out value="${e.title}"/>
                    </a>
                  </c:when>
                  <%-- 업무는 업무 보드로 이동 --%>
                  <c:when test="${e.type == 'task'}">
                    <a class="ev task" title="${fn:escapeXml(e.title)}"
                       href="${ctx}/workspace/${project_id}/board?task=${e.refId}">
                      <c:out value="${e.title}"/>
                    </a>
                  </c:when>
                  <c:otherwise>
                    <div class="ev fb" title="${fn:escapeXml(e.title)}">
                      <c:out value="${e.title}"/>
                    </div>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
            </div>
          </c:forEach>
        </c:forEach>
      </div>
    </div>
  </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />
</body>
</html>
