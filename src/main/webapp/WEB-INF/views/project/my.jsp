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
  <!-- ========== 내 프로젝트 목록 ========== -->
  <section id="v-myprojects">
    <div class="eyebrow">My projects</div>
    <h1 class="page"><em>내 프로젝트</em></h1>
    <p class="sub">참여 중인 프로젝트예요. 눌러서 들어가면 개요·업무·회의를 관리할 수 있어요.</p>

    <div class="psel-list">
      <c:forEach var="project" items="${project}">
        <a class="psel-card" style="--c:var(--cat-contest)" href="${ctx}/workspace/${project.projectId}/overview">
          <div class="psel-main">
            <div class="psel-top"><span class="psel-cat">${project.category}</span><span class="psel-role lead">${project.projectRole}</span><span class="chip ing">${project.status}</span></div>
            <h3>${project.title}</h3>
            <div class="psel-meta"><span>팀원 4명</span><span>D-24</span><span>내 업무 4건</span></div>
          </div>
        <svg class="psel-enter" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
        </a>
      </c:forEach>
    </div>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>