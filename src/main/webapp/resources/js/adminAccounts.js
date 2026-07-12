
  // ----- 계정 관리 테이블 -----
const accs=[
    {n:'이도현',id:'dohyun_lee',sch:'대진대 · 컴퓨터공학',role:'일반',date:'2025.09.02',status:'ok',c:'#2b46c8'},
    {n:'서지우',id:'jiwoo_seo',sch:'대진대 · 경영학',role:'일반',date:'2025.09.10',status:'ok',c:'#0f9d8c'},
    {n:'박준호',id:'mentor_park',sch:'대진대 · 컴퓨터공학',role:'멘토',date:'2025.03.14',status:'ok',c:'#e07a45'},
    {n:'김상우',id:'sangwoo_k',sch:'가천대 · 소프트웨어',role:'일반',date:'2025.10.21',status:'suspended',c:'#8256e0'},
    {n:'관리자',id:'admin',sch:'—',role:'관리자',date:'2025.01.02',status:'ok',c:'#16233f'},
  ];
const stChip={ok:'<span class="chip approve">정상</span>',suspended:'<span class="chip reject">정지</span>'};
document.getElementById('accTable').innerHTML=
    '<tr><th>회원</th><th>학교 / 학과</th><th>역할</th><th>가입일</th><th>상태</th><th>관리</th></tr>'+
    accs.map(a=>`<tr>
      <td><div class="u"><span class="pic" style="background:${a.c}">${a.n[0]}</span><div><div class="nm">${a.n}</div><div class="mono" style="font-size:11.5px">@${a.id}</div></div></div></td>
      <td><div class="mono">${a.sch}</div></td>
      <td><span class="rolebadge${a.role==='관리자'?' admin':''}">${a.role}</span></td>
      <td><div class="mono">${a.date}</div></td>
      <td>${stChip[a.status]}</td>
      <td>${a.role==='관리자'?'<span style="color:var(--ink-soft);font-size:12.5px">—</span>':(a.status==='ok'?'<button class="btn sm ghost">계정 정지</button>':'<button class="btn sm pri">정지 해제</button>')}</td>
    </tr>`).join('');
