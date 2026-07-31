// ----- 계정 관리 테이블 -----
const statusColor = {
  ACTIVE: '#2b46c8',
  SUSPENDED: '#8b1e1e',
  WITHDRAWN: '#888',
  PENDING: '#c48a1a'
};

const roleLabel = {
  ADMIN: '관리자',
  USER: '일반',
  MENTOR: '멘토'
};

const statusKey = {
  ACTIVE: 'active',
  SUSPENDED: 'suspended',
  WITHDRAWN: 'withdrawn',
  PENDING: 'pending'
};

function formatDate(value) {
  if (!value) return '—';
  return String(value).replace('T', ' ').slice(0, 10).replaceAll('-', '.');
}

const accs = (members || []).map(member => {
  const status = (member.account_status || '').toUpperCase();
  const role = roleLabel[member.role] || member.role || '';
  const school = [member.univ_name, member.dept_name].filter(Boolean).join(' · ') || '—';
  return {
    memberId: member.member_id,
    n: member.name || '',
    id: member.login_id || member.member_id,
    sch: school,
    role: role,
    date: formatDate(member.created_at),
    status: statusKey[status] || status.toLowerCase(),
    c: statusColor[status] || '#888'
  };
});

const stChip = {
  active: '<span class="chip approve">정상</span>',
  suspended: '<span class="chip reject">정지</span>',
  withdrawn: '<span class="chip">탈퇴</span>'
};

function escapeHtml(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;');
}

function actionForm(action, memberId, label, btnClass) {
  return `<form action="${ctx}/admin/accounts/${action}" method="post" style="display:inline">
    <input type="hidden" name="${escapeHtml(csrfParameter)}" value="${escapeHtml(csrfToken)}"/>
    <input type="hidden" name="memberId" value="${escapeHtml(memberId)}"/>
    <input type="hidden" name="page" value="${escapeHtml(currentPage)}"/>
    <input type="hidden" name="search" value="${escapeHtml(currentSearch)}"/>
    <input type="hidden" name="status" value="${escapeHtml(currentStatus)}"/>
    <input type="hidden" name="role" value="${escapeHtml(currentRole)}"/>
    <button type="submit" class="btn sm ${btnClass}">${label}</button>
  </form>`;
}

function renderAction(a) {
  if (a.role === '관리자') {
    return '<span style="color:var(--ink-soft);font-size:12.5px">—</span>';
  }
  if (a.status === 'active') {
    return actionForm('suspend', a.memberId, '계정 정지', 'ghost');
  }
  if (a.status === 'suspended') {
    return actionForm('resume', a.memberId, '정지 해제', 'pri');
  }
  if (a.status === 'withdrawn') {
    return '<span style="color:var(--ink-soft);font-size:12.5px">탈퇴</span>';
  }
  if (a.status === 'pending') {
    return actionForm('suspend', a.memberId, '계정 정지', 'ghost');
  }
  return '<span style="color:var(--ink-soft);font-size:12.5px">—</span>';
}

const table = document.getElementById('accTable');
if (!accs.length) {
  table.innerHTML =
    '<tr><th>회원</th><th>학교 / 학과</th><th>역할</th><th>가입일</th><th>상태</th><th>관리</th></tr>' +
    '<tr><td colspan="6" style="text-align:center;padding:28px;color:var(--ink-soft)">조건에 맞는 회원이 없습니다.</td></tr>';
} else {
  table.innerHTML =
    '<tr><th>회원</th><th>학교 / 학과</th><th>역할</th><th>가입일</th><th>상태</th><th>관리</th></tr>' +
    accs.map(a => {
      const name = escapeHtml(a.n);
      const loginId = escapeHtml(a.id);
      const school = escapeHtml(a.sch);
      const role = escapeHtml(a.role);
      const date = escapeHtml(a.date);
      return `<tr>
      <td><div class="u"><span class="pic" style="background:${a.c}">${(name[0] || '?')}</span><div><div class="nm">${name}</div><div class="mono" style="font-size:11.5px">@${loginId}</div></div></div></td>
      <td><div class="mono">${school}</div></td>
      <td><span class="rolebadge${a.role === '관리자' ? ' admin' : ''}">${role}</span></td>
      <td><div class="mono">${date}</div></td>
      <td>${stChip[a.status] || ''}</td>
      <td>${renderAction(a)}</td>
    </tr>`;
    }).join('');
}
