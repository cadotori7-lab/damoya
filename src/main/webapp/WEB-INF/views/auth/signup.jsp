<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
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
  <!-- ========== 회원가입 ========== -->
<main>
  <section id="v-signup">
    <div class="form-wrap">
      <div class="eyebrow">Create account</div>
      <h1 class="page"><em>회원가입</em></h1>
      <p class="sub">학교 정보를 등록하면 관리자 인증 후 교내 매칭을 이용할 수 있어요.</p>

      <div class="form-card">
        <div class="fsec-title"><span>1</span>계정</div>
        <div class="frow">
          <div class="fld"><label>아이디<span class="req">*</span></label><input type="text" placeholder="영문·숫자 4자 이상"></div>
          <div class="fld"><label>이메일<span class="req">*</span></label><input type="email" placeholder="학교 이메일 권장"></div>
        </div>
        <div class="frow">
          <div class="fld"><label>비밀번호<span class="req">*</span></label><input type="password" placeholder="8자 이상"></div>
          <div class="fld"><label>비밀번호 확인<span class="req">*</span></label><input type="password" placeholder="다시 입력"></div>
        </div>
      </div>

      <div class="form-card">
        <div class="fsec-title"><span>2</span>학적 정보</div>
        <div class="frow">
          <div class="fld"><label>이름<span class="req">*</span></label><input type="text" placeholder="실명"></div>
          <div class="fld"><label>학년<span class="req">*</span></label>
            <select><option>1학년</option><option>2학년</option><option selected>3학년</option><option>4학년</option><option>5학년 이상</option></select>
          </div>
        </div>
        <div class="frow">
          <div class="fld"><label>대학<span class="req">*</span></label>
            <select><option selected>대진대학교</option><option>가천대학교</option><option>단국대학교</option><option>직접 입력…</option></select>
            <div class="hint">등록한 학교는 관리자 인증 후 이용할 수 있어요.</div>
          </div>
          <div class="fld"><label>학과<span class="req">*</span></label>
            <select><option selected>컴퓨터공학과</option><option>소프트웨어학과</option><option>산업경영공학과</option><option>게임공학과</option></select>
          </div>
        </div>
        <div class="frow">
          <div class="fld"><label>주전공</label><input type="text" value="컴퓨터공학" ></div>
          <div class="fld"><label>복수전공 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><input type="text" placeholder="없으면 비워두세요"></div>
        </div>
        <div class="fld">
          <label>관심 분야 <span style="color:var(--ink-soft);font-weight:500">(여러 개 선택 가능)</span></label>
          <div class="picker">
            <input type="checkbox" id="i1" checked><label for="i1">백엔드</label>
            <input type="checkbox" id="i2"><label for="i2">프론트엔드</label>
            <input type="checkbox" id="i3" checked><label for="i3">데이터</label>
            <input type="checkbox" id="i4"><label for="i4">AI</label>
            <input type="checkbox" id="i5"><label for="i5">기획</label>
            <input type="checkbox" id="i6"><label for="i6">디자인</label>
            <input type="checkbox" id="i7"><label for="i7">게임</label>
          </div>
        </div>
        <div class="form-foot">
          <button class="btn ghost" onclick="go('login')">이미 계정이 있어요</button>
          <button class="btn pri" onclick="go('home')">가입하기</button>
        </div>
      </div>
    </div>
  </section>
</main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>