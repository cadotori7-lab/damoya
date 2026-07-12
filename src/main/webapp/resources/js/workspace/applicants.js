
  // ----- 모집 관리: 지원자 -----
  const applicants=[
    {n:'이도현',dept:'컴퓨터공학 · 4학년',want:'백엔드',pic:'#2b46c8',applied:'2일 전',status:'면접',
     motive:'Spring·MyBatis로 팀 프로젝트를 2번 해봤고, 캡스톤에 제대로 몰입하고 싶어 지원합니다. 트랜잭션·인증 파트를 맡고 싶어요.'},
    {n:'서지우',dept:'경영학 · 3학년',want:'기획·디자인',pic:'#0f9d8c',applied:'2일 전',status:'승인',
     motive:'Figma로 UI 작업이 가능하고, 서비스 기획 경험이 있습니다. 디자인·기획 파트로 참여하고 싶어요!'},
    {n:'한지민',dept:'소프트웨어 · 3학년',want:'프론트엔드',pic:'#8256e0',applied:'1일 전',status:'대기',
     motive:'React로 개인 프로젝트를 만들어봤어요. 실제 서비스 규모의 프론트를 경험하고 싶어 지원합니다.'},
    {n:'오세훈',dept:'컴퓨터공학 · 4학년',want:'백엔드',pic:'#c98a12',applied:'20시간 전',status:'대기',
     motive:'JPA·Spring 학습 중이고 배우며 기여하고 싶습니다. 성실하게 끝까지 참여할 자신 있습니다.'},
    {n:'윤가람',dept:'산업경영 · 2학년',want:'기획·디자인',pic:'#e07a45',applied:'3일 전',status:'거절',
     motive:'기획에 관심이 있어 지원했습니다.'},
  ];
  const STCLS={대기:'wait',면접:'interview',승인:'approve',거절:'reject'};
  let applFilter='all';
  function actionsFor(a,i){
    if(a.status==='대기') return `<button class="btn sm pri" onclick="setAppl(${i},'면접')">면접 제안</button><button class="btn sm ghost" onclick="setAppl(${i},'거절')">거절</button>`;
    if(a.status==='면접') return `<button class="btn sm pri" onclick="setAppl(${i},'승인')">승인</button><button class="btn sm ghost" onclick="setAppl(${i},'거절')">거절</button><div class="statusline">면접 진행 중</div>`;
    if(a.status==='승인') return `<div class="statusline" style="color:var(--ok);font-weight:700">✓ 팀원으로 등록됨</div>`;
    return `<div class="statusline">거절됨</div><button class="btn sm ghost" onclick="setAppl(${i},'대기')">되돌리기</button>`;
  }
  function renderAppl(){
    const list=applicants.map((a,i)=>({a,i})).filter(x=>applFilter==='all'||x.a.status===applFilter);
    document.getElementById('applList').innerHTML=list.map(({a,i})=>`
      <div class="appl-card${(a.status==='승인'||a.status==='거절')?' decided':''}">
        <span class="pic" style="background:${a.pic}">${a.n[0]}</span>
        <div class="ac-main">
          <div class="ac-top"><span class="nm">${a.n}</span><span class="dept">${a.dept}</span><span class="chip ${STCLS[a.status]}">${a.status}</span><span class="applied">지원 ${a.applied}</span></div>
          <div class="want">희망 분야 · ${a.want}</div>
          <div class="motive">${a.motive}</div>
        </div>
        <div class="ac-actions">${actionsFor(a,i)}</div>
      </div>`).join('') || '<p style="color:var(--ink-soft);padding:20px;text-align:center">해당 상태의 지원자가 없어요.</p>';
    // 탭 카운트 갱신
    const cnt={all:applicants.length,대기:0,면접:0,승인:0,거절:0};
    applicants.forEach(a=>cnt[a.status]++);
    document.querySelectorAll('#applTabs button').forEach(b=>{
      const f=b.dataset.f; b.querySelector('.mono').textContent=cnt[f];
    });
    const appr=applicants.filter(a=>a.status==='승인').length+1; // +1 팀장
    document.getElementById('recruitNum').innerHTML=`${appr}<small> / 4명 확정</small>`;
    document.getElementById('recruitFill').style.width=(appr/4*100)+'%';
  }
  window.setAppl=(i,st)=>{ applicants[i].status=st; renderAppl(); };
  document.querySelectorAll('#applTabs button').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('#applTabs button').forEach(x=>x.classList.remove('on'));
    b.classList.add('on'); applFilter=b.dataset.f; renderAppl();
  });
  renderAppl();
