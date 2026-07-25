// 정렬 함수
function sortList(sortType) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('sort', sortType);
    window.location.href = window.location.pathname + '?' + urlParams.toString();
}

// D-Day 및 마감 상태 자동 계산 함수
function updateDdayAndStatus() {
    const cards = document.querySelectorAll('.card-item');
    
    // 오늘 날짜 (시간을 00:00:00으로 설정하여 일수 정확히 계산)
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    cards.forEach(card => {
        const endDateStr = card.getAttribute('data-end-date'); // YYYY-MM-DD 형식
        if (!endDateStr) return;

        const endDate = new Date(endDateStr);
        endDate.setHours(0, 0, 0, 0);

        // 남은 일수 계산 (마감일 - 오늘)
        const diffTime = endDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        const statusBadge = card.querySelector('.status-badge');
        const dDaySpan = card.querySelector('.d-day-badge');

        if (diffDays < 0) {
            // [마감일이 지난 경우] -> 모집마감으로 강제 변경
            card.setAttribute('data-status', 'CLOSED'); 
            if (statusBadge) {
                statusBadge.className = 'status-badge closed';
                statusBadge.textContent = '모집마감';
            }
            if (dDaySpan) {
                dDaySpan.style.display = 'none'; // D-Day 뱃지 숨김
            }
        } else {
            // [마감일이 남은 경우] -> D-Day 표시
            if (dDaySpan) {
                if (diffDays === 0) {
                    dDaySpan.textContent = 'D-Day';
                } else {
                    dDaySpan.textContent = `D-${diffDays}`;
                }
            }
        }
    });
}

// 페이지 최초 로드 시 실행
document.addEventListener("DOMContentLoaded", function() {
    // 1. D-Day 및 마감 상태 계산 실행
    updateDdayAndStatus();

    // 2. 프로젝트 등록/수정 폼의 마감일(endDate) 오늘 이전 날짜 선택 방지 처리
    const endDateInput = document.getElementById("endDate");
    if (endDateInput) {
        const todayStr = new Date().toISOString().split('T')[0];
        endDateInput.min = todayStr;
    }
});