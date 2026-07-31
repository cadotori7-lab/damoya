<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>인재풀 - 다모여</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />
    <style>
        .tc-head .pic.mentor { background: linear-gradient(135deg, #e07a45, #c98a12); } 
        .tc-head .pic.member { background: linear-gradient(135deg, #2b46c8, #5b45c8); } 
        .talent-stats span { display: flex; align-items: center; gap: 4px; }
    </style>
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
  
  <main>
    <section id="v-talent" style="max-width: 1200px; margin: 0 auto; padding: 32px 16px;">
      <div class="eyebrow">Talent pool</div>
      <h1 class="page" style="margin-bottom: 8px;"><em>함께할 사람</em> 찾기</h1>
      <p class="sub" style="margin-bottom: 24px;">팀원·멘토가 올린 자기소개 글이에요. 마음에 드는 분에게 팀장이 함께하기를 제의할 수 있어요.</p>

      <div class="board">
        
        <!-- 좌측 필터 영역 -->
        <aside class="filters">
            <!-- 역할 필터 -->
            <h3 style="margin-top: 0;">역할</h3>
            <div class="scope" style="margin-bottom: 24px;">
                <button type="button" class="on" onclick="setRole('all')">전체보기</button>
                <button type="button" onclick="setRole('MEMBER')">팀원</button>
                <button type="button" onclick="setRole('MENTOR')">멘토</button>
            </div>

            <!--  매칭 범위 필터 -->
            <h3>매칭 범위</h3>
            <div class="scope" style="margin-bottom: 24px;" id="scopeFilterBtns">
                <button type="button" class="on" onclick="changeScope('교내', this)">교내</button>
                <button type="button" onclick="changeScope('전국', this)">전국</button>
            </div>

            <!--  카테고리 필터 -->
            <h3>카테고리</h3>
            <div class="flt" style="margin-bottom: 24px;" id="categoryFilterContainer">
                <input type="checkbox" id="c1" value="CONTEST" checked><label for="c1">공모전</label>
                <input type="checkbox" id="c2" value="DEPARTMENT"><label for="c2">학과</label>
                <input type="checkbox" id="c3" value="LIBERAL"><label for="c3">교양</label>
                <input type="checkbox" id="c4" value="CLUB"><label for="c4">교내활동</label>
            </div>

            <!--  대상 학년 필터 -->
            <h3>대상 학년</h3>
            <div class="flt">
                <input type="checkbox" id="g_all" value="all" checked><label for="g_all">학년 무관</label>
                <input type="checkbox" id="g1" value="1"><label for="g1">1학년</label>
                <input type="checkbox" id="g2" value="2"><label for="g2">2학년</label>
                <input type="checkbox" id="g3" value="3"><label for="g3">3학년</label>
                <input type="checkbox" id="g4" value="4"><label for="g4">4학년</label>
            </div>
        </aside>

        <!-- 우측 리스트 영역 -->
        <div class="list-section">
            
            <div class="searchbar" style="margin-bottom: 24px;">
                <form action="javascript:void(0);" onsubmit="doSearch();" style="display: flex; width: 100%; gap: 10px;">
                    <input type="text" id="searchInput" placeholder="관심 분야·기술·소개로 검색 (예: Spring, 디자인)" style="flex: 1;">
                    <button type="submit" class="btn pri">검색</button>
                </form>
            </div>

            <div class="list-sort-header" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--line); padding-bottom: 16px; margin-bottom: 24px;">
                
                <div class="list-sort">
                    <button type="button" class="sort-btn active" onclick="setSort('latest')">최신순</button>
                    <button type="button" class="sort-btn" onclick="setSort('views')">조회순</button>
                    <button type="button" class="sort-btn" onclick="setSort('likes')">좋아요순</button>
                </div>

                <div class="list-right-actions" style="display: flex; gap: 8px; align-items: center;">
                    <c:choose>
                        <c:when test="${isFavoriteView}">
                            <a class="btn ghost sm active" style="background-color: var(--ink); color: #fff; border-color: var(--ink);" href="${ctx}/talent/list">⭐ 내 관심등록 보기 중</a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn ghost sm" href="${ctx}/talent/list?view=favorite">⭐ 내 관심등록</a>
                        </c:otherwise>
                    </c:choose>
                    <a class="btn dark sm" href="${ctx}/talent/form">✏️ 글쓰기</a>
                </div>
            </div>

            <!-- 인재 카드 그리드 영역 -->
            <div class="talent-grid" id="talentGrid">
                <c:choose>
                    <c:when test="${not empty talentList}">
                        <c:forEach var="t" items="${talentList}">
                            
                            <div class="talent-card" onclick="location.href='${ctx}/talent/detail?id=${t.postId}'" style="cursor: pointer; background: var(--surface); border: 1px solid var(--line); border-radius: var(--r); padding: 20px; transition: .18s; display: flex; flex-direction: column;">
                                
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;">
                                    <div style="font-size: 13.5px; font-weight: 700; color: var(--accent);">
                                        <c:out value="${t.category}" />
                                    </div>
                                    <span class="tc-kind ${t.kind == 'MENTOR' ? 'mentor' : 'member'}" 
                                          style="font-size:11.5px; font-weight:700; padding:4px 10px; border-radius:20px; 
                                                 ${t.kind == 'MENTOR' ? 'background:#fff2e8; color:#d97034;' : 'background:var(--accent-soft); color:var(--accent);'}">
                                        ${t.kind == 'MENTOR' ? '멘토' : '팀원 지원'}
                                    </span>
                                </div>

                                <div class="tc-head" style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
                                    <span class="pic ${t.kind == 'MENTOR' ? 'mentor' : 'member'}" 
                                          style="width:40px; height:40px; border-radius:50%; display:grid; place-items:center; font-weight:700; color:#fff; font-size:15px; flex: none;
                                                 ${t.kind == 'MENTOR' ? 'background:linear-gradient(135deg, #e07a45, #c98a12);' : 'background:linear-gradient(135deg, #2b46c8, #5b45c8);'}">
                                        ${fn:substring(t.memberName, 0, 1)}
                                    </span>
                                    <div class="who">
                                        <div class="nm" style="font-size: 15.5px; font-weight: 800; color: var(--ink); margin-bottom: 1px;">
                                            ${t.memberName}
                                        </div>
                                        <div class="dept" style="font-size: 12.5px; color: var(--ink-soft);">
                                            ${t.memberMajor} · ${t.memberGrade}학년
                                        </div>
                                    </div>
                                </div>
                                
                                <h4 style="font-size:16px; margin: 0 0 8px 0; font-weight:700; color: var(--ink);">${t.title}</h4>

                                <p style="font-size:14px; color:#4E5968; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; overflow:hidden; line-height:1.6; margin-bottom:10px;">
                                    ${t.content}
                                </p>

                                <c:if test="${not empty t.field}">
                                    <div class="hope-area" style="font-size: 13px; color: var(--ink); margin-bottom: 10px; font-weight: 600;">
                                        <span style="color: var(--ink-soft); font-weight: 500;">희망 분야 ·</span> <c:out value="${t.field}" />
                                    </div>
                                </c:if>
                                
                                <c:if test="${not empty t.tags}">
                                    <div class="tc-tags" style="display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 16px;">
                                        <c:forEach var="tag" items="${fn:split(t.tags, ',')}">
                                            <span class="tag" style="background: var(--paper); padding: 5px 10px; border-radius: 6px; font-size: 12.5px; font-weight: 600; color: var(--ink-soft); border: 1px solid var(--line); font-family: var(--mono);">
                                                ${fn:trim(tag)}
                                            </span>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                
                                <div class="tc-foot" style="margin-top: auto; padding-top:14px; border-top:1px solid var(--line); display:flex; justify-content:space-between; align-items:center; font-family:var(--mono); font-size:13px; color:var(--ink-soft);">
                                    <div class="talent-stats" style="display:flex; gap:16px;">
                                        <span style="display: flex; align-items: center; gap: 4px;">
                                            <span class="material-symbols-outlined" style="font-size: 16px;">visibility</span> ${t.viewCount != null ? t.viewCount : 0}
                                        </span>
                                        <span style="display: flex; align-items: center; gap: 4px;">
                                            <span class="material-symbols-outlined" style="font-size: 16px;">favorite</span> <span class="fav-count">${t.favoriteCount != null ? t.favoriteCount : 0}</span>
                                        </span>
                                        <span style="display: flex; align-items: center; gap: 4px;">
                                            <span class="material-symbols-outlined" style="font-size: 16px;">mode_comment</span> ${t.commentCount != null ? t.commentCount : 0}
                                        </span>
                                    </div>
                                    <span style="font-weight: 500;">
                                        ${fn:substring(t.createdAt, 0, 10)}
                                    </span>
                                </div>

                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div style="grid-column: 1 / -1; text-align: center; padding: 60px 0; color: var(--ink-soft);">
                            <p style="font-size: 16px; font-weight: 600;">등록된 인재풀 게시물이 없습니다.</p>
                            <p style="font-size: 13.5px; margin-top: 6px;">새로운 프로필을 등록해 보세요!</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
      </div>
    </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/TalentList.js"></script>
</body>
</html>