<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="top">
  <div class="top-in">
    <div class="logo">다<b>모여</b></div>
    <nav class="main" id="nav">
      <button data-v="home" class="on"><a href="${ctx}/home">홈</a></button>
      <button data-v="match"><a href="${ctx}/project/list">프로젝트 찾기</a></button>
      <button data-v="talent"><a href="${ctx}/talent/list">인재풀</a></button>
      <button data-v="myprojects"><a href="${ctx}/project/my">내 프로젝트</a></button>
      <button data-v="feedback"><a href="${ctx}/feedback/feedback">멘토</a></button>
      <button data-v="mypage"><a href="${ctx}/mypage/index">마이페이지</a></button>
      <button data-v="admin"><a href="${ctx}/admin/dashboard"> 관리자</a></button>
    </nav>
    <div class="top-right">
      <button class="bell" aria-label="알림">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.7 21a2 2 0 0 1-3.4 0"/></svg>
        <span class="dot"></span>
      </button>
      <button class="avatar" onclick="go('mypage')" style="cursor:pointer">
        <div class="who">김민재<small>컴퓨터공학 · 4학년</small></div>
        <div class="pic">민</div>
      </button>
    </div>
  </div>
</header>