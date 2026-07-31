


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
