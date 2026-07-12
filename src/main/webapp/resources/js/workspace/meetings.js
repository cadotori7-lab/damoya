
  // ----- 회의 목록/회의록 -----
  let MEETS=[
    {dt:'08.24 (월) 19:00',date:'2026-08-24',time:'19:00',t:'2차 스프린트 회의',sum:'관리자·통계 기능 분담',att:[['#0f9d8c','도'],['#2b46c8','민'],['#e07a45','서'],['#8256e0','윤']],
     content:'[안건]\n- 관리자·통계 기능 담당 분담\n- 2차 스프린트 범위 확정\n- 배포 환경(Tomcat) 점검\n\n[결정 사항]\n- 통계 대시보드는 민재, 신고·계정 관리는 윤서가 담당\n- 08/28부터 통합 테스트 시작, 그 전까지 기능 개발 마무리'},
    {dt:'08.17 (월) 19:00',date:'2026-08-17',time:'19:00',t:'중간 점검 회의',sum:'진행 상황 공유, 블로커 논의',att:[['#0f9d8c','도'],['#2b46c8','민'],['#8256e0','윤']],
     content:'[안건]\n- 각자 진행 상황 공유\n- 지원–승인 트랜잭션 블로커 논의\n\n[결정 사항]\n- 정원 동시성 처리는 비관적 락으로 우선 구현 후 리뷰\n- 멘토님께 중간 피드백 요청드리기'},
    {dt:'08.10 (월) 19:00',date:'2026-08-10',time:'19:00',t:'킥오프 회의',sum:'역할 분담과 일정 확정',att:[['#0f9d8c','도'],['#2b46c8','민'],['#e07a45','서'],['#8256e0','윤']],
     content:'[안건]\n- 팀 역할 분담(매칭/업무/권한/관리자)\n- 5주 개발 일정 확정\n- 협업 도구·브랜치 전략 합의\n\n[결정 사항]\n- 주 2회(월·목) 오프라인 회의\n- GitHub feature 브랜치 + PR 리뷰 후 머지'},
  ];
  let curMeeting=0, editMeeting=-1;
  function meetingBodyHTML(content){
    const lines=content.split('\n'); let html=''; let inList=false;
    for(let raw of lines){
      const ln=raw.trim();
      if(!ln){ if(inList){html+='</ul>';inList=false;} continue; }
      const head=ln.match(/^\[(.+)\]$/);
      if(head){ if(inList){html+='</ul>';inList=false;} html+=`<h5>${head[1]}</h5>`; }
      else if(ln.startsWith('- ')){ if(!inList){html+='<ul>';inList=true;} html+=`<li>${ln.slice(2)}</li>`; }
      else { if(inList){html+='</ul>';inList=false;} html+=`<p>${ln}</p>`; }
    }
    if(inList)html+='</ul>';
    return html;
  }
  function renderMinutes(i){
    curMeeting=i;
    const m=MEETS[i];
    document.getElementById('minutes').innerHTML=`
      <div class="mh">
        <div style="display:flex;justify-content:space-between;align-items:flex-start;gap:10px">
          <div><div class="dt">${m.dt}</div><h2>${m.t}</h2></div>
          <button class="btn ghost sm" onclick="openMeetingEdit(${i})">✎ 수정</button>
        </div>
        <div class="att">${m.att.map(a=>`<span class="pic" style="background:${a[0]}">${a[1]}</span>`).join('')}<span style="align-self:center;font-size:12.5px;color:var(--ink-soft);margin-left:4px">참석 ${m.att.length}명</span></div>
      </div>
      <div class="prose">${meetingBodyHTML(m.content)}</div>`;
    document.querySelectorAll('#meetList .meet-card').forEach((c,idx)=>c.classList.toggle('on',idx===i));
  }
  function renderMeetList(){
    document.getElementById('meetList').innerHTML=MEETS.map((m,i)=>`
      <div class="meet-card${i===curMeeting?' on':''}" onclick="renderMinutes(${i})">
        <div class="dt">${m.dt}</div><h4>${m.t}</h4><p>${m.sum}</p>
      </div>`).join('');
  }
  // 회의 등록/수정 폼 진입
  function openMeetingNew(){
    editMeeting=-1;
    document.getElementById('mfEyebrow').textContent='New meeting';
    document.getElementById('mfTitle').textContent='회의 등록';
    document.getElementById('mfSave').textContent='회의 등록';
    document.getElementById('mfName').value='';
    document.getElementById('mfDate').value='2026-08-24';
    document.getElementById('mfTime').value='19:00';
    document.getElementById('mfAgenda').value='';
    document.getElementById('mfContent').value='';
    go('meetingform');
  }
  function openMeetingEdit(i){
    editMeeting=i; const m=MEETS[i];
    document.getElementById('mfEyebrow').textContent='Edit meeting';
    document.getElementById('mfTitle').textContent='회의 수정';
    document.getElementById('mfSave').textContent='수정 저장';
    document.getElementById('mfName').value=m.t;
    document.getElementById('mfDate').value=m.date;
    document.getElementById('mfTime').value=m.time;
    document.getElementById('mfAgenda').value=m.sum;
    document.getElementById('mfContent').value=m.content;
    go('meetingform');
  }
  function saveMeeting(){
    const name=document.getElementById('mfName').value.trim()||'제목 없는 회의';
    const date=document.getElementById('mfDate').value, time=document.getElementById('mfTime').value;
    const agenda=document.getElementById('mfAgenda').value.trim();
    const content=document.getElementById('mfContent').value;
    const dows=['일','월','화','수','목','금','토'];
    const dt=date?`${date.slice(5,7)}.${date.slice(8,10)} (${dows[new Date(date).getDay()]}) ${time||''}`.trim():'';
    if(editMeeting>=0){
      Object.assign(MEETS[editMeeting],{t:name,date,time,sum:agenda,content,dt});
      curMeeting=editMeeting;
    } else {
      MEETS.unshift({dt,date,time,t:name,sum:agenda,att:[['#2b46c8','민']],content});
      curMeeting=0;
    }
    renderMeetList(); renderMinutes(curMeeting); go('meetings');
  }
  window.openMeetingEdit=openMeetingEdit; window.openMeetingNew=openMeetingNew; window.saveMeeting=saveMeeting;
  renderMeetList();
  renderMinutes(0);