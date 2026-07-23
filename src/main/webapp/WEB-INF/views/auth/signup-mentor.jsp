<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isInternal" value="${not empty signupMentor.dept_id}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 - 멘토 회원가입</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>

<jsp:include page="../includes/header.jsp" />

<main>
  <section id="v-signup-mentor">
    <div class="form-wrap">
      <div class="eyebrow">Create account</div>
      <h1 class="page"><em>멘토</em> 회원가입</h1>
      <p class="sub">경력을 등록하면 관리자 확인 후 멘토로 활동할 수 있어요. 팀장의 초대를 받아 프로젝트에 참여합니다.</p>

      <%-- 가입 유형 전환 (페이지가 분리되어 있으므로 링크) --%>
      <div class="role-toggle">
        <a class="opt" href="${ctx}/auth/signup">
          <div class="rt">🎓 일반 회원</div>
          <div class="rd">프로젝트를 찾거나 모집하는 학생</div>
        </a>
        <a class="opt on" href="${ctx}/auth/signup/mentor">
          <div class="rt">🧭 멘토</div>
          <div class="rd">팀에 조언·피드백을 주는 교수·전문가</div>
        </a>
      </div>

      <form:form action="${ctx}/auth/signup/mentor" method="post" modelAttribute="signupMentor" autocomplete="off">

        <div class="form-card">
          <div class="fsec-title"><span>1</span>계정</div>
          <div class="frow">
            <div class="fld">
              <label>아이디<span class="req">*</span></label>
              <form:input path="login_id" placeholder="영문·숫자 4자 이상"/>
              <form:errors path="login_id" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
            </div>
            <div class="fld">
              <label>이메일<span class="req">*</span></label>
              <form:input path="email" type="email" placeholder="소속 기관 이메일 권장"/>
              <form:errors path="email" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
            </div>
          </div>
          <div class="frow">
            <div class="fld">
              <label>비밀번호<span class="req">*</span></label>
              <form:password path="password" placeholder="8자 이상"/>
              <form:errors path="password" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
            </div>
            <div class="fld">
              <label>비밀번호 확인<span class="req">*</span></label>
              <form:password path="password_confirm" placeholder="다시 입력"/>
              <form:errors path="password_confirm" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
            </div>
          </div>
        </div>

        <div class="form-card">
          <div class="fsec-title"><span>2</span>멘토 정보</div>
          <div class="frow">
            <div class="fld">
              <label>이름<span class="req">*</span></label>
              <form:input path="name" placeholder="실명"/>
              <form:errors path="name" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
            </div>
          </div>

          <div class="fld one">
            <label>소속 구분<span class="req">*</span></label>
            <div class="role-toggle" id="affiliationToggle" style="margin-bottom:0">
              <label class="opt ${isInternal ? 'on' : ''}" data-v="internal">
                <input type="radio" name="affiliationType" value="internal" style="display:none" ${isInternal ? 'checked' : ''}>
                <div class="rt">🏫 교내</div>
                <div class="rd">우리 학교 소속 교수·조교</div>
              </label>
              <label class="opt ${isInternal ? '' : 'on'}" data-v="external">
                <input type="radio" name="affiliationType" value="external" style="display:none" ${isInternal ? '' : 'checked'}>
                <div class="rt">🌐 외부</div>
                <div class="rd">소속 없는 외부 전문가</div>
              </label>
            </div>
          </div>

          <div class="frow" id="deptFields" style="display:none">
            <div class="fld">
              <label>학교<span class="req">*</span></label>
              <select name="univ_name" id="univSelect" required>
                <option value="">학교를 선택하세요</option>
                <c:forEach var="u" items="${univList}">
                  <option value="${u.univ_name}">${u.univ_name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="fld">
              <label>학과<span class="req">*</span></label>
              <select name="dept_id" id="deptSelect" required>
                <option value="" <c:if test="${empty signupMentor.dept_id}">selected</c:if>>학과를 선택하세요</option>
                <c:forEach var="dept" items="${univList}">
                  <option value="${dept.dept_id}" data-univ-name="${dept.univ_name}" <c:if test="${dept.dept_id == signupMentor.dept_id}">selected</c:if>>${dept.dept_name}</option>
                </c:forEach>
              </select>
              <form:errors path="dept_id" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
            </div>
          </div>

          <div class="fld one">
            <label>전문분야<span class="req">*</span></label>
            <form:input path="field" placeholder="예: 백엔드 아키텍처 / 서비스 기획 / UX 디자인"/>
            <form:errors path="field" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
            <div class="hint">팀장이 인재풀에서 검색할 때 쓰이는 키워드예요.</div>
          </div>

          <div class="fld one">
            <label>경력</label>
            <form:textarea path="career" placeholder="예: 대진대학교 컴퓨터공학과 교수 (2015~)&#10;○○기업 백엔드 개발 8년" cssStyle="min-height:90px"/>
          </div>

          <div class="fld one">
            <label>자격증 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
            <form:input path="cert" placeholder="예: 정보처리기사, AWS SA Professional"/>
          </div>

          <div class="fld one">
            <label>소개글 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
            <form:textarea path="intro" placeholder="어떤 팀에게 어떤 도움을 줄 수 있는지 적어주세요. 프로필에 노출돼요." cssStyle="min-height:80px"/>
          </div>

          <div class="role-hint">
            <b>가입 후</b> 관리자가 경력을 확인한 뒤 멘토 활동이 승인돼요.
            멘토는 <b>팀장의 초대</b>로 프로젝트에 참여하며, 한 프로젝트당 한 명이 활동합니다.
          </div>

          <div class="form-foot">
            <a class="btn ghost" href="${ctx}/auth/login">이미 계정이 있어요</a>
            <button type="submit" class="btn pri">멘토로 가입하기</button>
          </div>
        </div>
      </form:form>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
<script src="${ctx}/resources/js/common.js"></script>
<script src="${ctx}/resources/js/login.js"></script>
<script>
(function () {
  var toggle = document.getElementById('affiliationToggle');
  var deptFields = document.getElementById('deptFields');
  var univSelect = document.getElementById('univSelect');
  var deptSelect = document.getElementById('deptSelect');
  if (!toggle || !deptFields || !univSelect || !deptSelect) return;

  function applyMode(mode) {
    var isInternal = mode === 'internal';
    toggle.querySelectorAll('.opt').forEach(function (opt) {
      opt.classList.toggle('on', opt.dataset.v === mode);
    });
    deptFields.style.display = isInternal ? '' : 'none';
    univSelect.disabled = !isInternal;
    deptSelect.disabled = !isInternal;
    if (!isInternal) {
      univSelect.value = '';
      deptSelect.value = '';
    }
  }

  toggle.querySelectorAll('input[name="affiliationType"]').forEach(function (radio) {
    radio.addEventListener('change', function () { applyMode(this.value); });
  });

  var checked = toggle.querySelector('input[name="affiliationType"]:checked');
  applyMode(checked ? checked.value : 'external');
})();
</script>
</body>
</html>
