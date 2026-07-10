<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <!-- ========== 로그인 (별도 화면) ========== -->
<main>
  <section id="v-login">
    <div class="login-wrap" style="margin:-34px -24px -80px">
      <div class="login-art">
        <div class="lg">다<span style="color:var(--hl)">모여</span></div>
        <div>
          <h2>흩어진 팀원 찾기,<br><mark>한 곳에서</mark> 끝내요.</h2>
          <p>공모전·학과 프로젝트·교양 팀플까지. 우리 학교와 전국에서 함께할 팀을 찾고, 모집부터 완료까지 한 흐름으로 관리하세요.</p>
          <div class="feats"><span>대학·학과 매칭</span><span>지원–면접–승인</span><span>업무 보드</span><span>멘토 피드백</span></div>
        </div>
        <div class="mono" style="font-size:12px;color:#9fb0d8">대진대를 시작으로 전국 대학으로 확장 중</div>
      </div>
      <div class="login-form">
        <div class="login-box">
          <div class="eyebrow">Welcome back</div>
          <h3>로그인</h3>
          <div class="field"><label>아이디</label><input type="text" placeholder="학교 이메일 또는 아이디"></div>
          <div class="field"><label>비밀번호</label><input type="password" placeholder="••••••••"></div>
          <button class="btn pri" onclick="go('home')">로그인</button>
          <div class="alt">아직 계정이 없나요? <a onclick="go('signup')" style="cursor:pointer">회원가입</a></div>
          <div class="role-hint"><b>가입 후</b> 학교·학과를 등록하면 관리자 인증을 거쳐 교내 매칭을 이용할 수 있어요. 멘토는 팀장의 초대로 참여합니다.</div>
        </div>
      </div>
    </div>
  </section>
</main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>