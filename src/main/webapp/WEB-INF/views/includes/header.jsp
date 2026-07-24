<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="top">
  <div class="top-in">
    <a class="logo" href="${ctx}/">다<b>모여</b></a>
    <nav class="main">
      <%-- 로그인 안 해도 보이는 것 --%>
      <sec:authorize access="!isAuthenticated()">
        <a href="${ctx}/" class="${nav=='home' ? 'on' : ''}">홈</a>
        <a href="${ctx}/project/list" class="${nav=='match' ? 'on' : ''}">프로젝트 찾기</a>
        <a href="${ctx}/talent/list" class="${nav=='talent' ? 'on' : ''}">인재풀</a>
      </sec:authorize>

      <%-- 로그인해야 보이는 것 --%>
      <sec:authorize access="isAuthenticated()">
        <a href="${ctx}/home" class="${nav=='home' ? 'on' : ''}">홈</a>
        <a href="${ctx}/project/list" class="${nav=='match' ? 'on' : ''}">프로젝트 찾기</a>
        <a href="${ctx}/talent/list" class="${nav=='talent' ? 'on' : ''}">인재풀</a>
        <a href="${ctx}/project/my" class="${nav=='myprojects' ? 'on' : ''}">내 프로젝트</a>
        <a href="${ctx}/mypage/index" class="${nav=='mypage' ? 'on' : ''}">마이페이지</a>
      </sec:authorize>

      <%-- 멘토에게만 --%>
      <sec:authorize access="hasRole('MENTOR')">
        <a href="${ctx}/feedback/feedback" class="${nav=='feedback' ? 'on' : ''}">멘토</a>
      </sec:authorize>

      <%-- 관리자에게만 --%>
      <sec:authorize access="hasRole('ADMIN')">
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
      <button class="bell" aria-label="알림">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.7 21a2 2 0 0 1-3.4 0"/></svg>
        <span class="dot"></span>
      </button>
      <a class="avatar" href="${ctx}/mypage/index" style="cursor:pointer">
        <div class="who">${member.name}<small>컴퓨터공학 · 4학년</small></div>
        <div class="pic">${member.name.substring(0, 1)}</div>
      </a>
    </div>
    </sec:authorize>
  </div>
</header>
<form id="logoutForm" action="${ctx}/auth/logout" method="post" style="display:none;">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>