// 대상자 정보를 임시로 저장할 변수
let targetName = "";
let targetMemberId = "";

// 제의 모달 열기
function openOffer(name, field, memberId) {
    targetName = name;
    targetMemberId = memberId;
    
    // 모달 상단 텍스트 및 기본값 세팅 (요소가 화면에 있을 때만)
    const offerWho = document.getElementById('offerWho');
    if (offerWho) offerWho.textContent = name + " 님에게 초대를 보내요";
    
    const offerRole = document.getElementById('offerRole');
    if (offerRole) offerRole.value = field;
    
    // 상태 초기화: 입력 폼은 보여주고, 성공 메시지는 숨기기
    const offerForm = document.getElementById('offerForm');
    const offerSuccess = document.getElementById('offerSuccess');
    
    if (offerForm) offerForm.style.display = 'block';
    if (offerSuccess) offerSuccess.style.display = 'none';

    // 모달 표시
    document.getElementById('offerModal').classList.add('on');
    document.body.style.overflow = 'hidden'; 
}

// 모달 닫기
function closeOffer() {
    document.getElementById('offerModal').classList.remove('on');
    document.body.style.overflow = '';
    
    // 폼 초기화 (요소가 있을 때만)
    const offerForm = document.getElementById('offerForm');
    if (offerForm) offerForm.reset();
}

// 제의 보내기 처리 (프로젝트가 있을 때만 작동함)
function sendOffer() {
    const projId = document.getElementById('offerProj').value;
    const email = document.getElementById('offerEmail').value.trim();
    const role = document.getElementById('offerRole').value.trim();
    const message = document.getElementById('offerMessage').value.trim();

    if (!projId) {
        alert("선택된 프로젝트가 없습니다.");
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailRegex.test(email)) {
        alert("올바른 이메일 형식을 입력해주세요.");
        document.getElementById('offerEmail').focus();
        return;
    }

    const combinedMotive = "[연락처: " + email + "] \n" + message;

    // 백엔드로 전송 (AJAX)
    fetch(ctx + '/talent/offer/send', {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            // 스프링 시큐리티 쓰시는 경우 CSRF 토큰 추가 필요할 수 있음
        },
        body: JSON.stringify({
            projectId: projId,
            memberId: targetMemberId, // 💡 인재풀 작성자의 member_id
            wantPosition: role,
            motive: combinedMotive
        })
    }).then(res => {
        if(res.ok) {
            document.getElementById('offerForm').style.display = 'none'; 
            document.getElementById('offerSuccess').style.display = 'block'; 
            document.getElementById('offerSuccessText').innerHTML = 
                `<b>${targetName}</b> 님에게 함께하기 제의가 전달됐어요.<br>상대가 <b>수락</b>하면 팀원으로 합류해요.`;
        } else {
            alert("제의 전송에 실패했습니다.");
        }
    });
}

// 모달이 열려있을 때 ESC 키를 누르면 닫히는 기능
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        const modal = document.getElementById('offerModal');
        if (modal && modal.classList.contains('on')) {
            closeOffer();
        }
    }
});