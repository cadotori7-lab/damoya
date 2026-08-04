<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 지원자 관리</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
  <section id="v-applicants">
    <a class="back" href="${ctx}/workspace/${project_id}/members">← 팀원 관리</a>
    <div class="eyebrow">Recruitment · 팀장</div>
    <h1 class="page"><em>지원자 관리</em></h1>
    <p class="sub"><c:out value="${project.title}"/> · 대기 중인 지원서를 검토하고 승인 또는 거절해요.</p>

    <c:if test="${not empty teamMessage}">
      <div class="panel" style="margin-bottom:16px;color:var(--ok);font-weight:700"><c:out value="${teamMessage}"/></div>
    </c:if>
    <c:if test="${not empty teamError}">
      <div class="panel" style="margin-bottom:16px;color:var(--reject);font-weight:700"><c:out value="${teamError}"/></div>
    </c:if>

    <div class="recruit-bar">
      <div>
        <div class="rb-num">${fn:length(applicants)}<small>명 대기</small></div>
      </div>
      <c:if test="${project.capacity != null}">
        <div class="rb-fields"><span class="field-pill">모집 정원 <b>${project.capacity}명</b></span></div>
      </c:if>
    </div>

    <div id="applList">
      <c:forEach var="applicant" items="${applicants}">
        <article class="appl-card">
          <span class="pic" style="background:var(--accent-soft);color:var(--accent)">
            <c:out value="${fn:substring(applicant.memberName, 0, 1)}"/>
          </span>
          <div class="ac-main">
            <div class="ac-top">
              <span class="nm"><c:out value="${applicant.memberName}"/></span>
              <c:if test="${not empty applicant.memberMajor}">
                <span class="dept"><c:out value="${applicant.memberMajor}"/> <c:if test="${applicant.memberGrade > 0}">· ${applicant.memberGrade}학년</c:if></span>
              </c:if>
              <span class="chip wait">대기</span>
              <span class="applied">지원 <c:out value="${applicant.appliedAt}"/></span>
            </div>
            <div class="want">희망 포지션 · <c:out value="${applicant.wantPosition}" default="미입력"/></div>
            <div class="motive"><b>지원 동기</b><br><c:out value="${applicant.motive}" default="미입력"/></div>
            <div class="motive" style="margin-top:10px"><b>경력 및 경험</b><br><c:out value="${applicant.experience}" default="미입력"/></div>
          </div>
          <div class="ac-actions">
            <form method="post" action="${ctx}/workspace/${project_id}/applicants/${applicant.memberId}/approve"
                  onsubmit="return confirm('이 지원자를 팀원으로 승인할까요?');">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <button type="submit" class="btn sm pri">승인</button>
            </form>
            <form method="post" action="${ctx}/workspace/${project_id}/applicants/${applicant.memberId}/reject"
                  onsubmit="return confirm('이 지원서를 거절할까요?');">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <button type="submit" class="btn sm ghost">거절</button>
            </form>
          </div>
        </article>
      </c:forEach>

      <c:if test="${empty applicants}">
        <div class="panel" style="padding:32px;text-align:center;color:var(--ink-soft)">대기 중인 지원자가 없습니다.</div>
      </c:if>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>
