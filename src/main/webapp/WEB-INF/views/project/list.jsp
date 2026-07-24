<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로젝트 찾기</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
    <!-- ========== 매칭 목록 ========== -->
  <section id="v-match">
    <div class="eyebrow">Find your team</div>
    <h1 class="page"><em>함께할 팀</em>을 찾아보세요</h1>
    <p class="sub">우리 학교와 전국의 프로젝트를 카테고리·학과·학년으로 좁혀서 탐색해요.</p>

    <div class="searchbar">
      <input type="text" placeholder="제목, 소개, 태그로 검색  (예: 캡스톤, Spring, 데이터 분석)">
      <a class="btn pri" href="${ctx}/project/form">+ 프로젝트 모집</a>
    </div>

    <div class="board">
      <aside class="filters">
        <h3>매칭 범위</h3>
        <div class="scope">
          <button class="on">교내</button>
          <button>학과</button>
          <button>전국</button>
        </div>
        <h3>카테고리</h3>
        <div class="flt">
          <input type="checkbox" id="c1" checked><label for="c1">공모전</label>
          <input type="checkbox" id="c2"><label for="c2">학과</label>
          <input type="checkbox" id="c3"><label for="c3">교양</label>
          <input type="checkbox" id="c4"><label for="c4">교내활동</label>
        </div>
        <h3>대상 학년</h3>
        <div class="flt">
          <input type="checkbox" id="y1"><label for="y1">1학년</label>
          <input type="checkbox" id="y2"><label for="y2">2학년</label>
          <input type="checkbox" id="y3" checked><label for="y3">3학년</label>
          <input type="checkbox" id="y4" checked><label for="y4">4학년</label>
        </div>
        <h3>상태</h3>
        <div class="flt">
          <input type="checkbox" id="s1" checked><label for="s1">모집중</label>
          <input type="checkbox" id="s2"><label for="s2">진행중</label>
        </div>
      </aside>

      <div class="grid" id="cards">
        <c:forEach var="project" items="${projectList}">
          <c:choose>
            <c:when test="${project.category == 'CONTEST'}"><c:set var="cardColor" value="var(--cat-contest)" /></c:when>
            <c:when test="${project.category == 'DEPARTMENT'}"><c:set var="cardColor" value="var(--cat-major)" /></c:when>
            <c:when test="${project.category == 'LIBERAL'}"><c:set var="cardColor" value="var(--cat-liberal)" /></c:when>
            <c:otherwise><c:set var="cardColor" value="var(--cat-club)" /></c:otherwise>
          </c:choose>

          <!-- 개별 프로젝트 카드 렌더링 -->
          <article class="card" style="--c:${cardColor}" onclick="location.href='${ctx}/project/detail?id=${project.projectId}'" tabindex="0">
            <div class="row1">
              <span class="cat">${project.category}</span>
              <span class="chip recruit">${project.status == 'RECRUITING' ? '모집중' : '임시저장'}</span>
            </div>
            <h4><c:out value="${project.title}" /></h4>
            <p><c:out value="${project.summary}" /></p>
            
            <div class="meta">
              <span>대진대학교</span>
              <span>학년 무관</span>
            </div>
            
            <%-- 쉼표(,)로 저장된 태그들을 쪼개서 뱃지로 각각 출력 --%>
            <c:if test="${not empty project.tags}">
              <div class="tags">
                <c:forEach var="tag" items="${fn:split(project.tags, ',')}">
                  <span class="tag"><c:out value="${tag}" /></span>
                </c:forEach>
              </div>
            </c:if>
            
            <div class="card-foot">
              <span class="team-need">모집 <b class="mono" style="color:var(--ink)">0/${project.capacity}</b>명</span>
              <span class="dday">D-Day</span>
            </div>
          </article>
        </c:forEach>
        
        <!-- 만약 등록된 프로젝트가 한개도 없을 때 예외 화면 -->
        <c:if test="${empty projectList}">
          <div style="grid-column: 1 / -1; text-align: center; padding: 80px 0; color: var(--ink-soft);">
            <p style="font-size: 16px; font-weight: 600;">등록된 프로젝트 모집글이 없습니다.</p>
            <p style="font-size: 13.5px; margin-top: 6px;">첫 번째 프로젝트 모집글의 주인공이 되어보세요!</p>
          </div>
        </c:if>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>