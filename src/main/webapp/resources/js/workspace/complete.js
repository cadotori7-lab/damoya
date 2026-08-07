(function () {
  'use strict';

  const form = document.getElementById('finalResultForm');
  const fileInput = document.getElementById('finalFile');
  const preview = document.getElementById('finalFilePreview');
  const fileName = document.getElementById('finalFileName');
  const fileSize = document.getElementById('finalFileSize');
  const removeButton = document.getElementById('removeFinalFile');
  const submitButton = document.getElementById('completeSubmitButton');

  if (!form || !fileInput || !preview) {
    return;
  }

  function formatSize(bytes) {
    if (bytes < 1024) {
      return bytes + ' B';
    }
    if (bytes < 1024 * 1024) {
      return (bytes / 1024).toFixed(1) + ' KB';
    }
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  function updatePreview() {
    const file = fileInput.files && fileInput.files[0];
    preview.hidden = !file;
    if (!file) {
      fileName.textContent = '';
      fileSize.textContent = '';
      return;
    }
    fileName.textContent = file.name;
    fileSize.textContent = formatSize(file.size);
  }

  fileInput.addEventListener('change', updatePreview);
  removeButton.addEventListener('click', function () {
    fileInput.value = '';
    updatePreview();
    fileInput.focus();
  });

  form.addEventListener('submit', function () {
    if (submitButton) {
      submitButton.disabled = true;
      submitButton.textContent = '제출 중...';
    }
  });
}());
