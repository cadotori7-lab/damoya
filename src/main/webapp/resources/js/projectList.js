    // 정렬 함수
    function sortList(sortType) {
      const urlParams = new URLSearchParams(window.location.search);
      urlParams.set('sort', sortType);
      window.location.href = window.location.pathname + '?' + urlParams.toString();
    }

    // 교내 / 전국 범위 선택에 따른 카테고리 동적 변경 함수
    function setScope(scopeType) {
      document.querySelectorAll('.scope button').forEach(btn => btn.classList.remove('on'));
      event.target.classList.add('on');

      const categoryContainer = document.getElementById('categoryContainer');

      if (scopeType === 'CAMPUS') {
        // 교내 선택 시: 공모전, 학과, 교양, 교내활동
        categoryContainer.innerHTML = `
          <input type="checkbox" id="c1" value="CONTEST" checked><label for="c1">공모전</label>
          <input type="checkbox" id="c2" value="DEPARTMENT"><label for="c2">학과</label>
          <input type="checkbox" id="c3" value="LIBERAL"><label for="c3">교양</label>
          <input type="checkbox" id="c4" value="ACTIVITIES"><label for="c4">교내활동</label>
        `;
      } else if (scopeType === 'NATION') {
        // 전국 선택 시: 공모전, 사이드 프로젝트
        categoryContainer.innerHTML = `
          <input type="checkbox" id="c1" value="CONTEST" checked><label for="c1">공모전</label>
          <input type="checkbox" id="c2" value="SIDE_PROJECT"><label for="c2">사이드 프로젝트</label>
        `;
      }
    }