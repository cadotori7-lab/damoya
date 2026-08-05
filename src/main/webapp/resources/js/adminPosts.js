// ----- 게시물 관리 테이블 -----
const CATCLR = {
  공모전: '#2b46c8',
  학과: '#0f9d8c',
  교양: '#e07a45',
  교내활동: '#8256e0'
};

const PSTAT = { 게시중: 'approve', 숨김: 'wait', 삭제: 'reject' };
const PAGE_SIZE = 10;

function formatPostDate(value) {
  if (!value) return '—';
  const text = String(value).replace('T', ' ').slice(0, 10);
  const parts = text.split('-');
  if (parts.length === 3) {
    return parts[1] + '.' + parts[2];
  }
  return text;
}

function escapeHtml(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;');
}

function isProcessed(status) {
  return String(status || '').toUpperCase() === 'PROCESSED';
}

// report(targetType=PROJECT) → 게시물 단위로 집계
const postMap = new Map();
(reports || []).forEach(report => {
  const projectId = report.targetId;
  if (projectId == null) return;

  if (!postMap.has(projectId)) {
    postMap.set(projectId, {
      projectId,
      title: report.projectTitle || '(삭제된 게시물)',
      cat: report.projectCategory || '기타',
      author: report.memberName || '—',
      date: formatPostDate(report.projectCreatedAt),
      reports: 0,
      processedCount: 0
    });
  }
  const post = postMap.get(projectId);
  post.reports += 1;
  if (isProcessed(report.status)) {
    post.processedCount += 1;
  }
});

const posts = Array.from(postMap.values()).map(post => ({
  ...post,
  // 신고 status가 PROCESSED이면 숨김
  status: post.processedCount > 0 && post.processedCount === post.reports ? '숨김' : '게시중'
}));

let postFilter = 'all';
let postSearch = '';
let currentPage = 1;

function actionForm(action, projectId, label, btnClass, confirmMsg, btnStyle) {
  const onsubmit = confirmMsg
    ? ` onsubmit="return confirm('${confirmMsg}')"`
    : '';
  const styleAttr = btnStyle ? ` style="${btnStyle}"` : '';
  return `<form action="${ctx}/admin/posts/${action}" method="post" style="display:inline"${onsubmit}>
    <input type="hidden" name="${escapeHtml(csrfParameter)}" value="${escapeHtml(csrfToken)}"/>
    <input type="hidden" name="projectId" value="${escapeHtml(projectId)}"/>
    <button type="submit" class="btn sm ${btnClass}"${styleAttr}>${label}</button>
  </form>`;
}

function postActions(p) {
  const deleteBtn = actionForm('delete', p.projectId, '삭제', 'ghost', '게시물을 완전히 삭제할까요?', 'color:var(--reject)');
  if (p.status === '게시중') {
    return actionForm('hide', p.projectId, '숨김', 'ghost') + deleteBtn;
  }
  return actionForm('restore', p.projectId, '복원', 'pri') + deleteBtn;
}

function matchesSearch(p) {
  if (!postSearch) return true;
  const q = postSearch.toLowerCase();
  return (p.title || '').toLowerCase().includes(q)
    || (p.author || '').toLowerCase().includes(q);
}

function getFilteredPosts() {
  return posts.filter(p => {
    if (!matchesSearch(p)) return false;
    if (postFilter === 'all') return true;
    if (postFilter === 'reported') return p.reports > 0 && p.status === '게시중';
    return p.status === '숨김';
  });
}

function pageLinkStyle(active) {
  if (active) {
    return 'padding: 8px 14px; border: 1px solid var(--ink); border-radius: 6px; text-decoration: none; background-color: var(--ink); color: #fff;';
  }
  return 'padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none; color: var(--ink-soft); cursor:pointer;';
}

function renderPagination(totalCount) {
  const nav = document.getElementById('postPagination');
  if (!nav) return;

  const pageCnt = Math.max(1, Math.ceil(totalCount / PAGE_SIZE));
  if (currentPage > pageCnt) currentPage = pageCnt;
  if (currentPage < 1) currentPage = 1;

  if (totalCount === 0) {
    nav.innerHTML = '';
    return;
  }

  const blockSize = 10;
  const min = Math.floor((currentPage - 1) / blockSize) * blockSize + 1;
  let max = min + blockSize - 1;
  if (max > pageCnt) max = pageCnt;

  let html = '';
  if (min > 1) {
    html += `<li class="page-item"><a data-page="${min - 1}" style="${pageLinkStyle(false)}">이전</a></li>`;
  }
  for (let pageNum = min; pageNum <= max; pageNum++) {
    html += `<li class="page-item${pageNum === currentPage ? ' active' : ''}">
      <a data-page="${pageNum}" style="${pageLinkStyle(pageNum === currentPage)}">${pageNum}</a>
    </li>`;
  }
  if (max < pageCnt) {
    html += `<li class="page-item"><a data-page="${max + 1}" style="${pageLinkStyle(false)}">다음</a></li>`;
  }
  nav.innerHTML = html;

  nav.querySelectorAll('a[data-page]').forEach(a => {
    a.addEventListener('click', e => {
      e.preventDefault();
      currentPage = Number(a.dataset.page) || 1;
      renderPosts();
    });
  });
}

function renderPosts() {
  const filtered = getFilteredPosts();
  const pageCnt = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  if (currentPage > pageCnt) currentPage = pageCnt;

  const start = (currentPage - 1) * PAGE_SIZE;
  const rows = filtered.slice(start, start + PAGE_SIZE);

  const table = document.getElementById('postTable');
  if (!filtered.length) {
    table.innerHTML =
      '<tr><th>게시물</th><th>작성자</th><th>게시일</th><th>신고</th><th>상태</th><th>관리</th></tr>' +
      '<tr><td colspan="6" style="text-align:center;padding:28px;color:var(--ink-soft)">조건에 맞는 게시물이 없습니다.</td></tr>';
  } else {
    table.innerHTML =
      '<tr><th>게시물</th><th>작성자</th><th>게시일</th><th>신고</th><th>상태</th><th>관리</th></tr>' +
      rows.map(p => {
        const title = escapeHtml(p.title);
        const cat = escapeHtml(p.cat);
        const author = escapeHtml(p.author);
        const date = escapeHtml(p.date);
        const status = escapeHtml(p.status);
        const catColor = CATCLR[p.cat] || '#888';
        return `<tr${p.status !== '게시중' ? ' style="opacity:.65"' : ''}>
        <td><div class="nm" style="display:flex;align-items:center;gap:8px"><span style="width:8px;height:8px;border-radius:2px;background:${catColor};flex:none"></span>${title}</div><div class="mono" style="font-size:11px;color:var(--ink-soft);margin-left:16px">${cat}</div></td>
        <td><div class="mono">${author}</div></td>
        <td><div class="mono">${date}</div></td>
        <td>${p.reports > 0 ? `<span class="chip reject">${p.reports}</span>` : '<span style="color:var(--ink-soft)">—</span>'}</td>
        <td><span class="chip ${PSTAT[p.status] || ''}">${status}</span></td>
        <td><div class="act-btns">${postActions(p)}</div></td>
      </tr>`;
      }).join('');
  }

  renderPagination(filtered.length);

  const cAll = posts.filter(matchesSearch).length;
  const cRep = posts.filter(p => matchesSearch(p) && p.reports > 0 && p.status === '게시중').length;
  const cHid = posts.filter(p => matchesSearch(p) && p.status === '숨김').length;
  const tb = document.querySelectorAll('#postTabs button');
  if (tb[0]) tb[0].querySelector('.mono').textContent = cAll;
  if (tb[1]) tb[1].querySelector('.mono').textContent = cRep;
  if (tb[2]) tb[2].querySelector('.mono').textContent = cHid;
}

document.querySelectorAll('#postTabs button').forEach(b => {
  b.onclick = () => {
    document.querySelectorAll('#postTabs button').forEach(x => x.classList.remove('on'));
    b.classList.add('on');
    postFilter = b.dataset.f;
    currentPage = 1;
    renderPosts();
  };
});

const searchInput = document.getElementById('postSearch');
if (searchInput) {
  searchInput.addEventListener('input', () => {
    postSearch = searchInput.value.trim();
    currentPage = 1;
    renderPosts();
  });
}

renderPosts();

// ----- 댓글 신고 관리 테이블 -----
const COMMENT_PAGE_SIZE = 10;

function isCommentProcessed(status) {
  return String(status || '').toUpperCase() === 'PROCESSED';
}

// report(targetType=COMMENT) → 댓글 단위로 집계
const commentMap = new Map();
(commentReports || []).forEach(report => {
  const commentId = report.targetId;
  if (commentId == null) return;

  if (!commentMap.has(commentId)) {
    commentMap.set(commentId, {
      commentId,
      content: report.commentContent || '(삭제된 댓글)',
      projectTitle: report.projectTitle || '(삭제된 게시물)',
      author: report.memberName || '—',
      date: formatPostDate(report.commentCreatedAt),
      reports: 0,
      processedCount: 0
    });
  }
  const comment = commentMap.get(commentId);
  comment.reports += 1;
  if (isCommentProcessed(report.status)) {
    comment.processedCount += 1;
  }
});

const commentRows = Array.from(commentMap.values()).map(c => ({
  ...c,
  status: c.processedCount > 0 && c.processedCount === c.reports ? '처리완료' : '미처리'
}));

let commentFilter = 'all';
let commentSearch = '';
let commentPage = 1;

function commentActionForm(action, commentId, label, btnClass, confirmMsg, btnStyle) {
  const onsubmit = confirmMsg
    ? ` onsubmit="return confirm('${confirmMsg}')"`
    : '';
  const styleAttr = btnStyle ? ` style="${btnStyle}"` : '';
  return `<form action="${ctx}/admin/comments/${action}" method="post" style="display:inline"${onsubmit}>
    <input type="hidden" name="${escapeHtml(csrfParameter)}" value="${escapeHtml(csrfToken)}"/>
    <input type="hidden" name="commentId" value="${escapeHtml(commentId)}"/>
    <button type="submit" class="btn sm ${btnClass}"${styleAttr}>${label}</button>
  </form>`;
}

function commentActions(c) {
  const deleteBtn = commentActionForm('delete', c.commentId, '삭제', 'ghost', '댓글을 완전히 삭제할까요?', 'color:var(--reject)');
  if (c.status === '처리완료') {
    return commentActionForm('reopen', c.commentId, '재검토', 'pri') + deleteBtn;
  }
  return commentActionForm('resolve', c.commentId, '처리완료', 'ghost') + deleteBtn;
}

function matchesCommentSearch(c) {
  if (!commentSearch) return true;
  const q = commentSearch.toLowerCase();
  return (c.content || '').toLowerCase().includes(q)
    || (c.author || '').toLowerCase().includes(q);
}

function getFilteredComments() {
  return commentRows.filter(c => {
    if (!matchesCommentSearch(c)) return false;
    if (commentFilter === 'all') return true;
    if (commentFilter === 'reported') return c.reports > 0 && c.status === '미처리';
    return c.status === '처리완료';
  });
}

function renderCommentPagination(totalCount) {
  const nav = document.getElementById('commentPagination');
  if (!nav) return;

  const pageCnt = Math.max(1, Math.ceil(totalCount / COMMENT_PAGE_SIZE));
  if (commentPage > pageCnt) commentPage = pageCnt;
  if (commentPage < 1) commentPage = 1;

  if (totalCount === 0) {
    nav.innerHTML = '';
    return;
  }

  const blockSize = 10;
  const min = Math.floor((commentPage - 1) / blockSize) * blockSize + 1;
  let max = min + blockSize - 1;
  if (max > pageCnt) max = pageCnt;

  let html = '';
  if (min > 1) {
    html += `<li class="page-item"><a data-page="${min - 1}" style="${pageLinkStyle(false)}">이전</a></li>`;
  }
  for (let pageNum = min; pageNum <= max; pageNum++) {
    html += `<li class="page-item${pageNum === commentPage ? ' active' : ''}">
      <a data-page="${pageNum}" style="${pageLinkStyle(pageNum === commentPage)}">${pageNum}</a>
    </li>`;
  }
  if (max < pageCnt) {
    html += `<li class="page-item"><a data-page="${max + 1}" style="${pageLinkStyle(false)}">다음</a></li>`;
  }
  nav.innerHTML = html;

  nav.querySelectorAll('a[data-page]').forEach(a => {
    a.addEventListener('click', e => {
      e.preventDefault();
      commentPage = Number(a.dataset.page) || 1;
      renderComments();
    });
  });
}

function renderComments() {
  const filtered = getFilteredComments();
  const pageCnt = Math.max(1, Math.ceil(filtered.length / COMMENT_PAGE_SIZE));
  if (commentPage > pageCnt) commentPage = pageCnt;

  const start = (commentPage - 1) * COMMENT_PAGE_SIZE;
  const rows = filtered.slice(start, start + COMMENT_PAGE_SIZE);

  const table = document.getElementById('commentTable');
  if (!table) return;
  if (!filtered.length) {
    table.innerHTML =
      '<tr><th>댓글</th><th>작성자</th><th>작성일</th><th>신고</th><th>상태</th><th>관리</th></tr>' +
      '<tr><td colspan="6" style="text-align:center;padding:28px;color:var(--ink-soft)">조건에 맞는 댓글이 없습니다.</td></tr>';
  } else {
    table.innerHTML =
      '<tr><th>댓글</th><th>작성자</th><th>작성일</th><th>신고</th><th>상태</th><th>관리</th></tr>' +
      rows.map(c => {
        const content = escapeHtml(c.content);
        const projectTitle = escapeHtml(c.projectTitle);
        const author = escapeHtml(c.author);
        const date = escapeHtml(c.date);
        const status = escapeHtml(c.status);
        return `<tr${c.status === '처리완료' ? ' style="opacity:.65"' : ''}>
        <td><div class="nm">${content}</div><div class="mono" style="font-size:11px;color:var(--ink-soft)">${projectTitle}</div></td>
        <td><div class="mono">${author}</div></td>
        <td><div class="mono">${date}</div></td>
        <td>${c.reports > 0 ? `<span class="chip reject">${c.reports}</span>` : '<span style="color:var(--ink-soft)">—</span>'}</td>
        <td><span class="chip ${status === '처리완료' ? 'approve' : 'wait'}">${status}</span></td>
        <td><div class="act-btns">${commentActions(c)}</div></td>
      </tr>`;
      }).join('');
  }

  renderCommentPagination(filtered.length);

  const cAll = commentRows.filter(matchesCommentSearch).length;
  const cRep = commentRows.filter(c => matchesCommentSearch(c) && c.reports > 0 && c.status === '미처리').length;
  const cDone = commentRows.filter(c => matchesCommentSearch(c) && c.status === '처리완료').length;
  const tb = document.querySelectorAll('#commentTabs button');
  if (tb[0]) tb[0].querySelector('.mono').textContent = cAll;
  if (tb[1]) tb[1].querySelector('.mono').textContent = cRep;
  if (tb[2]) tb[2].querySelector('.mono').textContent = cDone;
}

document.querySelectorAll('#commentTabs button').forEach(b => {
  b.onclick = () => {
    document.querySelectorAll('#commentTabs button').forEach(x => x.classList.remove('on'));
    b.classList.add('on');
    commentFilter = b.dataset.f;
    commentPage = 1;
    renderComments();
  };
});

const commentSearchInput = document.getElementById('commentSearch');
if (commentSearchInput) {
  commentSearchInput.addEventListener('input', () => {
    commentSearch = commentSearchInput.value.trim();
    commentPage = 1;
    renderComments();
  });
}

renderComments();

// ----- 인재풀 신고 관리 테이블 -----
const TALENT_PAGE_SIZE = 10;

function isTalentProcessed(status) {
  return String(status || '').toUpperCase() === 'PROCESSED';
}

// report(targetType=POST) → 인재풀 게시글 단위로 집계
const talentMap = new Map();
(talentReports || []).forEach(report => {
  const postId = report.targetId;
  if (postId == null) return;

  if (!talentMap.has(postId)) {
    talentMap.set(postId, {
      postId,
      title: report.talentTitle || '(삭제된 게시물)',
      cat: report.talentCategory || '기타',
      author: report.memberName || '—',
      date: formatPostDate(report.talentCreatedAt),
      reports: 0,
      processedCount: 0
    });
  }
  const talent = talentMap.get(postId);
  talent.reports += 1;
  if (isTalentProcessed(report.status)) {
    talent.processedCount += 1;
  }
});

const talentRows = Array.from(talentMap.values()).map(t => ({
  ...t,
  status: t.processedCount > 0 && t.processedCount === t.reports ? '처리완료' : '미처리'
}));

let talentFilter = 'all';
let talentSearch = '';
let talentPage = 1;

function talentActionForm(action, postId, label, btnClass, confirmMsg, btnStyle) {
  const onsubmit = confirmMsg
    ? ` onsubmit="return confirm('${confirmMsg}')"`
    : '';
  const styleAttr = btnStyle ? ` style="${btnStyle}"` : '';
  return `<form action="${ctx}/admin/talents/${action}" method="post" style="display:inline"${onsubmit}>
    <input type="hidden" name="${escapeHtml(csrfParameter)}" value="${escapeHtml(csrfToken)}"/>
    <input type="hidden" name="postId" value="${escapeHtml(postId)}"/>
    <button type="submit" class="btn sm ${btnClass}"${styleAttr}>${label}</button>
  </form>`;
}

function talentActions(t) {
  const deleteBtn = talentActionForm('delete', t.postId, '삭제', 'ghost', '게시글을 완전히 삭제할까요?', 'color:var(--reject)');
  if (t.status === '처리완료') {
    return talentActionForm('reopen', t.postId, '재검토', 'pri') + deleteBtn;
  }
  return talentActionForm('resolve', t.postId, '처리완료', 'ghost') + deleteBtn;
}

function matchesTalentSearch(t) {
  if (!talentSearch) return true;
  const q = talentSearch.toLowerCase();
  return (t.title || '').toLowerCase().includes(q)
    || (t.author || '').toLowerCase().includes(q);
}

function getFilteredTalents() {
  return talentRows.filter(t => {
    if (!matchesTalentSearch(t)) return false;
    if (talentFilter === 'all') return true;
    if (talentFilter === 'reported') return t.reports > 0 && t.status === '미처리';
    return t.status === '처리완료';
  });
}

function renderTalentPagination(totalCount) {
  const nav = document.getElementById('talentPagination');
  if (!nav) return;

  const pageCnt = Math.max(1, Math.ceil(totalCount / TALENT_PAGE_SIZE));
  if (talentPage > pageCnt) talentPage = pageCnt;
  if (talentPage < 1) talentPage = 1;

  if (totalCount === 0) {
    nav.innerHTML = '';
    return;
  }

  const blockSize = 10;
  const min = Math.floor((talentPage - 1) / blockSize) * blockSize + 1;
  let max = min + blockSize - 1;
  if (max > pageCnt) max = pageCnt;

  let html = '';
  if (min > 1) {
    html += `<li class="page-item"><a data-page="${min - 1}" style="${pageLinkStyle(false)}">이전</a></li>`;
  }
  for (let pageNum = min; pageNum <= max; pageNum++) {
    html += `<li class="page-item${pageNum === talentPage ? ' active' : ''}">
      <a data-page="${pageNum}" style="${pageLinkStyle(pageNum === talentPage)}">${pageNum}</a>
    </li>`;
  }
  if (max < pageCnt) {
    html += `<li class="page-item"><a data-page="${max + 1}" style="${pageLinkStyle(false)}">다음</a></li>`;
  }
  nav.innerHTML = html;

  nav.querySelectorAll('a[data-page]').forEach(a => {
    a.addEventListener('click', e => {
      e.preventDefault();
      talentPage = Number(a.dataset.page) || 1;
      renderTalents();
    });
  });
}

function renderTalents() {
  const filtered = getFilteredTalents();
  const pageCnt = Math.max(1, Math.ceil(filtered.length / TALENT_PAGE_SIZE));
  if (talentPage > pageCnt) talentPage = pageCnt;

  const start = (talentPage - 1) * TALENT_PAGE_SIZE;
  const rows = filtered.slice(start, start + TALENT_PAGE_SIZE);

  const table = document.getElementById('talentTable');
  if (!table) return;
  if (!filtered.length) {
    table.innerHTML =
      '<tr><th>게시물</th><th>작성자</th><th>작성일</th><th>신고</th><th>상태</th><th>관리</th></tr>' +
      '<tr><td colspan="6" style="text-align:center;padding:28px;color:var(--ink-soft)">조건에 맞는 게시물이 없습니다.</td></tr>';
  } else {
    table.innerHTML =
      '<tr><th>게시물</th><th>작성자</th><th>작성일</th><th>신고</th><th>상태</th><th>관리</th></tr>' +
      rows.map(t => {
        const title = escapeHtml(t.title);
        const cat = escapeHtml(t.cat);
        const author = escapeHtml(t.author);
        const date = escapeHtml(t.date);
        const status = escapeHtml(t.status);
        return `<tr${t.status === '처리완료' ? ' style="opacity:.65"' : ''}>
        <td><div class="nm">${title}</div><div class="mono" style="font-size:11px;color:var(--ink-soft)">${cat}</div></td>
        <td><div class="mono">${author}</div></td>
        <td><div class="mono">${date}</div></td>
        <td>${t.reports > 0 ? `<span class="chip reject">${t.reports}</span>` : '<span style="color:var(--ink-soft)">—</span>'}</td>
        <td><span class="chip ${status === '처리완료' ? 'approve' : 'wait'}">${status}</span></td>
        <td><div class="act-btns">${talentActions(t)}</div></td>
      </tr>`;
      }).join('');
  }

  renderTalentPagination(filtered.length);

  const cAll = talentRows.filter(matchesTalentSearch).length;
  const cRep = talentRows.filter(t => matchesTalentSearch(t) && t.reports > 0 && t.status === '미처리').length;
  const cDone = talentRows.filter(t => matchesTalentSearch(t) && t.status === '처리완료').length;
  const tb = document.querySelectorAll('#talentTabs button');
  if (tb[0]) tb[0].querySelector('.mono').textContent = cAll;
  if (tb[1]) tb[1].querySelector('.mono').textContent = cRep;
  if (tb[2]) tb[2].querySelector('.mono').textContent = cDone;
}

document.querySelectorAll('#talentTabs button').forEach(b => {
  b.onclick = () => {
    document.querySelectorAll('#talentTabs button').forEach(x => x.classList.remove('on'));
    b.classList.add('on');
    talentFilter = b.dataset.f;
    talentPage = 1;
    renderTalents();
  };
});

const talentSearchInput = document.getElementById('talentSearch');
if (talentSearchInput) {
  talentSearchInput.addEventListener('input', () => {
    talentSearch = talentSearchInput.value.trim();
    talentPage = 1;
    renderTalents();
  });
}

renderTalents();

// ----- 게시물/댓글/인재풀 신고 유형 전환 탭 -----
document.querySelectorAll('#reportTypeTabs button').forEach(b => {
  b.onclick = () => {
    document.querySelectorAll('#reportTypeTabs button').forEach(x => x.classList.remove('on'));
    b.classList.add('on');
    document.getElementById('postSection').style.display = b.dataset.type === 'post' ? '' : 'none';
    document.getElementById('commentSection').style.display = b.dataset.type === 'comment' ? '' : 'none';
    document.getElementById('talentSection').style.display = b.dataset.type === 'talent' ? '' : 'none';
  };
});
