<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>GitHub 프로젝트 요약 | 다모야</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
  <style>
    .git-report-wrap{max-width:820px;margin:0 auto}
    .test-nav{display:flex;align-items:center;gap:16px;margin-bottom:28px}
    .test-nav .back{margin:0}
    .report-form{background:var(--surface);border:1px solid var(--line);border-radius:18px;padding:26px;box-shadow:0 16px 40px -28px rgba(20,35,63,.35)}
    .report-field label{display:flex;align-items:center;justify-content:space-between;font-size:13px;font-weight:800;margin-bottom:7px}
    .report-field label span{font-family:var(--mono);font-size:10px;color:var(--ink-soft);font-weight:500}
    .report-field input,.report-field select{width:100%;height:48px;border:1px solid var(--line-strong);border-radius:11px;padding:0 14px;font:inherit;background:var(--surface)}
    .report-field input:focus,.report-field select:focus{outline:none;border-color:var(--accent);box-shadow:0 0 0 3px var(--accent-soft)}
    .github-box{display:flex;align-items:center;justify-content:space-between;gap:14px;margin:18px 0;padding:15px 17px;border:1px solid var(--line);border-radius:12px;background:var(--surface)}
    .github-box p{margin:0;color:var(--ink-soft);font-size:12.5px;line-height:1.5}
    .github-box strong{color:var(--ink)}
    .inline-form{margin:0}
    .form-help{margin:13px 0 20px;color:var(--ink-soft);font-size:12.5px;line-height:1.55}
    .form-actions{display:flex;justify-content:flex-end;gap:9px}
    .form-actions .btn{justify-content:center}
    .loading-panel{display:none;margin-top:18px;padding:16px 18px;border:1px solid #cbd4ff;border-radius:12px;background:var(--accent-soft);color:var(--accent);font-size:13px;font-weight:700}
    .loading-panel.on{display:flex;align-items:center;gap:11px}
    .spinner{width:18px;height:18px;border:2px solid rgba(43,70,200,.2);border-top-color:var(--accent);border-radius:50%;animation:spin .8s linear infinite}
    @keyframes spin{to{transform:rotate(360deg)}}
    .report-alert{margin-top:20px;padding:16px 18px;border-radius:12px;white-space:pre-wrap;font-size:13px;line-height:1.6}
    .report-alert.error{border:1px solid #f0c4cc;background:var(--reject-bg);color:#982f43}
    .report-result{margin-top:24px;background:var(--surface);border:1px solid var(--line);border-radius:18px;overflow:hidden}
    .result-hero{padding:25px 26px;background:linear-gradient(135deg,#16233f,#3154d5);color:#fff}
    .result-hero .eyebrow{color:#bfcaff}
    .result-hero h2{font-size:24px;margin:5px 0 8px}
    .result-hero p{color:#e4e8f7;font-size:14px;line-height:1.65}
    .result-hero .repo{color:#cbd3e9;font-size:12px;word-break:break-all}
    .result-body{padding:24px 26px}
    .result-meta{display:flex;flex-wrap:wrap;gap:8px;margin-bottom:22px}
    .result-meta span{padding:6px 10px;border-radius:9px;background:var(--paper);font-size:11.5px;color:var(--ink-soft)}
    .result-label{font-family:var(--mono);font-size:10.5px;font-weight:700;color:var(--ink-soft);letter-spacing:.07em;text-transform:uppercase;margin-bottom:8px}
    .result-summary{font-size:14px;line-height:1.75;color:#33405c;margin:0 0 21px;white-space:pre-line}
    .tech-list{display:flex;flex-wrap:wrap;gap:7px;margin-bottom:23px}
    .tech-list span{padding:4px 10px;border-radius:999px;background:var(--accent-soft);color:var(--accent);font-family:var(--mono);font-size:11px;font-weight:700}
    .improvement-list{margin:0 0 24px;padding-left:21px;color:#33405c;font-size:14px;line-height:1.7}
    .improvement-list li{margin:8px 0;padding-left:3px}
    .result-download{display:flex;align-items:center;justify-content:space-between;gap:18px;padding-top:18px;border-top:1px solid var(--line)}
    .result-download .meta{font-size:11.5px;color:var(--ink-soft);word-break:break-all}
    @media(max-width:640px){
      .result-download{align-items:stretch;flex-direction:column}
      .result-download .btn{justify-content:center}
      .form-actions{flex-direction:column}
      .form-actions .btn{width:100%}
      .github-box{align-items:stretch;flex-direction:column}
    }
  </style>
</head>
<body>
  <jsp:include page="includes/header.jsp"/>

  <main>
    <section class="git-report-wrap" aria-labelledby="git-report-title">
      <div class="test-nav">
        <a class="back" href="${ctx}/home">← 메인페이지</a>
      </div>

      <h1 class="page" id="git-report-title"><em>GitHub 프로젝트</em> 요약</h1>
      <p class="sub">GitHub에 로그인하고 분석할 저장소를 선택하세요.</p>

      <c:choose>
        <c:when test="${githubConnected}">
          <div class="github-box">
            <p><strong>@<c:out value="${githubLogin}"/></strong> 계정과 연결되었습니다.</p>
            <form class="inline-form" action="${ctx}/gitTest/github/disconnect" method="post">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
              <button class="btn ghost" type="submit">연결 해제</button>
            </form>
          </div>
        </c:when>
        <c:otherwise>
          <div class="github-box">
            <p>PDF를 만들려면 GitHub 로그인이 필요합니다.</p>
            <a class="btn ghost" href="${ctx}/gitTest/github/connect">GitHub 로그인</a>
          </div>
        </c:otherwise>
      </c:choose>
      
      <c:if test="${not empty githubError}">
        <div class="report-alert error" role="alert"><c:out value="${githubError}"/></div>
      </c:if>

      <c:if test="${githubConnected}">
        <form class="report-form" id="gitReportForm" action="${ctx}/gitTest/analyze" method="post">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
          <div class="report-field">
            <label for="repositoryUrl">분석할 저장소 <span>PUBLIC / PRIVATE</span></label>
            <select id="repositoryUrl" name="repositoryUrl" required>
              <option value="">저장소를 선택하세요</option>
              <c:forEach var="repo" items="${githubRepositories}">
                <option value="${fn:escapeXml(repo.url)}" ${repositoryUrl eq repo.url ? 'selected' : ''}>
                  <c:out value="${repo.name}"/><c:if test="${repo.privateRepository}"> (비공개)</c:if>
                </option>
              </c:forEach>
            </select>
          </div>
          <p class="form-help">ZIP에서 대표 파일 최대 5개만 텍스트로 읽습니다. 코드는 실행하지 않습니다.</p>
          <div class="form-actions">
            <button class="btn pri" id="submitButton" type="submit">요약 PDF 만들기</button>
          </div>
        </form>

        <div class="loading-panel" id="loadingPanel" role="status" aria-live="polite">
          <span class="spinner" aria-hidden="true"></span>
          <span>OpenAI가 프로젝트를 분석해 PDF를 생성합니다.</span>
        </div>
      </c:if>

      <c:if test="${not empty error}">
        <div class="report-alert error" role="alert"><c:out value="${error}"/></div>
      </c:if>

      <c:if test="${submitted and not empty reportResult}">
        <article class="report-result">
          <header class="result-hero">
            <div class="eyebrow">Analysis complete</div>
            <h2><c:out value="${reportResult.projectName}"/></h2>
            <p><c:out value="${reportResult.projectTopic}"/></p>
            <div class="repo"><c:out value="${reportResult.repositoryUrl}"/></div>
          </header>
          <div class="result-body">
            <div class="result-meta">
              <span>분석 파일 <c:out value="${reportResult.fileCount}"/>개</span>
              <span>기준 <c:out value="${reportResult.analyzedRef}"/></span>
              <span>PDF <c:out value="${reportResult.pageCount}"/>쪽</span>
              <c:if test="${not empty reportResult.timings}">
                <span>전체 <c:out value="${reportResult.timings.totalSeconds}"/>초</span>
              </c:if>
            </div>

            <div class="result-label">Project summary</div>
            <p class="result-summary"><c:out value="${reportResult.executiveSummary}"/></p>

            <c:if test="${not empty reportResult.techStack}">
              <div class="result-label">Tech stack</div>
              <div class="tech-list">
                <c:forEach var="tech" items="${reportResult.techStack}"><span><c:out value="${tech}"/></span></c:forEach>
              </div>
            </c:if>

            <div class="result-label">Improvements</div>
            <ol class="improvement-list">
              <c:forEach var="item" items="${reportResult.improvements}"><li><c:out value="${item}"/></li></c:forEach>
            </ol>

            <div class="result-download">
              <div class="meta">
                소스 <c:out value="${reportResult.analyzedCommit}"/>
                <c:if test="${not empty reportResult.timings}">
                  <br>저장소 검사 <c:out value="${reportResult.timings.inspectSeconds}"/>초 ·
                  AI <c:out value="${reportResult.timings.llmSeconds}"/>초 ·
                  PDF <c:out value="${reportResult.timings.pdfSeconds}"/>초
                </c:if>
              </div>
              <a class="btn pri" href="${ctx}/gitTest/reports/${fn:escapeXml(reportResult.reportId)}/pdf">PDF 다운로드</a>
            </div>
          </div>
        </article>
      </c:if>
    </section>
  </main>

  <jsp:include page="includes/footer.jsp"/>
  <script>
    (function () {
      var form = document.getElementById('gitReportForm');
      var submitButton = document.getElementById('submitButton');
      var loadingPanel = document.getElementById('loadingPanel');

      if (!form) {
        return;
      }

      form.addEventListener('submit', function () {
        submitButton.disabled = true;
        submitButton.textContent = '분석 중...';
        loadingPanel.classList.add('on');
      });
    }());
  </script>
</body>
</html>
