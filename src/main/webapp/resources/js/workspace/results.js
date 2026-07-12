
  // ----- 결과물 (승인 완료 산출물) -----
  const RES=[
    {task:'화면 설계 (Figma)',who:'서지우',c:'#0f9d8c',file:'wireframe.fig',sz:'2.1MB',type:'img',date:'08.08'},
    {task:'서비스 소개·발표 자료',who:'서지우',c:'#0f9d8c',file:'midterm_deck.pptx',sz:'5.4MB',type:'deck',date:'08.18'},
    {task:'요구사항 정의서',who:'서지우',c:'#0f9d8c',file:'requirements_v2.docx',sz:'92KB',type:'doc',date:'08.06'},
    {task:'로그인·회원 인증 구현',who:'이도현',c:'#2b46c8',file:'auth_module.zip',sz:'340KB',type:'zip',date:'08.12'},
    {task:'권한 분기(전역×프로젝트)',who:'이도현',c:'#2b46c8',file:'security_config.zip',sz:'128KB',type:'zip',date:'08.16'},
    {task:'개발 환경 세팅',who:'이도현',c:'#2b46c8',file:'setup_guide.md',sz:'14KB',type:'doc',date:'08.07'},
    {task:'관리자 통계 화면',who:'김민재',c:'#2b46c8',file:'admin_stats.zip',sz:'410KB',type:'zip',date:'08.20'},
  ];
  const P3={이도현:'이',김민재:'민',서지우:'서'};
  const dlIcon='<svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>';
  const fIcon='<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/></svg>';
  let resFilter='all';
  function renderRes(){
    const list=RES.filter(r=>resFilter==='all'||r.who===resFilter);
    document.getElementById('resGrid').innerHTML=list.map(r=>`
      <div class="res-card">
        <div class="rc-file">
          <div class="ficon ${r.type}">${fIcon}</div>
          <div class="fmeta"><div class="fn">${r.file}</div><div class="fsz">${r.sz} · 승인 ${r.date}</div></div>
        </div>
        <div class="rc-task"><div class="tt">${r.task}</div></div>
        <div class="rc-foot">
          <span class="rc-who"><span class="pic" style="background:${r.c}">${P3[r.who]}</span>${r.who}<span class="chip approve" style="margin-left:4px">승인</span></span>
          <button class="dl" title="다운로드">${dlIcon}</button>
        </div>
      </div>`).join('');
    document.getElementById('resCount').textContent=RES.length;
  }
  document.querySelectorAll('#resFilter button').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('#resFilter button').forEach(x=>x.classList.remove('on'));
    b.classList.add('on'); resFilter=b.dataset.f; renderRes();
  });
  renderRes();

