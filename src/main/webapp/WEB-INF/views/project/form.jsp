<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로젝트 등록</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../resources/css/style.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
  <main>
    
  <!-- ========== 프로젝트 생성 ========== -->
  <section id="v-create">
  <form class="form-wrap" id="projectForm" action="${ctx}/project/register" method="POST">
      <a class="back" href="${ctx}/project/list">← 목록으로</a>
      <div class="eyebrow">New project</div>
      <h1 class="page"><em>프로젝트 모집</em> 등록</h1>
      <p class="sub">모집 정보를 입력하면 매칭 목록에 게시돼요. 모집이 끝나면 업무 보드가 열립니다.</p>

      <div class="form-card">
        <div class="fsec-title"><span>1</span>어떤 프로젝트인가요?</div>
        
        <div class="fld">
          <label>카테고리<span class="req">*</span></label>
          <div class="picker cats">
            <!-- 2. name="category" 와 value 추가 -->
            <input type="radio" name="category" value="공모전" id="pc1" checked><label for="pc1">공모전</label>
            <input type="radio" name="category" value="학과" id="pc2"><label for="pc2">학과</label>
            <input type="radio" name="category" value="교양" id="pc3"><label for="pc3">교양</label>
            <input type="radio" name="category" value="교내활동" id="pc4"><label for="pc4">교내활동</label>
          </div>
        </div>
    
        <div class="fld one">
            <label>제목<span class="req">*</span></label>
            <input type="text" name="title" id="title" placeholder="예: 2026 캡스톤 경진대회 — AI 헬스케어 웹서비스 팀원 모집">
        </div>
        <div class="fld one">
          <label>소개<span class="req">*</span></label>
          <textarea name="content" id="content" placeholder="프로젝트 목표, 진행 방식, 어떤 팀원을 찾는지 자유롭게 적어주세요."></textarea>
        </div>
      </div>

      <div class="form-card">
        <div class="fsec-title"><span>2</span>모집 조건</div>
        <div class="frow">
          <div class="fld"><label>매칭 범위<span class="req">*</span></label>
            <select name="matchScope">
                <option value="교내" selected>교내(동일 대학)</option>
                <option value="학과">학과</option>
                <option value="전국">전국</option>
            </select>
          </div>
          <div class="fld"><label>모집 인원<span class="req">*</span></label>
            <input type="number" name="recruitCount" value="4" min="1">
          </div>
        </div>
        
        <div class="frow">
          <div class="fld"><label>대상 학년</label>
            <div class="picker">
              <input type="checkbox" name="targetGrades" value="1" id="ty1"><label for="ty1">1</label>
              <input type="checkbox" name="targetGrades" value="2" id="ty2"><label for="ty2">2</label>
              <input type="checkbox" name="targetGrades" value="3" id="ty3" checked><label for="ty3">3</label>
              <input type="checkbox" name="targetGrades" value="4" id="ty4" checked><label for="ty4">4</label>
            </div>
          </div>
          <div class="fld"><label>모집 마감일<span class="req">*</span></label>
            <input type="date" name="deadline" value="2026-08-14">
          </div>
        </div>
        
        <div class="fld one">
          <label>태그</label>
          <div class="tag-input" onclick="this.querySelector('input').focus()">
            <span class="tg">Spring <b>×</b></span>
            <span class="tg">React <b>×</b></span>
            <span class="tg">MySQL <b>×</b></span>
            <input type="text" placeholder="태그 입력 후 Enter">
          </div>
          <div class="hint">기술·분야 태그로 검색 노출이 잘 돼요.</div>
          <input type="hidden" name="tags" id="hiddenTags">
        </div>
        
        <div class="form-foot">
          <input type="hidden" name="status" id="projectStatus">
          <button type="button" class="btn ghost" onclick="submitProject('DRAFT')">임시저장</button>
          <button type="button" class="btn pri" onclick="submitProject('RECRUITING')">모집 등록</button>
        </div>
      </div>
    </form>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
   <script src="/src/main/webapp/resources/js/projectBoard.js"></script>
</body>
</html>