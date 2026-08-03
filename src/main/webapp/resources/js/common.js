/* =========================================================
 * common.js — 모든 페이지 공통 스크립트
 * 핵심: 모달 여닫기. 페이지 전용 로직은 각 페이지 js에 둔다.
 * ========================================================= */

/* 모달 열기 : openModal('profileModal') */
function openModal(id) {
  var m = document.getElementById(id);
  if (!m) return;
  m.classList.add('on');
  document.body.style.overflow = 'hidden'; // 뒤 배경 스크롤 잠금
}

/* 모달 닫기 : closeModal('profileModal') */
function closeModal(id) {
  var m = document.getElementById(id);
  if (!m) return;
  m.classList.remove('on');
  document.body.style.overflow = '';
}

/* 열려 있는 모달 전부 닫기 */
function closeAllModals() {
  var opened = document.querySelectorAll('.modal-overlay.on');
  for (var i = 0; i < opened.length; i++) {
    opened[i].classList.remove('on');
  }
  document.body.style.overflow = '';
}

/* 오버레이(어두운 바깥) 클릭 시 닫기 — 모든 모달 공통으로 한 번만 등록 */
document.addEventListener('click', function (e) {
  if (e.target.classList && e.target.classList.contains('modal-overlay')) {
    e.target.classList.remove('on');
    document.body.style.overflow = '';
  }
});

/* Esc 키로 닫기 */
document.addEventListener('keydown', function (e) {
  if (e.key === 'Escape') {
    closeAllModals();
  }
});

/*
 * 데이터 속성 방식도 지원(선택):
 *   <button data-modal-open="profileModal">수정</button>
 *   <button data-modal-close="profileModal">취소</button>
 * onclick 없이 이 속성만 달아도 동작한다.
 */
document.addEventListener('click', function (e) {
  var openId = e.target.closest ? e.target.closest('[data-modal-open]') : null;
  if (openId) { openModal(openId.getAttribute('data-modal-open')); return; }
  var closeId = e.target.closest ? e.target.closest('[data-modal-close]') : null;
  if (closeId) { closeModal(closeId.getAttribute('data-modal-close')); }
});

var PV_CTX = (typeof window.CTX !== 'undefined') ? window.CTX : '';
 
function openProfile(memberId) {
  // 로딩 상태로 모달 먼저 연다 (반응 빠르게)
  document.getElementById('pvLoading').style.display = '';
  document.getElementById('pvContent').style.display = 'none';
  openModal('profileViewModal');
 
  fetch(PV_CTX + '/members/' + memberId + '/profile')
    .then(function (res) {
      if (!res.ok) throw new Error('not found');
      return res.json();
    })
    .then(function (m) {
      fillProfile(m);
    })
    .catch(function () {
      document.getElementById('pvLoading').textContent = '프로필을 불러올 수 없어요.';
    });
}
 
function fillProfile(m) {
  // 아바타 = 이름 첫 글자
  var avatar = document.getElementById('pvAvatar');
  avatar.textContent = m.name ? m.name.substring(0, 1) : '?';
 
  document.getElementById('pvName').textContent = m.name || '';
 
  // 학과 · 학년 (없을 수 있음)
  var subParts = [];
  if (m.deptName) subParts.push(m.deptName);
  if (m.grade) subParts.push(m.grade + '학년');
  if (m.major) subParts.push(m.major);
  document.getElementById('pvSub').textContent = subParts.join(' · ');
 
  // 배지
  var badges = '';
  if (m.mentor)  badges += '<span class="b">🧭 멘토</span>';
  if (m.approved) badges += '<span class="b">✓ 학교 인증됨</span>';
  document.getElementById('pvBadges').innerHTML = badges;
 
  // 비공개면 상세를 숨기고 안내만
  var introWrap  = document.getElementById('pvIntroWrap');
  var mentorWrap = document.getElementById('pvMentorWrap');
  var privateBox = document.getElementById('pvPrivate');
 
  if (m.public === false) {
    introWrap.style.display = 'none';
    mentorWrap.style.display = 'none';
    privateBox.style.display = '';
  } else {
    privateBox.style.display = 'none';
 
    // 소개글
    if (m.intro) {
      document.getElementById('pvIntro').textContent = m.intro;
      introWrap.style.display = '';
    } else {
      introWrap.style.display = 'none';
    }
 
    // 멘토 전용
    if (m.mentor) {
      document.getElementById('pvField').textContent = m.field || '-';
      if (m.career) {
        document.getElementById('pvCareer').textContent = m.career;
        document.getElementById('pvCareerLabel').style.display = '';
      } else {
        document.getElementById('pvCareer').textContent = '';
        document.getElementById('pvCareerLabel').style.display = 'none';
      }
      mentorWrap.style.display = '';
    } else {
      mentorWrap.style.display = 'none';
    }
  }
 
  // 로딩 → 내용 전환
  document.getElementById('pvLoading').style.display = 'none';
  document.getElementById('pvContent').style.display = '';
}
 
window.openProfile = openProfile;