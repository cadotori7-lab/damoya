<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="top">
  <div class="top-in">
    <a class="logo" href="${ctx}/">다<b>모여</b></a>
    <nav class="main">
      <sec:authorize access="!isAuthenticated()">
        <a href="${ctx}/" class="${nav=='home' ? 'on' : ''}">홈</a>
        <a href="${ctx}/project/list" class="${nav=='match' ? 'on' : ''}">프로젝트 찾기</a>
        <a href="${ctx}/talent/list" class="${nav=='talent' ? 'on' : ''}">인재풀</a>
      </sec:authorize>

      <sec:authorize access="isAuthenticated() && (hasRole('USER') || hasRole('MENTOR'))">
        <a href="${ctx}/home" class="${nav=='home' ? 'on' : ''}">홈</a>
        <a href="${ctx}/project/list" class="${nav=='match' ? 'on' : ''}">프로젝트 찾기</a>
        <a href="${ctx}/talent/list" class="${nav=='talent' ? 'on' : ''}">인재풀</a>
        <a href="${ctx}/project/my" class="${nav=='myprojects' ? 'on' : ''}">내 프로젝트</a>
        <a href="${ctx}/mypage/index" class="${nav=='mypage' ? 'on' : ''}">마이페이지</a>
      </sec:authorize>

      <sec:authorize access="isAuthenticated() && hasRole('MENTOR')">
        <a href="${ctx}/mentor/" class="${nav=='mentor' ? 'on' : ''}">멘토</a>
        <a href="${ctx}/feedback/feedback" class="${nav=='feedback' ? 'on' : ''}">피드백</a>
      </sec:authorize>

      <sec:authorize access="isAuthenticated() && hasRole('ADMIN')">
        <a href="${ctx}/admin/dashboard" class="${nav=='admin' ? 'on' : ''}">관리자</a>
      </sec:authorize>
    </nav>
    <div class="top-right">
      <sec:authorize access="!isAuthenticated()">
        <a class="btn ghost sm" href="${ctx}/auth/login">로그인</a>
        <a class="btn pri sm" href="${ctx}/auth/signup">시작하기</a>
      </sec:authorize>
      <sec:authorize access="isAuthenticated()">
      <button class="btn sec" onclick="document.getElementById('logoutForm').submit();">로그아웃</button>
      <button class="bell" aria-label="알림" type="button" data-member-id="${member.member_id}" data-ctx="${ctx}" onclick="toggleNotifications()">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.7 21a2 2 0 0 1-3.4 0"/></svg>
        <span class="dot" id="notiDot" style="display:none"></span>
      </button>
      <a class="avatar" href="${ctx}/mypage/index" style="cursor:pointer">
        <div class="who">${member.name}
          <c:choose>
            <c:when test="${isMentor}"><small>멘토<c:if test="${not empty mentor.field}"> · ${mentor.field}</c:if></small></c:when>
            <c:otherwise><small>${member.major} · ${member.grade}학년</small></c:otherwise>
          </c:choose>
        </div>
        <div class="pic">${member.name.substring(0, 1)}</div>
      </a>
    </div>
    </sec:authorize>
  </div>
</header>
<form id="logoutForm" action="${ctx}/auth/logout" method="post" style="display:none;">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>

<sec:authorize access="isAuthenticated()">
  <!-- 알림 모달 -->
  <div class="modal-overlay" id="notificationModal" onclick="if(event.target===this)closeModal('notificationModal')">
    <div class="modal" role="dialog" aria-modal="true" aria-labelledby="notiTitle">
      <div class="modal-head">
        <div class="mh-info"><h3 id="notiTitle">알림</h3><div class="role">새로운 소식을 확인하세요</div></div>
        <button class="modal-close" onclick="closeModal('notificationModal')" aria-label="닫기">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
        </button>
      </div>
      <div class="modal-body">
        <div class="noti-list" id="notiList">
          <p style="color:var(--ink-soft);padding:16px;text-align:center">불러오는 중...</p>
        </div>
      </div>
    </div>
  </div>

  <script src="${ctx}/resources/js/common.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
  <script src="${ctx}/resources/js/notification.js"></script>
</sec:authorize>
