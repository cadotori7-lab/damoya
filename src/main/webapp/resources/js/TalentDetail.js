// 댓글 수정 모달 열기 (클릭한 수정 버튼 기준으로 해당 댓글의 id/내용을 채워넣음)
function openCommentEditModal(btn) {
    const cmt = btn.closest('.cmt');
    if (!cmt) return;
    document.getElementById('editCommentId').value = cmt.dataset.commentId;
    document.getElementById('editCommentContent').value = cmt.querySelector('.cmt-content').textContent;
    openModal('editModal');
}
window.openCommentEditModal = openCommentEditModal;
