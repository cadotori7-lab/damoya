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
