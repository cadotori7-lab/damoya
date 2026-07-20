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