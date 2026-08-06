/* =========================================================
 * notification.js — 헤더 알림 모달 + 실시간 웹소켓 수신
 * ========================================================= */

(function () {
  var bell = document.querySelector('.bell');
  if (!bell) return; // 비로그인 상태에는 렌더링되지 않음

  var ctx = bell.getAttribute('data-ctx') || '';
  var memberId = bell.getAttribute('data-member-id');
  var listEl = document.getElementById('notiList');
  var dot = document.getElementById('notiDot');

  function kindLabel(kind) {
    if (kind === 'COMMENT1') return '프로젝트 댓글';
    if (kind === 'COMMENT2') return '인재풀 댓글';
    if (kind === 'APPLY') return '지원';
    if (kind === 'APPLY_APPROVED') return '지원 승인';
    if (kind === 'OFFER_RECEIVED') return '제안';
    if (kind === 'OFFER_ACCEPTED') return '제안 수락';
    return kind || '알림';
  }

  // kind별로 알림 클릭 시 이동할 위치. target_id가 없으면 이동하지 않는다.
  function targetUrl(n) {
    if (!n.target_id) return null;
    if (n.kind === 'COMMENT1') return ctx + '/project/detail?id=' + n.target_id;
    if (n.kind === 'COMMENT2') return ctx + '/talent/detail?id=' + n.target_id;
    if (n.kind === 'APPLY') return ctx + '/workspace/' + n.target_id + '/applicants';
    // 승인되는 순간 팀원(JOINED)이 되므로 워크스페이스로 바로 이동해도 된다.
    if (n.kind === 'APPLY_APPROVED') return ctx + '/workspace/' + n.target_id + '/overview';
    // 제안을 받은 사람은 아직 그 프로젝트 팀원이 아니라 워크스페이스 접근 권한이 없으므로 마이페이지로 보낸다.
    if (n.kind === 'OFFER_RECEIVED') return ctx + '/mypage/index';
    // 제안 수락 알림은 팀장(이미 팀원)에게 가므로 지원자 관리 페이지로 보낸다.
    if (n.kind === 'OFFER_ACCEPTED') return ctx + '/workspace/' + n.target_id + '/applicants';
    return null;
  }

  function renderItem(n) {
    var item = document.createElement('div');
    item.className = 'noti-item' + (n.is_read ? '' : ' unread');
    item.dataset.id = n.noti_id;

    var mainEl = document.createElement('div');
    mainEl.className = 'noti-main';

    var kindEl = document.createElement('div');
    kindEl.className = 'noti-kind';
    kindEl.textContent = kindLabel(n.kind);

    var contentEl = document.createElement('div');
    contentEl.className = 'noti-content';
    if(n.kind === 'COMMENT1') {
      contentEl.textContent = "(" + n.content + ") 게시글에 댓글이 작성되었습니다.";
    } else if(n.kind === 'COMMENT2') {
      contentEl.textContent = "(" + n.content + ") 인재풀 게시글에 댓글이 작성되었습니다.";
    } else if(n.kind === 'APPLY') {
      contentEl.textContent = "프로젝트 지원 알림: " + n.content;
    } else if(n.kind === 'APPLY_APPROVED') {
      contentEl.textContent = "지원 승인 알림: " + n.content;
    } else if(n.kind === 'OFFER_RECEIVED') {
      contentEl.textContent = "제안 알림: " + n.content;
    } else if(n.kind === 'OFFER_ACCEPTED') {
      contentEl.textContent = "제안 수락 알림: " + n.content;
    }

    mainEl.appendChild(kindEl);
    mainEl.appendChild(contentEl);

    var deleteBtn = document.createElement('button');
    deleteBtn.type = 'button';
    deleteBtn.className = 'noti-delete';
    deleteBtn.textContent = '삭제';
    deleteBtn.addEventListener('click', function (e) {
      e.stopPropagation();
      fetch(ctx + '/notification/delete/' + n.noti_id, { method: 'POST', credentials: 'same-origin' })
        .then(function () {
          item.remove();
          if (!listEl.querySelector('.noti-item')) {
            listEl.innerHTML = '<p style="color:var(--ink-soft);padding:16px;text-align:center">알림이 없어요.</p>';
          }
          updateDot();
        });
    });

    item.appendChild(mainEl);
    item.appendChild(deleteBtn);
    item.addEventListener('click', function () {
      markRead(n.noti_id, item);
      var url = targetUrl(n);
      if (url) location.href = url;
    });
    return item;
  }

  function updateDot() {
    var hasUnread = !!listEl.querySelector('.noti-item.unread');
    if (dot) dot.style.display = hasUnread ? '' : 'none';
  }

  function render(list) {
    listEl.innerHTML = '';
    if (!list || list.length === 0) {
      listEl.innerHTML = '<p style="color:var(--ink-soft);padding:16px;text-align:center">알림이 없어요.</p>';
    } else {
      list.forEach(function (n) { listEl.appendChild(renderItem(n)); });
    }
    updateDot();
  }

  function loadNotifications() {
    fetch(ctx + '/notification/list', { credentials: 'same-origin' })
      .then(function (res) { return res.json(); })
      .then(render)
      .catch(function () {
        listEl.innerHTML = '<p style="color:var(--ink-soft);padding:16px;text-align:center">알림을 불러오지 못했어요.</p>';
      });
  }

  function markRead(notiId, item) {
    if (!item.classList.contains('unread')) return;
    item.classList.remove('unread');
    updateDot();
    // keepalive: 읽음 처리 직후 다른 페이지로 이동해도 요청이 취소되지 않도록
    fetch(ctx + '/notification/read/' + notiId, { method: 'POST', credentials: 'same-origin', keepalive: true });
  }

  window.toggleNotifications = function () {
    openModal('notificationModal');
    loadNotifications();
  };

  // 뱃지 표시를 위해 페이지 진입 시 한 번 불러온다
  loadNotifications();

  // 실시간 알림 수신 (SockJS + STOMP)
  if (memberId && window.SockJS && window.Stomp) {
    var socket = new SockJS(ctx + '/ws');
    var stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function () {
      stompClient.subscribe('/user/queue/notifications/' + memberId, function (frame) {
        var body = JSON.parse(frame.body);
        var noti = body.message;
        if (listEl.querySelector('p')) listEl.innerHTML = ''; // "알림이 없어요" 문구 제거
        listEl.insertBefore(renderItem(noti), listEl.firstChild);
        updateDot();
      });
    });
  }
})();
