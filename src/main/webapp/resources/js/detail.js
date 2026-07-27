// 게시글 삭제
function deleteProject(projectId) {
    if (confirm("정말 이 프로젝트 모집글을 삭제하시겠습니까?")) {
        location.href = ctx + '/project/delete?id=' + projectId;
    }
}
  
/* ==========================================
   여기서부터 아래 코드를 추가해 주세요! 
   ========================================== */

// 모달(팝업창) 열기 함수
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex'; // 화면에 보이게 설정 (CSS에 따라 block일 수도 있음)
    } else {
        console.error(modalId + " 팝업창을 찾을 수 없습니다.");
    }
}

// 모달(팝업창) 닫기 함수
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none'; // 화면에서 숨기기
    }
}
document.addEventListener("DOMContentLoaded", function() {
    const endDateDiv = document.getElementById("projectEndDate");
    if (!endDateDiv) return;

    const endDateStr = endDateDiv.getAttribute("data-end"); // YYYY-MM-DD
    if (!endDateStr) return;

    const endDate = new Date(endDateStr);
    endDate.setHours(0, 0, 0, 0);

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // 마감일이 오늘보다 이전(지난) 경우
    if (endDate < today) {
        const applyBtn = document.getElementById("applyBtn");
        if (applyBtn) {
            applyBtn.outerHTML = `
                <button type="button" class="btn ghost" style="width:100%;justify-content:center;margin-top:18px;background:#f3f4f6;color:#9ca3af;cursor:not-allowed;" disabled>
                    모집이 마감된 프로젝트입니다
                </button>
            `;
        }
    }
});