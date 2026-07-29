// 전역 변수
let currentTab = 'all';
let currentScope = '교내';

// [1] 상단 탭 (전체/모집중/마감) 클릭 시
function filterTab(status) {
    currentTab = status;
    submitSearch(1); 
}

// [2] 매칭 범위 (교내/전국) 클릭 시
function setScope(scopeType) {
    currentScope = scopeType;
    submitSearch(1); 
}

// [3] 필터(카테고리/학년) 체크박스 제어 및 검색 요청
function handleFilterChange(e) {
    const target = e.target;
    
    if (target.closest('#categoryContainer')) {
        const checkedCats = document.querySelectorAll('#categoryContainer input[type="checkbox"]:checked');
        if (checkedCats.length === 0) {
            alert("카테고리는 최소 1개 이상 선택해야 합니다.");
            target.checked = true;
            return;
        }
    }

    if (target.closest('.filters .flt') && !target.closest('#categoryContainer')) {
        const gradeAny = document.getElementById('y0');
        const specGrades = document.querySelectorAll('.filters .flt:not(#categoryContainer) input[type="checkbox"]:not(#y0)');

        if (target.id === 'y0') {
            if (gradeAny.checked) {
                specGrades.forEach(el => el.checked = false);
            } else {
                gradeAny.checked = true; 
            }
        } else {
            const activeSpecs = Array.from(specGrades).filter(el => el.checked);
            if (activeSpecs.length === 4) {
                specGrades.forEach(el => el.checked = false);
                if (gradeAny) gradeAny.checked = true;
            } else if (activeSpecs.length > 0) {
                if (gradeAny) gradeAny.checked = false;
            } else {
                if (gradeAny) gradeAny.checked = true;
            }
        }
    }

    submitSearch(1); 
}

// 서버(Controller)로 파라미터를 묶어서 전송하는 함수
function submitSearch(pageNo) {
    const searchInput = document.getElementById('searchInput');
    const keyword = searchInput ? searchInput.value.trim() : '';

    const catEls = document.querySelectorAll('#categoryContainer input[type="checkbox"]:checked');
    const categories = Array.from(catEls).map(el => el.value).join(',');

    const gradeEls = document.querySelectorAll('.filters .flt:not(#categoryContainer) input[type="checkbox"]:checked');
    const grades = Array.from(gradeEls).map(el => el.value).join(',');

    let url = `${ctx}/project/list?page=${pageNo}&scope=${currentScope}&tab=${currentTab}&keyword=${encodeURIComponent(keyword)}&categories=${encodeURIComponent(categories)}&grades=${encodeURIComponent(grades)}`;
    
    const urlParams = new URLSearchParams(window.location.search);
    if(urlParams.has('sort')) url += `&sort=${urlParams.get('sort')}`;

    window.location.href = url;
}

// 검색창 엔터 키 이벤트
document.getElementById('searchInput')?.addEventListener('keyup', (e) => {
    if(e.key === 'Enter') submitSearch(1);
});

// 정렬 변경 함수
function sortList(sortType) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('sort', sortType);
    urlParams.set('page', '1'); 
    window.location.href = window.location.pathname + '?' + urlParams.toString();
}

// 목록 페이지용 좋아요(관심등록) 토글 함수
function toggleFavorite(projectId, buttonElement, event) {
    if (event) event.stopPropagation();
    
    fetch(`${ctx}/project/favorite/toggle?projectId=${projectId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'FAIL') {
            alert(data.message);
            if (data.message && data.message.includes('로그인')) {
                location.href = `${ctx}/auth/login`;
            }
            return;
        }

        const countSpan = buttonElement.querySelector('.fav-count');
        if (countSpan) {
            countSpan.innerText = data.favoriteCount;
        }

        if (data.isLiked) {
            buttonElement.classList.add('active');
        } else {
            buttonElement.classList.remove('active');
        }
    })
    .catch(error => console.error('Error:', error));
}

function restoreStateFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    
    if (urlParams.has('tab')) currentTab = urlParams.get('tab');
    document.querySelectorAll('.tab-item').forEach(btn => {
        btn.classList.remove('active');
        if (btn.getAttribute('onclick').includes(currentTab)) btn.classList.add('active');
    });

    if (urlParams.has('scope')) currentScope = urlParams.get('scope');
    document.querySelectorAll('.scope button').forEach(btn => {
        btn.classList.remove('on');
        if (btn.innerText.trim() === currentScope) btn.classList.add('on');
    });

    const categoryContainer = document.getElementById('categoryContainer');
    if (categoryContainer) {
        if (currentScope === '교내' || currentScope === 'CAMPUS') {
            categoryContainer.innerHTML = `
                <input type="checkbox" id="c1" value="공모전"><label for="c1">공모전</label>
                <input type="checkbox" id="c2" value="학과"><label for="c2">학과</label>
                <input type="checkbox" id="c3" value="교양"><label for="c3">교양</label>
                <input type="checkbox" id="c4" value="교내활동"><label for="c4">교내활동</label>
            `;
        } else {
            categoryContainer.innerHTML = `
                <input type="checkbox" id="c1" value="공모전"><label for="c1">공모전</label>
                <input type="checkbox" id="c2" value="사이드 프로젝트"><label for="c2">사이드 프로젝트</label>
            `;
        }
    }

    const paramCategories = urlParams.has('categories') ? urlParams.get('categories').split(',') : ['공모전','학과','교양','교내활동','사이드 프로젝트'];
    document.querySelectorAll('#categoryContainer input').forEach(el => {
        if (paramCategories.includes(el.value)) el.checked = true;
    });

    const paramGrades = urlParams.has('grades') ? urlParams.get('grades').split(',') : ['ALL'];
    document.querySelectorAll('.filters .flt:not(#categoryContainer) input').forEach(el => {
        if (paramGrades.includes(el.value)) el.checked = true;
        else el.checked = false;
    });

    if (urlParams.has('keyword')) {
        const searchInput = document.getElementById('searchInput');
        if (searchInput) searchInput.value = urlParams.get('keyword');
    }
}

function updateDdayAndStatus() {
    const cards = document.querySelectorAll('.card-item');
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    cards.forEach(card => {
        const endDateStr = card.getAttribute('data-end-date'); 
        if (!endDateStr) return;

        const endDate = new Date(endDateStr);
        endDate.setHours(0, 0, 0, 0);
        const diffDays = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24));

        const statusBadge = card.querySelector('.status-badge');
        const dDaySpan = card.querySelector('.d-day-badge');

        if (diffDays < 0) {
            if (statusBadge) {
                statusBadge.className = 'status-badge closed';
                statusBadge.textContent = '모집마감';
            }
            if (dDaySpan) dDaySpan.style.display = 'none'; 
        } else {
            if (dDaySpan) {
                if (diffDays === 0) dDaySpan.textContent = 'D-Day';
                else dDaySpan.textContent = `D-${diffDays}`;
            }
        }
    });
}

document.addEventListener("DOMContentLoaded", function() {
    restoreStateFromURL();
    updateDdayAndStatus();

    document.querySelectorAll('.filters input[type="checkbox"]').forEach(el => {
        el.addEventListener('change', handleFilterChange);
    });
});
// 목록 페이지용 좋아요(관심등록) 토글 함수
function toggleFavorite(projectId, buttonElement, event) {
    // 이벤트 전파를 완벽하게 차단하여 카드 전체 링크(상세페이지 이동)가 실행되지 않도록 막음
    if (event) {
        event.stopPropagation();
        if (typeof event.stopImmediatePropagation === 'function') {
            event.stopImmediatePropagation();
        }
    }
    
    fetch(`${ctx}/project/favorite/toggle?projectId=${projectId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'FAIL') {
            alert(data.message);
            if (data.message && data.message.includes('로그인')) {
                location.href = `${ctx}/auth/login`;
            }
            return;
        }

        // 좋아요 카운트 텍스트 갱신
        const countSpan = buttonElement.querySelector('.fav-count');
        if (countSpan) {
            countSpan.innerText = data.favoriteCount;
        }

        // 하트 채우기 클래스 토글
        if (data.isLiked) {
            buttonElement.classList.add('active');
        } else {
            buttonElement.classList.remove('active');
        }
    })
    .catch(error => console.error('Error:', error));
}
// 정렬 탭을 눌렀을 때 실행되는 함수
    function changeSort(sortType) {
        // 현재 URL의 파라미터들을 가져옴 (검색어, 카테고리 등 기존 필터 유지용)
        const urlParams = new URLSearchParams(window.location.search);
        
        // 정렬 값 변경
        urlParams.set('sort', sortType);
        // 정렬이 바뀌면 1페이지부터 다시 보도록 page 값 초기화
        urlParams.set('page', 1); 
        
        // 변경된 URL로 이동
        window.location.search = urlParams.toString();
    }