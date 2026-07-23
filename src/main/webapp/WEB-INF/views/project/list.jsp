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
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  
  <main>
    <section id="v-match" style="max-width: 1200px; margin: 0 auto; padding: 32px 16px;">
      <div class="eyebrow">Find your team</div>
      <h1 class="page" style="margin-bottom: 8px;"><em>함께할 팀</em>을 찾아보세요</h1>
      <p class="sub" style="margin-bottom: 24px;">우리 학교와 전국의 프로젝트를 카테고리·학과·학년으로 좁혀서 탐색해요.</p>

      <div class="board">
        
        <!-- 1. 상단 헤더 영역 (탭, 검색바) -->
        <div class="board-header">
          <div class="board-tabs">
            <div class="tab-group">
              <button type="button" class="tab-item active" onclick="filterTab('all')">전체</button>
              <button type="button" class="tab-item" onclick="filterTab('RECRUITING')">모집중</button>
              <button type="button" class="tab-item" onclick="filterTab('CLOSED')">모집마감</button>
            </div>
          </div>

          <div class="board-top-actions">
            <div class="searchbar" style="margin-bottom: 0;">
              <input type="text" placeholder="제목, 소개, 태그로 검색 (예: 캡스톤, Spring, 데이터 분석)">
            </div>
            <button type="button" class="btn pri">검색</button>
          </div>
        </div>

        <!-- 2. 좌측 필터 사이드바 -->
        <aside class="filters">
          <h3>매칭 범위</h3>
          <div class="scope">
            <button type="button" class="on" onclick="setScope('CAMPUS')">교내</button>
            <button type="button" onclick="setScope('NATION')">전국</button>
          </div>

          <h3>카테고리</h3>
          <div class="flt" id="categoryContainer">
            <!-- 기본값(교내 선택 시): 공모전, 학과, 교양, 교내활동 -->
            <input type="checkbox" id="c1" value="CONTEST" checked><label for="c1">공모전</label>
            <input type="checkbox" id="c2" value="DEPARTMENT"><label for="c2">학과</label>
            <input type="checkbox" id="c3" value="LIBERAL"><label for="c3">교양</label>
            <input type="checkbox" id="c4" value="ACTIVITIES"><label for="c4">교내활동</label>
          </div>

          <h3>대상 학년</h3>
          <div class="flt">
            <input type="checkbox" id="y0" value="ALL" checked><label for="y0">학년 무관</label>
            <input type="checkbox" id="y1" value="1"><label for="y1">1학년</label>
            <input type="checkbox" id="y2" value="2"><label for="y2">2학년</label>
            <input type="checkbox" id="y3" value="3"><label for="y3">3학년</label>
            <input type="checkbox" id="y4" value="4"><label for="y4">4학년</label>
          </div>
        </aside>

        <!-- 3. 우측 리스트 영역 -->
        <div class="list-section">
          
          <!-- 정렬 메뉴(좌)와 '내 스크랩' + '글쓰기' 버튼 그룹(우) -->
          <div class="list-sort-header">
            <div class="list-sort">
              <button type="button" class="sort-btn ${currentSort eq 'latest' or empty currentSort ? 'active' : ''}" onclick="sortList('latest')">최신순</button>
              <button type="button" class="sort-btn ${currentSort eq 'deadline' ? 'active' : ''}" onclick="sortList('deadline')">마감임박순</button>
              <button type="button" class="sort-btn ${currentSort eq 'likes' ? 'active' : ''}" onclick="sortList('likes')">좋아요순</button>
              <button type="button" class="sort-btn ${currentSort eq 'recommend' ? 'active' : ''}" onclick="sortList('recommend')">추천순</button>
            </div>
            
            <!-- 우측 버튼 그룹 (내 스크랩 + 글쓰기) -->
            <div class="list-right-actions" style="display: flex; gap: 8px; align-items: center;">
              <button type="button" class="btn ghost sm" onclick="toggleScrapView()">⭐ 내 스크랩</button>
              <a class="btn dark sm" href="${ctx}/project/form">✏️ 글쓰기</a>
            </div>
          </div>

          <div class="project-list">
            <c:forEach var="project" items="${projectList}">
              
              <c:choose>
                <c:when test="${project.category eq 'CONTEST'}">
                  <c:set var="catClass" value="cat-contest" />
                  <c:set var="catName" value="공모전" />
                </c:when>
                <c:when test="${project.category eq 'DEPARTMENT'}">
                  <c:set var="catClass" value="cat-major" />
                  <c:set var="catName" value="학과" />
                </c:when>
                <c:when test="${project.category eq 'LIBERAL'}">
                  <c:set var="catClass" value="cat-liberal" />
                  <c:set var="catName" value="교양" />
                </c:when>
                <c:when test="${project.category eq 'SIDE_PROJECT'}">
                  <c:set var="catClass" value="cat-side" />
                  <c:set var="catName" value="사이드 프로젝트" />
                </c:when>
                <c:otherwise>
                  <c:set var="catClass" value="cat-club" />
                  <c:set var="catName" value="교내활동" />
                </c:otherwise>
              </c:choose>

              <div class="card-item" onclick="location.href='${ctx}/project/detail?id=${project.projectId}'">
                
                <div class="card-top">
                  <c:choose>
                    <c:when test="${project.status eq 'RECRUITING'}">
                      <span class="status-badge recruiting">모집중</span>
                      <span class="d-day">D-7</span>
                    </c:when>
                    <c:otherwise>
                      <span class="status-badge closed">모집마감</span>
                    </c:otherwise>
                  </c:choose>
                  <span class="card-cat ${catClass}" style="margin-left: auto;">${catName}</span>
                </div>
                
                <h4><c:out value="${project.title}" /></h4>
                <p><c:out value="${project.summary}" /></p>
                
                <c:if test="${not empty project.tags}">
                  <div class="tags" style="margin-bottom: 12px;">
                    <c:forEach var="tag" items="${fn:split(project.tags, ',')}">
                      <span class="tag"><c:out value="${tag}" /></span>
                    </c:forEach>
                  </div>
                </c:if>

                <div class="card-item-foot">
                  <div class="author-info">
                    <span>대진대학교 · 컴퓨터공학</span>
                    <span>·</span>
                    <span>모집인원 <b>${project.capacity}명</b></span>
                  </div>
                  <div class="stats" style="display: flex; align-items: center; gap: 8px;">
                    <span style="display: inline-flex; align-items: center; gap: 3px;"><span class="material-symbols-outlined" style="font-size: 16px;">favorite</span> 0</span>
                    <span style="display: inline-flex; align-items: center; gap: 3px;"><span class="material-symbols-outlined" style="font-size: 16px;">visibility</span> 0</span>
                    <span style="display: inline-flex; align-items: center; gap: 3px;"><span class="material-symbols-outlined" style="font-size: 16px;">mode_comment</span> 0</span>
                  </div>
                </div>
              </div>
              
            </c:forEach>
            
            <c:if test="${empty projectList}">
              <div style="text-align: center; padding: 80px 0; color: var(--ink-soft);">
                <p style="font-size: 16px; font-weight: 600;">등록된 프로젝트 모집글이 없습니다.</p>
                <p style="font-size: 13.5px; margin-top: 6px;">첫 번째 프로젝트 모집글의 주인공이 되어보세요!</p>
              </div>
            </c:if>
          </div>
          
        </div>

      </div>
    </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/projectList.js"></script>
</body>
</html>