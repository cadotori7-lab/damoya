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
  <!-- ========== 멘토 피드백 ========== -->
  <section id="v-feedback">
    <div class="eyebrow">Mentor feedback</div>
    <h1 class="page"><em>멘토 피드백</em></h1>
    <p class="sub">담당 프로젝트의 진행 상황을 확인하고 단계별 피드백을 남겨요.</p>

    <div class="detail">
      <div>
        <div class="panel">
          <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px">
            <div>
              <div class="d-head"><span class="cat">공모전 · 교내</span></div>
              <h2 style="font-size:20px;font-weight:800;margin-top:4px">AI 헬스케어 웹서비스</h2>
            </div>
            <span class="mentor-badge"><span class="pic">박</span>박준호 멘토 · 초대됨</span>
          </div>
          <div class="fb-summary">
            <div class="box"><div class="n mono">58%</div><div class="k">진행도</div></div>
            <div class="box"><div class="n mono" style="color:var(--ok)">6</div><div class="k">승인 완료 업무</div></div>
            <div class="box"><div class="n mono">D-24</div><div class="k">마감까지</div></div>
          </div>
        </div>

        <div class="panel">
          <div class="fsec-title" style="margin-bottom:14px"><span>✎</span>피드백 작성</div>
          <div class="fld">
            <label>평가 단계<span class="req">*</span></label>
            <div class="picker">
              <input type="radio" name="stg" id="st1" checked><label for="st1">중간 점검</label>
              <input type="radio" name="stg" id="st2"><label for="st2">최종 평가</label>
              <input type="radio" name="stg" id="st3"><label for="st3">완료 검토</label>
            </div>
          </div>
          <div class="fld one">
            <label>내용<span class="req">*</span></label>
            <textarea placeholder="진행 상황에 대한 피드백을 남겨주세요. 잘된 점과 보완할 점을 구체적으로 적어주면 팀에 도움이 돼요."></textarea>
          </div>
          <div class="form-foot"><button class="btn pri">피드백 등록</button></div>
        </div>
      </div>

      <div class="side">
        <div class="panel" style="position:sticky;top:86px">
          <div class="fsec-title" style="margin-bottom:16px"><span>◷</span>지난 피드백</div>
          <div class="fb-timeline">
            <div class="fb-item">
              <div class="fh"><span class="chip stage">중간 점검</span><span class="date">2026.08.18</span></div>
              <p>ERD 설계가 깔끔합니다. 다만 지원–승인 트랜잭션에서 예외 처리 흐름을 한 번 더 점검해보면 좋겠어요.</p>
            </div>
            <div class="fb-item">
              <div class="fh"><span class="chip stage">중간 점검</span><span class="date">2026.08.11</span></div>
              <p>초기 화면 설계와 역할 분담이 명확해서 좋습니다. 다음 회의 전까지 인증 뼈대를 먼저 세워두는 걸 추천해요.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>