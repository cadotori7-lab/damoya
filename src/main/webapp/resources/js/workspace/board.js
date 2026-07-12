
  // ----- 업무 보드 (칸반) -----
  const MYID=1; // 데모: 로그인 사용자 = 김민재(팀원 인덱스 1)
  let boardFilter='all';
  const MEM=[{n:'이도현',c:'#2b46c8',i:'이'},{n:'김민재',c:'#2b46c8',i:'민'},{n:'서지우',c:'#0f9d8c',i:'서'},{n:'최윤서',c:'#c98a12',i:'최'}];
  const COLS=[
    {key:'등록',label:'등록',cc:'var(--grey)',chip:'done'},
    {key:'진행중',label:'진행중',cc:'var(--accent)',chip:'ing'},
    {key:'검수대기',label:'제출·검수 대기',cc:'var(--wait)',chip:'wait'},
    {key:'승인',label:'승인 완료',cc:'var(--ok)',chip:'approve'}
  ];
  let TASKS=[
    {id:1,t:'로그인·회원 인증 구현',d:'Spring Security 세션 인증',who:0,due:'08.12',status:'등록'},
    {id:2,t:'관리자 통계 화면',d:'집계 쿼리 + 대시보드',who:1,due:'08.20',status:'등록'},
    {id:3,t:'매칭 검색 필터 구현',d:'카테고리·학과·학년 동적 쿼리',who:1,due:'08.15',soon:1,status:'진행중'},
    {id:4,t:'신고·계정 관리 API',d:'관리자 기능',who:0,due:'08.22',status:'진행중'},
    {id:5,t:'ERD·DB 스키마 설계',d:'5개 핵심 테이블 + 참여/신고 관계까지 반영했습니다. 정규화 검토 부탁드려요.',who:0,due:'08.10',soon:1,status:'검수대기',file:['erd_schema_v2.pdf','1.4MB'],submittedAt:'08.10 제출'},
    {id:6,t:'지원·승인 API',d:'면접→승인 트랜잭션. 정원 초과 동시 요청은 비관적 락으로 막았고 테스트 코드 포함했습니다.',who:1,due:'08.14',status:'검수대기',file:['recruit_api.zip','320KB'],submittedAt:'08.13 제출'},
    {id:7,t:'화면 설계 (Figma)',d:'전체 15개 화면 와이어프레임입니다.',who:2,due:'08.08',status:'승인',file:['wireframe.fig','2.1MB'],submittedAt:'08.08 제출'},
    {id:8,t:'개발 환경 세팅',d:'Tomcat · Maven · Git',who:1,due:'08.07',status:'승인'},
    {id:9,t:'요구사항 정의서',d:'기능 명세서',who:3,due:'08.06',status:'승인',file:['requirements_v2.docx','92KB'],submittedAt:'08.06 제출'},
    {id:10,t:'회원 도메인 설계',d:'엔티티·매퍼 구조',who:0,due:'08.05',status:'승인'},
    {id:11,t:'프로젝트 CRUD API',d:'모집 등록·수정·조회',who:1,due:'08.09',status:'승인'},
    {id:12,t:'공통 레이아웃·헤더',d:'JSP 공통 화면',who:2,due:'08.05',status:'승인'},
    {id:13,t:'DB 초기 스키마 반영',d:'테이블 생성 스크립트',who:0,due:'08.04',status:'승인'}
  ];
  const APPROVED_LIMIT=5;
  let taskSearch='', approvedExpanded=false;
  function taskMatch(t,q){ return !q || (t.t+' '+(t.d||'')+' '+MEM[t.who].n).toLowerCase().includes(q); }
  function renderKanban(){
    const q=taskSearch.trim().toLowerCase();
    let src=boardFilter==='mine'?TASKS.filter(t=>t.who===MYID):TASKS;
    src=src.filter(t=>taskMatch(t,q));
    document.getElementById('kanban').innerHTML=COLS.map(c=>{
      let items=src.filter(t=>t.status===c.key);
      const total=items.length;
      let extra='';
      if(c.key==='승인' && !q && !approvedExpanded && total>APPROVED_LIMIT){
        items=items.slice(0,APPROVED_LIMIT);
        extra=`<button class="col-more" onclick="expandApproved()">지난 완료 ${total-APPROVED_LIMIT}개 더보기</button>`;
      } else if(c.key==='승인' && approvedExpanded && !q && total>APPROVED_LIMIT){
        extra=`<button class="col-more" onclick="collapseApproved()">완료 칸 접기</button>`;
      }
      return `<div class="col">
        <div class="col-h" style="--cc:${c.cc}">
          <div class="t"><span class="chip ${c.chip}">${c.label}</span></div>
          <div class="cnt">${total}</div>
        </div>
        ${items.map(t=>`
          <div class="task" onclick="openTask(${t.id})">
            <h5>${t.t}</h5><p>${t.d}</p>
            ${t.rejected?'<span class="chip reject" style="margin-bottom:8px">반려 · 재작업 필요</span>':''}
            <div class="task-foot">
              <span class="who-mini"><span class="pic" style="background:${MEM[t.who].c}">${MEM[t.who].i}</span>${MEM[t.who].n}</span>
              <span class="due${t.soon?' soon':''}">~${t.due}</span>
            </div>
          </div>`).join('')}
        ${extra}
        ${total===0?'<p class="col-empty">'+(q?'검색 결과 없음':'없음')+'</p>':''}
      </div>`;
    }).join('');
  }
  window.onTaskSearch=(v)=>{ taskSearch=v; approvedExpanded=false; renderKanban(); };
  window.expandApproved=()=>{ approvedExpanded=true; renderKanban(); };
  window.collapseApproved=()=>{ approvedExpanded=false; renderKanban(); };
  renderKanban();

  // ----- 업무 보드 토글 (전체/내업무/팀현황) -----
  document.querySelectorAll('#taskToggle button').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('#taskToggle button').forEach(x=>x.classList.remove('on'));
    b.classList.add('on');
    const p=b.dataset.p, team=p==='team';
    document.getElementById('boardPanel').style.display=team?'none':'';
    document.getElementById('teamPanel').style.display=team?'':'none';
    document.getElementById('boardTools').style.display=team?'none':'';
    if(!team){ boardFilter=p; renderKanban(); }
  });

  // ----- 업무 상세 + 검수 모달 -----
  const TSTAT={등록:'done',진행중:'ing',검수대기:'wait',승인:'approve'};
  const TSTATLABEL={등록:'등록',진행중:'진행중',검수대기:'검수 대기',승인:'승인 완료'};
  const fileIconT='<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/></svg>';
  let curTask=null;
  function openTask(id){
    const t=TASKS.find(x=>x.id===id); if(!t)return; curTask=id;
    const m=MEM[t.who];
    let sub='';
    if(t.file){
      sub=`<div class="submission"><div class="slabel">제출물 · 결과물</div>
        <div class="file">${fileIconT}${t.file[0]} <span class="sz">${t.file[1]}</span></div>
        ${t.d?`<p>${t.d}</p>`:''}</div>`;
    }
    let action='';
    if(t.status==='검수대기'){
      action=`<div class="sec-label" style="margin:2px 0 10px">팀장 검수</div>
        <div class="rv-actions">
          <button class="btn pri" onclick="taskApprove()">승인</button>
          <button class="btn ghost" onclick="taskToggleReason()">반려</button>
        </div>
        <div class="reason-box" id="taskReason">
          <textarea placeholder="반려 사유를 적어주세요. 담당자에게 전달돼요."></textarea>
          <div style="text-align:right;margin-top:8px"><button class="btn sm ghost" onclick="taskReject()">반려 확정</button></div>
        </div>`;
    } else if(t.status==='진행중' && t.who===MYID){
      action=(t.rejected&&t.reason?`<div class="reason-shown"><b>반려 사유</b> · ${t.reason}</div>`:'')+
        `<div class="rv-actions" style="margin-top:12px"><button class="btn pri" onclick="closeTask();openSubmit('${t.t}','${t.due}')">결과물 제출</button></div>`;
    } else if(t.status==='진행중' && t.rejected){
      action=`<div class="reason-shown"><b>반려됨</b> · ${t.reason||'보완 후 재제출 필요'}</div>`;
    } else if(t.status==='승인'){
      action=`<div class="rv-result ok">✓ 승인 완료된 업무예요</div>`;
    } else if(t.status==='등록'){
      action=`<div style="font-size:13px;color:var(--ink-soft)">아직 착수 전이에요. 담당자가 진행하면 업데이트돼요.</div>`;
    }
    document.getElementById('taskModalTitle').textContent=t.t;
    document.getElementById('taskModalBody').innerHTML=`
      <div class="tm-meta">
        <span class="chip ${TSTAT[t.status]}">${TSTATLABEL[t.status]}</span>
        <span class="who-mini"><span class="pic" style="background:${m.c}">${m.i}</span>${m.n}</span>
        <span class="tm-sub mono">마감 ~${t.due}${t.submittedAt?' · '+t.submittedAt:''}</span>
      </div>
      ${(!t.file&&t.d)?`<p class="tm-desc">${t.d}</p>`:''}
      ${sub}
      ${action}`;
    document.getElementById('taskModal').classList.add('on');
    document.body.style.overflow='hidden';
  }
  function closeTask(){document.getElementById('taskModal').classList.remove('on');document.body.style.overflow='';}
  function taskToggleReason(){document.getElementById('taskReason').classList.toggle('on');}
  function taskApprove(){const t=TASKS.find(x=>x.id===curTask);t.status='승인';t.rejected=false;renderKanban();closeTask();}
  function taskReject(){
    const t=TASKS.find(x=>x.id===curTask);
    const ta=document.querySelector('#taskReason textarea');
    t.reason=ta.value.trim()||'보완 후 다시 제출해주세요.'; t.status='진행중'; t.rejected=true; t.soon=0;
    renderKanban(); closeTask();
  }
  window.openTask=openTask; window.closeTask=closeTask; window.taskToggleReason=taskToggleReason;
  window.taskApprove=taskApprove; window.taskReject=taskReject;
  document.addEventListener('keydown',e=>{if(e.key==='Escape')closeTask();});

  // ----- 팀원 업무 상세 모달 -----
  const MEMBERS={
    '이도현':{pic:'이',c:'#2b46c8',role:'팀원 · 백엔드 · 컴퓨터공학 4학년',contrib:60,
      tasks:[
        {t:'로그인·회원 인증 구현',d:'Spring Security 세션 인증',st:'승인',due:'08.12'},
        {t:'권한 분기(전역×프로젝트)',d:'필터·인터셉터 접근 제어',st:'승인',due:'08.16'},
        {t:'개발 환경 세팅',d:'Tomcat·Maven·Git',st:'승인',due:'08.07'},
        {t:'ERD·DB 스키마 설계',d:'제출 완료 · 검수 대기',st:'검수 대기',due:'08.10'},
        {t:'신고·계정 관리 API',d:'관리자 기능',st:'진행중',due:'08.22'},
      ]},
    '김민재':{pic:'민',c:'#2b46c8',role:'팀원 · 백엔드 · 컴퓨터공학 4학년',contrib:50,
      tasks:[
        {t:'매칭 검색 필터 구현',d:'카테고리·학과·학년 동적 쿼리',st:'진행중',due:'08.15'},
        {t:'지원·승인 API',d:'면접→승인 트랜잭션 · 검수 대기',st:'검수 대기',due:'08.14'},
        {t:'로그인·회원 인증 구현',d:'Spring Security 세션 인증',st:'승인',due:'08.12'},
        {t:'관리자 통계 화면',d:'집계 쿼리 + 대시보드',st:'승인',due:'08.20'},
      ]},
    '서지우':{pic:'서',c:'#0f9d8c',role:'팀원 · 기획/디자인 · 경영학 3학년',contrib:100,
      tasks:[
        {t:'화면 설계 (Figma)',d:'전체 15개 화면 와이어프레임',st:'승인',due:'08.08'},
        {t:'요구사항 정의서',d:'반려 후 보완하여 재승인',st:'승인',due:'08.06'},
        {t:'서비스 소개·발표 자료',d:'중간 발표용',st:'승인',due:'08.18'},
      ]},
  };
  const MST={'승인':'approve','검수 대기':'wait','진행중':'ing','반려':'reject'};
  function openMember(name){
    const m=MEMBERS[name]; if(!m)return;
    document.getElementById('mmPic').textContent=m.pic;
    document.getElementById('mmPic').style.background=m.c;
    document.getElementById('mmName').textContent=name;
    document.getElementById('mmRole').textContent=m.role;
    const cnt={all:m.tasks.length,승인:0,'검수 대기':0,진행중:0};
    m.tasks.forEach(t=>cnt[t.st]!==undefined&&cnt[t.st]++);
    document.getElementById('mmStats').innerHTML=`
      <div class="ms"><div class="n">${cnt.all}</div><div class="k">배정</div></div>
      <div class="ms"><div class="n" style="color:var(--accent)">${cnt.진행중}</div><div class="k">진행중</div></div>
      <div class="ms"><div class="n" style="color:var(--wait)">${cnt['검수 대기']}</div><div class="k">검수 대기</div></div>
      <div class="ms"><div class="n" style="color:var(--ok)">${cnt.승인}</div><div class="k">완료</div></div>`;
    document.getElementById('mmTasks').innerHTML=m.tasks.map(t=>`
      <div class="task-line" style="--cc:var(--${MST[t.st]==='approve'?'ok':MST[t.st]==='wait'?'wait':MST[t.st]==='ing'?'accent':'reject'})">
        <div class="tl-main"><h4>${t.t}</h4><div class="tl-desc">${t.d}</div></div>
        <span class="chip ${MST[t.st]}">${t.st}</span>
        <span class="due">${t.due}</span>
      </div>`).join('');
    document.getElementById('memberModal').classList.add('on');
    document.body.style.overflow='hidden';
  }
  function closeMember(){
    document.getElementById('memberModal').classList.remove('on');
    document.body.style.overflow='';
  }
  window.openMember=openMember; window.closeMember=closeMember;
  document.addEventListener('keydown',e=>{if(e.key==='Escape')closeMember();});

  // ----- 결과물 제출 모달 -----
  const submitFormHTML=document.getElementById('submitBody').innerHTML;
  const fileIconSvg='<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/></svg>';
  function openSubmit(name,due){
    document.getElementById('submitBody').innerHTML=submitFormHTML; // 폼 복원
    if(name) document.getElementById('smTaskName').textContent=name;
    document.getElementById('smTaskDue').textContent='마감 '+(due||'08.15');
    document.getElementById('submitModal').classList.add('on');
    document.body.style.overflow='hidden';
  }
  function closeSubmit(){
    document.getElementById('submitModal').classList.remove('on');
    document.body.style.overflow='';
  }
  function pickFile(){
    // 데모: 파일 선택된 것처럼 표시
    document.getElementById('dropzone').style.display='none';
    document.getElementById('dzFileWrap').innerHTML=`
      <div class="dz-file">
        <span class="fi">${fileIconSvg}</span>
        <div class="fmeta"><div class="fn">search_filter.zip</div><div class="fs">280KB</div></div>
        <span class="rm" onclick="removeFile()" title="제거">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
        </span>
      </div>`;
  }
  function removeFile(){
    document.getElementById('dzFileWrap').innerHTML='';
    document.getElementById('dropzone').style.display='';
  }
  function doSubmit(){
    document.getElementById('submitBody').innerHTML=`
      <div class="submit-success">
        <div class="ok-ic"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg></div>
        <h3>제출 완료!</h3>
        <p>결과물이 제출됐어요.<br>이제 <b>팀장의 검수</b>를 기다리면 돼요. 결과는 알림으로 알려드릴게요.</p>
        <button class="btn pri" style="margin-top:20px" onclick="closeSubmit()">확인</button>
      </div>`;
  }
  window.openSubmit=openSubmit; window.closeSubmit=closeSubmit;
  window.pickFile=pickFile; window.removeFile=removeFile; window.doSubmit=doSubmit;
  document.addEventListener('keydown',e=>{if(e.key==='Escape')closeSubmit();});
