// 댓글 수정 모달 열기 (클릭한 수정 버튼 기준으로 해당 댓글의 id/내용을 채워넣음)
function openCommentEditModal(btn) {
    const cmt = btn.closest('.cmt');
    if (!cmt) return;
    document.getElementById('editCommentId').value = cmt.dataset.commentId;
    document.getElementById('editCommentContent').value = cmt.querySelector('.cmt-content').textContent;
    openModal('editModal');
}
window.openCommentEditModal = openCommentEditModal;

function deleteTalent(postId) {
          if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
              location.href = ctx + "/talent/delete?id=" + postId;
          }
      }
      
//비로그인 유저가 접근할 때 호출되는 함수
function requireLogin() {
alert("로그인 후 사용할 수 있어요!");
location.href = ctx + "/auth/login";
}