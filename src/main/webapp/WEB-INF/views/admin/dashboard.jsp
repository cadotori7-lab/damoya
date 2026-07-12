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
  <!-- ========== 관리자 ========== -->
  <section id="v-admin">
    <div class="eyebrow">Admin console</div>
    <h1 class="page"><em>관리자</em> 대시보드</h1>
    <p class="sub">학교 인증, 신고 처리, 서비스 통계를 한곳에서 관리해요.</p>
    <div style="margin:-12px 0 22px;display:flex;gap:8px"><a class="btn ghost sm" href="${ctx}/admin/posts">게시물 관리 →</a><a class="btn ghost sm" href="${ctx}/admin/accounts">계정 관리 →</a></div>

    <div class="stats">
      <div class="stat"><div class="k">전체 회원</div><div class="n">1,284<small>명</small></div><div class="d up">▲ 8.2% 이번 주</div></div>
      <div class="stat"><div class="k">진행 중 프로젝트</div><div class="n">143<small>개</small></div><div class="d up">▲ 12개</div></div>
      <div class="stat"><div class="k">인증 대기 학교</div><div class="n">6<small>건</small></div><div class="d">처리 필요</div></div>
      <div class="stat"><div class="k">미처리 신고</div><div class="n">4<small>건</small></div><div class="d down">▲ 2건 신규</div></div>
    </div>

    <div class="admin-grid">
      <div class="tbl-wrap">
        <div class="tbl-h">학교 인증 승인 <span class="cnt">6</span></div>
        <table>
          <tr><th>신청자</th><th>학교 / 학과</th><th>처리</th></tr>
          <tr><td><div class="nm">박서연</div></td><td><div class="mono">대진대 · 산업경영</div></td><td><div class="act-btns"><button class="btn sm pri">승인</button><button class="btn sm ghost">반려</button></div></td></tr>
          <tr><td><div class="nm">정하람</div></td><td><div class="mono">가천대 · 소프트웨어</div></td><td><div class="act-btns"><button class="btn sm pri">승인</button><button class="btn sm ghost">반려</button></div></td></tr>
          <tr><td><div class="nm">김도윤</div></td><td><div class="mono">단국대 · 전자공학</div></td><td><div class="act-btns"><button class="btn sm pri">승인</button><button class="btn sm ghost">반려</button></div></td></tr>
        </table>
      </div>

      <div class="tbl-wrap">
        <div class="tbl-h">신고 처리 <span class="cnt">4</span></div>
        <table>
          <tr><th>대상</th><th>사유</th><th>상태</th></tr>
          <tr><td><div class="nm">게시물 #482</div><div class="mono">프로젝트</div></td><td>허위 모집 정보</td><td><span class="chip wait">접수</span></td></tr>
          <tr><td><div class="nm">user_2201</div><div class="mono">사용자</div></td><td>부적절한 댓글</td><td><span class="chip wait">접수</span></td></tr>
          <tr><td><div class="nm">게시물 #467</div><div class="mono">프로젝트</div></td><td>중복 게시</td><td><span class="chip done">처리완료</span></td></tr>
          <tr><td><div class="nm">user_1980</div><div class="mono">사용자</div></td><td>노쇼·연락 두절</td><td><span class="chip wait">접수</span></td></tr>
        </table>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>