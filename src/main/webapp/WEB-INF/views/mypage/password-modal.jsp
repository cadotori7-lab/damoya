<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<c:if test="${member.provider == 'LOCAL'}">

  <div class="account-zone">
    <div class="az-text">
      <b>비밀번호 변경</b>
      <span>주기적으로 바꾸면 계정을 더 안전하게 지킬 수 있어요.</span>
    </div>
    <button type="button" class="btn ghost" onclick="openModal('passwordModal')">비밀번호 변경</button>
  </div>

  <div class="modal-overlay" id="passwordModal">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="pwTitle" style="max-width:440px">
      <div class="modal-head">
        <div class="mh-info">
          <h3 id="pwTitle">비밀번호 변경</h3>
          <div class="role">현재 비밀번호를 확인한 뒤 바꿔요</div>
        </div>
        <button type="button" class="modal-close" onclick="closeModal('passwordModal')" aria-label="닫기">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
        </button>
      </div>

      <form class="modal-body" action="${ctx}/mypage/password" method="post" autocomplete="off">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <c:if test="${not empty passwordError}">
          <div class="form-error"
               style="background:var(--reject-bg);color:var(--reject);border-radius:11px;padding:11px 14px;margin-bottom:16px;font-size:13.5px;font-weight:600">
            ${passwordError}
          </div>
        </c:if>

        <div class="fld one">
          <label>현재 비밀번호<span class="req">*</span></label>
          <input type="password" name="currentPassword" placeholder="현재 비밀번호" required>
        </div>

        <div class="fld one">
          <label>새 비밀번호<span class="req">*</span></label>
          <input type="password" name="newPassword" placeholder="8자 이상" required minlength="8">
          <div class="hint">영문·숫자를 섞어 8자 이상으로 만들어주세요.</div>
        </div>

        <div class="fld one">
          <label>새 비밀번호 확인<span class="req">*</span></label>
          <input type="password" name="newPasswordConfirm" placeholder="다시 입력" required minlength="8">
        </div>

        <div class="form-foot">
          <button type="button" class="btn ghost" onclick="closeModal('passwordModal')">취소</button>
          <button type="submit" class="btn pri">변경하기</button>
        </div>
      </form>
    </div>
  </div>

</c:if>
