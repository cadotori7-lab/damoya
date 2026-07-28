
  // // ----- 매칭 카드 데이터 -----
  // const CAT={공모전:{c:'var(--cat-contest)'},학과:{c:'var(--cat-major)'},교양:{c:'var(--cat-liberal)'},교내활동:{c:'var(--cat-club)'}};
  // const projects=[
  //   {cat:'공모전',scope:'교내',title:'2026 캡스톤 경진대회 — AI 헬스케어 웹서비스',desc:'학생 건강 관리를 돕는 AI 기반 웹서비스를 함께 만들 팀원을 찾습니다.',dept:'컴퓨터공학',year:'3–4학년',have:2,need:4,tags:['Spring','React','MySQL'],dday:'D-9'},
  //   {cat:'학과',scope:'학과',title:'데이터베이스 텀 프로젝트 팀원 구해요',desc:'ERD 설계부터 구현까지. DB 수업 텀 프로젝트를 같이 할 3명을 모집합니다.',dept:'컴퓨터공학',year:'3학년',have:1,need:3,tags:['MySQL','ERD'],dday:'D-4'},
  //   {cat:'교양',scope:'교내',title:'교양 〈창업과 경영〉 발표 조원 모집',desc:'중간·기말 발표를 함께 준비할 조원을 찾습니다. 부담 없는 팀플이에요.',dept:'전체',year:'전 학년',have:3,need:5,tags:['발표','기획'],dday:'D-6'},
  //   {cat:'교내활동',scope:'전국',title:'전국 대학생 연합 해커톤 참가팀',desc:'48시간 해커톤에 나갈 연합팀을 꾸립니다. 학교 무관, 실력·열정 환영.',dept:'무관',year:'무관',have:3,need:6,tags:['해커톤','Node','AI'],dday:'D-12'},
  //   {cat:'공모전',scope:'전국',title:'ESG 아이디어 공모전 — 기획·개발 파트너',desc:'친환경 서비스 아이디어를 서비스로 구현할 개발자를 찾습니다.',dept:'무관',year:'2–4학년',have:2,need:4,tags:['기획','Spring'],dday:'D-15'},
  //   {cat:'학과',scope:'교내',title:'졸업작품 — Unity 게임 개발 팀원',desc:'카드 전략 RPG 졸업작품을 함께 완성할 클라이언트·기획 팀원 모집.',dept:'게임공학',year:'4학년',have:2,need:3,tags:['Unity','C#'],dday:'D-3'},
  // ];
  // document.getElementById('cards').innerHTML=projects.map(p=>`
  //   <article class="card" style="--c:${CAT[p.cat].c}" onclick="go('detail')" tabindex="0" onkeydown="if(event.key==='Enter')go('detail')">
  //     <div class="row1"><span class="cat">${p.cat} · ${p.scope}</span><span class="chip recruit">모집중</span></div>
  //     <h4>${p.title}</h4>
  //     <p>${p.desc}</p>
  //     <div class="meta"><span>${p.dept}</span><span>${p.year}</span></div>
  //     <div class="tags">${p.tags.map(t=>`<span class="tag">${t}</span>`).join('')}</div>
  //     <div class="card-foot"><span class="team-need">모집 <b class="mono" style="color:var(--ink)">${p.have}/${p.need}</b>명</span><span class="dday">${p.dday}</span></div>
  //   </article>`).join('');
