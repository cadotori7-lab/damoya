
  // ----- 프로젝트 완료 -----
  function pickFinal(){
    document.getElementById('finalDrop').style.display='none';
    document.getElementById('finalFileWrap').innerHTML=`
      <div class="final-file">
        <span class="fi"><svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/></svg></span>
        <div class="fmeta"><div class="fn">final_deliverable.zip</div><div class="fs">8.7MB</div></div>
        <span class="rm" onclick="removeFinal()" title="제거"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg></span>
      </div>`;
  }
  function removeFinal(){
    document.getElementById('finalFileWrap').innerHTML='';
    document.getElementById('finalDrop').style.display='';
  }
  function completeProject(){
    document.getElementById('completeBody').innerHTML=`
      <div class="complete-done">
        <div class="big-ic"><svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg></div>
        <h2>프로젝트를 완료했어요! 🎉</h2>
        <p>AI 헬스케어 웹서비스가 완료 처리됐어요. 최종 결과물과 팀원별 기여도가 <b>포트폴리오</b>에 기록됐고, 팀원 전원에게 알림이 전송됐어요.</p>
        <div style="display:flex;gap:10px;justify-content:center;margin-top:22px">
          <button class="btn ghost" onclick="go('results')">결과물 보기</button>
          <button class="btn pri" onclick="go('mypage')">마이페이지로</button>
        </div>
      </div>`;
    window.scrollTo({top:0,behavior:'smooth'});
  }
  window.pickFinal=pickFinal; window.removeFinal=removeFinal; window.completeProject=completeProject;