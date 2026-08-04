function changeScope(scope, btnElement) {
        //  버튼 활성화 UI 변경
        const btns = document.querySelectorAll('#scopeFilterBtns button');
        btns.forEach(btn => btn.classList.remove('on'));
        btnElement.classList.add('on');

        //  카테고리 HTML 교체
        const categoryContainer = document.getElementById('categoryFilterContainer');
        if (scope === '교내') {
            categoryContainer.innerHTML = `
                <input type="checkbox" id="c1" value="CONTEST" checked><label for="c1">공모전</label>
                <input type="checkbox" id="c2" value="DEPARTMENT"><label for="c2">학과</label>
                <input type="checkbox" id="c3" value="LIBERAL"><label for="c3">교양</label>
                <input type="checkbox" id="c4" value="CLUB"><label for="c4">교내활동</label>
            `;
        } else if (scope === '전국') {
            categoryContainer.innerHTML = `
                <input type="checkbox" id="c1" value="CONTEST" checked><label for="c1">공모전</label>
                <input type="checkbox" id="c5" value="SIDE_PROJECT"><label for="c5">사이드프로젝트</label>
            `;
        }
        if(typeof doSearch === 'function') {
            doSearch();
        }
    }
    
// 비로그인 유저가 접근할 때 호출되는 함수
function requireLogin() {
    alert("로그인 후 이용할 수 있어요!");
    location.href = ctx + "/auth/login"; 
}