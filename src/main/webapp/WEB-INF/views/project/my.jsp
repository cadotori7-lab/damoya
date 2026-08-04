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

    <c:if test="${not empty msg}">
      <div class="panel" style="margin-bottom:16px;color:var(--ok);font-weight:700"><c:out value="${msg}"/></div>
    </c:if>

    <c:choose>
      <c:when test="${empty project}">
        <div style="text-align:center;padding:60px 24px;background:var(--surface);border:1px solid var(--line);border-radius:var(--r)">
          <p style="font-size:16px;font-weight:700;color:var(--ink)">아직 참여 중인 프로젝트가 없어요.</p>
          <p style="font-size:13.5px;color:var(--ink-soft);margin-top:6px">프로젝트를 찾아 지원하거나, 함께할 팀원을 인재풀에서 찾아보세요.</p>
          <div style="display:flex;gap:10px;justify-content:center;margin-top:20px;flex-wrap:wrap">
            <a class="btn pri" href="${ctx}/project/list">프로젝트 찾기</a>
            <a class="btn ghost" href="${ctx}/talent/list">인재풀 보기</a>
          </div>
        </div>
      </c:when>
      <c:otherwise>
        <div class="psel-list">
          <c:forEach var="project" items="${project}">
            <a class="psel-card" style="--c:var(--cat-${project.category})" href="${ctx}/workspace/${project.projectId}/overview">
              <div class="psel-main">
                <div class="psel-top">
                  <span class="psel-cat">${project.category}</span>
                  <span class="psel-role ${project.projectRole == 'LEADER' ? 'lead' : 'member'}">${project.projectRole == 'LEADER' ? '팀장' : '팀원'}</span>
                  <span class="chip approve">참여중</span>
                </div>
                <h3>${project.title}</h3>
              </div>
            <svg class="psel-enter" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
            </a>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>
