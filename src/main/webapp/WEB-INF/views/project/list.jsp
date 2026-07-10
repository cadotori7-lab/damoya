<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <!-- ========== 매칭 목록 ========== -->
  <section id="v-match">
    <div class="eyebrow">Find your team</div>
    <h1 class="page"><em>함께할 팀</em>을 찾아보세요</h1>
    <p class="sub">우리 학교와 전국의 프로젝트를 카테고리·학과·학년으로 좁혀서 탐색해요.</p>

    <div class="searchbar">
      <input type="text" placeholder="제목, 소개, 태그로 검색  (예: 캡스톤, Spring, 데이터 분석)">
      <button class="btn pri" onclick="go('create')">+ 프로젝트 모집</button>
    </div>

    <div class="board">
      <aside class="filters">
        <h3>매칭 범위</h3>
        <div class="scope">
          <button class="on">교내</button>
          <button>학과</button>
          <button>전국</button>
        </div>
        <h3>카테고리</h3>
        <div class="flt">
          <input type="checkbox" id="c1" checked><label for="c1">공모전</label>
          <input type="checkbox" id="c2"><label for="c2">학과</label>
          <input type="checkbox" id="c3"><label for="c3">교양</label>
          <input type="checkbox" id="c4"><label for="c4">교내활동</label>
        </div>
        <h3>대상 학년</h3>
        <div class="flt">
          <input type="checkbox" id="y1"><label for="y1">1학년</label>
          <input type="checkbox" id="y2"><label for="y2">2학년</label>
          <input type="checkbox" id="y3" checked><label for="y3">3학년</label>
          <input type="checkbox" id="y4" checked><label for="y4">4학년</label>
        </div>
        <h3>상태</h3>
        <div class="flt">
          <input type="checkbox" id="s1" checked><label for="s1">모집중</label>
          <input type="checkbox" id="s2"><label for="s2">진행중</label>
        </div>
      </aside>

      <div class="grid" id="cards"></div>
    </div>
  </section>
    </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>