document.addEventListener('DOMContentLoaded', () => {
    const tagInputBox = document.getElementById('tagInputBox');
    const tagTextInput = document.getElementById('tagTextInput');
    const tagsHiddenInput = document.getElementById('tagsHiddenInput');
    const talentForm = document.getElementById('talentForm') || document.querySelector('form');
    const scopeRadios = document.querySelectorAll('input[name="matchScope"]');

    let tagsArray = [];

    //  기존에 저장된 태그가 있다면 불러와서 화면에 세팅
    if (tagsHiddenInput && tagsHiddenInput.value.trim() !== '') {
        tagsArray = tagsHiddenInput.value.split(',').map(tag => tag.trim()).filter(tag => tag !== '');
        renderTags();
    }

    //  태그 입력창 키 입력 이벤트 (엔터 또는 쉼표)
    if (tagTextInput) {
        tagTextInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ',') {
                e.preventDefault(); 
                
                let tagValue = tagTextInput.value.trim().replace(/^,+|,+$/g, ''); 
                
                if (tagValue !== '') {
                    if (tagsArray.length >= 6) {
                        alert('태그는 최대 6개까지만 등록할 수 있습니다.');
                        tagTextInput.value = '';
                        return;
                    }

                    if (tagValue.length > 20) {
                        alert('개별 태그는 20자 이내로 입력해 주세요.');
                        return;
                    }

                    if (!tagsArray.includes(tagValue)) {
                        tagsArray.push(tagValue);
                        renderTags();
                    }
                    tagTextInput.value = ''; 
                }
            } else if (e.key === 'Backspace' && tagTextInput.value === '' && tagsArray.length > 0) {
                tagsArray.pop();
                renderTags();
            }
        });
    }

    // 태그 뱃지 그려주는 함수
    function renderTags() {
        if (!tagInputBox) return;

        const existingTags = tagInputBox.querySelectorAll('.tg');
        existingTags.forEach(el => el.remove());

        tagsArray.forEach((tag, index) => {
            const tagBadge = document.createElement('div');
            tagBadge.className = 'tg';
            tagBadge.innerHTML = `
                #${tag} 
                <b onclick="window.removeTalentTag(${index})" title="삭제" style="cursor:pointer; margin-left:4px;">×</b>
            `;
            tagInputBox.insertBefore(tagBadge, tagTextInput);
        });

        if (tagsHiddenInput) {
            tagsHiddenInput.value = tagsArray.join(',');
        }

        if (tagsArray.length >= 6) {
            tagTextInput.placeholder = '최대 6개까지 등록되었습니다.';
        } else {
            tagTextInput.placeholder = '태그 입력 후 엔터 (예: Spring, React)';
        }
    }

    // 개별 태그 삭제 함수
    window.removeTalentTag = function(index) {
        tagsArray.splice(index, 1);
        renderTags();
    };

    // 최초 페이지 로드 시 카테고리 렌더링 및 기존 선택값 복원
    updateCategories();

    // 매칭 범위 라디오 버튼 변경 시 카테고리 동적 변경 이벤트 연결
    scopeRadios.forEach(radio => {
        radio.addEventListener('change', () => {
            updateCategories();
        });
    });

    //  폼 제출 시 유효성 검사 (관심 카테고리 1개 이상 필수 체크)
    if (talentForm) {
        talentForm.addEventListener('submit', (e) => {
            const checkedCategories = document.querySelectorAll('input[name="categoryList"]:checked');
            const errorMsg = document.getElementById('categoryError');
            
            if (checkedCategories.length === 0) {
                alert('관심 카테고리를 1개 이상 선택해 주세요.');
                
                if(errorMsg) errorMsg.style.display = 'block';
                
                const categoryWrapper = document.getElementById('categoryWrapper');
                if(categoryWrapper) {
                    categoryWrapper.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }

                e.preventDefault(); // 전송 중단
            } else {
                if(errorMsg) errorMsg.style.display = 'none';
            }
        });
    }
});


// 카테고리 동적 생성 및 수정 모드 데이터 복원 함수
function updateCategories() {
    const scopeRadio = document.querySelector('input[name="matchScope"]:checked');
    if (!scopeRadio) return;
    
    const scope = scopeRadio.value;
    const wrapper = document.getElementById('categoryWrapper');
    if (!wrapper) return;
    
    let categoriesHtml = '';
    if (scope === '교내') {
        categoriesHtml = `
            <input type="checkbox" name="categoryList" value="공모전" id="tpc1"><label for="tpc1">공모전</label>
            <input type="checkbox" name="categoryList" value="학과" id="tpc2"><label for="tpc2">학과</label>
            <input type="checkbox" name="categoryList" value="교양" id="tpc3"><label for="tpc3">교양</label>
            <input type="checkbox" name="categoryList" value="교내활동" id="tpc4"><label for="tpc4">교내활동</label>
        `;
    } else if (scope === '전국') {
        categoriesHtml = `
            <input type="checkbox" name="categoryList" value="공모전" id="tpc1"><label for="tpc1">공모전</label>
            <input type="checkbox" name="categoryList" value="사이드프로젝트" id="tpc5"><label for="tpc5">사이드프로젝트</label>
        `;
    }
    wrapper.innerHTML = categoriesHtml;

    // 수정 모드일 때 기존에 체크되어 있던 카테고리 자동 체크
    const savedCategory = window.savedCategory || "";
    if (savedCategory) {
        const checkboxes = wrapper.querySelectorAll('input[name="categoryList"]');
        checkboxes.forEach(cb => {
            if (savedCategory.includes(cb.value)) {
                cb.checked = true;
            }
        });
    }
}