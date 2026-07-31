<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Git AI 리포트 테스트 | 다모여</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
  <style>
    .git-report-wrap{max-width:860px;margin:0 auto}
    .test-nav{display:flex;align-items:center;justify-content:space-between;gap:16px;margin-bottom:28px}
    .test-nav .back{margin:0}
    .test-badge{font-family:var(--mono);font-size:11px;font-weight:700;color:var(--accent);background:var(--accent-soft);padding:5px 10px;border-radius:999px}
    .report-form{background:var(--surface);border:1px solid var(--line);border-radius:18px;padding:26px;box-shadow:0 16px 40px -28px rgba(20,35,63,.35)}
    .report-form-grid{display:grid;grid-template-columns:1fr 180px;gap:16px}
    .report-field{margin-bottom:17px}
    .report-field.full{grid-column:1/-1}
    .report-field label{display:flex;align-items:center;justify-content:space-between;font-size:13px;font-weight:800;margin-bottom:7px}
    .report-field label span{font-family:var(--mono);font-size:10px;color:var(--ink-soft);font-weight:500}
    .report-field input{width:100%;height:46px;border:1px solid var(--line-strong);border-radius:11px;padding:0 14px;font:inherit;background:var(--surface)}
    .report-field input:focus{outline:none;border-color:var(--accent);box-shadow:0 0 0 3px var(--accent-soft)}
    .form-help{display:flex;align-items:flex-start;gap:9px;margin:-3px 0 20px;padding:12px 14px;border-radius:10px;background:var(--paper);color:var(--ink-soft);font-size:12.5px;line-height:1.55}
    .form-help strong{color:var(--ink);white-space:nowrap}
    .form-actions{display:flex;justify-content:flex-end;gap:9px}
    .form-actions .btn{justify-content:center}
    .loading-panel{display:none;margin-top:18px;padding:16px 18px;border:1px solid #cbd4ff;border-radius:12px;background:var(--accent-soft);color:var(--accent);font-size:13px;font-weight:700}
    .loading-panel.on{display:flex;align-items:center;gap:11px}
    .spinner{width:18px;height:18px;border:2px solid rgba(43,70,200,.2);border-top-color:var(--accent);border-radius:50%;animation:spin .8s linear infinite}
    @keyframes spin{to{transform:rotate(360deg)}}
    .report-error{margin-top:20px;padding:16px 18px;border:1px solid #f0c4cc;border-radius:12px;background:var(--reject-bg);color:#982f43;white-space:pre-wrap;font-size:13px}
    .report-result{margin-top:24px;background:var(--surface);border:1px solid var(--line);border-radius:18px;overflow:hidden}
    .result-hero{padding:25px 26px;background:linear-gradient(135deg,#16233f,#263c72);color:#fff}
    .result-hero .eyebrow{color:#aebcff}
    .result-hero h2{font-size:24px;margin:5px 0 8px}
    .result-hero p{color:#d8dff2;font-size:13.5px;line-height:1.7}
    .result-body{padding:24px 26px}
    .result-metrics{display:grid;grid-template-columns:repeat(4,1fr);gap:10px;margin-bottom:22px}
    .result-metric{padding:14px;background:var(--paper);border-radius:11px}
    .result-metric span{display:block;font-size:11px;color:var(--ink-soft)}
    .result-metric strong{display:block;margin-top:3px;font-family:var(--mono);font-size:16px;word-break:break-all}
    .result-label{font-family:var(--mono);font-size:10.5px;font-weight:700;color:var(--ink-soft);letter-spacing:.07em;text-transform:uppercase;margin-bottom:8px}
    .result-summary{font-size:14px;line-height:1.75;color:#33405c;margin-bottom:20px}
    .tech-list{display:flex;flex-wrap:wrap;gap:7px;margin-bottom:23px}
    .tech-list span{padding:4px 10px;border-radius:999px;background:var(--accent-soft);color:var(--accent);font-family:var(--mono);font-size:11px;font-weight:700}
    .result-download{display:flex;align-items:center;justify-content:space-between;gap:18px;padding-top:18px;border-top:1px solid var(--line)}
    .result-download .meta{font-size:12px;color:var(--ink-soft);word-break:break-all}
    @media(max-width:720px){
      .report-form-grid{grid-template-columns:1fr}
      .report-field.full{grid-column:auto}
      .result-metrics{grid-template-columns:repeat(2,1fr)}
      .result-download{align-items:stretch;flex-direction:column}
      .result-download .btn{justify-content:center}
    }
    @media(max-width:480px){
      .report-form,.result-body{padding:20px}
      .result-hero{padding:22px 20px}
      .result-metrics{grid-template-columns:1fr}
      .form-actions{flex-direction:column}
      .form-actions .btn{width:100%}
    }
  </style>
</head>
<body>
  <jsp:include page="../includes/header.jsp"/>

  <main>
    <section class="git-report-wrap" aria-labelledby="git-report-title">
      <div class="test-nav">
        <a class="back" href="${ctx}/test/">← 테스트 홈</a>
        <span class="test-badge">MCP TEST PAGE</span>
      </div>

      <div class="eyebrow">AI repository analysis</div>
      <h1 class="page" id="git-report-title"><em>Git AI 리포트</em> 테스트</h1>
      <p class="sub">공개 GitHub 저장소를 MCP로 분석하고 Playwright PDF를 생성합니다.</p>

      <form class="report-form" id="gitReportForm" action="${ctx}/test/git-report" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="report-form-grid">
          <div class="report-field full">
            <label for="repositoryUrl">
              Git 저장소 주소
              <span>PUBLIC HTTPS</span>
            </label>
            <input
              id="repositoryUrl"
              type="url"
              name="repositoryUrl"
              value="${fn:escapeXml(repositoryUrl)}"
              placeholder="https://github.com/owner/repository"
              maxlength="500"
              pattern="https://github\.com/.+/.+"
              autocomplete="url"
              required
            >
          </div>
          <div class="report-field">
            <label for="projectName">프로젝트 이름</label>
            <input
              id="projectName"
              type="text"
              name="projectName"
              value="${fn:escapeXml(projectName)}"
              placeholder="예: 다모여"
              maxlength="200"
              required
            >
          </div>
          <div class="report-field">
            <label for="ref">
              브랜치 또는 태그
              <span>선택</span>
            </label>
            <input
              id="ref"
              type="text"
              name="ref"
              value="${fn:escapeXml(ref)}"
              placeholder="main"
              maxlength="200"
            >
          </div>
        </div>

        <div class="form-help">
          <strong>테스트 범위</strong>
          <span>현재는 공개 GitHub 저장소만 지원합니다. 저장소 크기와 LLM 응답에 따라 PDF 생성까지 시간이 걸릴 수 있습니다.</span>
        </div>

        <div class="form-actions">
          <button class="btn ghost" id="exampleButton" type="button">예시 주소 채우기</button>
          <button class="btn pri" id="submitButton" type="submit">AI 리포트 생성</button>
        </div>
      </form>

      <div class="loading-panel" id="loadingPanel" role="status" aria-live="polite">
        <span class="spinner" aria-hidden="true"></span>
        <span>저장소를 읽고 AI 리포트와 PDF를 생성하고 있습니다. 잠시 기다려 주세요.</span>
      </div>

      <c:if test="${not empty error}">
        <div class="report-error" role="alert"><c:out value="${error}"/></div>
      </c:if>

      <c:if test="${submitted and not empty reportResult}">
        <article class="report-result">
          <header class="result-hero">
            <div class="eyebrow">Report generated</div>
            <h2><c:out value="${reportResult.projectName}"/></h2>
            <p><c:out value="${reportResult.repositoryUrl}"/></p>
          </header>
          <div class="result-body">
            <div class="result-metrics">
              <div class="result-metric">
                <span>종합 점수</span>
                <strong><c:out value="${reportResult.overallScore}"/> / 100</strong>
              </div>
              <div class="result-metric">
                <span>분석 파일</span>
                <strong><c:out value="${reportResult.fileCount}"/>개</strong>
              </div>
              <div class="result-metric">
                <span>PDF 페이지</span>
                <strong><c:out value="${reportResult.pageCount}"/>쪽</strong>
              </div>
              <div class="result-metric">
                <span>분석 기준</span>
                <strong><c:out value="${reportResult.analyzedRef}"/></strong>
              </div>
            </div>

            <div class="result-label">Executive summary</div>
            <p class="result-summary"><c:out value="${reportResult.executiveSummary}"/></p>

            <c:if test="${not empty reportResult.techStack}">
              <div class="result-label">Detected stack</div>
              <div class="tech-list">
                <c:forEach var="tech" items="${reportResult.techStack}">
                  <span><c:out value="${tech}"/></span>
                </c:forEach>
              </div>
            </c:if>

            <div class="result-download">
              <div class="meta">
                commit <c:out value="${reportResult.analyzedCommit}"/><br>
                <c:out value="${reportResult.fileName}"/>
              </div>
              <a
                class="btn pri"
                href="${ctx}/test/git-report/${fn:escapeXml(reportResult.reportId)}/pdf"
              >PDF 다운로드</a>
            </div>
          </div>
        </article>
      </c:if>
    </section>
  </main>

  <jsp:include page="../includes/footer.jsp"/>
  <script>
    (function () {
      var form = document.getElementById('gitReportForm');
      var submitButton = document.getElementById('submitButton');
      var exampleButton = document.getElementById('exampleButton');
      var loadingPanel = document.getElementById('loadingPanel');

      exampleButton.addEventListener('click', function () {
        document.getElementById('repositoryUrl').value =
          'https://github.com/cadotori7-lab/damoya';
        document.getElementById('projectName').value = '다모여';
        document.getElementById('ref').value = 'main';
      });

      form.addEventListener('submit', function () {
        submitButton.disabled = true;
        submitButton.textContent = '생성 중…';
        loadingPanel.classList.add('on');
      });
    }());
  </script>
</body>
</html>
