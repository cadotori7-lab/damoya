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
  <!-- ========== 계정 관리 (관리자) ========== -->
  <section id="v-accounts">
    <a class="back" href="${ctx}/admin/dashboard">← 관리자 대시보드</a>
    <div class="eyebrow">Account management</div>
    <h1 class="page"><em>계정 관리</em></h1>
    <p class="sub">회원 계정을 조회하고, 신고·규정 위반 계정을 제재할 수 있어요.</p>

    <div class="acc-toolbar">
      <input type="text" placeholder="이름·아이디·학교로 검색">
      <button class="btn ghost">전체 역할 ▾</button>
      <button class="btn ghost">전체 상태 ▾</button>
    </div>

    <div class="tbl-wrap">
      <table id="accTable">
        <tr><th>회원</th><th>학교 / 학과</th><th>역할</th><th>가입일</th><th>상태</th><th>관리</th></tr>
      </table>
    </div>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
<script src="${ctx}/resources/js/common.js"></script>
<script src="${ctx}/resources/js/adminAccounts.js"></script>
</body>
</html>