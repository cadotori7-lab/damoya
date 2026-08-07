<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- 모달 스타일 -->
<style>
  .modal-overlay {
    display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%;
    background: rgba(0,0,0,0.5); z-index: 9999; align-items: center; justify-content: center;
  }
  .modal-overlay.on { display: flex; }
  .form-modal {
    background: #fff; border-radius: 12px; width: 440px;
    padding: 24px; box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  }
</style>

<!-- ===== 함께하기 제의 모달 ===== -->
<div class="modal-overlay" id="offerModal" onclick="if(event.target===this)closeOffer()">
  <div class="modal form-modal" role="dialog" aria-modal="true">
    
    <div class="modal-head" style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
      <div class="mh-info">
          <h3 id="offerTitle" style="margin:0; font-size:18px;">함께하기 제의</h3>
          <div class="role" id="offerWho" style="font-size: 13px; color: var(--ink-soft); margin-top: 4px;"></div>
      </div>
      <button class="modal-close" onclick="closeOffer()" aria-label="닫기" style="background:none; border:none; cursor:pointer;">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>
    
    <div class="modal-body" id="offerBody">
      <c:choose>
          <c:when test="${not empty leaderProjects and fn:length(leaderProjects) > 0}">
              <form id="offerForm" action="${ctx}/talent/offer/send" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                
                <!-- 제의를 받는 상대방의 member_id -->
                <input type="hidden" id="offerMemberId" name="memberId" value="">
                
                <!-- 현재 보고 있던 인재풀 글 번호 (리다이렉트용) -->
                <input type="hidden" name="postId" value="${talent.postId}">
                
              <div class="offer-proj" style="margin-bottom: 16px;">
                <div class="k" style="font-size: 13px; font-weight: 600; margin-bottom: 8px;">초대할 프로젝트 (복수 선택 가능)</div>
                <div class="project-checkbox-list" style="max-height: 150px; overflow-y: auto; border: 1px solid var(--line); border-radius: 8px; padding: 10px;">
                  
                  <c:forEach var="project" items="${leaderProjects}">
    
                    <!-- 이미 제의했는지 체크 -->
                    <c:set var="isOffered" value="false" />
                    <c:forEach var="offeredId" items="${offeredProjectIds}">
                        <c:if test="${offeredId == project.projectId}">
                            <c:set var="isOffered" value="true" />
                        </c:if>
                    </c:forEach>

                    <!-- 모집 마감 여부 체크 -->
                    <c:set var="isClosed" value="${project.status == 'CLOSED'}" /> 
                    
                    <!-- 인원 초과 여부 체크 -->
                    <c:set var="isFull" value="${project.currentNum >= project.capacity}" />

                    <!-- 매칭 범위 불일치 여부 체크 -->
                    <c:set var="isScopeMismatch" value="${project.matchScope != talent.matchScope}" />

                    <!-- 넷 중 하나라도 해당되면 비활성화 -->
                    <c:set var="isDisabled" value="${isOffered or isClosed or isFull or isScopeMismatch}" />

                    <label style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px; font-size: 14px; cursor: ${isDisabled ? 'not-allowed' : 'pointer'}; color: ${isDisabled ? '#9ca3af' : 'inherit'};">
                        
                        <input type="checkbox" name="projectIds" value="${project.projectId}" 
                          style="width: 16px; height: 16px;" 
                          <c:if test="${isDisabled}">disabled</c:if>>
                              
                        <span>${project.title} <small style="color:#9ca3af;">(${project.matchScope})</small></span>
                        
                        <!-- 조건에 따라 각기 다른 라벨 뱃지 표시  -->
                        <c:choose>
                            <c:when test="${isOffered}">
                                <span style="font-size: 11px; background: #f3f4f6; color: #6b7280; padding: 2px 6px; border-radius: 4px; margin-left: auto;">참여중/제의함</span>
                            </c:when>
                            <c:when test="${isFull}">
                                <span style="font-size: 11px; background: #fee2e2; color: #ef4444; padding: 2px 6px; border-radius: 4px; margin-left: auto;">인원 꽉 참</span>
                            </c:when>
                            <c:when test="${isClosed}">
                                <span style="font-size: 11px; background: #f3f4f6; color: #9ca3af; padding: 2px 6px; border-radius: 4px; margin-left: auto;">모집 마감</span>
                            </c:when>
                            <c:when test="${isScopeMismatch}">
                                <span style="font-size: 11px; background: #fffbeb; color: #f59e0b; padding: 2px 6px; border-radius: 4px; margin-left: auto;">범위 다름</span>
                            </c:when>
                        </c:choose>
                    </label>
                </c:forEach>
                                  
                </div>
              </div>
                
                <div class="fld one" style="margin-bottom: 16px;">
                    <label style="font-size: 13px; font-weight: 600; margin-bottom: 8px; display: block;">맡아줬으면 하는 역할</label>
                    <input type="text" id="offerRole" name="wantPosition" placeholder="예: 백엔드 · 인증/권한" style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--line);">
                </div>

                <div class="fld one" style="margin-bottom: 16px;">
                    <label style="font-size: 13px; font-weight: 600; margin-bottom: 8px; display: block;">회신 받을 연락처 (이메일 필수) <span style="color: red;">*</span></label>
                    <input type="email" id="offerEmail" name="contactEmail" placeholder="example@email.com" required style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--line);">
                </div>
                
                <div class="fld one">
                    <label style="font-size: 13px; font-weight: 600; margin-bottom: 8px; display: block;">제의 메시지</label>
                    <textarea id="offerMessage" name="motive" placeholder="왜 함께하고 싶은지, 어떤 점이 좋았는지 적어주세요." style="min-height:90px; width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--line);"></textarea>
                </div>
                
                <div class="form-foot" style="margin-top: 24px; display:flex; justify-content:flex-end; gap:8px;">
                  <button type="button" class="btn ghost" onclick="closeOffer()">취소</button>
                  <button type="submit" class="btn pri">제의 보내기</button>
                </div>
            </form>
        
              <!-- 성공 메시지 영역 -->
              <div id="offerSuccess" class="submit-success" style="display: none; text-align:center; padding: 20px 0;">
                <div style="color: #2b46c8; margin-bottom:16px;">
                    <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                </div>
                <h3 style="margin-bottom:12px; font-size:20px;">제의를 보냈어요!</h3>
                <p id="offerSuccessText" style="color:var(--ink-soft); font-size:14px; line-height:1.6; margin-bottom:24px;"></p>
                <button type="button" class="btn pri" style="width:100%; justify-content:center; padding:12px;" onclick="closeOffer()">확인</button>
              </div>

          </c:when>

          <c:otherwise>
              <div id="noProjectForm" style="text-align:center; padding: 30px 10px 10px;">
                  <div style="margin-bottom: 16px; color: #9ca3af;">
                      <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="margin: 0 auto;"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path><line x1="9" y1="13" x2="15" y2="13"></line></svg>
                  </div>
                  <h3 style="font-size: 18px; font-weight: 700; color: var(--ink); margin-bottom: 12px;">아직 팀장으로 등록된 프로젝트가 없어요!</h3>
                  <p style="font-size: 14.5px; color: var(--ink-soft); line-height: 1.6; margin-bottom: 32px;">
                      팀원에게 제의를 보내려면 먼저 프로젝트를 생성해야 합니다.<br>지금 새로운 프로젝트를 만들러 갈까요?
                  </p>
                  <div style="display: flex; gap: 8px;">
                      <button type="button" class="btn ghost" style="flex: 1; padding: 12px;" onclick="closeOffer()">아니요</button>
                      <button type="button" class="btn pri" style="flex: 1; padding: 12px;" onclick="location.href='${ctx}/project/list'">네</button>
                  </div>
              </div>
          </c:otherwise>
      </c:choose>
    </div>
    
  </div>
</div>