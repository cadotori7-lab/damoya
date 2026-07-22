<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
  <jsp:include page="../includes/header.jsp" />

  <!-- ========== 회원가입 ========== -->
  <main>
    <section id="v-signup">
      <form:form action="${ctx}/auth/signup" method="post" modelAttribute="signupMember">
        <div class="form-wrap">
          <div class="eyebrow">Create account</div>
          <h1 class="page"><em>회원가입</em></h1>
          <p class="sub">학교 정보를 등록하면 관리자 인증 후 교내 매칭을 이용할 수 있어요.</p>
          <!-- 가입 유형 전환 (페이지가 분리되어 있으므로 링크) -->
          <div class="role-toggle">
            <a class="opt on" href="${ctx}/auth/signup">
              <div class="rt">🎓 일반 회원</div>
              <div class="rd">프로젝트를 찾거나 모집하는 학생</div>
            </a>
            <a class="opt" href="${ctx}/auth/signup/mentor">
              <div class="rt">🧭 멘토</div>
              <div class="rd">팀에 조언·피드백을 주는 교수·전문가</div>
            </a>
          </div>
          <!-- 1. 계정 -->
          <div class="form-card">
            <div class="fsec-title"><span>1</span>계정</div>

            <div class="frow">
              <div class="fld">
                <label>아이디<span class="req">*</span></label>
                <form:input path="login_id" placeholder="영문·숫자 4자 이상" />
                <form:errors path="login_id" element="span" cssClass="error" />
              </div>
              <div class="fld">
                <label>이메일<span class="req">*</span></label>
                <form:input path="email" type="email" placeholder="학교 이메일 권장" />
                <form:errors path="email" element="span" cssClass="error" />
              </div>
            </div>

            <div class="frow">
              <div class="fld">
                <label>비밀번호<span class="req">*</span></label>
                <form:password path="password" placeholder="8자 이상" />
                <form:errors path="password" element="span" cssClass="error" />
              </div>
              <div class="fld">
                <label>비밀번호 확인<span class="req">*</span></label>
                <form:password path="password_confirm" placeholder="다시 입력" />
                <form:errors path="password_confirm" element="span" cssClass="error" />
              </div>
            </div>
          </div>

          <!-- 2. 학적 정보 -->
          <div class="form-card">
            <div class="fsec-title"><span>2</span>학적 정보</div>
            <div class="frow">
              <div class="fld">
                <label>학교<span class="req">*</span></label>
                <select name="univ_name" id="univSelect" required>
                  <option value="" selected>학교를 선택하세요</option>
                  <c:forEach var="univ" items="${univList}">
                    <option value="${univ.univ_name}">${univ.univ_name}</option>
                  </c:forEach>
                </select>
                <label>학과<span class="req">*</span></label>
                <select name="dept_id" id="deptSelect" required>
                  <option value="" selected>학과를 선택하세요</option>
                  <c:forEach var="dept" items="${univList}">
                    <option value="${dept.dept_id}" data-univ-name="${dept.univ_name}">${dept.dept_name}</option>
                  </c:forEach>
                </select>
              </div>
            </div>
            <div class="frow">
              <div class="fld">
                <label>이름<span class="req">*</span></label>
                <form:input path="name" placeholder="실명" />
                <form:errors path="name" element="span" cssClass="error" />
              </div>
              <div class="fld">
                <label>학년<span class="req">*</span></label>
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
              <div class="fld">
                <label>주전공</label>
                <input type="text" name="major" placeholder="예) 컴퓨터공학">
              </div>
              <div class="fld">
                <label>복수전공 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
                <input type="text" name="double_major" placeholder="없으면 비워두세요">
              </div>
            </div>

            <div class="frow one">
              <div class="fld">
                <label>소개글</label>
                <textarea name="intro" placeholder="프로필에 등록할 소개글을 입력하세요."></textarea>
              </div>
            </div>

            <div class="form-foot">
              <a class="btn ghost" href="${ctx}/auth/login">로그인하기</a>
              <button class="btn pri" type="submit">가입하기</button>
            </div>
          </div>
        </div>
      </form:form>
    </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/login.js"></script>
</body>
</html>
