<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>챗봇 테스트</title>
</head>
<body>
  <h1>챗봇 테스트</h1>

  <form id="chatForm">
    <textarea
      id="message"
      rows="6"
      cols="60"
      maxlength="4000"
      placeholder="메시지를 입력하세요"
      required
    ></textarea>
    <br>
    <button id="sendButton" type="submit">전송</button>
  </form>

  <h2>응답</h2>
  <pre id="result">아직 응답이 없습니다.</pre>

  <script>
    const form = document.getElementById('chatForm');
    const messageInput = document.getElementById('message');
    const sendButton = document.getElementById('sendButton');
    const result = document.getElementById('result');
    const contextPath = '${pageContext.request.contextPath}';

    form.addEventListener('submit', async (event) => {
      event.preventDefault();

      const message = messageInput.value.trim();
      if (!message) {
        result.textContent = '메시지를 입력하세요.';
        return;
      }

      sendButton.disabled = true;
      result.textContent = '응답을 기다리는 중...';

      try {
        const response = await fetch(contextPath + '/test/chat/api', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ message })
        });

        const data = await response.json();
        if (!response.ok) {
          throw new Error(data.detail || '요청에 실패했습니다.');
        }

        result.textContent = data.reply + '\n\nmodel: ' + data.model;
      } catch (error) {
        result.textContent = '오류: ' + error.message;
      } finally {
        sendButton.disabled = false;
      }
    });
  </script>
</body>
</html>
