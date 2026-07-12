
  // ----- 인재풀 (자기홍보 게시물) -----
  const TALENT=[
    {name:'한지민',dept:'소프트웨어 · 3학년',pic:'한',c:'#8256e0',kind:'member',field:'프론트엔드',time:'평일 저녁·주말',cats:'공모전, 교내활동',tags:['React','TypeScript','Figma'],
     intro:'React로 개인 프로젝트를 3개 만들어봤어요. 실제 서비스 규모의 프론트엔드를 경험하고 싶어 팀을 찾고 있습니다. UI 구현부터 상태관리까지 맡을 수 있어요.'},
    {name:'박준호',dept:'컴퓨터공학 교수',pic:'박',c:'#e07a45',kind:'mentor',field:'백엔드·아키텍처 멘토링',time:'주 1회 화상',cats:'공모전, 학과',tags:['Spring','아키텍처','코드리뷰'],
     intro:'백엔드 설계와 코드 리뷰를 도와드립니다. 캡스톤·공모전 팀을 대상으로 중간 점검과 방향 피드백을 제공해요. 실무 관점의 조언이 필요한 팀 환영합니다.'},
    {name:'오세훈',dept:'컴퓨터공학 · 4학년',pic:'오',c:'#c98a12',kind:'member',field:'백엔드',time:'평일 오후',cats:'공모전',tags:['Spring','MyBatis','MySQL'],
     intro:'Spring·MyBatis 학습 중이고 배우며 기여하고 싶습니다. 인증·권한, CRUD API 파트를 성실히 맡을 자신 있어요. 끝까지 완주합니다.'},
    {name:'이서연',dept:'디자인 · 2학년',pic:'이',c:'#0f9d8c',kind:'member',field:'UI/UX 디자인',time:'주말',cats:'공모전, 교양',tags:['Figma','디자인','기획'],
     intro:'서비스 UI/UX 디자인과 발표 자료를 맡을 수 있어요. 공모전 수상 경험이 있고, 개발자와 협업해본 경험도 많습니다.'},
  ];
  const TKIND={member:['member','팀원 지원'],mentor:['mentor','멘토']};
  let talentFilter='all', curTalent=0;
  function renderTalent(){
    const list=TALENT.map((t,i)=>({t,i})).filter(({t})=>talentFilter==='all'||t.kind===talentFilter);
    document.getElementById('talentGrid').innerHTML=list.map(({t,i})=>`
      <div class="talent-card" onclick="openTalent(${i})" tabindex="0" onkeydown="if(event.key==='Enter')openTalent(${i})">
        <div class="tc-head">
          <span class="pic" style="background:${t.c}">${t.pic}</span>
          <div class="who"><div class="nm">${t.name}</div><div class="dept">${t.dept}</div></div>
          <span class="tc-kind ${TKIND[t.kind][0]}">${TKIND[t.kind][1]}</span>
        </div>
        <p>${t.intro}</p>
        <div class="tc-tags">${t.tags.map(g=>`<span class="tag">${g}</span>`).join('')}</div>
        <div class="tc-foot"><span>희망 · ${t.field}</span><span>${t.time}</span></div>
      </div>`).join('');
  }
  function openTalent(i){
    curTalent=i; const t=TALENT[i];
    document.getElementById('tdPic').textContent=t.pic;
    document.getElementById('tdPic').style.background=t.c;
    document.getElementById('tdName').textContent=t.name;
    document.getElementById('tdDept').textContent=t.dept;
    const k=document.getElementById('tdKind'); k.className='tc-kind '+TKIND[t.kind][0]; k.textContent=TKIND[t.kind][1];
    document.getElementById('tdField').textContent=t.field;
    document.getElementById('tdTime').textContent=t.time;
    document.getElementById('tdCat').textContent=t.cats;
    document.getElementById('tdIntro').innerHTML=`<p>${t.intro}</p>`;
    document.getElementById('tdTags').innerHTML=t.tags.map(g=>`<span class="tag">${g}</span>`).join('');
    go('talentdetail');
  }
  document.querySelectorAll('#talentTabs button').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('#talentTabs button').forEach(x=>x.classList.remove('on'));
    b.classList.add('on'); talentFilter=b.dataset.f; renderTalent();
  });
  window.openTalent=openTalent;
  renderTalent();

  // ----- 함께하기 제의 모달 -----
  const SENT_OFFERS=[
    {name:'오세훈',pic:'오',c:'#c98a12',role:'백엔드 · CRUD API',status:'수락'},
    {name:'이서연',pic:'이',c:'#0f9d8c',role:'UI/UX 디자인',status:'거절'},
  ];
  function renderSentOffers(){
    const el=document.getElementById('sentOffers'); if(!el)return;
    el.innerHTML=SENT_OFFERS.map((o,i)=>`
      <div class="offer-item" style="--c:var(--accent)">
        <span class="pic" style="width:40px;height:40px;border-radius:50%;display:grid;place-items:center;font-weight:700;color:#fff;flex:none;background:${o.c}">${o.pic}</span>
        <div class="oi-main">
          <h4 style="margin:0 0 3px">${o.name}</h4>
          <div class="oi-msg">${o.role} 제의</div>
        </div>
        <div class="oi-act">
          ${o.status==='대기'
            ? `<span class="chip wait">수락 대기</span><button class="btn sm ghost" onclick="cancelOffer(${i})">취소</button>`
            : `<span class="chip ${o.status==='수락'?'approve':'reject'}">${o.status==='수락'?'수락함 · 합류':'거절함'}</span>`}
        </div>
      </div>`).join('') || '<p style="color:var(--ink-soft);padding:14px;text-align:center">아직 보낸 제의가 없어요. 인재풀에서 함께할 사람을 초대해보세요.</p>';
  }
  window.cancelOffer=(i)=>{ SENT_OFFERS.splice(i,1); renderSentOffers(); };
  renderSentOffers();

  function openOffer(){
    document.getElementById('offerBody').style.display='';
    const t=TALENT[curTalent];
    document.getElementById('offerWho').textContent=`${t.name}님에게 초대를 보내요`;
    document.querySelectorAll('#offerBody input,#offerBody textarea').forEach(el=>el.value='');
    document.getElementById('offerModal').classList.add('on');
    document.body.style.overflow='hidden';
  }
  function closeOffer(){document.getElementById('offerModal').classList.remove('on');document.body.style.overflow='';}
  function sendOffer(){
    const t=TALENT[curTalent];
    const role=(document.getElementById('offerRole').value||'').trim()||t.field;
    SENT_OFFERS.unshift({name:t.name,pic:t.pic,c:t.c,role,status:'대기'});
    renderSentOffers();
    document.getElementById('offerBody').innerHTML=`
      <div class="submit-success">
        <div class="ok-ic"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg></div>
        <h3>제의를 보냈어요!</h3>
        <p>${t.name}님에게 함께하기 제의가 전달됐어요.<br>상대가 <b>수락</b>하면 팀원으로 합류해요. 보낸 제의는 <b>팀원 관리</b>에서 확인할 수 있어요.</p>
        <button class="btn pri" style="margin-top:20px" onclick="closeOffer()">확인</button>
      </div>`;
  }
  window.openOffer=openOffer; window.closeOffer=closeOffer; window.sendOffer=sendOffer;
  document.addEventListener('keydown',e=>{if(e.key==='Escape')closeOffer();});
