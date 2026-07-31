
//  매칭 범위(교내/전국)에 따라 카테고리 체크박스를 동적으로 움직임
function renderCategoriesByScope(scope) {
    const categoryContainer = document.getElementById('categoryContainer');
    if (!categoryContainer) return;

    if (scope === '교내') {
        categoryContainer.innerHTML = `
            <input type="checkbox" id="c1" value="공모전" checked><label for="c1">공모전</label>
            <input type="checkbox" id="c2" value="학과" checked><label for="c2">학과</label>
            <input type="checkbox" id="c3" value="교양" checked><label for="c3">교양</label>
            <input type="checkbox" id="c4" value="교내활동" checked><label for="c4">교내활동</label>
        `;
    } else if (scope === '전국') {
        categoryContainer.innerHTML = `
            <input type="checkbox" id="c1" value="공모전" checked><label for="c1">공모전</label>
            <input type="checkbox" id="c5" value="사이드프로젝트" checked><label for="c5">사이드프로젝트</label>
        `;
    }

    // 새로 생성된 체크박스에 변경 감지 이벤트 재연결
    const newCheckboxes = categoryContainer.querySelectorAll('input[type="checkbox"]');
    newCheckboxes.forEach(cb => {
        cb.addEventListener('change', handleFilterChange);
    });
}

// 검색 조건(범위, 카테고리, 학년 등)을 수집하여 서버로 전송
function submitSearch(pageNum = 1) {
    const keywordInput = document.querySelector('input[name="keyword"]');
    const keyword = keywordInput ? keywordInput.value.trim() : '';

    const tabInput = document.querySelector('input[name="tab"]');
    const tab = tabInput ? tabInput.value : 'all';

    const scopeBtn = document.querySelector('.scope button.on');
    const matchScope = scopeBtn ? scopeBtn.innerText.trim() : '교내';

    const sortInput = document.querySelector('input[name="sort"]');
    const sort = sortInput ? sortInput.value : 'latest';

    // 체크된 카테고리 수집
    const catEls = document.querySelectorAll('#categoryContainer input[type="checkbox"]:checked');
    const categoryList = Array.from(catEls).map(el => el.value).join(',');

    // 체크된 학년 수집
    const gradeEls = document.querySelectorAll('.filters .flt:not(#categoryContainer) input[type="checkbox"]:checked');
    const gradeList = Array.from(gradeEls).map(el => el.value).join(',');

    const params = new URLSearchParams();
    
    params.append('page', pageNum);
    if (tab !== 'all') params.append('tab', tab);
    
    // VO 필드명과 파라미터명 일치화
    if (matchScope) params.append('matchScope', matchScope);
    if (keyword) params.append('keyword', keyword);
    if (categoryList) params.append('categoryList', categoryList); 
    if (gradeList) params.append('gradeList', gradeList); 
    if (sort && sort !== 'latest') params.append('sort', sort);

    location.href = ctx + '/project/list?' + params.toString();
}

// 3체크박스 변경 시 최소 개수 유지 및 학년 무관 로직 처리 함수
function handleFilterChange(e) {
    const target = e.target;
    
    // 카테고리가 모두 해제되지 않도록 방어
    if (target.closest('#categoryContainer')) {
        const checkedCats = document.querySelectorAll('#categoryContainer input[type="checkbox"]:checked');
        if (checkedCats.length === 0) {
            alert("카테고리는 최소 1개 이상 선택해야 합니다.");
            target.checked = true; 
            return;
        }
    }

    // 학년 무관 및 개별 학년 선택 상호작용 제어
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

// 상단 탭(전체/모집중/마감) 변경 함수
function filterTab(tabValue) {
    const tabInput = document.querySelector('input[name="tab"]');
    if (tabInput) tabInput.value = tabValue;
    submitSearch(1);
}

//  매칭 범위(교내/전국) 변경 함수
function setScope(scopeValue, btnElement) {
    document.querySelectorAll('.scope button').forEach(b => b.classList.remove('on'));
    
    if (btnElement) {
        btnElement.classList.add('on');
    } else {
        document.querySelectorAll('.scope button').forEach(b => {
            if (b.innerText.trim() === scopeValue) b.classList.add('on');
        });
    }
    
    renderCategoriesByScope(scopeValue);
    submitSearch(1);
}

// 정렬 기준 변경 함수
function changeSort(sortValue) {
    const sortInput = document.querySelector('input[name="sort"]');
    if (sortInput) sortInput.value = sortValue;
    submitSearch(1);
}

// 관심 등록(좋아요) 토글 비동기 처리 함수
function toggleFavorite(projectId, buttonElement, event) {
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
        const countSpan = buttonElement.querySelector('.fav-count');
        if (countSpan) countSpan.innerText = data.favoriteCount;

        if (data.isLiked) {
            buttonElement.classList.add('active');
        } else {
            buttonElement.classList.remove('active');
        }
    })
    .catch(error => console.error('좋아요 처리 중 에러 발생:', error));
}

// 마감일에 따른 D-Day 계산 및 상태 배지 갱신 함수
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

// 페이지 로드 시 URL 파라미터 상태를 UI에 복원하는 초기화 블록
document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    let currentScope = '교내';

    // 매칭 범위 복원
    if (urlParams.has('matchScope')) {
        currentScope = urlParams.get('matchScope');
        document.querySelectorAll('.scope button').forEach(b => {
            if (b.innerText.trim() === currentScope) b.classList.add('on');
            else b.classList.remove('on');
        });
    }
    renderCategoriesByScope(currentScope);

    // 상단 탭 복원
    if (urlParams.has('tab')) {
        const currentTab = urlParams.get('tab');
        const tabInput = document.querySelector('input[name="tab"]');
        if (tabInput) tabInput.value = currentTab;
        
        document.querySelectorAll('.tab-item').forEach(btn => {
            btn.classList.remove('active');
            if ((currentTab === 'all' && btn.innerText.trim() === '전체') ||
                (currentTab === 'RECRUITING' && btn.innerText.trim() === '모집중') ||
                (currentTab === 'CLOSED' && btn.innerText.trim() === '모집마감')) {
                btn.classList.add('active');
            }
        });
    }

    // 카테고리 체크 상태 복원
    if (urlParams.has('categoryList')) {
        const urlCategories = urlParams.get('categoryList').split(',');
        document.querySelectorAll('input[id^="c"]').forEach(cb => {
            cb.checked = urlCategories.includes(cb.value);
        });
    }

    // 학년 체크 상태 복원
    if (urlParams.has('gradeList')) {
        const urlGrades = urlParams.get('gradeList').split(',');
        document.querySelectorAll('input[id^="y"]').forEach(cb => {
            cb.checked = urlGrades.includes(cb.value);
        });
    }

    updateDdayAndStatus();

    // 검색 폼 전송 이벤트 연결
    const searchForm = document.querySelector('.searchbar form');
    if(searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault(); 
            submitSearch(1);    
        });
    }

    // 필터 체크박스 변경 이벤트 연결
    const checkboxes = document.querySelectorAll('.flt input[type="checkbox"]');
    checkboxes.forEach(cb => {
        cb.addEventListener('change', handleFilterChange);
    });
});