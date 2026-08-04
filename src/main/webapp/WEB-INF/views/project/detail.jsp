<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로젝트 상세</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  
  <main>
    <section id="v-detail">
      <!-- ← 목록으로' 링크 (이전 필터/페이지 조건 유지) -->
      <a class="back" href="${ctx}/project/list${pageContext.request.queryString != null ? '?' : ''}${pageContext.request.queryString}">← 목록으로</a>
      
      <div class="detail">
        
        <!-- 좌측 상세 내용 영역 -->
        <div>
          <div class="panel d-head">
            <div class="cat">${project.category} · ${project.matchScope}
              <c:choose>
                <c:when test="${not empty member and not isOwner}">
                  <button type="button" style="color:var(--ink-soft);font-size:12px; float:right; background:none; border:none; cursor:pointer; padding:0;" onclick="openModal('reportModal')">신고</button>
                </c:when>
                <c:when test="${empty member}">
                  <a href="${ctx}/auth/login" style="color:var(--ink-soft);font-size:12px; float:right;" onclick="alert('로그인이 필요한 서비스입니다.');">신고</a>
                </c:when>
              </c:choose>
            </div>
            <h2><c:out value="${project.title}" /></h2>
            
            <!-- 태그 영역 (모집중 뱃지 + 등록한 태그들) -->
            <div style="display:flex;gap:8px;flex-wrap:wrap; margin-bottom: 20px;">
              <span class="chip ${project.status == 'RECRUITING' ? 'recruit' : 'done'}">${project.status == 'RECRUITING' ? '모집중' : '모집마감'}</span>
              
              <c:if test="${not empty project.tags}">
                <c:forEach var="tag" items="${fn:split(project.tags, ',')}">
                  <span class="tag"><c:out value="${tag}" /></span>
                </c:forEach>
              </c:if>
            </div>

            <div class="d-meta">
              <div><div class="k">카테고리</div><div class="v">${project.category}</div></div>
              <div><div class="k">대상 학년</div><div class="v">${project.targetGrade}</div></div>
              <div><div class="k">모집 인원</div><div class="v">${project.capacity}명</div></div>
              <div><div class="k">모집 마감일</div><div class="v mono"> ${project.endDate}</div></div>
              <!-- 작성자(owner)의 이름과 학과 출력 -->
              <div><div class="k">대학 / 학과</div><div class="v">${owner.name} · ${owner.major}</div></div>
            </div>
            
            <div class="prose" style="white-space: pre-wrap; line-height: 1.6;">
              <p><c:out value="${project.summary}" /></p>
            </div>

            <!-- 프로젝트 수정 및 삭제 버튼 영역 -->
            <div style="display: flex; flex-direction: row; gap: 8px; margin-bottom: 20px; align-items: center;">
                <c:if test="${isOwner}">
                    <a class="btn ghost sm" style="width: auto !important; flex: none !important; display: inline-flex !important; align-items: center; justify-content: center;" href="${ctx}/project/edit?id=${project.projectId}">수정하기</a>
                    <button type="button" class="btn ghost sm" style="width: auto !important; flex: none !important; display: inline-flex !important; align-items: center; justify-content: center; color: #e07a45; border-color: #e07a45;" onclick="deleteProject(${project.projectId})">삭제하기</button>
                    <a class="btn pri sm" href="${ctx}/project/mentor-recommend?id=${project.projectId}">멘토 AI 추천</a>
                </c:if>
                <!--   목록으로 -->
                <a class="back" style="margin: 0; padding: 6px 12px;" href="${ctx}/project/list${pageContext.request.queryString != null ? '?' : ''}${pageContext.request.queryString}">← 목록으로</a>
            </div>
          </div>

          <div class="panel">
            <h5 style="font-size:16px;font-weight:800;margin-bottom:4px">댓글 <span class="mono" style="color:var(--ink-soft);font-size:14px">${fn:length(commentList)}</span></h5>
            <c:forEach var="comment" items="${commentList}">
              <div class="cmt" data-comment-id="${comment.comment_id}">
                <div class="pic" style="background:linear-gradient(135deg,#2b46c8,#5b45c8)">
                  <a href="#" style="color:inherit;text-decoration:none" onclick="openProfile(${comment.member_id}); return false;">${comment.memberName.substring(0, 1)}</a>
                </div>
                <div class="body">
                  <div class="nm">
                    ${comment.memberName} <span>${comment.created_at}</span>
                    <c:if test="${not empty member and member.member_id == comment.member_id}">
                      <button type="button" class="cmt-edit-btn" onclick="openCommentEditModal(this)">수정</button>
                      <form method="post" action="${ctx}/project/comment/delete" style="display:inline" onsubmit="return confirm('댓글을 삭제하시겠습니까?');">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="comment_id" value="${comment.comment_id}" />
                        <input type="hidden" name="projectId" value="${project.projectId}" />
                        <button type="submit" class="cmt-edit-btn cmt-delete-btn">삭제</button>
                      </form>
                    </c:if>
                    <c:if test="${not empty member and member.member_id != comment.member_id}">
                      <button type="button" class="cmt-edit-btn" onclick="openCommentReportModal(${comment.comment_id})">신고</button>
                    </c:if>
                  </div>
                  <p class="cmt-content"><c:out value="${comment.content}" /></p>
                </div>
              </div>
            </c:forEach>
            <c:choose>
              <c:when test="${not empty member}">
                <div class="cmt-form">
                  <div class="pic">${member.name.substring(0, 1)}</div>
                  <form id="commentform" method="post" action="${ctx}/project/comment/add">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <input type="hidden" name="project_id" value="${project.projectId}" />
                    <div class="cf-input">
                      <textarea id="cmtInput" name="content" placeholder="궁금한 점이나 지원 관련 문의를 남겨보세요."></textarea>
                      <div class="cf-foot"><button class="btn pri sm" type="submit">댓글 등록</button></div>
                    </div>
                  </form>
                </div>
              </c:when>
              <c:otherwise>
                <div class="cmt-form">
                  <a href="${ctx}/auth/login">로그인 후 댓글을 작성할 수 있습니다.</a>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <!-- 우측 사이드바 영역 -->
        <div class="side">
          <div class="apply-card">
            <div class="num">2<small> / ${project.capacity}명 모집</small></div>
            <div class="bar"><span style="width:50%"></span></div>
              <div class="team-need mono" style="font-size:12px;color:var(--ink-soft)">팀원 ${project.status == 'RECRUITING' ? '모집중' : '모집마감'}</div>            
              <div class="lead">
              <div class="pic">
                <a href="#" style="color:inherit;text-decoration:none" onclick="openProfile(${owner.member_id}); return false;">
                  ${fn:substring(owner.name, 0, 1)}
                </a>
              </div>
              
              <!-- 접속자가 아닌 '글 작성자(owner)'의 정보가 뜨도록 수정 -->
              <div class="nm">${owner.name} <small>팀장 · ${owner.major} ${owner.grade} 학년</small></div>
            </div>
            <div style="font-family:var(--mono);font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:var(--ink-soft);margin-bottom:6px">지원 절차</div>
            <div class="stepline">
              <div class="act">지원</div><div>면접</div><div>승인</div>
            </div>
            
            <!-- 자바스크립트 마감일 체크용 숨김 데이터 -->
            <div id="projectEndDate" data-end="${project.endDate}" style="display:none;"></div>
            
            <!-- 우측 사이드바 버튼 영역 (지원하기 + 관심등록 가로 나란히 배치) -->
            <div style="display: flex; gap: 8px; margin-top: 18px;">
                
                <!-- 비로그인 유저 접근 차단 -->
                <c:choose>
                    <c:when test="${empty member}">
                        <button type="button" class="btn pri" style="flex: 1.2; justify-content:center; padding:0;" onclick="alert('로그인 후 이용하세요!'); location.href='${ctx}/auth/login';">
                            지원하기
                        </button>
                    </c:when>
                    <c:when test="${isOwner}">
                        <!-- 내 프로젝트일 경우 -->
                        <div style="flex: 1.2; background:#f8f9fa; border-radius:8px; border:1px solid #e5e7eb; display:flex; align-items:center; justify-content:center;">
                            <span style="font-size:13px; font-weight:600; color:var(--ink-soft);">내 프로젝트</span>
                        </div>
                    </c:when>
                    <c:when test="${hashApplied}">
                        <button type="button" class="btn ghost" style="flex: 1.2; justify-content:center; background:#f3f4f6; color:#6b7280; cursor:not-allowed; padding:0;" disabled>
                            이미 지원함
                        </button>
                    </c:when>
                    <c:when test="${project.status eq 'CLOSED'}">
                        <button type="button" class="btn ghost" style="flex: 1.2; justify-content:center; background:#f3f4f6; color:#9ca3af; cursor:not-allowed; padding:0;" disabled>
                            모집 마감
                        </button>
                    </c:when>
                    <c:otherwise>
                        <button type="button" id="applyBtn" class="btn pri" style="flex: 1.2; justify-content:center; padding:0;" onclick="openModal('applyModal')">
                            지원하기
                        </button>
                    </c:otherwise>
                </c:choose>

                <!-- 관심 등록 버튼 로그인 체크 -->
                <c:choose>
                    <c:when test="${empty member}">
                        <button type="button" class="btn-favorite" 
                            style="flex: 1; height: 44px; display: inline-flex; align-items: center; justify-content: center; gap: 4px; border: 1px solid #e5e7eb; border-radius: 8px; font-weight: 600; font-size: 14px; background: #fff; margin: 0;"
                            onclick="alert('로그인 후 이용하세요!'); location.href='${ctx}/auth/login';">
                            ♥ 관심 <span class="fav-count">${project.favoriteCount}</span>
                        </button>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn-favorite ${isLiked ? 'active' : ''}" 
                            style="flex: 1; height: 44px; display: inline-flex; align-items: center; justify-content: center; gap: 4px; border: 1px solid #e5e7eb; border-radius: 8px; font-weight: 600; font-size: 14px; background: #fff; margin: 0;"
                            onclick="toggleFavorite(${project.projectId}, this, event)">
                            ♥ 관심 <span class="fav-count">${project.favoriteCount}</span>
                        </button> 
                    </c:otherwise>
                </c:choose>

            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
  
  <!-- 댓글 수정 모달 -->
  <div class="modal-overlay" id="editModal">
    <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="editCommentTitle">
      <div class="modal-head">
        <div class="mh-info">
          <h3 id="editCommentTitle">댓글 수정</h3>
        </div>
        <button type="button" class="modal-close" onclick="closeModal('editModal')" aria-label="닫기">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
        </button>
      </div>
      <form class="modal-body" method="post" action="${ctx}/project/comment/update">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <input type="hidden" name="project_id" value="${project.projectId}" />
        <input type="hidden" name="comment_id" id="editCommentId" value="" />
        <div class="fld one">
          <textarea name="content" id="editCommentContent" style="min-height:110px" required maxlength="1000"></textarea>
        </div>
        <div class="form-foot">
          <button type="button" class="btn ghost" onclick="closeModal('editModal')">취소</button>
          <button type="submit" class="btn pri">수정 완료</button>
        </div>
      </form>
    </div>
  </div>

  <!-- 프로젝트 신고 모달 -->
  <div class="modal-overlay" id="reportModal">
    <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="reportTitle">
      <div class="modal-head">
        <div class="mh-info">
          <h3 id="reportTitle">신고</h3>
        </div>
        <button type="button" class="modal-close" onclick="closeModal('reportModal')" aria-label="닫기">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
        </button>
      </div>
      <form class="modal-body" method="post" action="${ctx}/project/report">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <input type="hidden" name="targetId" value="${project.projectId}" />
        <div class="fld one">
          <select name="reason" required>
            <option value="" disabled selected>신고 사유를 선택해주세요</option>
            <option value="부적절한 내용">부적절한 내용</option>
            <option value="욕설/비방">욕설/비방</option>
            <option value="스팸/광고">스팸/광고</option>
            <option value="저작권 침해">저작권 침해</option>
            <option value="기타">기타</option>
          </select>
        </div>
        <div class="form-foot">
          <button type="button" class="btn ghost" onclick="closeModal('reportModal')">취소</button>
          <button type="submit" class="btn pri">신고하기</button>
        </div>
      </form>
    </div>
  </div>

  <!-- 댓글 신고 모달 -->
  <div class="modal-overlay" id="commentReportModal">
    <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="commentReportTitle">
      <div class="modal-head">
        <div class="mh-info">
          <h3 id="commentReportTitle">댓글 신고</h3>
        </div>
        <button type="button" class="modal-close" onclick="closeModal('commentReportModal')" aria-label="닫기">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
        </button>
      </div>
      <div class="modal-body">
        <div class="fld one">
          <select id="commentReportReason" required>
            <option value="" disabled selected>신고 사유를 선택해주세요</option>
            <option value="부적절한 내용">부적절한 내용</option>
            <option value="욕설/비방">욕설/비방</option>
            <option value="스팸/광고">스팸/광고</option>
            <option value="저작권 침해">저작권 침해</option>
            <option value="기타">기타</option>
          </select>
        </div>
        <div class="form-foot">
          <button type="button" class="btn ghost" onclick="closeModal('commentReportModal')">취소</button>
          <button type="button" class="btn pri" onclick="submitCommentReport()">신고하기</button>
        </div>
      </div>
    </div>
  </div>

  <jsp:include page="apply_form.jsp" />
  <jsp:include page="../includes/footer.jsp" />
  <jsp:include page="../includes/profile-view-modal.jsp" />
  
  <script>
      const ctx = '${pageContext.request.contextPath}';
      <c:if test="${not empty msg}">
          alert("${msg}");
      </c:if>
  </script>
  
  <script src="${ctx}/resources/js/detail.js"></script>
  <script src="${ctx}/resources/js/common.js"></script>
</body>
</html>