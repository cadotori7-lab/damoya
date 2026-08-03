
  // ----- 마이페이지 탭 (서버에서 렌더링된 목록을 보여주기/숨기기만 함) -----
  document.querySelectorAll('#mpTabs button').forEach(b=>b.onclick=()=>{
    document.querySelectorAll('#mpTabs button').forEach(x=>x.classList.remove('on'));
    b.classList.add('on');
    document.querySelectorAll('.mp-list[data-tab]').forEach(el=>{
      el.style.display = el.dataset.tab===b.dataset.t ? '' : 'none';
    });
  });

  // ----- 프로필 수정 모달 -----
  function closeProfile(){
    document.getElementById('profileModal').classList.remove('on');
    document.body.style.overflow='';
  }
  window.closeProfile=closeProfile;
  document.addEventListener('keydown',e=>{if(e.key==='Escape')closeProfile();});
