<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
     <!-- ========== 업무 등록 ========== -->
  <section id="v-taskform">
    <div class="form-wrap">
      <button class="back" onclick="go('tasks')">← 업무 보드</button>
      <div class="eyebrow">New task</div>
      <h1 class="page"><em>업무 등록</em></h1>
      <p class="sub">AI 헬스케어 웹서비스 · 팀장이 업무를 등록하고 담당자를 배정해요.</p>

      <div class="form-card">
        <div class="fld one"><label>업무명<span class="req">*</span></label><input type="text" placeholder="예: 프로젝트 지원·승인 API 구현"></div>
        <div class="fld one">
          <label>설명</label>
          <textarea placeholder="업무 내용, 완료 기준, 참고 사항을 적어주세요." style="min-height:90px"></textarea>
        </div>
        <div class="frow">
          <div class="fld"><label>담당자<span class="req">*</span></label>
            <select><option>이도현 (백엔드)</option><option selected>김민재 (백엔드)</option><option>서지우 (기획/디자인)</option><option>최윤서 (팀장)</option></select>
          </div>
          <div class="fld"><label>마감일<span class="req">*</span></label><input type="date" value="2026-08-20"></div>
        </div>
        <div class="fld one">
          <label>초기 상태</label>
          <div class="picker">
            <input type="radio" name="tst" id="ts1" checked><label for="ts1">등록</label>
            <input type="radio" name="tst" id="ts2"><label for="ts2">진행중</label>
          </div>
          <div class="hint">담당자가 결과물을 제출하면 검수 대기로, 팀장이 검수하면 승인/반려로 넘어가요.</div>
        </div>
        <div class="form-foot">
          <button class="btn ghost" onclick="go('tasks')">취소</button>
          <button class="btn pri" onclick="go('tasks')">업무 등록</button>
        </div>
      </div>
    </div>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>