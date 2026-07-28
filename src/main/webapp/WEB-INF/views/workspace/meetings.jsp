<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 회의</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />

  <main>
  <section id="v-meetings">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>${project.title}</em></h1>
    <p class="sub">모집이 끝난 팀의 작업 공간이에요. 업무·일정·회의를 한곳에서 관리해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <div class="meet-wrap">

      <div>
        <c:if test="${myRole == 'LEADER'}">
          <a class="btn pri" style="width:100%;justify-content:center;margin-bottom:12px"
             href="${ctx}/workspace/${project_id}/meetings/form">+ 회의 등록</a>
        </c:if>

        <div class="meet-list">
          <c:forEach var="m" items="${meetingList}">
            <a class="meet-card ${m.meeting_id == meeting.meeting_id ? 'on' : ''}"
               href="${ctx}/workspace/${project_id}/meetings/${m.meeting_id}">
              <div class="dt">
                ${m.meetDateDisplay}
              </div>
              <h4><c:out value="${m.title}"/></h4>
              <p><c:out value="${m.summary}"/></p>
            </a>
          </c:forEach>

          <c:if test="${empty meetingList}">
            <div class="panel" style="text-align:center;color:var(--ink-soft);font-size:13.5px">
              아직 등록된 회의가 없어요.
            </div>
          </c:if>
        </div>
      </div>

      <div class="panel minutes">
        <c:choose>
          <c:when test="${meeting != null}">
            <div class="mh">
              <div style="display:flex;justify-content:space-between;align-items:flex-start;gap:10px">
                <div>
                  <div class="dt">
                    ${meeting.meetDateDisplay}
                  </div>
                  <h2><c:out value="${meeting.title}"/></h2>
                </div>

                <c:if test="${myRole == 'LEADER'}">
                  <div style="display:flex;gap:6px;flex:none">
                    <a class="btn ghost sm"
                       href="${ctx}/workspace/${project_id}/meetings/${meeting.meeting_id}/edit">✎ 수정</a>

                    <form action="${ctx}/workspace/${project_id}/meetings/${meeting.meeting_id}/delete"
                          method="post" style="display:inline"
                          onsubmit="return confirm('이 회의록을 삭제할까요?');">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                      <button type="submit" class="btn ghost sm" style="color:var(--reject)">삭제</button>
                    </form>
                  </div>
                </c:if>
              </div>

              <c:if test="${not empty meeting.summary}">
                <p style="color:var(--ink-soft);font-size:13.5px;margin-top:8px">
                  <c:out value="${meeting.summary}"/>
                </p>
              </c:if>
            </div>

            <div class="minutes-body"><c:out value="${meeting.content}"/></div>
          </c:when>

          <c:otherwise>
            <div style="text-align:center;padding:50px 20px;color:var(--ink-soft)">
              <p style="font-size:14.5px">왼쪽에서 회의를 선택하면 회의록이 보여요.</p>
              <c:if test="${myRole == 'LEADER' and empty meetingList}">
                <a class="btn pri" style="margin-top:16px"
                   href="${ctx}/workspace/${project_id}/meetings/form">첫 회의 등록하기</a>
              </c:if>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

    </div>
  </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />
</body>
</html>
