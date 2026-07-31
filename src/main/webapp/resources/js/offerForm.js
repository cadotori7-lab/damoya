// 대상자 이름을 임시로 저장할 변수
let targetName = "";

// 제의 모달 열기
function openOffer(name, field) {
    targetName = name;
    
    // 모달 상단 텍스트 및 기본값 세팅
    document.getElementById('offerWho').textContent = name + " 님에게 초대를 보내요";
    document.getElementById('offerRole').value = field;
    
    // 상태 초기화: 입력 폼은 보여주고, 성공 메시지는 숨기기
    document.getElementById('offerForm').style.display = 'block';
    document.getElementById('offerSuccess').style.display = 'none';

    // 모달 표시
    document.getElementById('offerModal').classList.add('on');
    document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
}

//  모달 닫기
function closeOffer() {
    document.getElementById('offerModal').classList.remove('on');
    document.body.style.overflow = '';
    document.getElementById('offerForm').reset();
}

// 제의 보내기 처리
function sendOffer() {
    
    // 전송 완료 후 화면 전환
    document.getElementById('offerForm').style.display = 'none'; // 폼 숨김
    document.getElementById('offerSuccess').style.display = 'block'; // 성공 메시지 표시
    
    // 성공 메시지에 대상자 이름 꽂아넣기
    document.getElementById('offerSuccessText').innerHTML = 
        `<b>${targetName}</b> 님에게 함께하기 제의가 전달됐어요.<br>
         상대가 <b>수락</b>하면 팀원으로 합류해요.`;
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