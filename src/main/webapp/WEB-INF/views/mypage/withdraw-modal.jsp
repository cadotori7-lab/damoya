<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- 마이페이지 맨 아래 '위험 구역' --%>
<div class="danger-zone">
  <div class="dz-text">
    <b>회원 탈퇴</b>
    <span>탈퇴하면 다시 로그인할 수 없어요. 작성한 기록은 익명으로 남습니다.</span>
  </div>
  <button type="button" class="btn ghost danger" onclick="openModal('withdrawModal')">회원 탈퇴</button>
</div>

<%-- 확인 모달 --%>
<div class="modal-overlay" id="withdrawModal">
  <div class="modal" role="dialog" aria-modal="true" aria-labelledby="wdTitle" style="max-width:440px">
    <div class="modal-head">
      <div class="mh-info">
        <h3 id="wdTitle">정말 탈퇴할까요?</h3>
        <div class="role">이 작업은 되돌릴 수 없어요</div>
      </div>
      <button type="button" class="modal-close" onclick="closeModal('withdrawModal')" aria-label="닫기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>

    <form class="modal-body" action="${ctx}/mypage/withdraw" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <ul class="wd-notice">
        <li>계정이 <b>탈퇴 상태</b>로 바뀌고 다시 로그인할 수 없어요.</li>
        <li>내가 <b>팀장인 프로젝트</b>는 승계 순위에 따라 다음 팀원에게 넘어가요.</li>
        <li>작성한 댓글·피드백은 <b>익명(탈퇴회원)</b>으로 남아요.</li>
      </ul>

      <div class="fld one">
        <label>확인을 위해 비밀번호를 입력해주세요</label>
        <input type="password" name="password" placeholder="현재 비밀번호" required>
        <c:if test="${not empty withdrawError}">
          <div class="hint" style="color:var(--reject)">${withdrawError}</div>
        </c:if>
      </div>

      <div class="form-foot">
        <button type="button" class="btn ghost" onclick="closeModal('withdrawModal')">취소</button>
        <button type="submit" class="btn danger-solid">탈퇴하기</button>
      </div>
    </form>
  </div>
</div>

<c:if test="${openWithdraw}">
  <script>document.addEventListener('DOMContentLoaded', function(){ openModal('withdrawModal'); });</script>
</c:if>
