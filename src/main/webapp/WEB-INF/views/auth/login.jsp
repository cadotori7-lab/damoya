<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>다모여 - 로그인</title>
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
          <form action="/auth/login" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="field"><label>아이디</label><input type="text" placeholder="학교 이메일 또는 아이디" name="login_id"></div>
            <div class="field"><label>비밀번호</label><input type="password" placeholder="••••••••" name="password"></div>
            <button class="btn pri" type="submit">로그인</button>
          </form>
          <div class="oauth-divider"><span>또는</span></div>
          <a class="btn ghost oauth-google" href="${ctx}/oauth/google">
            <svg width="18" height="18" viewBox="0 0 48 48" aria-hidden="true">
              <path fill="#FFC107" d="M43.6 20.5H42V20H24v8h11.3C33.7 32.4 29.3 35 24 35c-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.8 1.1 8 3l5.7-5.7C34.6 4.9 29.6 3 24 3 12.4 3 3 12.4 3 24s9.4 21 21 21 21-9.4 21-21c0-1.2-.1-2.4-.4-3.5z"/>
              <path fill="#FF3D00" d="M6.3 14.7l6.6 4.8C14.6 15.9 18.9 13 24 13c3.1 0 5.8 1.1 8 3l5.7-5.7C34.6 6.9 29.6 5 24 5c-7.7 0-14.4 4.4-17.7 10.7z"/>
              <path fill="#4CAF50" d="M24 45c5.5 0 10.4-1.9 14.2-5.1l-6.6-5.6c-2.1 1.5-4.8 2.4-7.6 2.4-5.3 0-9.7-3.6-11.3-8.4l-6.6 5.1C9.5 40.4 16.2 45 24 45z"/>
              <path fill="#1976D2" d="M43.6 20.5H42V20H24v8h11.3c-.8 2.3-2.2 4.2-4.2 5.6l6.6 5.6C41.9 36.5 45 30.8 45 24c0-1.2-.1-2.4-.4-3.5z"/>
            </svg>
            구글로 계속하기
          </a>
          <div class="alt">아직 계정이 없나요? <a href="${ctx}/auth/signup">회원가입</a></div>
          <div class="role-hint"><b>가입 후</b> 학교·학과를 등록하면 관리자 인증을 거쳐 교내 매칭을 이용할 수 있어요. 멘토는 팀장의 초대로 참여합니다.</div>
        </div>
      </div>
    </div>
  </section>
</main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>