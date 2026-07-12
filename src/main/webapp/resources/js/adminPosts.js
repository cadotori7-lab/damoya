
// ----- 게시물 관리 테이블 -----
const CATCLR={공모전:'#2b46c8',학과:'#0f9d8c',교양:'#e07a45',교내활동:'#8256e0'};
const posts=[
    {title:'2026 캡스톤 경진대회 — AI 헬스케어 웹서비스',cat:'공모전',author:'최윤서',date:'08.03',reports:0,status:'게시중'},
    {title:'데이터베이스 텀 프로젝트 팀원 구해요',cat:'학과',author:'정하람',date:'08.05',reports:0,status:'게시중'},
    {title:'전국 대학생 연합 해커톤 참가팀',cat:'교내활동',author:'김상우',date:'08.02',reports:2,status:'게시중'},
    {title:'교양 〈창업과 경영〉 발표 조원 모집',cat:'교양',author:'박서연',date:'08.06',reports:0,status:'게시중'},
    {title:'[급구] 상금 나눠요 아무나 지원 ㄱㄱ',cat:'공모전',author:'user_2201',date:'08.04',reports:1,status:'숨김'},
    {title:'졸업작품 — Unity 게임 개발 팀원',cat:'학과',author:'오세훈',date:'08.01',reports:0,status:'게시중'},
  ];
const PSTAT={게시중:'approve',숨김:'wait',삭제:'reject'};
let postFilter='all';
function postActions(p,i){
    if(p.status==='게시중') return `<button class="btn sm ghost" onclick="setPost(${i},'숨김')">숨김</button><button class="btn sm ghost" onclick="setPost(${i},'삭제')" style="color:var(--reject)">삭제</button>`;
    if(p.status==='숨김') return `<button class="btn sm pri" onclick="setPost(${i},'게시중')">복원</button><button class="btn sm ghost" onclick="setPost(${i},'삭제')" style="color:var(--reject)">삭제</button>`;
    return `<button class="btn sm pri" onclick="setPost(${i},'게시중')">복원</button>`;
  }
function renderPosts(){
    const rows=posts.map((p,i)=>({p,i})).filter(({p})=>
      postFilter==='all'?true:postFilter==='reported'?p.reports>0:(p.status==='숨김'||p.status==='삭제'));
    document.getElementById('postTable').innerHTML=
      '<tr><th>게시물</th><th>작성자</th><th>게시일</th><th>신고</th><th>상태</th><th>관리</th></tr>'+
      rows.map(({p,i})=>`<tr${(p.status!=='게시중')?' style="opacity:.65"':''}>
        <td><div class="nm" style="display:flex;align-items:center;gap:8px"><span style="width:8px;height:8px;border-radius:2px;background:${CATCLR[p.cat]};flex:none"></span>${p.title}</div><div class="mono" style="font-size:11px;color:var(--ink-soft);margin-left:16px">${p.cat}</div></td>
        <td><div class="mono">${p.author}</div></td>
        <td><div class="mono">08.${p.date.split('.')[1]}</div></td>
        <td>${p.reports>0?`<span class="chip reject">${p.reports}</span>`:'<span style="color:var(--ink-soft)">—</span>'}</td>
        <td><span class="chip ${PSTAT[p.status]}">${p.status}</span></td>
        <td><div class="act-btns">${postActions(p,i)}</div></td>
      </tr>`).join('');
    // 탭 카운트
    const cAll=posts.length, cRep=posts.filter(p=>p.reports>0).length, cHid=posts.filter(p=>p.status!=='게시중').length;
    const tb=document.querySelectorAll('#postTabs button');
    tb[0].querySelector('.mono').textContent=cAll;
    tb[1].querySelector('.mono').textContent=cRep;
    tb[2].querySelector('.mono').textContent=cHid;
  }
  window.setPost=(i,st)=>{posts[i].status=st;renderPosts();};
  document.querySelectorAll('#postTabs button').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('#postTabs button').forEach(x=>x.classList.remove('on'));
    b.classList.add('on'); postFilter=b.dataset.f; renderPosts();
  });
renderPosts();