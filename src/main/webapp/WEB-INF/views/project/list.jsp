<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로젝트 찾기 - 다모여</title>
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
        
        <div class="board-header">
          <div class="board-tabs">
            <div class="tab-group">
              <button type="button" class="tab-item active" onclick="filterTab('all')">전체</button>
              <button type="button" class="tab-item" onclick="filterTab('RECRUITING')">모집중</button>
              <button type="button" class="tab-item" onclick="filterTab('CLOSED')">모집마감</button>
            </div>
          </div>
        </div>

        <aside class="filters">
          <h3>매칭 범위</h3>
            <div class="scope">
            <c:choose>
                <c:when test="${isExternalMentor}">
                    <button type="button" class="on" style="width: 100%; cursor: default;">전국 프로젝트</button>
                </c:when>
                <c:otherwise>
                    <button type="button" class="${matchScope == '교내' ? 'on' : ''}" onclick="setScope('교내', this)">교내</button>
                    <button type="button" class="${matchScope == '전국' ? 'on' : ''}" onclick="setScope('전국', this)">전국</button>
                </c:otherwise>
            </c:choose>
          </div>

          <h3>카테고리</h3>
          <div class="flt" id="categoryContainer">
            <input type="checkbox" id="c1" value="공모전" checked><label for="c1">공모전</label>
            <input type="checkbox" id="c2" value="학과" checked><label for="c2">학과</label>
            <input type="checkbox" id="c3" value="교양" checked><label for="c3">교양</label>
            <input type="checkbox" id="c4" value="교내활동" checked><label for="c4">교내활동</label>
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

        <div class="list-section">
          
          <div class="searchbar" style="margin-bottom: 24px;">
              <form action="${ctx}/project/list" method="get" style="display: flex; width: 100%; gap: 10px;">
                  <input type="hidden" name="tab" value="${param.tab != null ? param.tab : 'all'}">
                  <input type="hidden" name="sort" value="${currentSort}">
                  <input type="text" name="keyword" value="${param.keyword}" placeholder="관심 분야·기술·소개로 검색 (예: Spring, 디자인)" style="flex: 1;">
                  <button type="submit" class="btn pri">검색</button>
              </form>
          </div>

          <div class="list-sort-header" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--line); padding-bottom: 16px; margin-bottom: 24px;">
              <div class="list-sort">
                  <button type="button" class="sort-btn ${empty currentSort or currentSort == 'latest' ? 'active' : ''}" onclick="changeSort('latest')">최신순</button>
                  <button type="button" class="sort-btn ${currentSort == 'recommend' ? 'active' : ''}" onclick="changeSort('recommend')">추천순</button>
                  <c:if test="${param.tab ne 'CLOSED'}">
                    <button type="button" class="sort-btn ${currentSort == 'deadline' ? 'active' : ''}" onclick="changeSort('deadline')">마감임박순</button>
                  </c:if>
                  <button type="button" class="sort-btn ${currentSort == 'likes' ? 'active' : ''}" onclick="changeSort('likes')">좋아요순</button>
              </div>

              <div class="list-right-actions" style="display: flex; gap: 8px; align-items: center;">
                  <c:choose>
                      <c:when test="${isFavoriteView}">
                          <a class="btn ghost sm active" style="background-color: var(--ink); color: #fff; border-color: var(--ink);" href="${ctx}/project/list">⭐ 내 관심등록 보기 중</a>
                      </c:when>
                      <c:otherwise>
                          <a class="btn ghost sm" href="${ctx}/project/list?view=favorite">⭐ 내 관심등록</a>
                      </c:otherwise>
                  </c:choose>
                  <c:choose>
                      <c:when test="${isMentor}">
                          <a class="btn dark sm" href="javascript:void(0);" onclick="alert('멘토는 프로젝트 게시글을 작성할 수 없습니다.');">✏️ 글쓰기</a>
                      </c:when>
                      <c:otherwise>
                          <a class="btn dark sm" href="${ctx}/project/form">✏️ 글쓰기</a>
                      </c:otherwise>
                  </c:choose>            
              </div>
          </div>

          <div class="project-list">
            <c:choose>
                <%-- 1. 비로그인 상태로 교내 클릭 시 --%>
                <c:when test="${isLoginRequired}">
                    <div style="grid-column: 1 / -1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 100px 20px; background: #ffffff; border: 1px solid #e5e7eb; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.06); margin-top: 20px;">
                        <div style="width: 72px; height: 72px; background: #eff6ff; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 24px;">
                            <span class="material-symbols-outlined" style="font-size: 36px; color: #3b82f6;">lock</span>
                        </div>
                        <h3 style="font-size: 20px; font-weight: 700; color: #111827; margin: 0 0 12px 0;">
                            교내 프로젝트는 로그인 후 확인할 수 있어요
                        </h3>
                        <p style="font-size: 15px; color: #6b7280; margin: 0 0 32px 0; text-align: center; line-height: 1.6;">
                            우리 학교 학생들만 모인 안전한 공간입니다.<br>
                            다모여에 로그인하고 다양한 프로젝트와 팀원들을 만나보세요!
                        </p>
                        <a href="${ctx}/auth/login" class="btn pri" style="display: inline-flex; align-items: center; gap: 8px; padding: 14px 28px; border-radius: 10px; font-size: 15px; font-weight: 600; box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2); text-decoration: none;">
                            로그인하러 가기
                            <span class="material-symbols-outlined" style="font-size: 18px;">arrow_forward</span>
                        </a>
                    </div>
                </c:when>

                <%-- 2. 로그인했으나 관리자 승인이 안 된 경우 --%>
                <c:when test="${isApprovalRequired}">
                    <div style="grid-column: 1 / -1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 100px 20px; background: #ffffff; border: 1px solid #e5e7eb; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.06); margin-top: 20px;">
                        <div style="width: 72px; height: 72px; background: #fff7ed; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 24px;">
                            <span class="material-symbols-outlined" style="font-size: 36px; color: #ea580c;">hourglass_empty</span>
                        </div>
                        <h3 style="font-size: 20px; font-weight: 700; color: #111827; margin: 0 0 12px 0;">
                            아직 학교 인증이 확인되지 않았어요!
                        </h3>
                        <p style="font-size: 15px; color: #6b7280; margin: 0 0 32px 0; text-align: center; line-height: 1.6;">
                            안전한 교내 매칭을 위해 관리자 승인 절차를 진행하고 있습니다.<br>
                            승인이 완료되면 우리 학교의 모든 프로젝트를 확인할 수 있어요.
                        </p>
                    </div>
                </c:when>

                <%-- 3. 검색된 데이터가 없는 경우 (통일된 디자인 적용) --%>
                <c:when test="${empty projectList}">
                    <div style="grid-column: 1 / -1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 100px 20px; background: #ffffff; border: 1px solid #e5e7eb; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.06); margin-top: 20px;">
                        <div style="width: 72px; height: 72px; background: #f3f4f6; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 24px;">
                            <span class="material-symbols-outlined" style="font-size: 36px; color: #9ca3af;">search_off</span>
                        </div>
                        <h3 style="font-size: 20px; font-weight: 700; color: #111827; margin: 0 0 12px 0;">
                            등록된 프로젝트 모집글이 없습니다
                        </h3>
                        <p style="font-size: 15px; color: #6b7280; margin: 0; text-align: center; line-height: 1.6;">
                            조건에 맞는 프로젝트가 없네요.<br>
                            첫 번째 프로젝트 모집글의 주인공이 되어보세요!
                        </p>
                    </div>
                </c:when> 
                
                <%-- 4. 정상 출력 --%>
                <c:otherwise>
                    <c:forEach var="project" items="${projectList}">
                      <c:choose>
                        <c:when test="${project.category eq '공모전' or project.category eq 'CONTEST'}">
                          <c:set var="catClass" value="cat-contest" />
                          <c:set var="catName" value="공모전" />
                        </c:when>
                        <c:when test="${project.category eq '학과' or project.category eq 'DEPARTMENT'}">
                          <c:set var="catClass" value="cat-major" />
                          <c:set var="catName" value="학과" />
                        </c:when>
                        <c:when test="${project.category eq '교양' or project.category eq 'LIBERAL'}">
                          <c:set var="catClass" value="cat-liberal" />
                          <c:set var="catName" value="교양" />
                        </c:when>
                        <c:when test="${project.category eq '사이드 프로젝트' or project.category eq 'SIDE_PROJECT' or project.category eq '사이드프로젝트'}">
                          <c:set var="catClass" value="cat-side" />
                          <c:set var="catName" value="사이드프로젝트" />
                        </c:when>
                        <c:otherwise>
                          <c:set var="catClass" value="cat-club" />
                          <c:set var="catName" value="교내활동" />
                        </c:otherwise>
                      </c:choose>
        
                      <!-- 툴팁: 클릭 시 현재 검색/필터 쿼리스트링을 통째로 들고 상세페이지로 이동 -->
                      <div class="card-item" 
                      data-match="${project.matchScope}"
                      data-category="${catName}"
                      data-grade="${project.targetGrade}"
                      data-status="${project.status}"
                      data-end-date="${project.endDate}"
                      onclick="location.href='${ctx}/project/detail?id=${project.projectId}&' + window.location.search.substring(1)" style="display: flex; flex-direction: column; gap: 12px; padding: 20px; border-bottom: 1px solid #eee; cursor: pointer;">
                        
                        <div class="card-top" style="display: flex; justify-content: space-between; align-items: center;">
                          <div class="status-group" style="display: flex; gap: 8px; align-items: center;">
                            <c:choose>
                              <c:when test="${project.status eq 'RECRUITING'}">
                                <span class="status-badge recruiting">모집중</span>
                                <span class="d-day-badge"></span>
                              </c:when>
                              <c:otherwise>
                                <span class="status-badge closed">모집마감</span>
                              </c:otherwise>
                            </c:choose>
                          </div>
                          <span class="card-cat ${catClass}">${catName}</span>
                        </div>
                        
                        <div class="card-body">
                          <h4 style="margin: 0 0 8px 0; font-size: 18px; color: var(--ink);"><c:out value="${project.title}" /></h4>
                          <p style="margin: 0; font-size: 14px; color: var(--ink-soft); line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;"><c:out value="${project.summary}" /></p>
                        </div>
                        
                        <c:if test="${not empty project.tags}">
                          <div class="tags" style="display: flex; flex-wrap: wrap; gap: 6px;">
                            <c:forEach var="tag" items="${fn:split(project.tags, ',')}">
                              <span class="tag" style="background: var(--surface-alt); padding: 4px 8px; border-radius: 4px; font-size: 12px;"><c:out value="${tag}" /></span>
                            </c:forEach>
                          </div>
                        </c:if>
        
                        <div class="card-item-foot" style="display: flex; justify-content: space-between; align-items: center; margin-top: 8px; font-size: 13px; color: var(--ink-soft);">
                          <div class="author-info">
                            <span>대진대학교 · 컴퓨터공학</span>
                            <span style="margin: 0 4px;">·</span>
                            <span>모집인원 <b>${project.capacity}명</b></span>
                          </div>
                          <div class="stats" style="display: flex; align-items: center; gap: 12px;">
                              <button type="button" class="btn-favorite ${project.liked ? 'active' : ''}" style="font-size: 13px; display: inline-flex; align-items: center; gap: 4px;" onclick="toggleFavorite(${project.projectId}, this, event)">
                                  <span class="material-symbols-outlined" style="font-size: 16px;">favorite</span> 
                                  <span class="fav-count">${project.favoriteCount}</span>
                              </button>
                              <span style="display: inline-flex; align-items: center; gap: 4px;">
                                  <span class="material-symbols-outlined" style="font-size: 16px;">visibility</span> ${project.viewCount}
                              </span>
                              <span style="display: inline-flex; align-items: center; gap: 4px;">
                                  <span class="material-symbols-outlined" style="font-size: 16px;">mode_comment</span> 
                                  ${project.commentCount}
                              </span>
                          </div>
                        </div>
                      </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
          </div>
          
          <nav aria-label="page" style="margin-top: 32px;">
            <ul class="pagination" style="display: flex; justify-content: center; gap: 8px; list-style: none; padding: 0;">
                <c:if test="${pageBean.min > 1}">
                    <li><a href="#" onclick="submitSearch(${pageBean.prevPage}); return false;" style="padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none; color: var(--ink-soft);">이전</a></li>
                </c:if>
                <c:forEach begin="${pageBean.min}" end="${pageBean.max}" var="pageNum">
                    <li class="${pageNum == pageBean.currentPage ? 'active' : ''}">
                        <a href="#" onclick="submitSearch(${pageNum}); return false;" style="padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none; ${pageNum == pageBean.currentPage ? 'background-color: var(--ink); color: #fff; border-color: var(--ink);' : 'color: var(--ink-soft);'}">${pageNum}</a>
                    </li>
                </c:forEach> 
                <c:if test="${pageBean.max < pageBean.pageCnt}">
                    <li><a href="#" onclick="submitSearch(${pageBean.nextPage}); return false;" style="padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none; color: var(--ink-soft);">다음</a></li>
                </c:if>
            </ul>
          </nav>
        </div>
      </div>
    </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script>
    const ctx = '${pageContext.request.contextPath}';
  </script>
  <script src="${ctx}/resources/js/projectList.js"></script>
</body>
</html>