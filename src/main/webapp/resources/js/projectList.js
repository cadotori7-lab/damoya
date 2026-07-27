// 사용자가 현재 선택한 필터 상태를 저장하여 여러 함수에서 공유
let currentTab = 'all';    // 상단 탭 상태 
let currentScope = '교내'; // 매칭 범위 상태


//  최신순, 마감임박순 등의 버튼 클릭 시 URL 파라미터를 변경해 새로고침
function sortList(sortType) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('sort', sortType); 
    window.location.href = window.location.pathname + '?' + urlParams.toString();
}

//  전체 / 모집중 / 모집마감 탭 클릭 시 UI 업데이트 및 필터링
function filterTab(status) {
    currentTab = status; 
    
    // 기존 활성화된 탭 스타일 제거
    document.querySelectorAll('.tab-item').forEach(btn => btn.classList.remove('active'));
    
    // 클릭된 탭에 활성화 스타일 추가
    const target = (window.event && window.event.target) ? window.event.target : null;
    if (target) {
        target.classList.add('active');
    }
    
    // 탭 상태가 변경되었으므로 즉시 목록 재필터링
    filterProjects();
}


// 교내 혹은 전국 버튼 클릭 시 처리
function setScope(scopeType, btnElement) {
    currentScope = scopeType; // 전역 변수 업데이트
    
    document.querySelectorAll('.scope button').forEach(btn => btn.classList.remove('on'));
    
    if (btnElement) {
        btnElement.classList.add('on');
    } else if (window.event && window.event.target) {
        window.event.target.classList.add('on');
    }

    //  매칭 범위에 따라 표시할 카테고리 항목들을 다르게 세팅 
    const categoryContainer = document.getElementById('categoryContainer');

    if (categoryContainer) {
        if (scopeType === '교내' || scopeType === 'CAMPUS') {
            categoryContainer.innerHTML = `
                <input type="checkbox" id="c1" value="공모전" checked><label for="c1">공모전</label>
                <input type="checkbox" id="c2" value="학과" checked><label for="c2">학과</label>
                <input type="checkbox" id="c3" value="교양" checked><label for="c3">교양</label>
                <input type="checkbox" id="c4" value="교내활동" checked><label for="c4">교내활동</label>
            `;
        } else if (scopeType === '전국' || scopeType === 'NATION') {
            categoryContainer.innerHTML = `
                <input type="checkbox" id="c1" value="공모전" checked><label for="c1">공모전</label>
                <input type="checkbox" id="c2" value="사이드 프로젝트" checked><label for="c2">사이드 프로젝트</label>
            `;
        }
    }

    //  새로 생성된 카테고리 HTML 요소들에 클릭 이벤트를 다시 연결해주고 필터링 수행
    bindFilterEvents();
    filterProjects();
}

// 영문 DB 데이터나 띄어쓰기가 다른 데이터를 하나의 표준 한글명으로 통일
function getCategoryName(category) {
    if (!category) return '';
    const cat = category.toUpperCase().trim();
    if (cat === 'CONTEST' || cat === '공모전') return '공모전';
    if (cat === 'DEPARTMENT' || cat === '학과') return '학과';
    if (cat === 'LIBERAL' || cat === '교양') return '교양';
    if (cat === 'SIDE_PROJECT' || cat === '사이드 프로젝트') return '사이드 프로젝트';
    if (cat === 'CLUB' || cat === 'ACTIVITIES' || cat === '교내활동') return '교내활동';
    return category; 
}

// 사용자가 체크박스)를 클릭했을 때 발생하는 이벤트 제어
function handleFilterChange(e) {
    const target = e.target;
    
    //  카테고리 영역을 클릭한 경우 (최소 1개 선택)
    if (target.closest('#categoryContainer')) {
        if (target.tagName !== 'INPUT' && target.tagName !== 'LABEL') {
            target.classList.toggle('active');
        }

        // 현재 선택되어 있는 카테고리 개수를 파악
        const checkedCats = document.querySelectorAll('#categoryContainer input[type="checkbox"]:checked, #categoryContainer .active');
        
        // 마지막 남은 1개를 해제하려고 하면 경고를 띄우고 다시 선택 상태로 강제 복구
        if (checkedCats.length === 0) {
            alert("카테고리는 최소 1개 이상 선택해야 합니다.");
            if (target.tagName === 'INPUT') target.checked = true;
            else target.classList.add('active');
            return;
        }
    }

    //대상 학년 영역을 클릭한 경우 (학년 무관 ↔ 개별 학년 상호작용 규칙)

    if (!target.closest('#categoryContainer') && (target.closest('.filters') || target.closest('#gradeContainer'))) {
        const gradeContainer = document.getElementById('gradeContainer') || target.closest('.flt');
        if (!gradeContainer) return;

        if (target.tagName !== 'INPUT' && target.tagName !== 'LABEL') {
            target.classList.toggle('active');
        }

        const gradeAny = gradeContainer.querySelector('#y0, .grade-any'); // '학년 무관' 요소
        const specGrades = gradeContainer.querySelectorAll('input[type="checkbox"]:not(#y0), .grade-spec, button:not(#y0):not(.grade-any)'); // '1~4학년' 요소들

        // 사용자가 방금 클릭한 것이 '학년 무관'인지 판별
        const isAnyClicked = target.id === 'y0' || target.classList.contains('grade-any') || target.getAttribute('data-value') === 'ALL';

        if (isAnyClicked) {
            //  '학년 무관'을 클릭했다면  선택되어 있던 1~4학년을 모두 강제 해제
            specGrades.forEach(el => {
                if (el.tagName === 'INPUT') el.checked = false;
                else el.classList.remove('active');
            });
            
            // 학년 무관 자체는 활성화 상태로 둠
            if (gradeAny) {
                if (gradeAny.tagName === 'INPUT') gradeAny.checked = true;
                else gradeAny.classList.add('active');
            }
        } else {
            // 개별 학년(1, 2, 3, 4학년)을 클릭했다면
            // 현재 선택된 개별 학년이 몇 개인지 파악
            const activeSpecs = Array.from(specGrades).filter(el => el.checked || el.classList.contains('active'));
            
            //  1, 2, 3, 4학년(총 4개)이 모두 선택된 경우  '학년 무관' 하나만 켜진 상태로 자동 전환
            if (activeSpecs.length === 4) {
                specGrades.forEach(el => {
                    if (el.tagName === 'INPUT') el.checked = false;
                    else el.classList.remove('active');
                });
                if (gradeAny) {
                    if (gradeAny.tagName === 'INPUT') gradeAny.checked = true;
                    else gradeAny.classList.add('active');
                }
            } 
            //개별 학년이 1개~3개 선택된 경우 학년 무관'을 꺼줌
            else if (activeSpecs.length > 0) {
                if (gradeAny) {
                    if (gradeAny.tagName === 'INPUT') gradeAny.checked = false;
                    else gradeAny.classList.remove('active');
                }
            } 
            // 선택했던 개별 학년들을 다 눌러서 선택 해제(0개)된 경우  자동으로 '학년 무관'으로 
            else {
                if (gradeAny) {
                    if (gradeAny.tagName === 'INPUT') gradeAny.checked = true;
                    else gradeAny.classList.add('active');
                }
            }
        }
    }

    filterProjects();
}



// 탭 + 매칭 범위 + 카테고리 + 대상 학년 + 검색어 5가지 조건을 모두 검사
function filterProjects() {
    const searchInput = document.getElementById('searchInput');
    const keyword = searchInput ? searchInput.value.toLowerCase().trim() : '';

    // 카테고리 선택 값 추출
    const categoryElements = document.querySelectorAll('#categoryContainer input[type="checkbox"]:checked, #categoryContainer .active, #categoryContainer .filter-chip.active');
    const checkedCategories = Array.from(categoryElements).map(el => {
        return (el.value || el.getAttribute('data-value') || el.innerText).trim();
    });

    //  학년 선택 값 추출
    const gradeElements = document.querySelectorAll('#gradeContainer input[type="checkbox"]:checked, #gradeContainer .active, .filters .flt:not(#categoryContainer) input[type="checkbox"]:checked, .filters .flt:not(#categoryContainer) .active');
    const checkedGrades = Array.from(gradeElements).map(el => {
        return (el.value || el.getAttribute('data-value') || el.innerText).trim();
    });

    // 사용자가 '학년 무관'을 체크한 상태인지 판별하는 플래그
    const isGradeAnyChecked = checkedGrades.some(g => g === 'ALL' || g === '학년 무관' || g === '무관' || g === 'y0');

    // DOM에 렌더링 된 모든 프로젝트 카드 요소들
    const cards = document.querySelectorAll('.card-item');

    cards.forEach(card => {
        // 매칭 범위 필터링
        const rawMatch = (card.getAttribute('data-match') || '').trim();
        let matchScopeCond = false;
        if (currentScope === '교내') {
            matchScopeCond = (rawMatch === '교내' || rawMatch === 'CAMPUS' || rawMatch === '');
        } else if (currentScope === '전국') {
            matchScopeCond = (rawMatch === '전국' || rawMatch === 'NATION');
        }

        // 카테고리 필터링 
        const rawCategory = card.getAttribute('data-category') || '';
        const categoryName = getCategoryName(rawCategory); 
        let matchCategory = checkedCategories.length === 0 || checkedCategories.includes(categoryName);

        // 대상 학년 필터링
        const cardGrade = (card.getAttribute('data-grade') || '').trim();
        const cardGradeArr = cardGrade.split(',').map(g => g.trim()); // 1,2학년 동시 등록 글 대응

        let matchGrade = false;
        if (isGradeAnyChecked) {
            // 사이드바에서 '학년 무관'을 골랐다면, 게시글의 학년 제한과 상관없이 모든 글 노출
            matchGrade = true; 
        } else {
            // 게시글 자체가 1~4학년 다 받는 '무관' 글로 등록되었는지 여부
            const isCardAnyGrade = (cardGrade === 'ALL' || cardGrade === '무관' || cardGrade === '학년 무관' || cardGrade === '');
            // 사이드바에서 고른 학년이 게시글이 요구하는 학년 배열에 하나라도 속하는지 여부
            const isGradeMatched = cardGradeArr.some(g => checkedGrades.some(cg => cg.includes(g)));
            
            matchGrade = isCardAnyGrade || isGradeMatched;
        }

        //  모집 상태 탭 필터링
        const status = card.getAttribute('data-status') || '';
        let matchTab = (currentTab === 'all' || status === currentTab);

        // 검색어 필터링 
        const cardText = card.innerText.toLowerCase();
        let matchKeyword = (keyword === '' || cardText.includes(keyword));

        // 위 5가지 조건이 모두 true일 때만 화면에 카드 노출 
        if (matchTab && matchScopeCond && matchCategory && matchGrade && matchKeyword) {
            card.style.display = 'flex';
        } else {
            card.style.display = 'none';
        }
    });
}


// [D-Day 계산기] 마감일이 지난 카드는 상태를 강제로 'CLOSED'로 변경하고 D-Day 뱃지를 조작함
function updateDdayAndStatus() {
    const cards = document.querySelectorAll('.card-item');
    const today = new Date();
    today.setHours(0, 0, 0, 0); // 시간 단위를 날리고 날짜 단위로만 정확히 비교하기 위함

    cards.forEach(card => {
        const endDateStr = card.getAttribute('data-end-date'); 
        if (!endDateStr) return;

        const endDate = new Date(endDateStr);
        endDate.setHours(0, 0, 0, 0);

        // 마감일까지 남은 일수 계산
        const diffTime = endDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        const statusBadge = card.querySelector('.status-badge');
        const dDaySpan = card.querySelector('.d-day-badge');

        if (diffDays < 0) {
            // 이미 마감일이 지났다면 자바스크립트 단에서 시각적으로 마감 처리
            card.setAttribute('data-status', 'CLOSED'); 
            if (statusBadge) {
                statusBadge.className = 'status-badge closed';
                statusBadge.textContent = '모집마감';
            }
            if (dDaySpan) {
                dDaySpan.style.display = 'none'; 
            }
        } else {
            // 아직 마감 전이라면 D-n 형식으로 표시
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

//  동적으로 HTML이 변경될 때마다(ex. 교내↔전국 변경 시) 기존 이벤트를 지우고 다시 씌워줌
function bindFilterEvents() {
    document.querySelectorAll('.filters input[type="checkbox"], .filters label, .filters button, .filters .filter-chip').forEach(el => {
        // 중복 등록 방지를 위해 리스너 제거 먼저 수행
        el.removeEventListener('change', handleFilterChange);
        el.removeEventListener('click', handleFilterChange);
        
        // 매칭 범위 버튼 자체는 setScope() 함수를 타야 하므로 여기서는 이벤트 리스너를 건너뜀
        if (!el.closest('.scope')) {
            if (el.tagName === 'INPUT') {
                el.addEventListener('change', handleFilterChange);
            } else {
                el.addEventListener('click', handleFilterChange);
            }
        }
    });
}

//  브라우저가 화면을 다 그린 직후 최초 1회 실행되는 영역
document.addEventListener("DOMContentLoaded", function() {
    //  D-Day 및 마감 상태 우선 계산 (이 값이 세팅되어야 모집중/마감 탭 필터가 정확히 돎)
    updateDdayAndStatus();

    //  초기 세팅: 대상 학년을 '학년 무관'으로 강제 설정
    const gradeAny = document.getElementById('y0') || document.querySelector('.grade-any');
    if (gradeAny) {
        if (gradeAny.tagName === 'INPUT') gradeAny.checked = true;
        else gradeAny.classList.add('active');
    }

    bindFilterEvents();

    // 4상단 검색창에 키보드 입력 발생 시 실시간 필터링 동작 연결
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', filterProjects);
    }

    //  달력 입력(input type="date")에서 오늘 이전 과거 날짜는 선택할 수 없도록 
    const endDateInput = document.getElementById("endDate");
    if (endDateInput) {
        const todayStr = new Date().toISOString().split('T')[0];
        endDateInput.min = todayStr;
    }
    filterProjects();
});