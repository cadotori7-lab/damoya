
  // ----- 마이페이지 탭 -----
  const CATC={공모전:'var(--cat-contest)',학과:'var(--cat-major)',교양:'var(--cat-liberal)',교내활동:'var(--cat-club)'};
  const mp={
    joined:[
      {cat:'공모전',t:'AI 헬스케어 웹서비스',meta:['진행중','팀 4명','D-24'],role:'lead',prog:58},
      {cat:'학과',t:'데이터베이스 텀 프로젝트',meta:['진행중','팀 3명','D-11'],role:'member',prog:40},
      {cat:'교양',t:'〈창업과 경영〉 발표 팀플',meta:['진행중','팀 5명','D-6'],role:'member',prog:72},
    ],
    applied:[
      {cat:'교내활동',t:'전국 대학생 연합 해커톤 참가팀',meta:['지원 3일 전'],chip:'interview',chipT:'면접 예정'},
      {cat:'공모전',t:'ESG 아이디어 공모전 파트너',meta:['지원 1일 전'],chip:'wait',chipT:'승인 대기'},
    ],
    liked:[
      {cat:'학과',t:'졸업작품 — Unity 게임 개발 팀원',meta:['모집중','2/3명','D-3']},
      {cat:'공모전',t:'교내 창업 아이디어톤 팀원 모집',meta:['모집중','1/5명','D-8']},
    ],
    offers:[
      {cat:'공모전',from:'최윤서 팀장',proj:'AI 헬스케어 웹서비스',role:'백엔드 · 인증/권한',msg:'인증·권한 경험이 잘 맞을 것 같아요. 함께해요!',status:'대기'},
      {cat:'학과',from:'정하람 팀장',proj:'데이터베이스 텀 프로젝트',role:'ERD 설계',msg:'ERD 잘 다루신다고 들었어요. 제안드립니다.',status:'대기'},
    ]
  };
  function renderMp(tab){
    if(tab==='offers'){
      document.getElementById('mpList').innerHTML=mp.offers.map((o,i)=>`
        <div class="offer-item" style="--c:${CATC[o.cat]}">
          <div class="oi-main">
            <div class="oi-from">${o.from} · ${o.proj}</div>
            <h4>${o.role} 제안</h4>
            <div class="oi-msg">"${o.msg}"</div>
          </div>
          <div class="oi-act" id="offerAct${i}">
            ${o.status==='대기'
              ? `<button class="btn sm pri" onclick="answerOffer(${i},'수락')">수락</button><button class="btn sm ghost" onclick="answerOffer(${i},'거절')">거절</button>`
              : `<span class="chip ${o.status==='수락'?'approve':'reject'}">${o.status==='수락'?'수락함 · 팀 합류':'거절함'}</span>`}
          </div>
        </div>`).join('') || '<p style="color:var(--ink-soft);padding:16px">받은 제의가 없어요.</p>';
      return;
    }
    document.getElementById('mpList').innerHTML=mp[tab].map(x=>{
      let right='';
      if(x.role) right=`<span class="role-tag ${x.role}">${x.role==='lead'?'팀장':'팀원'}</span>`;
      else if(x.chip) right=`<span class="chip ${x.chip}">${x.chipT}</span>`;
      else right=`<button class="btn sm ghost">보기</button>`;
      return `<div class="mp-item" style="--c:${CATC[x.cat]}" onclick="go('${tab==='joined'?'overview':'detail'}')">
        <div class="m-main">
          <div class="m-cat">${x.cat}</div><h4>${x.t}</h4>
          <div class="m-meta">${x.meta.map(m=>`<span>${m}</span>`).join('')}</div>
        </div>
        <div class="m-right">${right}</div>
      </div>`;
    }).join('');
  }
  window.answerOffer=(i,ans)=>{ mp.offers[i].status=ans; renderMp('offers'); };
  document.querySelectorAll('#mpTabs button').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('#mpTabs button').forEach(x=>x.classList.remove('on'));
    b.classList.add('on'); renderMp(b.dataset.t);
  });
  renderMp('joined');

  // ----- 프로필 수정 모달 -----
  function openProfile(){
    document.getElementById('profileModal').classList.add('on');
    document.body.style.overflow='hidden';
  }
  function closeProfile(){
    document.getElementById('profileModal').classList.remove('on');
    document.body.style.overflow='';
  }
  function saveProfile(){
    const name=document.getElementById('pfName').value.trim()||'김민재';
    const dept=document.getElementById('pfDept').value;
    const year=document.getElementById('pfYear').value;
    const head=document.querySelector('#v-mypage .mp-head');
    head.querySelector('.info h2').textContent=name;
    head.querySelector('.info .line').textContent=`대진대학교 · ${dept} · ${year}`;
    head.querySelector('.big').textContent=name.slice(-2,-1)||name.charAt(0);
    // 관심 분야 배지 갱신 (학교 인증 배지는 유지)
    const picked=[...document.querySelectorAll('#pfInterests input:checked')].map(c=>c.nextElementSibling.textContent);
    head.querySelector('.info .badges').innerHTML=
      picked.map(p=>`<span class="b">${p}</span>`).join('')+'<span class="b">✓ 학교 인증됨</span>';
    closeProfile();
  }
  window.openProfile=openProfile; window.closeProfile=closeProfile; window.saveProfile=saveProfile;
  document.addEventListener('keydown',e=>{if(e.key==='Escape')closeProfile();});
