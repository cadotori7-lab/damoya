// 대상자 정보를 임시로 저장할 변수
let targetName = "";
let targetMemberId = "";

// 제의 모달 열기
function openOffer(name, field, memberId) {
    const memberIdInput = document.getElementById('offerMemberId');
    if(memberIdInput) memberIdInput.value = memberId;
    
    targetName = name;
    targetMemberId = memberId;
    
    // 모달 상단 텍스트 및 기본값 세팅
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
    
    // 폼 초기화
    const offerForm = document.getElementById('offerForm');
    if (offerForm) offerForm.reset();
}

// 폼이 제출되기 직전, 이메일과 메시지를 합쳐서 hidden input에 넣는 함수
function prepareOfferSubmit() {
    // 체크박스 중 하나라도 체크되었는지 확인
    const checkboxes = document.querySelectorAll('input[name="projectIds"]:checked');
    if (checkboxes.length === 0) {
        alert("초대할 프로젝트를 최소 1개 이상 선택해주세요!");
        return false;
    }

    var email = document.getElementById('offerEmail').value.trim();
    var msg = document.getElementById('offerMessage').value.trim();
    
    var combined = "[연락처: " + email + "]\n\n" + msg;
    document.getElementById('realMotive').value = combined;
    
    return true;
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