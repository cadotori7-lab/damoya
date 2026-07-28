// 태그 데이터를 관리할 배열
let tagsArray = [];

// 페이지가 로드될 때 (수정 모드 등) 기존 태그가 숨겨진 input에 있다면 배열로 복원
window.addEventListener('DOMContentLoaded', () => {
    const hiddenTags = document.getElementById('hiddenTags');
    if (hiddenTags && hiddenTags.value.trim() !== '') {
        tagsArray = hiddenTags.value.split(',').map(t => t.trim());
        updateTagUI();
    }
});

// 엔터 키로 태그 추가
function handleTagInput(event) {
    if (event.key === 'Enter') {
        event.preventDefault(); // 폼 자동 제출 방지
        
        const input = document.getElementById('tagInput');
        const tagValue = input.value.trim();
        
        // 빈 값이 아니고, 이미 추가된 태그가 아닐 때만 추가
        if (tagValue !== '' && !tagsArray.includes(tagValue)) {
            tagsArray.push(tagValue);
            updateTagUI();
            input.value = ''; // 입력창 초기화
        }
    }
}

// 화면에 태그 뱃지를 다시 그려주고 hidden input에 쉼표로 묶어 세팅하는 함수
function updateTagUI() {
    const tagBox = document.getElementById('tagBox');
    const input = document.getElementById('tagInput');
    const hiddenTags = document.getElementById('hiddenTags');
    
    // 기존에 그려진 태그 span들만 싹 지우기 (input은 유지)
    const existingTags = tagBox.querySelectorAll('.tg');
    existingTags.forEach(el => el.remove());
    
    // 배열을 돌면서 태그 뱃지 HTML 생성 및 input 앞으로 집어넣기
    tagsArray.forEach((tag, index) => {
        const span = document.createElement('span');
        span.className = 'tg';
        span.style.cssText = "background:var(--surface-alt); padding:2px 8px; border-radius:4px; font-size:13px; display:inline-flex; align-items:center; gap:4px;";
        span.innerHTML = `${tag} <b onclick="removeTag(${index})" style="cursor:pointer; color:var(--ink-soft);">×</b>`;
        tagBox.insertBefore(span, input);
    });
    
    // 백엔드로 넘어갈 hidden 필드에 쉼표로 연결된 문자열 대입
    if (hiddenTags) {
        hiddenTags.value = tagsArray.join(',');
    }
}

// 태그 뱃지 안의 X 버튼을 눌렀을 때 삭제하는 함수
function removeTag(index) {
    tagsArray.splice(index, 1);
    updateTagUI();
}

// 대상 학년 버튼 선택 함수 
function selectGrade(gradeValue, buttonElement) {
    // hidden input에 선택된 학년 값 세팅
    document.getElementById('targetGrade').value = gradeValue;
    
    // 모든 학년 버튼에서 active 클래스 제거
    const buttons = document.querySelectorAll('.btn-grade');
    buttons.forEach(btn => btn.classList.remove('active'));
    
    // 클릭한 버튼에만 active 클래스 추가
    buttonElement.classList.add('active');
}

// 프로젝트 모집 등록/수정 양식 전송
function submitProject(status) {
    const titleInput = document.getElementById("title");
    const title = titleInput ? titleInput.value.trim() : "";
    
    if (!title) {
        alert("프로젝트 제목을 입력해주세요.");
        if (titleInput) titleInput.focus();
        return;
    }

    const projectStatusInput = document.getElementById("projectStatus");
    if (projectStatusInput) {
        projectStatusInput.value = status;
    }

    const projectForm = document.getElementById("projectForm");
    if (projectForm) {
        projectForm.submit();
    }
}