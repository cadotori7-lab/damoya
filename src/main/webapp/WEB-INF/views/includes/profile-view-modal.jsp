<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="modal-overlay" id="profileViewModal">
  <div class="modal" role="dialog" aria-modal="true" style="max-width:420px">
    <button type="button" class="modal-close" onclick="closeModal('profileViewModal')" aria-label="닫기"
            style="position:absolute;top:16px;right:16px;z-index:2">
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
    </button>

    <div class="pv-body" id="pvBody">
      <%-- 로딩 중 표시 (JS가 채우기 전) --%>
      <div id="pvLoading" style="text-align:center;padding:50px 20px;color:var(--ink-soft)">
        불러오는 중…
      </div>

      <%-- 실제 내용 (JS가 값 채우고 display 켜기) --%>
      <div id="pvContent" style="display:none">
        <div class="pv-head">
          <div class="pv-avatar" id="pvAvatar"></div>
          <h3 id="pvName"></h3>
          <div class="pv-sub" id="pvSub"></div>
          <div class="pv-badges" id="pvBadges"></div>
        </div>

        <%-- 소개글 --%>
        <div class="pv-section" id="pvIntroWrap" style="display:none">
          <div class="pv-label">소개</div>
          <p id="pvIntro"></p>
        </div>

        <%-- 멘토 전용 --%>
        <div class="pv-section" id="pvMentorWrap" style="display:none">
          <div class="pv-label">전문분야</div>
          <p id="pvField"></p>
          <div class="pv-label" id="pvCareerLabel" style="display:none;margin-top:10px">경력</div>
          <p id="pvCareer" style="white-space:pre-wrap"></p>
        </div>

        <%-- 비공개 안내 --%>
        <div id="pvPrivate" style="display:none;text-align:center;color:var(--ink-soft);font-size:13px;padding:16px 0">
          비공개 프로필이에요.
        </div>
      </div>
    </div>
  </div>
</div>
