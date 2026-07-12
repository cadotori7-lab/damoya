<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../resources/css/style.css">
</head>
<body>
  <header class="lp-top">
  <div class="in">
    <a class="logo" href="${ctx}/">다<b>모여</b></a>
    <div class="r">
      <a class="btn ghost sm" href="${ctx}/auth/login">로그인</a>
      <a class="btn pri sm" href="${ctx}/auth/signup">시작하기</a>
    </div>
  </div>
</header>
  <!-- ========== 회원가입 ========== -->
<main>
  <section id="v-signup">
    <form action="${ctx}/auth/signup" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div class="form-wrap">
        <div class="eyebrow">Create account</div>
        <h1 class="page"><em>회원가입</em></h1>
        <p class="sub">학교 정보를 등록하면 관리자 인증 후 교내 매칭을 이용할 수 있어요.</p>

        <div class="form-card">
          <div class="fsec-title"><span>1</span>계정</div>
          <div class="frow">
            <div class="fld"><label>아이디<span class="req">*</span></label><input type="text" name="login_id" placeholder="영문·숫자 4자 이상"></div>
            <div class="fld"><label>이메일<span class="req">*</span></label><input type="email" name="email" placeholder="학교 이메일 권장"></div>
          </div>
          <div class="frow">
            <div class="fld"><label>비밀번호<span class="req">*</span></label><input type="password" name="password" placeholder="8자 이상"></div>
            <div class="fld"><label>비밀번호 확인<span class="req">*</span></label><input type="password" name="confirmPassword" placeholder="다시 입력">
          </div>
        </div>
        </div>

        <div class="form-card">
          <div class="fsec-title"><span>2</span>학적 정보</div>
          <div class="frow">
            <div class="fld"><label>이름<span class="req">*</span></label><input type="text" name="name" placeholder="실명"></div>
            <div class="fld"><label>학년<span class="req">*</span></label>
              <select name="grade">
                <option>1</option>
                <option>2</option>
                <option selected>3</option>
                <option>4</option>
                <option>5</option>
              </select>
            </div>
          </div>
          <div class="frow">
            <!-- <div class="fld"><label>대학<span class="req">*</span></label>
              <select name="university"><option selected>대진대학교</option><option>가천대학교</option><option>단국대학교</option><option>직접 입력…</option></select>
              <div class="hint">등록한 학교는 관리자 인증 후 이용할 수 있어요.</div>
            </div> -->
            <!-- <div class="fld"><label>학과<span class="req">*</span></label>
              <select name="dept_id"><option selected>1</option><option>소프트웨어학과</option><option>산업경영공학과</option><option>게임공학과</option></select>
            </div> -->
          </div>
          <div class="frow">
            <div class="fld"><label>주전공</label><input type="text" name="major" value="컴퓨터공학" ></div>
            <div class="fld"><label>복수전공 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><input type="text" name="double_major" placeholder="없으면 비워두세요"></div>
          </div>
          <div class="frow">
            <div class="fld"><label>소개글</label><input type="text" name="intro" value="프로필에 등록할 소개글을 입력하세요." ></div>
          </div>
          <div class="form-foot">
            <div class="btn ghost"><a href="${ctx}/auth/login">로그인하기</a></div>
            <button class="btn pri" type="submit">가입하기</button>
          </div>
        </div>
      </div>
    </form>
  </section>
</main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>