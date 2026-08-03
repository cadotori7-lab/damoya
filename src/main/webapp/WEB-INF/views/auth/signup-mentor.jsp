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
            <div id="certList" class="cert-list"></div>
            <button type="button" id="certAddBtn" class="btn ghost sm" style="margin-top:8px">+ 자격증 추가</button>
            <form:hidden path="cert" id="certHidden"/>
            <div class="hint">자격증을 입력하면 해당 항목의 이미지 인증이 필수예요. 여러 개는 쉼표로 이어 저장됩니다.</div>
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
            <button type="submit" class="btn pri" id="submitBtn">멘토로 가입하기</button>
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
    const ctx = '${ctx}';
    const csrfToken = '${_csrf.token}';
    const csrfHeader = '${_csrf.headerName}';
    const initialCert = '<c:out value="${signupMentor.cert}" default=""/>';

    const nameInput = document.getElementById('name');
    const certList = document.getElementById('certList');
    const certAddBtn = document.getElementById('certAddBtn');
    const certHidden = document.getElementById('certHidden');
    const form = document.getElementById('signupMentor');
    const deptFields = document.getElementById('deptFields');
    const univSelect = document.getElementById('univSelect');
    const deptSelect = document.getElementById('deptSelect');
    const affiliationToggle = document.getElementById('affiliationToggle');

    function syncCertHidden() {
      const values = Array.from(certList.querySelectorAll('.cert-item'))
        .map((input) => (input.value || '').trim())
        .filter(Boolean);
      certHidden.value = values.join(', ');
      updateRemoveButtons();
    }

    function updateRemoveButtons() {
      const rows = certList.querySelectorAll('.cert-card');
      rows.forEach((row) => {
        const removeBtn = row.querySelector('.cert-remove');
        if (removeBtn) removeBtn.style.display = rows.length > 1 ? '' : 'none';
      });
    }

    function setRowStatus(row, text, state) {
      const status = row.querySelector('.cert-status');
      status.textContent = text;
      status.classList.remove('ok', 'bad');
      if (state) status.classList.add(state);
    }

    async function verifyRow(row) {
      const name = (nameInput.value || '').trim();
      const certLabel = (row.querySelector('.cert-item').value || '').trim();
      const fileInput = row.querySelector('.cert-file');
      const verifyBtn = row.querySelector('.cert-verify-btn');
      const file = fileInput.files[0];

      if (!name) { setRowStatus(row, '먼저 이름을 입력해주세요.', 'bad'); return; }
      if (!certLabel) { setRowStatus(row, '자격증명을 먼저 입력해주세요.', 'bad'); return; }
      if (!file) { setRowStatus(row, '자격증 이미지를 선택해주세요.', 'bad'); return; }

      verifyBtn.disabled = true;
      setRowStatus(row, '인증 중... (수 초 걸릴 수 있어요)');

      const fd = new FormData();
      fd.append('name', name);
      fd.append('certLabel', certLabel);
      fd.append('file', file);

      try {
        const res = await fetch(ctx + '/auth/signup/mentor/verify-cert', {
          method: 'POST',
          headers: { [csrfHeader]: csrfToken },
          body: fd
        });
        const data = await res.json();
        if (res.ok && data.matched) {
          setRowStatus(row, '✅ 자격증 인증 완료', 'ok');
          row.dataset.verified = 'true';
          row.dataset.verifiedLabel = certLabel;
        } else {
          setRowStatus(row, '❌ ' + (data.detail || '이름과 자격증이 일치하지 않아요.'), 'bad');
          row.dataset.verified = 'false';
          row.dataset.verifiedLabel = '';
        }
      } catch (e) {
        setRowStatus(row, '오류: ' + e.message, 'bad');
        row.dataset.verified = 'false';
        row.dataset.verifiedLabel = '';
      } finally {
        verifyBtn.disabled = false;
      }
    }

    function addCertRow(value) {
      const row = document.createElement('div');
      row.className = 'cert-card';
      row.dataset.verified = 'false';
      row.dataset.verifiedLabel = '';
      row.innerHTML =
        '<div class="cert-row">' +
          '<input type="text" class="cert-item" maxlength="100" placeholder="예: 정보처리기사">' +
          '<button type="button" class="btn ghost sm cert-remove">삭제</button>' +
        '</div>' +
        '<div class="cert-verify">' +
          '<input type="file" class="cert-file" accept="image/*">' +
          '<button type="button" class="btn ghost cert-verify-btn">자격증 인증</button>' +
        '</div>' +
        '<div class="hint cert-status">자격증을 입력하면 이미지 인증이 필수예요.</div>';

      const input = row.querySelector('.cert-item');
      input.value = value || '';
      input.addEventListener('input', () => {
        row.dataset.verified = 'false';
        row.dataset.verifiedLabel = '';
        const label = (input.value || '').trim();
        if (label) {
          setRowStatus(row, '자격증명이 바뀌었어요. 이미지 인증을 완료해주세요.', 'bad');
        } else {
          setRowStatus(row, '비워 두면 인증 없이 가입할 수 있어요.');
        }
        syncCertHidden();
      });
      row.querySelector('.cert-remove').addEventListener('click', () => {
        row.remove();
        if (!certList.children.length) addCertRow('');
        syncCertHidden();
      });
      row.querySelector('.cert-verify-btn').addEventListener('click', () => verifyRow(row));
      row.querySelector('.cert-file').addEventListener('change', () => {
        row.dataset.verified = 'false';
        row.dataset.verifiedLabel = '';
        setRowStatus(row, '이미지가 바뀌었어요. 다시 인증해주세요.', 'bad');
      });

      certList.appendChild(row);
      syncCertHidden();
    }

    const seed = (initialCert || '').split(',').map((s) => s.trim()).filter(Boolean);
    if (seed.length) seed.forEach(addCertRow);
    else addCertRow('');

    certAddBtn.addEventListener('click', () => addCertRow(''));

    if (form) {
      form.addEventListener('submit', (event) => {
        syncCertHidden();
        const invalid = Array.from(certList.querySelectorAll('.cert-card')).find((row) => {
          const label = (row.querySelector('.cert-item').value || '').trim();
          if (!label) return false;
          return row.dataset.verified !== 'true' || row.dataset.verifiedLabel !== label;
        });
        if (invalid) {
          event.preventDefault();
          setRowStatus(invalid, '이 자격증은 이미지 인증이 필요해요.', 'bad');
          invalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
      });
    }

    function setAffiliation(type) {
      const internal = type === 'internal';
      affiliationToggle.querySelectorAll('.opt').forEach((opt) => {
        const on = opt.dataset.v === type;
        opt.classList.toggle('on', on);
        const radio = opt.querySelector('input[type="radio"]');
        if (radio) radio.checked = on;
      });
      deptFields.style.display = internal ? '' : 'none';
      univSelect.required = internal;
      deptSelect.required = internal;
      if (!internal) {
        univSelect.value = '';
        deptSelect.value = '';
        deptSelect.disabled = true;
      } else if (univSelect.value) {
        deptSelect.disabled = false;
      }
    }

    affiliationToggle.querySelectorAll('.opt').forEach((opt) => {
      opt.addEventListener('click', () => setAffiliation(opt.dataset.v));
    });
    setAffiliation('${isInternal ? "internal" : "external"}');

    nameInput.addEventListener('input', () => {
      certList.querySelectorAll('.cert-card').forEach((row) => {
        row.dataset.verified = 'false';
        setRowStatus(row, '이름이 바뀌었어요. 이미지 인증을 다시 할 수 있어요.');
      });
    });
  })();
</script>
</body>
</html>
