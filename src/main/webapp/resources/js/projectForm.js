// 사용자가 입력한 태그들을 메모리에 보관할 배열
let tagsArray = [];

window.addEventListener('DOMContentLoaded', () => {
    //수정 모드일 때 기존에 등록된 태그가 있다면 불러와서 배열에 담고 화면에 태그 뱃지로 복원
    const hiddenTags = document.getElementById('hiddenTags');
    if (hiddenTags && hiddenTags.value.trim() !== '') {
        tagsArray = hiddenTags.value.split(',').map(t => t.trim());
        updateTagUI();
    }
    //페이지가 켜질 때 현재 선택된 매칭 범위(교내/전국)에 맞는 카테고리 목록 세팅
    changeCategoryOptions();
});


//  매칭 범위에 따른 카테고리 동적 변경 함수
function changeCategoryOptions() {
    const matchScope = document.getElementById('matchScope').value; // '교내' 또는 '전국'
    const categorySelect = document.getElementById('categorySelect'); // 카테고리 select 박스
    const savedCategory = document.getElementById('selectedCategory').value; // 수정 모드 시 기존 선택값

    categorySelect.innerHTML = '';

    if (matchScope === '교내') {
        // '교내' 선택 시 제공할 카테고리 옵션 리스트
        const options = [
            { value: 'CONTEST', text: '공모전' },
            { value: 'DEPARTMENT', text: '학과' },
            { value: 'LIBERAL', text: '교양' },
            { value: 'CLUB', text: '교내활동' }
        ];
        // 옵션들을 순회하며 <option> 태그 생성 후 추가
        options.forEach(opt => {
            const el = document.createElement('option');
            el.value = opt.value;
            el.textContent = opt.text;
            // 수정 모드일 때 기존에 저장된 카테고리와 일치하면 기본 선택 처리
            if (savedCategory === opt.value) el.selected = true;
            categorySelect.appendChild(el);
        });
    } else if (matchScope === '전국') {
        // '전국' 선택 시 제공할 카테고리 옵션 리스트 (공모전, 사이드 프로젝트)
        const options = [
            { value: 'CONTEST', text: '공모전' },
            { value: 'SIDE_PROJECT', text: '사이드 프로젝트' }
        ];
        options.forEach(opt => {
            const el = document.createElement('option');
            el.value = opt.value;
            el.textContent = opt.text;
            // 수정 모드일 때 기존에 저장된 카테고리와 일치하면 기본 선택 처리
            if (savedCategory === opt.value) el.selected = true;
            categorySelect.appendChild(el);
        });
    }
}



//  사진 첨부(이미지 업로드) 안내 함수

// 툴바의 사진 아이콘을 통해 파일을 선택했을 때 실행되는 함수
function handleImageUpload(input) {
    if (input.files && input.files[0]) {
        const fileName = input.files[0].name;
        const textarea = document.getElementById('summaryArea');
        
        // 텍스트area 하단에 선택된 이미지 파일명이 포함된 안내 문구를 자동으로 덧붙임
        textarea.value += `\n[첨부된 이미지: ${fileName}]\n`;
    }
}



//태그 입력 및 관리 함수들

// 태그 입력창에서 엔터(Enter) 키를 눌렀을 때 실행되는 함수
function handleTagInput(event) {
    if (event.key === 'Enter') {
        event.preventDefault(); // 엔터 입력 시 폼이 제멋대로 제출되는 기본 동작 방지
        
        const input = document.getElementById('tagInput');
        const tagValue = input.value.trim();
        
        // 입력값이 비어있지 않고, 이미 추가된 중복 태그가 아닐 경우에만 배열에 추가
        if (tagValue !== '' && !tagsArray.includes(tagValue)) {
            tagsArray.push(tagValue);
            updateTagUI(); // 화면 UI 갱신
            input.value = ''; // 입력창 초기화
        }
    }
}

// 태그 배열에 담긴 데이터를 바탕으로 화면에 태그 뱃지(HTML)를 예쁘게 다시 그려주는 함수
function updateTagUI() {
    const tagBox = document.getElementById('tagBox');
    const input = document.getElementById('tagInput');
    const hiddenTags = document.getElementById('hiddenTags');
    
    // 이미 생성되어 있던 기존 태그 뱃지(span)들은 전부 삭제 (입력창 input은 제외)
    const existingTags = tagBox.querySelectorAll('.tg');
    existingTags.forEach(el => el.remove());
    
    // 태그 배열을 돌면서 각각의 태그 뱃지 요소를 생성하고 입력창 앞으로 삽입
    tagsArray.forEach((tag, index) => {
        const span = document.createElement('span');
        span.className = 'tg';
        span.style.cssText = "background:var(--surface-alt); padding:2px 8px; border-radius:4px; font-size:13px; display:inline-flex; align-items:center; gap:4px;";
        // 태그 텍스트와 삭제용 '×' 버튼 생성 (클릭 시 removeTag 실행)
        span.innerHTML = `${tag} <b onclick="removeTag(${index})" style="cursor:pointer; color:var(--ink-soft);">×</b>`;
        tagBox.insertBefore(span, input);
    });
    
    // 서버(백엔드)로 전송하기 위해 태그들을 쉼표(,)로 묶어서 hidden input에 대입
    if (hiddenTags) {
        hiddenTags.value = tagsArray.join(',');
    }
}

// 태그 뱃지 우측의 '×' 버튼을 눌렀을 때 해당 태그를 삭제하는 함수
function removeTag(index) {
    tagsArray.splice(index, 1); // 배열에서 지정한 인덱스의 태그 제거
    updateTagUI(); // 화면 갱신
}



//  대상 학년 버튼 선택 함수
function selectGrade(gradeValue, buttonElement) {
    // 숨겨진 input(targetGrade)에 사용자가 선택한 학년 값 대입
    document.getElementById('targetGrade').value = gradeValue;
    
    // 모든 학년 버튼에서 'active' 클래스 제거하여 비활성화 상태로 만듦
    const buttons = document.querySelectorAll('.btn-grade');
    buttons.forEach(btn => btn.classList.remove('active'));
    
    // 방금 클릭한 버튼에만 'active' 클래스를 추가하여 선택된 스타일 적용
    buttonElement.classList.add('active');
}



//  프로젝트 등록 및 수정 폼 제출 검증 함수

function submitProject(status) {
    const titleInput = document.getElementById("title");
    const title = titleInput ? titleInput.value.trim() : "";
    
    // 필수 입력값인 '프로젝트 제목'이 비어있는지 검사
    if (!title) {
        alert("프로젝트 제목을 입력해주세요.");
        if (titleInput) titleInput.focus();
        return;
    }

    // 프로젝트 상태값(모집중/마감) 반영 여부 확인
    const projectStatusInput = document.getElementById("projectStatus");
    if (projectStatusInput && projectStatusInput.value) {
        // 라디오 버튼을 통해 설정된 상태값 유지
    }

    // 최종적으로 폼(form) 데이터 전송 실행
    const projectForm = document.getElementById("projectForm");
    if (projectForm) {
        projectForm.submit();
    }
}
