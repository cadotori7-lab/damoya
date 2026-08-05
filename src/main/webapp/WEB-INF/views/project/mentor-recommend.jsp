<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>멘토 추천 — <c:out value="${project.title}"/></title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
  <meta name="_csrf" content="${_csrf.token}"/>
  <meta name="_csrf_header" content="${_csrf.headerName}"/>
  <style>
    .ai-loading-overlay {
      position: fixed;
      inset: 0;
      z-index: 2000;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(248, 249, 252, 0.92);
      backdrop-filter: blur(6px);
    }
    .ai-loading-overlay[hidden] { display: none !important; }
    .ai-loading-card {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 18px;
      text-align: center;
      padding: 36px 40px;
    }
    .ai-logo {
      width: 72px;
      height: 72px;
      border-radius: 50%;
      background: #10a37f;
      display: grid;
      place-items: center;
      box-shadow: 0 10px 28px rgba(16, 163, 127, 0.28);
      animation: ai-pulse 1.6s ease-in-out infinite;
    }
    .ai-logo svg { width: 40px; height: 40px; fill: #fff; }
    .ai-loading-title {
      margin: 0;
      font-size: 20px;
      font-weight: 800;
      color: var(--ink);
      letter-spacing: -0.02em;
    }
    .ai-loading-sub {
      margin: 0;
      font-size: 14px;
      color: var(--ink-soft);
    }
    .ai-dots span {
      display: inline-block;
      width: 7px;
      height: 7px;
      margin: 0 3px;
      border-radius: 50%;
      background: #10a37f;
      animation: ai-dot 1.2s ease-in-out infinite;
    }
    .ai-dots span:nth-child(2) { animation-delay: 0.15s; }
    .ai-dots span:nth-child(3) { animation-delay: 0.3s; }
    @keyframes ai-pulse {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.06); }
    }
    @keyframes ai-dot {
      0%, 80%, 100% { opacity: 0.25; transform: translateY(0); }
      40% { opacity: 1; transform: translateY(-4px); }
    }
  </style>
</head>
<body>
  <jsp:include page="../includes/header.jsp" />

  <div id="aiLoading" class="ai-loading-overlay" <c:if test="${!autoMatch}">hidden</c:if>>
    <div class="ai-loading-card">
      <div class="ai-logo" aria-hidden="true">
        <!-- ChatGPT mark -->
        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
          <path d="M22.2819 9.8211a5.9847 5.9847 0 0 0-.5157-4.9108 6.0462 6.0462 0 0 0-6.5098-2.9A6.0651 6.0651 0 0 0 4.9807 4.1818a5.9847 5.9847 0 0 0-3.9977 2.9 6.0462 6.0462 0 0 0 .7427 7.0966 5.98 5.98 0 0 0 .5157 4.9107 6.051 6.051 0 0 0 6.5146 2.9001A5.9847 5.9847 0 0 0 13.2599 24a6.0557 6.0557 0 0 0 5.7718-4.2058 5.9894 5.9894 0 0 0 3.9977-2.9001 6.0557 6.0557 0 0 0-.7475-7.0729zm-9.022 12.6081a4.4755 4.4755 0 0 1-2.8764-1.0408l.1419-.0804 4.7783-2.7582a.7948.7948 0 0 0 .3927-.6813v-6.7369l2.02 1.1686a.071.071 0 0 1 .038.052v5.5826a4.504 4.504 0 0 1-4.4945 4.4944zm-9.6607-4.1254a4.4708 4.4708 0 0 1-.5346-3.0137l.142.0852 4.783 2.7582a.7712.7712 0 0 0 .7806 0l5.8428-3.3685v2.3324a.0804.0804 0 0 1-.0332.0615L9.74 19.9502a4.4992 4.4992 0 0 1-6.1408-1.6464zM2.3408 7.8956a4.485 4.485 0 0 1 2.3655-1.9728V11.6a.7664.7664 0 0 0 .3879.6765l5.8144 3.3543-2.0201 1.1685a.0757.0757 0 0 1-.071 0l-4.8303-2.7865A4.504 4.504 0 0 1 2.3408 7.872zm16.5963 3.8558L13.1038 8.364 15.1192 7.2a.0757.0757 0 0 1 .071 0l4.8303 2.7913a4.4944 4.4944 0 0 1-.6765 8.1042v-5.6772a.79.79 0 0 0-.407-.667zm2.0107-3.0231l-.1419-.0852-4.783-2.7622a.7759.7759 0 0 0-.7854 0L9.409 9.2297V6.8974a.0662.0662 0 0 1 .0284-.0615l4.8303-2.7866a4.4992 4.4992 0 0 1 6.6802 4.66zM8.3065 12.863l-2.02-1.1638a.0804.0804 0 0 1-.038-.052v-5.5864a4.4992 4.4992 0 0 1 7.3757-3.4537l-.142.0805L8.704 7.738a.7948.7948 0 0 0-.3927.6813zm1.0976-2.3654l2.602-1.4998 2.6069 1.4998v2.9994l-2.5974 1.4997-2.6067-1.4997Z"/>
        </svg>
      </div>
      <p class="ai-loading-title">AI 추천 중</p>
      <p class="ai-loading-sub">프로젝트에 맞는 멘토를 찾고 있어요</p>
      <div class="ai-dots" aria-hidden="true"><span></span><span></span><span></span></div>
    </div>
  </div>

  <main>
    <section style="max-width:860px;margin:40px auto;padding:0 20px;">
      <a class="back" href="${ctx}/project/detail?id=${project.projectId}">← 프로젝트 상세로</a>

      <div class="eyebrow">Mentor Match</div>
      <h1 class="page" style="margin-bottom:8px;"><em>멘토</em> 추천</h1>
      <p class="sub" style="margin-bottom:24px;">프로젝트 태그와 소개글을 바탕으로 AI가 어울리는 멘토를 찾아줘요.</p>

      <div class="panel" style="margin-bottom:20px;">
        <div class="cat" style="margin-bottom:8px;">요청 정보</div>
        <h2 style="font-size:22px;font-weight:800;margin:0 0 12px;"><c:out value="${projectName}"/></h2>
        <c:if test="${not empty project.tags}">
          <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:14px;">
            <c:forEach var="tag" items="${fn:split(project.tags, ',')}">
              <span class="tag"><c:out value="${tag}"/></span>
            </c:forEach>
          </div>
        </c:if>
        <div class="prose">
          <p style="margin:0 0 8px;color:var(--ink-soft);font-size:13px;">AI에 전달한 설명 (태그 + 소개)</p>
          <p style="margin:0;white-space:pre-wrap;"><c:out value="${projectDescription}"/></p>
        </div>
        <div style="margin-top:14px;display:flex;gap:8px;flex-wrap:wrap;">
          <button type="button" class="btn ghost sm" id="retryMatchBtn">다시 추천받기</button>
          <a class="btn pri sm" href="${ctx}/project/detail?id=${project.projectId}">상세로 돌아가기</a>
        </div>
      </div>

      <div id="errorBox" class="panel" style="margin-bottom:20px;border-color:#f0c2b0;background:#fff7f3;${empty error ? 'display:none;' : ''}">
        <h5 style="margin:0 0 8px;font-size:16px;font-weight:800;color:#c24b1f;">추천 실패</h5>
        <p id="errorText" style="margin:0;white-space:pre-wrap;color:#8a3a1d;"><c:out value="${error}"/></p>
      </div>

      <div id="resultBox" class="panel" style="display:none;">
        <h5 style="font-size:16px;font-weight:800;margin:0 0 6px;">추천 결과</h5>
        <p class="hint" id="resultMeta" style="margin:0 0 16px;"></p>
        <div id="resultList" style="display:flex;flex-direction:column;gap:12px;"></div>
      </div>
    </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />

  <script>
    (function () {
      var ctx = '${ctx}';
      var projectId = '${project.projectId}';
      var autoMatch = ${autoMatch == true};
      var loading = document.getElementById('aiLoading');
      var errorBox = document.getElementById('errorBox');
      var errorText = document.getElementById('errorText');
      var resultBox = document.getElementById('resultBox');
      var resultMeta = document.getElementById('resultMeta');
      var resultList = document.getElementById('resultList');
      var retryBtn = document.getElementById('retryMatchBtn');

      function escapeHtml(value) {
        return String(value == null ? '' : value)
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
          .replace(/"/g, '&quot;')
          .replace(/'/g, '&#39;');
      }

      function showLoading(show) {
        if (show) loading.removeAttribute('hidden');
        else loading.setAttribute('hidden', '');
      }

      function showError(message) {
        errorText.textContent = message || '알 수 없는 오류가 발생했습니다.';
        errorBox.style.display = '';
        resultBox.style.display = 'none';
      }

      function renderResult(matchResult) {
        errorBox.style.display = 'none';
        resultBox.style.display = '';
        var candidates = matchResult.candidateCount != null ? matchResult.candidateCount : '-';
        var indexed = matchResult.indexedCount != null ? matchResult.indexedCount : '-';
        resultMeta.innerHTML =
          '벡터 검색 후보 <span class="mono">' + escapeHtml(candidates) + '</span>명'
          + ' · 인덱싱 멘토 <span class="mono">' + escapeHtml(indexed) + '</span>명';

        var list = matchResult.recommendations || [];
        if (!list.length) {
          resultList.innerHTML = '<p style="margin:0;color:var(--ink-soft);">추천된 멘토가 없습니다.</p>';
          return;
        }

        resultList.innerHTML = list.map(function (mentor, index) {
          var cert = mentor.cert ? mentor.cert : '—';
          return ''
            + '<article class="panel" style="margin:0;padding:16px;background:var(--surface);">'
            +   '<div style="display:flex;justify-content:space-between;gap:12px;align-items:flex-start;flex-wrap:wrap;">'
            +     '<div>'
            +       '<div class="cat" style="margin-bottom:4px;">추천 ' + (index + 1) + '</div>'
            +       '<h3 style="margin:0;font-size:18px;font-weight:800;">'
            +         escapeHtml(mentor.name)
            +         ' <span class="mono" style="font-size:13px;color:var(--ink-soft);font-weight:600;">#'
            +         escapeHtml(mentor.memberId) + '</span>'
            +       '</h3>'
            +     '</div>'
            +     '<span class="chip recruit">유사도 ' + escapeHtml(mentor.similarityScore) + '</span>'
            +   '</div>'
            +   '<div class="d-meta" style="margin-top:14px;">'
            +     '<div><div class="k">전문 분야</div><div class="v">' + escapeHtml(mentor.field) + '</div></div>'
            +     '<div><div class="k">경력</div><div class="v">' + escapeHtml(mentor.career) + '</div></div>'
            +     '<div><div class="k">자격증</div><div class="v">' + escapeHtml(cert) + '</div></div>'
            +   '</div>'
            +   '<div class="prose" style="margin-top:12px;">'
            +     '<p style="margin:0 0 4px;font-size:13px;color:var(--ink-soft);">추천 이유</p>'
            +     '<p style="margin:0;">' + escapeHtml(mentor.reason) + '</p>'
            +   '</div>'
            +   '<div style="margin-top:14px;display:flex;justify-content:flex-end;">'
            +     '<button type="button" class="btn pri sm offer-mentor-btn" data-member-id="' + escapeHtml(mentor.memberId) + '" data-member-name="' + escapeHtml(mentor.name) + '">제안하기</button>'
            +   '</div>'
            + '</article>';
        }).join('');

        resultList.querySelectorAll('.offer-mentor-btn').forEach(function (btn) {
          btn.addEventListener('click', function () { offerMentor(btn); });
        });
      }

      var csrfMeta = document.querySelector('meta[name="_csrf"]');
      var csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

      function offerMentor(btn) {
        var mentorMemberId = btn.dataset.memberId;
        var mentorName = btn.dataset.memberName;
        if (!confirm(mentorName + ' 멘토에게 프로젝트 참여를 제안하시겠습니까?')) return;

        btn.disabled = true;
        var headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
        if (csrfMeta && csrfHeaderMeta) headers[csrfHeaderMeta.content] = csrfMeta.content;

        fetch(ctx + '/project/mentor-recommend/offer', {
          method: 'POST',
          headers: headers,
          credentials: 'same-origin',
          body: 'projectId=' + encodeURIComponent(projectId) + '&mentorMemberId=' + encodeURIComponent(mentorMemberId)
        })
          .then(function (res) {
            return res.json().then(function (data) { return { okHttp: res.ok, data: data }; });
          })
          .then(function (payload) {
            if (payload.okHttp && payload.data && payload.data.ok) {
              btn.textContent = '제안 완료';
            } else {
              btn.disabled = false;
              alert((payload.data && payload.data.error) || '제안에 실패했습니다.');
            }
          })
          .catch(function () {
            btn.disabled = false;
            alert('제안 요청 중 오류가 발생했습니다.');
          });
      }

      function requestMatch() {
        showLoading(true);
        errorBox.style.display = 'none';
        resultBox.style.display = 'none';

        fetch(ctx + '/project/mentor-recommend/api?id=' + encodeURIComponent(projectId), {
          headers: { 'Accept': 'application/json' }
        })
          .then(function (res) {
            return res.json().then(function (data) {
              return { okHttp: res.ok, data: data };
            });
          })
          .then(function (payload) {
            showLoading(false);
            if (!payload.data || payload.data.ok === false) {
              showError((payload.data && payload.data.error) || '멘토 추천에 실패했습니다.');
              return;
            }
            renderResult(payload.data.matchResult || {});
          })
          .catch(function () {
            showLoading(false);
            showError('멘토 추천 요청 중 오류가 발생했습니다.');
          });
      }

      if (retryBtn) {
        retryBtn.addEventListener('click', requestMatch);
      }

      if (autoMatch) {
        requestMatch();
      }
    })();
  </script>
</body>
</html>
