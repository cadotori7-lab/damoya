let currentParams = {
    page: 1,
    matchScope: '교내',
    kind: 'all',     
    keyword: '',
    sort: 'latest', // 정렬 기본값 명시
    categoryList: ['공모전', '사이드프로젝트', '학과', '교양', '교내활동'] 
};

document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    
    if (urlParams.has('page')) currentParams.page = parseInt(urlParams.get('page')) || 1;
    if (urlParams.has('matchScope')) currentParams.matchScope = urlParams.get('matchScope');
    if (urlParams.has('kind')) currentParams.kind = urlParams.get('kind');
    if (urlParams.has('sort')) currentParams.sort = urlParams.get('sort');
    if (urlParams.has('keyword')) currentParams.keyword = urlParams.get('keyword');
    
    if (urlParams.has('categoryList')) {
        currentParams.categoryList = urlParams.get('categoryList').split(',').filter(Boolean);
    }

    syncUIFromParams();
    initEventListeners();
});

//  역할 선택 (전체보기 / 팀원 / 멘토)
function setRole(kindValue) {
    currentParams.kind = kindValue;
    currentParams.page = 1; 
    triggerSearch();
}

//  매칭 범위 선택 (교내 / 전국)
function changeScope(scopeValue, btnElement) {
    currentParams.matchScope = scopeValue;
    currentParams.page = 1;
    
    document.querySelectorAll('#scopeFilterBtns button').forEach(btn => btn.classList.remove('on'));
    btnElement.classList.add('on');

    if (scopeValue === '전국') {
        const allowedCategories = ['공모전', '사이드프로젝트'];
        currentParams.categoryList = currentParams.categoryList.filter(cat => allowedCategories.includes(cat));
        // 0개가 되면 허용된 전체를 다시 채움
        if (currentParams.categoryList.length === 0) currentParams.categoryList = [...allowedCategories];
    } else {
        const allowedCategories = ['공모전', '학과', '교양', '교내활동'];
        currentParams.categoryList = currentParams.categoryList.filter(cat => allowedCategories.includes(cat));
        // 0개가 되면 허용된 전체를 다시 채움
        if (currentParams.categoryList.length === 0) currentParams.categoryList = [...allowedCategories];
    }
    
    syncUIFromParams();
    triggerSearch();
}

//  카테고리 다중 선택 (최소 1개)
function handleCategoryChange(checkbox) {
    const checkedBoxes = document.querySelectorAll('#categoryFilterContainer input[type="checkbox"]:checked');
    
    if (checkedBoxes.length === 0) {
        alert("카테고리는 최소 1개 이상 선택해야 합니다.");
        checkbox.checked = true;
        return;
    }

    currentParams.categoryList = Array.from(checkedBoxes).map(cb => cb.value);
    currentParams.page = 1;
    triggerSearch();
}

function doSearch() {
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        currentParams.keyword = searchInput.value.trim();
        currentParams.page = 1;
        triggerSearch();
    }
}

function setSort(sortValue) {
    currentParams.sort = sortValue;
    currentParams.page = 1;
    triggerSearch();
}

function goPage(pageNum) {
    currentParams.page = pageNum;
    triggerSearch();
}

// 쿼리스트링 조립 및 전송 (kind 포함)
function triggerSearch() {
    const query = new URLSearchParams();
    
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('view') === 'favorite') {
        query.set('view', 'favorite');
    }

    query.set('page', currentParams.page);
    query.set('matchScope', currentParams.matchScope);
    query.set('kind', currentParams.kind);
    query.set('sort', currentParams.sort);
    
    if (currentParams.keyword) {
        query.set('keyword', currentParams.keyword);
    }
    
    if (currentParams.categoryList && currentParams.categoryList.length > 0) {
        query.set('categoryList', currentParams.categoryList.join(','));
    }

    window.location.href = window.location.pathname + '?' + query.toString();
}
// 화면 동기화
function syncUIFromParams() {
    // 역할(kind) 버튼 동기화
    const roleButtons = document.querySelectorAll('.filters .scope:first-of-type button');
    const types = ['all', 'MEMBER', 'MENTOR'];
    roleButtons.forEach((btn, index) => {
        if (types[index] === currentParams.kind) {
            btn.classList.add('on');
        } else {
            btn.classList.remove('on');
        }
    });

    // 매칭범위 버튼 동기화
    const scopeButtons = document.querySelectorAll('#scopeFilterBtns button');
    scopeButtons.forEach(btn => {
        if (btn.textContent.trim() === currentParams.matchScope) btn.classList.add('on');
        else btn.classList.remove('on');
    });

    // 카테고리 체크박스 및 라벨 숨김/보임 처리
    const checkboxes = document.querySelectorAll('#categoryFilterContainer input[type="checkbox"]');
    checkboxes.forEach(cb => {
        const label = document.querySelector(`label[for="${cb.id}"]`);
        
        // 체크 상태 복구
        cb.checked = currentParams.categoryList.includes(cb.value);

        // 보이기 숨기기
        if (currentParams.matchScope === '전국') {
            if (cb.value === '공모전' || cb.value === '사이드프로젝트') {
                if (label) label.style.display = 'inline-flex';
            } else {
                if (label) label.style.display = 'none';
            }
        } else {
            if (cb.value === '사이드프로젝트') {
                if (label) label.style.display = 'none';
            } else {
                if (label) label.style.display = 'inline-flex';
            }
        }
    });

    const searchInput = document.getElementById("searchInput");
    if (searchInput) searchInput.value = currentParams.keyword;
}

function initEventListeners() {
    document.querySelectorAll('#categoryFilterContainer input[type="checkbox"]').forEach(cb => {
        cb.addEventListener('change', function() { handleCategoryChange(this); });
    });
}

//정렬
function setSort(sortValue) {
    currentParams.sort = sortValue;
    currentParams.page = 1; // 정렬 변경 시 1페이지로 초기화
    triggerSearch();
}

//검색
function doSearch() {
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        currentParams.keyword = searchInput.value.trim();
        currentParams.page = 1; // 검색 시 첫 페이지(1페이지)로 초기화
    
        triggerSearch(); 
    }
}