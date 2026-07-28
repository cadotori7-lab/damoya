<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>다모여 - 홈</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="includes/header.jsp" />
  <main>
<!-- ========== 홈 (메인) ========== -->
  <section class="view on" id="v-home">
    <div class="home-hero">
      <div>
        <div class="greet">안녕하세요, <em>${member.name}</em> 님 👋</div>
        <div class="gsub">오늘도 팀 프로젝트를 이어가 볼까요? 지금 확인할 것들을 모아봤어요.</div>
      </div>
      <a class="btn pri" href="${ctx}/project/form">+ 프로젝트 모집하기</a>
    </div>

    <div class="home-stats">
      <a class="hstat" href="${ctx}/project/my">
        <div class="ic a"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg></div>
        <div><div class="n">3</div><div class="k">참여 중 프로젝트</div></div>
      </a>
      <a class="hstat" href="${ctx}/workspace/overview">
        <div class="ic b"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/></svg></div>
        <div><div class="n">2</div><div class="k">진행 중 내 업무</div></div>
      </a>
      <a class="hstat" href="${ctx}/mypage/index">
        <div class="ic c"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2a10 10 0 1 0 10 10"/><path d="M12 6v6l4 2"/></svg></div>
        <div><div class="n">2</div><div class="k">지원 진행 중</div></div>
      </a>
      <a class="hstat" href="${ctx}/mypage/index">
        <div class="ic d"><svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.7 21a2 2 0 0 1-3.4 0"/></svg></div>
        <div><div class="n">3</div><div class="k">새 알림</div></div>
      </a>
    </div>

    <div class="ov-grid two">
      <!-- 이어서 할 프로젝트 -->
      <div class="ocard">
        <div class="oc-head"><div class="t">이어서 할 프로젝트</div><button class="oc-more" onclick="go('myprojects')">전체 보기</button></div>
        <div class="psel-list">
          <div class="psel-card" style="--c:var(--cat-contest);padding:16px 18px" onclick="go('overview')">
            <div class="psel-main">
              <div class="psel-top"><span class="psel-cat">공모전</span><span class="psel-role lead">팀장</span></div>
              <h3 style="font-size:15.5px">AI 헬스케어 웹서비스</h3>
              <div class="psel-meta"><span>검수 대기 1건</span><span>D-24</span></div>
            </div>
          </div>
          <div class="psel-card" style="--c:var(--cat-major);padding:16px 18px" onclick="go('overview')">
            <div class="psel-main">
              <div class="psel-top"><span class="psel-cat">학과</span><span class="psel-role member">팀원</span></div>
              <h3 style="font-size:15.5px">데이터베이스 텀 프로젝트</h3>
              <div class="psel-meta"><span>내 업무 2건</span><span>D-11</span></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 오늘/이번 주 할 일 -->
      <div class="ocard">
        <div class="oc-head"><div class="t">이번 주 할 일</div><button class="oc-more" onclick="go('tasks')">내 업무</button></div>
        <div class="task-line" style="--cc:var(--accent);margin-bottom:9px">
          <div class="tl-main"><h4>매칭 검색 필터 구현</h4><div class="tl-desc">AI 헬스케어 · 진행중</div></div>
          <span class="due soon">~08.15</span>
          <button class="btn sm pri" onclick="openSubmit('매칭 검색 필터 구현','08.15')">제출</button>
        </div>
        <div class="task-line" style="--cc:var(--wait);margin-bottom:9px">
          <div class="tl-main"><h4>ERD 정규화 보완</h4><div class="tl-desc">데이터베이스 텀 · 진행중</div></div>
          <span class="due">~08.18</span>
        </div>
        <div class="sch-item" style="border-top:1px solid var(--line);padding-top:12px"><span class="dt">08.17 (월)</span><span class="tt">중간 점검 회의 · 19:00</span></div>
      </div>
    </div>

    <div class="ov-grid two">
      <!-- 지원 현황 -->
      <div class="ocard">
        <div class="oc-head"><div class="t">내 지원 현황</div><button class="oc-more" onclick="go('mypage')">전체 보기</button></div>
        <div class="home-appl">
          <div class="ha">
            <span class="rc-spine" style="width:4px;height:34px;border-radius:3px;background:var(--cat-club)"></span>
            <div class="ha-main"><div class="tt">전국 대학생 연합 해커톤 참가팀</div><div class="ct">교내활동 · 지원 3일 전</div></div>
            <span class="chip interview">면접 예정</span>
          </div>
          <div class="ha">
            <span class="rc-spine" style="width:4px;height:34px;border-radius:3px;background:var(--cat-contest)"></span>
            <div class="ha-main"><div class="tt">ESG 아이디어 공모전 파트너</div><div class="ct">공모전 · 지원 1일 전</div></div>
            <span class="chip wait">승인 대기</span>
          </div>
        </div>
      </div>

      <!-- 추천 프로젝트 -->
      <div class="ocard">
        <div class="oc-head"><div class="t">추천 공모</div><button class="oc-more" onclick="go('match')">더 보기</button></div>
        <div class="home-reco">
          <div class="reco-item" onclick="go('detail')">
            <span class="rc-spine" style="--c:var(--cat-major)"></span>
            <div class="rc-main"><div class="tt">졸업작품 — Unity 게임 개발 팀원</div><div class="mt">학과 · 게임공학 · 2/3명 · D-3</div></div>
            <span class="chip recruit">모집중</span>
          </div>
          <div class="reco-item" onclick="go('detail')">
            <span class="rc-spine" style="--c:var(--cat-contest)"></span>
            <div class="rc-main"><div class="tt">교내 창업 아이디어톤 팀원 모집</div><div class="mt">공모전 · 무관 · 1/5명 · D-8</div></div>
            <span class="chip recruit">모집중</span>
          </div>
          <div class="reco-item" onclick="go('detail')">
            <span class="rc-spine" style="--c:var(--cat-liberal)"></span>
            <div class="rc-main"><div class="tt">교양 발표 스터디 같이 해요</div><div class="mt">교양 · 전 학년 · 3/5명 · D-6</div></div>
            <span class="chip recruit">모집중</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</main>
<jsp:include page="includes/footer.jsp" />
</body>
</html>