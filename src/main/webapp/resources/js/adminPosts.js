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
