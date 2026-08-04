// 게시글 삭제
function deleteProject(projectId) {
    if (confirm("정말 이 프로젝트 모집글을 삭제하시겠습니까?")) {
        location.href = ctx + '/project/delete?id=' + projectId;
    }
}
  


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

// 댓글 수정 모달 열기 (클릭한 수정 버튼 기준으로 해당 댓글의 id/내용을 채워넣음)
function openCommentEditModal(btn) {
    const cmt = btn.closest('.cmt');
    if (!cmt) return;
    document.getElementById('editCommentId').value = cmt.dataset.commentId;
    document.getElementById('editCommentContent').value = cmt.querySelector('.cmt-content').textContent;
    openModal('editModal');
}
window.openCommentEditModal = openCommentEditModal;

// 댓글 신고 모달 열기 (신고 대상 댓글 id를 기억해둔다)
let pendingReportCommentId = null;
function openCommentReportModal(commentId) {
    pendingReportCommentId = commentId;
    document.getElementById('commentReportReason').value = '';
    openModal('commentReportModal');
}
window.openCommentReportModal = openCommentReportModal;

// 댓글 신고 전송
function submitCommentReport() {
    if (!pendingReportCommentId) return;

    const reason = document.getElementById('commentReportReason').value;
    if (!reason) {
        alert('신고 사유를 선택해주세요.');
        return;
    }

    const body = new URLSearchParams();
    body.set('comment_id', pendingReportCommentId);
    body.set('reason', reason);

    fetch(`${ctx}/project/comment/report`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body.toString()
    })
        .then(res => {
            if (res.status === 401) throw new Error('unauthorized');
            if (!res.ok) throw new Error('failed');
            alert('댓글을 신고했습니다.');
            closeModal('commentReportModal');
        })
        .catch(err => {
            if (err.message === 'unauthorized') {
                alert('로그인이 필요한 서비스입니다.');
            } else {
                alert('신고 처리 중 오류가 발생했습니다.');
            }
        });
}
window.submitCommentReport = submitCommentReport;

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
function toggleFavorite(projectId, buttonElement, event) {
    // 목록 카드나 부모 컨테이너에 걸린 onclick 이벤트(예: 상세페이지 이동)가 실행되는 것을 막습니다.
    if (event) {
        event.stopPropagation();
        if (typeof event.stopImmediatePropagation === 'function') {
            event.stopImmediatePropagation();
        }
    }
    
    fetch(`${ctx}/project/favorite/toggle?projectId=${projectId}`, {
        method: 'POST'
    })
    .then(response => {
        // 서버에서 응답한 데이터가 JSON 형태가 아닐 경우를 대비한 예외 처리
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return response.json();
        } else {
            throw new Error("서버 응답이 JSON 형식이 아닙니다. 로그인이 풀렸거나 세션이 만료되었을 수 있습니다.");
        }
    })
    .then(data => {
        // 서버 처리 결과 상태 검사
        if (data.status === 'FAIL') {
            alert(data.message);
            // 로그인이 필요한 경우 로그인 페이지로 자동 이동
            if (data.message && data.message.includes('로그인')) {
                location.href = `${ctx}/auth/login`;
            }
            return;
        }
        
        const countSpan = buttonElement.querySelector('.fav-count');
        if (countSpan) {
            countSpan.innerText = data.favoriteCount;
        }

        // 하트 아이콘 활성화/비활성화 클래스 토글 
        if (data.isLiked) {
            buttonElement.classList.add('active'); // 좋아요 추가
        } else {
            buttonElement.classList.remove('active'); // 좋아요 취소
        }
    })
    .catch(error => {
        console.error('관심 등록 처리 중 오류 발생:', error);
    });
}