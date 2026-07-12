
  // ----- 일정 캘린더 (2026년 8월) -----
  const EVENTS={
    6:[{t:'요구사항 정의서 마감',c:'task'}],
    7:[{t:'개발 환경 세팅 마감',c:'task'}],
    10:[{t:'킥오프 회의',c:'meet'},{t:'ERD 스키마 마감',c:'task'}],
    14:[{t:'지원·승인 API 마감',c:'task'}],
    17:[{t:'중간 점검 회의',c:'meet'}],
    18:[{t:'멘토 중간 피드백',c:'fb'}],
    20:[{t:'통계 화면 마감',c:'task'}],
    24:[{t:'2차 스프린트 회의',c:'meet'}],
    28:[{t:'통합 테스트 시작',c:'task'}],
    31:[{t:'최종 리허설 회의',c:'meet'}],
  };
  (function buildCal(){
    const year=2026,month=7; // 8월(0-indexed 7)
    const first=new Date(year,month,1).getDay(); // 요일
    const days=new Date(year,month+1,0).getDate(); // 31
    const prevDays=new Date(year,month,0).getDate();
    const today=12; // 데모: 8/12를 오늘로
    let cells='';
    for(let i=0;i<first;i++){cells+=`<div class="cell out"><div class="dn">${prevDays-first+i+1}</div></div>`;}
    for(let d=1;d<=days;d++){
      const evs=(EVENTS[d]||[]).map(e=>`<div class="ev ${e.c}" title="${e.t}">${e.t}</div>`).join('');
      cells+=`<div class="cell${d===today?' today':''}"><div class="dn">${d}</div>${evs}</div>`;
    }
    const rem=(first+days)%7; if(rem){for(let i=1;i<=7-rem;i++)cells+=`<div class="cell out"><div class="dn">${i}</div></div>`;}
    document.getElementById('calGrid').innerHTML=cells;
  })();
