// 사용자가 입력한 태그들을 메모리에 보관할 배열
let tagsArray = [];

window.addEventListener('DOMContentLoaded', () => {
    // 수정 모드일 때 기존에 등록된 태그가 있다면 불러와서 배열에 담고 화면에 태그 뱃지로 복원
    const hiddenTags = document.getElementById('hiddenTags');
    if (hiddenTags && hiddenTags.value.trim() !== '') {
        tagsArray = hiddenTags.value.split(',').map(t => t.trim());
        updateTagUI();
    }
    
    // 페이지가 켜질 때 현재 선택된 매칭 범위(교내/전국)에 맞는 카테고리 목록 세팅
    changeCategoryOptions();
    
    // 매칭 범위가 바뀔 때마다 카테고리 갱신되도록 이벤트 리스너 연결
    const matchScopeEl = document.getElementById('matchScope');
    if (matchScopeEl) {
        matchScopeEl.addEventListener('change', changeCategoryOptions);
    }
    
    // 툴바 버튼들에 텍스트 서식 기능(굵기, 기울임 등) 연결
    initEditorToolbar();
});


//  매칭 범위에 따른 카테고리 동적 변경 함수 (DB 저장용 한글 value 매핑)
function changeCategoryOptions() {
    const matchScope = document.getElementById('matchScope').value; // '교내' 또는 '전국'
    const categorySelect = document.getElementById('categorySelect'); // 카테고리 select 박스
    const savedCategory = document.getElementById('selectedCategory').value; // 수정 모드 시 기존 선택값

    categorySelect.innerHTML = '';

    let options = [];

    if (matchScope === '교내') {
        options = [
            { value: '공모전', text: '공모전' },
            { value: '학과', text: '학과' },
            { value: '교양', text: '교양' },
            { value: '교내활동', text: '교내활동' }
        ];
    } else if (matchScope === '전국') {
        options = [
            { value: '공모전', text: '공모전' },
            { value: '사이드 프로젝트', text: '사이드 프로젝트' }
        ];
    }

    options.forEach(opt => {
        const el = document.createElement('option');
        el.value = opt.value;
        el.textContent = opt.text;
        
        // 수정 모드일 때 기존에 저장된 카테고리와 일치하면 기본 선택 처리
        if (savedCategory === opt.value) {
            el.selected = true;
        }
        categorySelect.appendChild(el);
    });
}


// 2. 사진 첨부(이미지 업로드) 안내 함수
function handleImageUpload(input) {
    if (input.files && input.files[0]) {
        const fileName = input.files[0].name;
        const textarea = document.getElementById('summaryArea');
        
        // 텍스트area 하단에 선택된 이미지 파일명이 포함된 안내 문구를 자동으로 덧붙임
        textarea.value += `\n[첨부된 이미지: ${fileName}]\n`;
    }
}


//  태그 입력 및 관리 함수들
function handleTagInput(event) {
    if (event.key === 'Enter') {
        event.preventDefault(); // 엔터 입력 시 폼 제출 방지
        
        const input = document.getElementById('tagInput');
        const tagValue = input.value.trim();
        
        if (tagValue !== '' && !tagsArray.includes(tagValue)) {
            tagsArray.push(tagValue);
            updateTagUI(); 
            input.value = ''; 
        }
    }
}

function updateTagUI() {
    const tagBox = document.getElementById('tagBox');
    const input = document.getElementById('tagInput');
    const hiddenTags = document.getElementById('hiddenTags');
    
    const existingTags = tagBox.querySelectorAll('.tg');
    existingTags.forEach(el => el.remove());
    
    tagsArray.forEach((tag, index) => {
        const span = document.createElement('span');
        span.className = 'tg';
        span.style.cssText = "background:var(--surface-alt); padding:2px 8px; border-radius:4px; font-size:13px; display:inline-flex; align-items:center; gap:4px;";
        span.innerHTML = `${tag} <b onclick="removeTag(${index})" style="cursor:pointer; color:var(--ink-soft);">×</b>`;
        tagBox.insertBefore(span, input);
    });
    
    if (hiddenTags) {
        hiddenTags.value = tagsArray.join(',');
    }
}

function removeTag(index) {
    tagsArray.splice(index, 1);
    updateTagUI();
}


//  대상 학년 버튼 선택 함수
function selectGrade(gradeValue, buttonElement) {
    document.getElementById('targetGrade').value = gradeValue;
    
    const buttons = document.querySelectorAll('.btn-grade');
    buttons.forEach(btn => btn.classList.remove('active'));
    
    buttonElement.classList.add('active');
}


//  툴바 기능 및 에디터 서식(굵기, 크기 등) 제어 함수
function initEditorToolbar() {
    const toolbarButtons = document.querySelectorAll('.editor-toolbar button');
    
    if (toolbarButtons.length > 0) {
        // JSP 내 툴바 버튼 순서에 맞춰 클릭 이벤트 매핑
        // 0: 굵게, 1: 기울임, 2: 취소선, 3: 링크, 5: 코드블록, 6: 인용구, 7: 글머리 기호, 8: 번호 매기기
        const actions = [
            () => wrapText('**', '**'),          // 굵게
            () => wrapText('*', '*'),            // 기울임
            () => wrapText('~~', '~~'),          // 취소선
            () => wrapText('[', '](url)'),       // 링크
            null, // 이미지 (input[type=file]이 처리)
            () => wrapText('```\n', '\n```'),    // 코드 블록
            () => wrapText('> ', ''),            // 인용구
            () => wrapText('- ', ''),            // 글머리 기호
            () => wrapText('1. ', '')            // 번호 매기기
        ];

        toolbarButtons.forEach((btn, index) => {
            if (actions[index]) {
                btn.addEventListener('click', actions[index]);
            }
        });
    }

    const toolbar = document.querySelector('.editor-toolbar');
    if (toolbar && !document.getElementById('fontSizeSelector')) {
        const select = document.createElement('select');
        select.id = 'fontSizeSelector';
        select.style.cssText = "margin-left: auto; padding: 2px 6px; border: 1px solid #ddd; border-radius: 4px; font-size: 12px; background: #fff; cursor: pointer;";
        select.innerHTML = `
            <option value="">글자 크기</option>
            <option value="12px">작게 (12px)</option>
            <option value="15px">보통 (15px)</option>
            <option value="18px">크게 (18px)</option>
            <option value="22px">아주 크게 (22px)</option>
        `;
        select.onchange = function() {
            changeFontSize(this.value);
            this.value = ""; // 선택 후 초기화
        };
        toolbar.appendChild(select);
    }
}

// 텍스트 감싸기 유틸 함수 
function wrapText(startTag, endTag) {
    const textarea = document.getElementById('summaryArea');
    if (!textarea) return;
    
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const text = textarea.value;

    const selectedText = text.substring(start, end);
    textarea.value = text.substring(0, start) + startTag + selectedText + endTag + text.substring(end);
    
    textarea.focus();
    textarea.setSelectionRange(start + startTag.length, end + startTag.length);
}

// 글자 크기를 HTML span 태그로 감싸서 조절해 주는 함수
function changeFontSize(size) {
    if (!size) return;
    const textarea = document.getElementById('summaryArea');
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const text = textarea.value;

    const selectedText = text.substring(start, end) || "텍스트를 입력하세요";
    const wrappedText = `<span style="font-size: ${size};">${selectedText}</span>`;

    textarea.value = text.substring(0, start) + wrappedText + text.substring(end);
    textarea.focus();
}


// 프로젝트 등록 및 수정 폼 제출 
function submitProject(status) {
    const titleInput = document.getElementById("title");
    const title = titleInput ? titleInput.value.trim() : "";
    
    if (!title) {
        alert("프로젝트 제목을 입력해주세요.");
        if (titleInput) titleInput.focus();
        return;
    }

    const projectForm = document.getElementById("projectForm");
    if (projectForm) {
        projectForm.submit();
    }
}

//현재 일 기준으로 지난 날짜는 선택 못하게끔
document.addEventListener("DOMContentLoaded", function() {
        // 오늘 날짜를 YYYY-MM-DD 포맷으로 구하기
        const today = new Date().toISOString().split('T')[0];
        
        // id가 endDate인 input 요소의 min 속성에 오늘 날짜 대입
        const endDateInput = document.getElementById("endDate");
        if (endDateInput) {
            endDateInput.min = today;
        }
    });