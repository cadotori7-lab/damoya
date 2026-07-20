<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 대학생 프로젝트 매칭·협업 플랫폼</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>

<jsp:include page="includes/header.jsp" />

<main>
<%-- 히어로 --%>
<section class="lp-hero">
  <div class="lp-wrap">
    <div class="lp-eyebrow">대학생 프로젝트 매칭 · 협업 플랫폼</div>
    <h1>흩어진 팀원, 여기서 <em>다&nbsp;모여</em>.</h1>
    <p>공모전·학과·교양 프로젝트, 혼자 구하기 막막했죠. 함께할 팀원과 멘토를 찾고,
       모집부터 업무·회의·검수까지 한곳에서 끝내요.</p>
    <div class="cta">
      <a class="btn pri big" href="${ctx}/auth/signup">무료로 시작하기</a>
      <a class="btn ghost big" href="${ctx}/project/list">프로젝트 둘러보기</a>
    </div>
    <div class="trust">학교 인증 회원만 · 대학생 팀 프로젝트에 딱 맞게</div>
  </div>
</section>

<%-- 핵심 기능 --%>
<section class="lp-section">
  <div class="lp-wrap">
    <div class="lp-sec-head">
      <div class="eb">What you get</div>
      <h2>모집부터 완료까지, 하나의 흐름으로</h2>
      <p>따로 놀던 오픈채팅·구글폼·엑셀을 다모여 하나로 대체해요.</p>
    </div>
    <div class="lp-features">
      <div class="feat">
        <div class="fic" style="background:var(--cat-contest)">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
        </div>
        <h3>프로젝트 매칭</h3>
        <p>카테고리·학과·학년으로 딱 맞는 팀을 찾아요. 지원하거나, 내 소개를 올려 팀장의 제의를 받아요.</p>
      </div>
      <div class="feat">
        <div class="fic" style="background:var(--cat-major)">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg>
        </div>
        <h3>팀 협업 워크스페이스</h3>
        <p>업무 보드·일정·회의록·결과물 검수까지. 누가 뭘 하는지 한눈에 보이고, 팀장이 진행을 관리해요.</p>
      </div>
      <div class="feat">
        <div class="fic" style="background:var(--cat-club)">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <h3>인재풀 · 멘토</h3>
        <p>팀원으로 나서거나, 멘토로 팀을 도와요. 팀장은 인재풀에서 함께할 사람을 직접 초대할 수 있어요.</p>
      </div>
    </div>
  </div>
</section>

<%-- 카테고리 --%>
<section class="lp-section">
  <div class="lp-wrap">
    <div class="lp-sec-head">
      <div class="eb">Categories</div>
      <h2>이런 프로젝트를 위한 곳</h2>
    </div>
    <div class="lp-cats">
      <div class="lp-cat"><span class="dot" style="background:var(--cat-contest)"></span>공모전</div>
      <div class="lp-cat"><span class="dot" style="background:var(--cat-major)"></span>학과 프로젝트</div>
      <div class="lp-cat"><span class="dot" style="background:var(--cat-liberal)"></span>교양 팀플</div>
      <div class="lp-cat"><span class="dot" style="background:var(--cat-club)"></span>교내활동</div>
    </div>
  </div>
</section>

<%-- 이용 흐름 --%>
<section class="lp-section">
  <div class="lp-wrap">
    <div class="lp-sec-head">
      <div class="eb">How it works</div>
      <h2>세 단계면 팀이 굴러가요</h2>
    </div>
    <div class="lp-steps">
      <div class="lp-step">
        <h4>모집하거나 지원해요</h4>
        <p>팀원을 모집하는 글을 올리거나, 마음에 드는 프로젝트에 지원해요. 면접·승인으로 팀원을 확정해요.</p>
      </div>
      <div class="lp-step">
        <h4>팀을 꾸려요</h4>
        <p>역할을 나누고 팀장을 정해요. 멘토를 초대할 수도 있고, 승계 순위로 팀장 공백도 대비해요.</p>
      </div>
      <div class="lp-step">
        <h4>협업하고 완료해요</h4>
        <p>업무 보드로 진행하고, 제출·검수를 거쳐 결과물을 모아요. 완료하면 포트폴리오로 남아요.</p>
      </div>
    </div>
  </div>
</section>

<%-- 하단 CTA --%>
<div class="lp-wrap">
  <div class="lp-band">
    <h2>이번 학기 팀 프로젝트, 다모여에서 시작하세요</h2>
    <p>학교 이메일로 3분이면 가입 완료.</p>
    <a class="btn pri big" href="${ctx}/auth/signup">무료로 시작하기</a>
  </div>
</div>

</main>

<%-- 랜딩 전용 푸터 --%>
<footer class="lp-foot">
  <div class="logo">다모여</div>
  대학생 프로젝트 매칭·협업 플랫폼 · 캡스톤 팀 프로젝트
</footer>

</body>
</html>
