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
      <p class="sub">예: <code>로그인 화면 어디야?</code> · <code>웹 프로젝트에 맞는 멘토 추천해줘</code></p>

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

  const GREETING = '안녕하세요! 다모여 AI 도우미입니다. 사이트 안내나 프로젝트에 맞는 멘토 추천을 도와드려요.';

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

  async function sendChat(question) {
    sendButton.disabled = true;
    try {
      const response = await fetch(contextPath + '/test/chat/api', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ question })
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.detail || '요청에 실패했습니다.');
      }
      appendBubble('bot', data.answer, 'source: ' + (data.source || 'mcp'));
    } catch (error) {
      appendBubble('bot', '오류: ' + error.message);
    } finally {
      sendButton.disabled = false;
    }
  }

  appendBubble('bot', GREETING);

  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    const question = messageInput.value.trim();
    if (!question) return;
    appendBubble('user', question);
    messageInput.value = '';
    await sendChat(question);
  });

  resetButton.addEventListener('click', () => {
    log.innerHTML = '';
    linksBox.innerHTML = '';
    appendBubble('bot', GREETING);
  });
</script>
</body>
</html>
