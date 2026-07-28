// 게시글 삭제
function deleteProject(projectId) {
    if (confirm("정말 이 프로젝트 모집글을 삭제하시겠습니까?")) {
        location.href = ctx + '/project/delete?id=' + projectId;
    }
}
  
  // ----- 댓글 등록 -----
  function addComment(){
    const ta=document.getElementById('cmtInput');
    const txt=ta.value.trim(); if(!txt)return;
    const form=ta.closest('.cmt-form');
    const el=document.createElement('div');
    el.className='cmt';
    el.innerHTML=`<div class="pic" style="background:linear-gradient(135deg,#2b46c8,#5b45c8)">민</div>
      <div class="body"><div class="nm">김민재 <span>컴퓨터공학 · 4학년 · 방금</span></div><p></p></div>`;
    el.querySelector('p').textContent=txt;
    form.parentNode.insertBefore(el,form);
    ta.value='';
    // 댓글 수 +1
    const cnt=document.querySelector('#v-detail .panel h5 .mono');
    if(cnt) cnt.textContent=parseInt(cnt.textContent||'0',10)+1;
  }
  window.addComment=addComment;
