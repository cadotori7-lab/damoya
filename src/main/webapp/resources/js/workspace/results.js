(function () {
  'use strict';

  const filter = document.getElementById('resFilter');
  const grid = document.getElementById('resGrid');
  const scrollArea = document.getElementById('resultsScroll');
  const count = document.getElementById('resCount');
  const scope = document.getElementById('resScope');
  const empty = document.getElementById('resEmpty');
  const emptyTitle = document.getElementById('resEmptyTitle');
  const emptyDescription = document.getElementById('resEmptyDescription');

  if (!filter || !grid || !scrollArea || !count || !scope || !empty) {
    return;
  }

  const cards = Array.from(grid.querySelectorAll('.res-card'));
  const buttons = Array.from(filter.querySelectorAll('button[data-member-id]'));
  const scrollThreshold = Number(scrollArea.dataset.scrollThreshold) || 6;
  const avatarColors = ['#2b46c8', '#0f9d8c', '#8b5cf6', '#d97706', '#db4f67', '#287f9e'];

  function colorFor(memberId) {
    const numericId = Number(memberId);
    const index = Number.isFinite(numericId)
      ? Math.abs(numericId) % avatarColors.length
      : 0;
    return avatarColors[index];
  }

  cards.forEach(function (card) {
    const avatar = card.querySelector('[data-avatar-id]');
    if (avatar) {
      avatar.style.backgroundColor = colorFor(avatar.dataset.avatarId);
    }
  });

  buttons.forEach(function (button) {
    const memberId = button.dataset.memberId;
    const memberCount = memberId === 'all'
      ? cards.length
      : cards.filter(function (card) {
          return card.dataset.memberId === memberId;
        }).length;
    const countLabel = button.querySelector('.filter-count');
    if (countLabel) {
      countLabel.textContent = memberCount;
    }
  });

  function applyFilter(button) {
    const memberId = button.dataset.memberId;
    const isAll = memberId === 'all';
    let visibleCount = 0;

    cards.forEach(function (card) {
      const visible = isAll || card.dataset.memberId === memberId;
      card.hidden = !visible;
      if (visible) {
        visibleCount += 1;
      }
    });

    buttons.forEach(function (item) {
      const selected = item === button;
      item.classList.toggle('on', selected);
      item.setAttribute('aria-pressed', String(selected));
    });

    const memberName = button.dataset.memberName || '전체';
    count.textContent = visibleCount;
    scope.textContent = memberName;
    grid.hidden = visibleCount === 0;
    empty.hidden = visibleCount !== 0;
    scrollArea.classList.toggle('is-scrollable', visibleCount > scrollThreshold);
    scrollArea.scrollTop = 0;

    if (visibleCount === 0) {
      emptyTitle.textContent = isAll
        ? '승인된 결과물이 아직 없어요'
        : memberName + '님의 승인 결과물이 아직 없어요';
      emptyDescription.textContent = '업무가 승인되면 이곳에 자동으로 표시됩니다.';
    }
  }

  buttons.forEach(function (button) {
    button.addEventListener('click', function () {
      applyFilter(button);
    });
  });

  applyFilter(buttons[0]);
}());
