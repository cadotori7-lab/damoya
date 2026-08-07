// 댓글 수정 모달 열기
function openCommentEditModal(btn) {
    const cmt = btn.closest('.cmt');
    if (!cmt) return;
    document.getElementById('editCommentId').value = cmt.dataset.commentId;
    document.getElementById('editCommentContent').value = cmt.querySelector('.cmt-content').textContent;
    openModal('editModal');
}
window.openCommentEditModal = openCommentEditModal;

// 게시글 삭제
function deleteTalent(postId) {
    if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
        location.href = window.ctx + "/talent/delete?id=" + postId;
    }
}
      
// 비로그인 유저 접근 제어
function requireLogin() {
    alert("로그인 후 사용할 수 있어요!");
    location.href = window.ctx + "/auth/login";
}

// 관심 표시 (좋아요) 토글 비동기 함수
function toggleTalentFavorite(postId, btn) {
    const formData = new URLSearchParams();
    formData.append("postId", postId);
    
    // JSP에서 선언한 전역 변수 사용
    formData.append(window.csrfParameter, window.csrfToken); 

    fetch(window.ctx + "/talent/favorite/toggle", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        if (data.status === "SUCCESS") {
            const countSpan = btn.querySelector('.fav-count');
            
            // 상태에 맞춰 버튼 UI (색상/배경) 즉시 변경
            if (data.isLiked) {
                btn.style.color = '#e07a45';
                btn.style.borderColor = '#e07a45';
                btn.style.background = '#fff2e8';
                btn.classList.add('active');
            } else {
                btn.style.color = ''; // 기본값 복구
                btn.style.borderColor = '#e5e7eb';
                btn.style.background = '';
                btn.classList.remove('active');
            }
            
            // 관심 등록 숫자 업데이트
            if (countSpan) {
                countSpan.textContent = data.favoriteCount;
            }
        } else {
            alert(data.message);
        }
    })
    .catch(err => {
        console.error(err);
        alert("서버와 통신 중 오류가 발생했습니다.");
    });
}
// 관심 표시 (좋아요) 토글 비동기 함수
function toggleTalentFavorite(postId, buttonElement, event) {
    if (event) {
        event.stopPropagation();
        if (typeof event.stopImmediatePropagation === 'function') {
            event.stopImmediatePropagation();
        }
    }

    //  폼 데이터 및 CSRF 토큰 세팅 (스프링 시큐리티 POST 요청 필수)
    const formData = new URLSearchParams();
    formData.append("postId", postId);
    if (window.csrfParameter && window.csrfToken) {
        formData.append(window.csrfParameter, window.csrfToken);
    }

    fetch(window.ctx + "/talent/favorite/toggle", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: formData
    })
    .then(response => {
        // 서버에서 응답한 데이터가 JSON 형태가 아닐 경우를 대비한 예외 처리
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return response.json();
        } else {
            throw new Error("서버 응답이 JSON 형식이 아닙니다. 세션이 만료되었을 수 있습니다.");
        }
    })
    .then(data => {
        // 서버 처리 결과 상태 검사 (비로그인 접근 등)
        if (data.status === 'FAIL') {
            alert(data.message);
            if (data.message && data.message.includes('로그인')) {
                location.href = window.ctx + "/auth/login";
            }
            return;
        }

        // 성공 시 UI 업데이트 (숫자 반영)
        const countSpan = buttonElement.querySelector('.fav-count');
        if (countSpan) {
            countSpan.innerText = data.favoriteCount;
        }

        // 하트 아이콘 활성화/비활성화 클래스 및 스타일 토글
        if (data.isLiked) {
            buttonElement.classList.add('active');
            buttonElement.style.color = '#ef4444';       // 글자/하트 빨간색
            buttonElement.style.borderColor = '#ef4444'; // 테두리 빨간색
            buttonElement.style.background = '#fff';     // 바탕은 흰색 
        } else {
            buttonElement.classList.remove('active');
            buttonElement.style.color = '';            
            buttonElement.style.borderColor = '#e5e7eb';
            buttonElement.style.background = '#fff';    
        }
    })
    .catch(error => {
        console.error('관심 등록 처리 중 오류 발생:', error);
        alert('관심 등록 처리 중 오류가 발생했습니다.');
    });
}

// 전역 함수로 등록 (JSP의 onclick에서 정상적으로 인식하게 함)
window.toggleTalentFavorite = toggleTalentFavorite;