//프로젝트 모집 등록 양식 보내기
    function submitProject(status) {
        // 1. 필수 입력값 검사 (제목)
        const title = document.getElementById("title").value.trim();
        if(!title) {
            alert("프로젝트 제목을 입력해주세요.");
            document.getElementById("title").focus();
            return;
        }

        // 2. 화면에 있는 태그(span)들의 글자만 뽑아서 쉼표(,)로 연결
        const tagElements = document.querySelectorAll('.tag-input .tg');
        const tagsArray = Array.from(tagElements).map(tg => tg.textContent.replace(' ×', '').trim());
        document.getElementById("hiddenTags").value = tagsArray.join(',');

        //  임시저장인지 등록인지 상태값 설정
        document.getElementById("projectStatus").value = status;

        // 서버로 폼 전송
        document.getElementById("projectForm").submit();
    }

    // 태그 데이터를 관리할 배열
let tagsArray = [];

function handleTagInput(event) {
    if (event.key === 'Enter') {
        event.preventDefault(); // 폼이 자동으로 제출되는 것을 막음
        
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

// 화면에 뱃지를 다시 그려주고 hidden input에 값(쉼표 구분)을 세팅하는 함수
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
        span.innerHTML = `${tag} <b onclick="removeTag(${index})">×</b>`;
        tagBox.insertBefore(span, input);
    });
    
    // 백엔드로 넘어갈 hidden 필드에 쉼표로 연결된 문자열 대입 (예: "Spring,React,MySQL")
    hiddenTags.value = tagsArray.join(',');
}

// 태그 뱃지 안의 X 버튼을 눌렀을 때 삭제하는 함수
function removeTag(index) {
    tagsArray.splice(index, 1);
    updateTagUI();
}