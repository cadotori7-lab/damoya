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