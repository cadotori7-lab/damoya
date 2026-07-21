<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 챗봇</title>
  <style>
    body { font-family: sans-serif; max-width: 720px; margin: 24px auto; padding: 0 16px; }
    #log {
      border: 1px solid #ddd; border-radius: 8px; padding: 14px;
      min-height: 260px; max-height: 420px; overflow-y: auto;
      background: #fafafa; white-space: pre-wrap; margin-bottom: 12px;
    }
    .bubble { margin: 10px 0; padding: 10px 12px; border-radius: 10px; }
    .bubble.bot { background: #fff; border: 1px solid #e5e5e5; }
    .bubble.user { background: #eef2ff; border: 1px solid #cdd6ff; text-align: right; }
    .bubble .meta { font-size: 11px; color: #888; margin-top: 6px; }
    #links a { display: block; margin: 6px 0; }
    textarea { width: 100%; box-sizing: border-box; }
    .hint { color: #666; font-size: 13px; margin-bottom: 10px; }
  </style>
</head>
<body>
  <h1>다모여 챗봇</h1>
  <p class="hint">예: <code>1</code> → 사이트 안내 → <code>로그인 화면을 보고 싶어</code></p>

  <div id="log"></div>
  <div id="links"></div>

  <form id="chatForm">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <textarea
      id="message"
      rows="3"
      maxlength="4000"
      placeholder="번호를 입력하거나 원하는 화면을 말씀해 주세요"
      required
    ></textarea>
    <br>
    <button id="sendButton" type="submit">전송</button>
    <button id="resetButton" type="button">처음부터</button>
  </form>

  <script>
    const form = document.getElementById('chatForm');
    const messageInput = document.getElementById('message');
    const sendButton = document.getElementById('sendButton');
    const resetButton = document.getElementById('resetButton');
    const log = document.getElementById('log');
    const linksBox = document.getElementById('links');
    const contextPath = '${pageContext.request.contextPath}';
    const csrfToken = '${_csrf.token}';
    const csrfHeader = '${_csrf.headerName}';

    let currentStep = 'menu';

    function appendBubble(role, text, meta) {
      const div = document.createElement('div');
      div.className = 'bubble ' + role;
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
        a.textContent = '바로가기: ' + link.title + ' — ' + link.url;
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

    // 페이지 로드 시 안내문(메뉴) 자동 표시
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
