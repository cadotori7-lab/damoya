<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>인재풀 상세 - 다모여</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
    
  <main>
  <!-- ========== 인재 상세 ========== -->
  <section id="v-talentdetail" style="max-width: 1200px; margin: 0 auto; padding: 32px 16px;">
    <!-- 목록으로 돌아가기 (이전 필터 상태 유지) -->
    <a class="back" href="${ctx}/talent/list${pageContext.request.queryString != null ? '?' : ''}${pageContext.request.queryString}">← 목록으로</a>
    
    <div class="detail" style="display: grid; grid-template-columns: 1fr 340px; gap: 24px; align-items: start; margin-top: 24px;">
      
      <!-- 좌측 영역 (프로필 정보 + 수정/삭제 버튼 + 댓글) 묶음 -->
      <div class="left-column">
        
        <!-- 프로필 카드 -->
        <div class="panel" style="background: var(--surface); border: 1px solid var(--line); border-radius: 12px; padding: 32px; margin-bottom: 24px;">
          
          <!-- 프로필 사진, 이름, 전공, 역할 -->
          <div class="tc-head" style="display:flex; align-items:center; gap:20px; margin-bottom: 0;">
            <span class="pic ${talent.kind == 'MENTOR' ? 'mentor' : 'member'}" 
                  style="width:72px;height:72px;border-radius:50%;display:grid;place-items:center;font-weight:700;color:#fff;font-size:26px; flex: none;
                         ${talent.kind == 'MENTOR' ? 'background:linear-gradient(135deg, #e07a45, #c98a12);' : 'background:linear-gradient(135deg, #2b46c8, #5b45c8);'}">
                ${fn:substring(talent.memberName, 0, 1)}
            </span>
            <div class="who" style="flex:1;">
                <div class="nm" style="font-size:22px; font-weight:800; color:var(--ink); margin-bottom:4px;">${talent.memberName}</div>
                <div class="dept" style="font-size:14.5px; color:var(--ink-soft);">${talent.memberMajor} · ${talent.memberGrade}학년</div>
            </div>
            
            <c:choose>
                <c:when test="${talent.kind == 'MENTOR'}">
                    <span class="tc-kind" style="padding:6px 14px; border-radius:20px; font-size:13px; font-weight:700; background:#fff2e8; color:#d97034;">멘토</span>
                </c:when>
                <c:otherwise>
                    <span class="tc-kind" style="padding:6px 14px; border-radius:20px; font-size:13px; font-weight:700; background:#f0f4ff; color:#2b46c8;">팀원 지원</span>
                </c:otherwise>
            </c:choose>

            <!-- 신고 (작성자 본인이 아닐 때만 노출) -->
            <c:if test="${not isOwner}">
                <c:choose>
                    <c:when test="${not empty member}">
                        <button type="button" style="color:var(--ink-soft);font-size:12px;background:none;border:none;cursor:pointer;padding:0;margin-left:12px;flex:none;" onclick="openModal('reportModal')">신고</button>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/auth/login" style="color:var(--ink-soft);font-size:12px;margin-left:12px;flex:none;" onclick="alert('로그인이 필요한 서비스입니다.');">신고</a>
                    </c:otherwise>
                </c:choose>
            </c:if>
          </div>
          
          <hr style="border:0; border-bottom:1px solid #eaeaea; margin:28px 0;">
          
          <!-- 희망 분야, 가능 시간, 카테고리 -->
          <div class="d-meta" style="display:flex; gap:48px; margin-top:0;">
            <div>
                <div class="k" style="font-size:12px; color:var(--ink-soft); margin-bottom:6px;">희망 분야</div>
                <div class="v" style="font-size:15px; font-weight:700; color:var(--ink);">${talent.field}</div>
            </div>
            <div>
                <div class="k" style="font-size:12px; color:var(--ink-soft); margin-bottom:6px;">가능 시간</div>
                <div class="v" style="font-size:15px; font-weight:700; color:var(--ink);">${talent.availableTime}</div>
            </div>
            <div>
                <div class="k" style="font-size:12px; color:var(--ink-soft); margin-bottom:6px;">관심 카테고리</div>
                <div class="v" style="font-size:15px; font-weight:700; color:var(--ink);">${talent.category}</div>
            </div>
          </div>
          
          <hr style="border:0; border-bottom:1px solid #eaeaea; margin:28px 0;">
          
          <!-- 자기소개 본문 -->
          <h3 style="font-size: 18px; font-weight: 800; margin-bottom: 12px; color: var(--ink);">${talent.title}</h3>
          <div class="prose" style="font-size:15.5px; color:#4E5968; line-height:1.7; white-space: pre-wrap;">${talent.content}</div>
          
          <!-- 기술 태그 -->
          <div class="tc-tags" style="margin-top:24px; display:flex; gap:8px; flex-wrap:wrap;">
            <c:if test="${not empty talent.tags}">
                <c:forEach var="tag" items="${fn:split(talent.tags, ',')}">
                    <span style="background:#f3f4f6; padding:6px 12px; border-radius:6px; font-size:13px; font-weight:600; color:#4b5563; font-family:var(--mono); border: 1px solid #e5e7eb;">
                        ${fn:trim(tag)}
                    </span>
                </c:forEach>
            </c:if>
          </div>

          <!-- 수정 및 삭제 버튼 영역 (작성자 본인일 경우에만 노출) -->
          <c:if test="${isOwner}">
              <div style="display: flex; gap: 8px; margin-top: 32px; padding-top: 20px; border-top: 1px solid #eaeaea;">
                  <a class="btn ghost sm" href="${ctx}/talent/edit?id=${talent.postId}">수정하기</a>
                  <button type="button" class="btn ghost sm" style="color: #e07a45; border-color: #e07a45;" onclick="deleteTalent(${talent.postId})">삭제하기</button>
              </div>
          </c:if>

        </div>

        <!-- 댓글 영역 -->
        <div class="panel" style="background: var(--surface); border: 1px solid var(--line); border-radius: 12px; padding: 32px;">
            <h5 style="font-size:16px;font-weight:800;margin-bottom:4px">댓글 <span class="mono" style="color:var(--ink-soft);font-size:14px">${fn:length(commentList)}</span></h5>
            <c:forEach var="comment" items="${commentList}">
              <div class="cmt" data-comment-id="${comment.comment_id}">
                <div class="pic" style="background:linear-gradient(135deg,#2b46c8,#5b45c8)">${comment.memberName.substring(0, 1)}</div>
                <div class="body">
                  <div class="nm">
                    ${comment.memberName} <span>${comment.created_at}</span> 
                    <c:if test="${not empty member and member.member_id == comment.member_id}">
                      <button type="button" class="cmt-edit-btn" onclick="openCommentEditModal(this)">수정</button>
                      <form method="post" action="${ctx}/talent/comment/delete" style="display:inline" onsubmit="return confirm('댓글을 삭제하시겠습니까?');">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="commentId" value="${comment.comment_id}" />
                        <input type="hidden" name="postId" value="${talent.postId}" />
                        <button type="submit" class="cmt-edit-btn cmt-delete-btn">삭제</button>
                      </form>
                    </c:if>
                  </div>
                  <p class="cmt-content"><c:out value="${comment.content}" /></p>
                </div>
              </div>
            </c:forEach>
            
            <c:choose>
              <c:when test="${not empty member}">
                <div class="cmt-form" style="margin-top: 16px;">
                  <div class="pic">${member.name.substring(0, 1)}</div>
                  <form id="commentform" method="post" action="${ctx}/talent/comment/add">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <input type="hidden" name="post_id" value="${talent.postId}" />
                    <div class="cf-input">
                      <textarea id="cmtInput" name="content" placeholder="응원이나 문의 메시지를 남겨보세요."></textarea>
                      <div class="cf-foot"><button class="btn pri sm" type="submit">댓글 등록</button></div>
                    </div>
                  </form>
                </div>
              </c:when>
              <c:otherwise>
                <div class="cmt-form" style="margin-top: 16px;">
                  <a href="${ctx}/auth/login" style="color: var(--ink-soft); font-size: 14px; text-decoration: underline;">로그인 후 댓글을 작성할 수 있습니다.</a>
                </div>
              </c:otherwise>
            </c:choose>
        </div>

      </div>
      
      <!-- 우측 액션 사이드바 영역 -->
      <div class="side">
        <div class="apply-card" style="position:sticky; top:86px; background:var(--surface); border:1px solid var(--line); border-radius:12px; padding:24px;">
          <div style="font-size:13px; color:var(--ink-soft); margin-bottom:12px; font-weight:600;">팀장이라면</div>
          <p style="font-size:14px; color:var(--ink-soft); line-height:1.5; margin-bottom:20px;">
              내 프로젝트에 이 분을 초대할 수 있어요. 제의를 보내면 상대가 수락/거절해요.
          </p>
          <button class="btn pri" style="width:100%; justify-content:center; padding:12px; font-size:15px;" onclick="openOffer('${talent.memberName}', '${talent.field}')">함께하기 제의</button>
          <button class="btn ghost" style="width:100%; justify-content:center; margin-top:8px; padding:12px; font-size:15px; border-color:#e5e7eb;">♥ 관심 표시</button>
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
      <form class="modal-body" method="post" action="${ctx}/talent/comment/update">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <input type="hidden" name="post_id" value="${talent.postId}" />
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

  <!-- 게시글 신고 모달 -->
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
      <form class="modal-body" method="post" action="${ctx}/talent/report">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <input type="hidden" name="targetId" value="${talent.postId}" />
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

  <!-- 함께하기 제의 모달 include -->
  <jsp:include page="offer_form.jsp" />

  <jsp:include page="../includes/footer.jsp" />
    <script>
      const ctx = '${pageContext.request.contextPath}';
      <c:if test="${not empty msg}">
          alert("${msg}");
      </c:if>
      function deleteTalent(postId) {
          if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
              location.href = ctx + "/talent/delete?id=" + postId;
          }
      }
  </script>
  <script src="${ctx}/resources/js/common.js"></script>
  <script src="${ctx}/resources/js/offerForm.js"></script>
  <script src="${ctx}/resources/js/TalentDetail.js"></script>
  
</body>
</html>