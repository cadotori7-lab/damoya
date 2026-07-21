<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 멘토 회원가입</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>

<%-- 비로그인 화면이므로 랜딩용 상단바 --%>
<header class="lp-top">
  <div class="in">
    <a class="logo" href="${ctx}/">다<b>모여</b></a>
    <div class="r">
      <a class="btn ghost sm" href="${ctx}/auth/login">로그인</a>
    </div>
  </div>
</header>

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
              <form:password path="passwordConfirm" placeholder="다시 입력"/>
              <form:errors path="passwordConfirm" element="div" cssClass="hint" cssStyle="color:var(--reject)"/>
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
            <div class="fld">
              <label>소속 학과 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
              <form:select path="dept_id">
                <form:option value="" label="외부 전문가 (소속 없음)"/>
                <c:forEach var="d" items="${depts}">
                  <form:option value="${d.deptId}">${d.univName} · ${d.deptName}</form:option>
                </c:forEach>
              </form:select>
              <div class="hint">교내 교수님이면 학과를 선택해주세요.</div>
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
            <label>자격증 <span style="color:var(--ink-soft);font-weight:500">(필수)</span></label>
            <form:input path="cert" placeholder="예: 정보처리기사, AWS SA Professional"/>
          </div>

          <div class="fld one">
            <label>자격증 이미지 인증<span class="req">*</span></label>
            <div class="cert-verify">
              <input type="file" id="certFile" accept="image/*">
              <button type="button" id="certVerifyBtn" class="btn ghost">자격증 인증</button>
            </div>
            <div id="certStatus" class="hint cert-status">위의 <b>이름</b>과 자격증 이미지를 올린 뒤 인증해주세요. 인증되어야 가입할 수 있어요.</div>
            <c:if test="${not empty certError}">
              <div class="hint cert-status bad">${certError}</div>
            </c:if>
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
            <button type="submit" class="btn pri" id="submitBtn" ${certVerified ? '' : 'disabled'}>멘토로 가입하기</button>
          </div>
        </div>
      </form:form>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
<script src="${ctx}/resources/js/common.js"></script>
<script>
  (function () {
    const ctx = '${ctx}';
    const csrfToken = '${_csrf.token}';
    const csrfHeader = '${_csrf.headerName}';

    const nameInput = document.getElementById('name');
    const certFile = document.getElementById('certFile');
    const certVerifyBtn = document.getElementById('certVerifyBtn');
    const certStatus = document.getElementById('certStatus');
    const submitBtn = document.getElementById('submitBtn');

    function setStatus(text, state) {
      certStatus.textContent = text;
      certStatus.classList.remove('ok', 'bad');
      if (state) certStatus.classList.add(state);
    }

    certVerifyBtn.addEventListener('click', async () => {
      const name = (nameInput.value || '').trim();
      const file = certFile.files[0];
      if (!name) { setStatus('먼저 이름을 입력해주세요.', 'bad'); return; }
      if (!file) { setStatus('자격증 이미지를 선택해주세요.', 'bad'); return; }

      certVerifyBtn.disabled = true;
      setStatus('인증 중... (수 초 걸릴 수 있어요)');

      const fd = new FormData();
      fd.append('name', name);
      fd.append('file', file);

      try {
        const res = await fetch(ctx + '/auth/signup/mentor/verify-cert', {
          method: 'POST',
          headers: { [csrfHeader]: csrfToken }, // CSRF 는 헤더로 전송(멀티파트 본문 파싱 전에 검증됨)
          body: fd
        });
        const data = await res.json();
        if (res.ok && data.matched) {
          setStatus('✅ 자격증 인증 완료', 'ok');
          submitBtn.disabled = false;
        } else {
          setStatus('❌ ' + (data.detail || '이름과 자격증이 일치하지 않아요.'), 'bad');
          submitBtn.disabled = true;
        }
      } catch (e) {
        setStatus('오류: ' + e.message, 'bad');
        submitBtn.disabled = true;
      } finally {
        certVerifyBtn.disabled = false;
      }
    });

    // 이름을 바꾸면 다시 인증해야 함
    nameInput.addEventListener('input', () => {
      submitBtn.disabled = true;
      setStatus('이름이 바뀌었어요. 다시 인증해주세요.');
    });
  })();
</script>
</body>
</html>
