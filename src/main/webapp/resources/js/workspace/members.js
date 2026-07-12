
  // ----- 팀원 관리 (조직도 + 승계순위/강퇴) -----
  let team=[
    {name:'이도현',roleTxt:'백엔드',dept:'컴퓨터공학 · 4학년',pic:'이',c:'#2b46c8',rc:'var(--accent)'},
    {name:'김민재',roleTxt:'백엔드',dept:'컴퓨터공학 · 4학년',pic:'민',c:'#2b46c8',rc:'var(--accent)'},
    {name:'서지우',roleTxt:'기획/디자인',dept:'경영학 · 3학년',pic:'서',c:'#0f9d8c',rc:'var(--cat-major)'},
  ];
  let confirmKick=-1;
  function renderTeam(){
    // 조직도 노드
    const n=team.length;
    const bus=n>1?(50/n).toFixed(2)+'%':'50%';
    const row=document.getElementById('orgRow');
    row.style.setProperty('--bus',bus);
    row.style.visibility=n?'visible':'hidden';
    row.innerHTML=team.map((m,i)=>`
      <div class="org-col">
        <div class="org-node">
          <div class="succ" title="승계 우선순위 ${i+1}순위">${i+1}</div>
          <div class="pic" style="background:${m.c}">${m.pic}</div>
          <div class="nm">${m.name}</div>
          <div class="role" style="color:${m.rc}">팀원 · ${m.roleTxt}</div>
          <div class="dept">${m.dept}</div>
        </div>
      </div>`).join('') || '<p style="color:var(--ink-soft);padding:10px">팀원이 없어요.</p>';
    // 관리 리스트
    document.getElementById('tmList').innerHTML=team.map((m,i)=>`
      <div class="tm-row">
        <span class="pic" style="background:${m.c}">${m.pic}</span>
        <div class="tm-info"><div class="nm">${m.name}</div><div class="role">팀원 · ${m.roleTxt} · ${m.dept}</div></div>
        <div class="tm-succ">
          <span class="lbl">승계</span>
          <span class="rank">${i+1}</span>
          <div class="arrows">
            <button onclick="moveSucc(${i},-1)" ${i===0?'disabled':''} aria-label="위로">▲</button>
            <button onclick="moveSucc(${i},1)" ${i===team.length-1?'disabled':''} aria-label="아래로">▼</button>
          </div>
        </div>
        <div class="tm-kick">${
          confirmKick===i
          ? `<span class="tm-confirm">강퇴할까요? <button class="btn sm danger" onclick="kickMember(${i})">확인</button><button class="btn sm ghost" onclick="cancelKick()">취소</button></span>`
          : `<button class="btn sm danger" onclick="askKick(${i})">강퇴</button>`
        }</div>
      </div>`).join('');
  }
  window.moveSucc=(i,dir)=>{
    const j=i+dir; if(j<0||j>=team.length)return;
    [team[i],team[j]]=[team[j],team[i]]; confirmKick=-1; renderTeam();
  };
  window.askKick=(i)=>{confirmKick=i;renderTeam();};
  window.cancelKick=()=>{confirmKick=-1;renderTeam();};
  window.kickMember=(i)=>{team.splice(i,1);confirmKick=-1;renderTeam();};
  renderTeam();
