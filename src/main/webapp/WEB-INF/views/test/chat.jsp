<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — 챗봇</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>

<header class="lp-top">
  <div class="in">
    <a class="logo" href="${ctx}/">다<b>모여</b></a>
    <div class="r">
      <a class="btn ghost sm" href="${ctx}/home">홈</a>
      <a class="btn pri sm" href="${ctx}/auth/login">로그인</a>
    </div>
  </div>
</header>

<main>
  <section>
    <div class="chat-wrap">
      <div class="eyebrow">AI Assistant</div>
      <h1 class="page"><em>다모여</em> 챗봇</h1>
      <p class="sub">예: <code>1</code> → 사이트 안내 → <code>로그인 화면을 보고 싶어</code></p>

      <div class="chat-card">
        <div id="log" class="chat-log" aria-live="polite"></div>
        <div id="links" class="chat-links"></div>

        <form id="chatForm" class="chat-compose">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <textarea
            id="message"
            rows="3"
            maxlength="4000"
            placeholder="번호를 입력하거나 원하는 화면을 말씀해 주세요"
            required
          ></textarea>
          <div class="chat-actions">
            <button id="resetButton" type="button" class="btn ghost">처음부터</button>
            <button id="sendButton" type="submit" class="btn pri">전송</button>
          </div>
        </form>
      </div>
    </div>
  </section>
</main>

<jsp:include page="/WEB-INF/views/includes/footer.jsp" />

<script>
  const form = document.getElementById('chatForm');
  const messageInput = document.getElementById('message');
  const sendButton = document.getElementById('sendButton');
  const resetButton = document.getElementById('resetButton');
  const log = document.getElementById('log');
  const linksBox = document.getElementById('links');
  const contextPath = '${ctx}';
  const csrfToken = '${_csrf.token}';
  const csrfHeader = '${_csrf.headerName}';

  let currentStep = 'menu';

  function appendBubble(role, text, meta) {
    const div = document.createElement('div');
    div.className = 'chat-bubble ' + role;
    div.textContent = text;
    if (meta) {
      const m = document.createElement('div');
      m.className = 'meta';
      m.textContent = meta;
      div.appendChild(m);
    }
    log.appendChild(div);
    log.scrollTop = log.scrollHeight;
  }

  function renderLinks(links) {
    linksBox.innerHTML = '';
    if (!Array.isArray(links) || links.length === 0) return;
    links.forEach((link) => {
      const a = document.createElement('a');
      a.href = link.url;
      a.target = '_blank';
      a.rel = 'noopener noreferrer';
      a.textContent = link.title + ' 바로가기';
      const span = document.createElement('span');
      span.textContent = link.url;
      a.appendChild(span);
      linksBox.appendChild(a);
    });
  }

  async function sendChat(message) {
    sendButton.disabled = true;
    try {
      const response = await fetch(contextPath + '/test/chat/api', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ message, step: currentStep })
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.detail || '요청에 실패했습니다.');
      }
      currentStep = data.step || currentStep;
      appendBubble('bot', data.reply, 'step: ' + currentStep + ' / model: ' + data.model);
      renderLinks(data.links || []);
    } catch (error) {
      appendBubble('bot', '오류: ' + error.message);
    } finally {
      sendButton.disabled = false;
    }
  }

  sendChat('');

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const message = messageInput.value.trim();
    if (!message) return;
    appendBubble('user', message);
    messageInput.value = '';
    linksBox.innerHTML = '';
    await sendChat(message);
  });

  resetButton.addEventListener('click', async () => {
    log.innerHTML = '';
    linksBox.innerHTML = '';
    currentStep = 'menu';
    await sendChat('');
  });
</script>
</body>
</html>
