<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
  <main>
  <!-- ========== 프로젝트 대시보드 (개요) ========== -->
  <section id="v-overview">
    <a class="back" href="${ctx}/project/my">← 내 프로젝트</a>
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>AI 헬스케어 웹서비스</em></h1>
    <p class="sub">프로젝트 현황을 한눈에 보고, 각 영역으로 이동해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <!-- 헤더 -->
    <div class="ov-hero">
      <div class="oh-main">
        <div class="oh-title"><h2>AI 헬스케어 웹서비스</h2><span class="chip ing">진행중</span><span class="tag">공모전</span></div>
        <div class="oh-meta"><span>팀원 <b>4명</b></span><span>기간 <b>2026.08.03 ~ 09.04</b></span><span>남은 기간 <b>D-24</b></span></div>
      </div>
      <div class="oh-prog">
        <div class="k">업무 현황</div>
        <div class="big">11<span style="font-size:18px"> / 12</span></div>
        <div class="mono" style="font-size:12px;color:var(--ink-soft)">승인 완료 · 검수 대기 1건</div>
      </div>
    </div>

    <!-- 요약 / 회의 일정 / 회의 결과 -->
    <div class="ov-grid">
      <div class="ocard">
        <div class="oc-head"><div class="t">프로젝트 요약</div></div>
        <div class="oc-desc">학생 건강 관리를 돕는 AI 기반 웹서비스예요. 교내 캡스톤 경진대회 출품을 목표로 방학 동안 완성도 있는 결과물을 만듭니다.</div>
        <div class="oc-tags"><span class="tag">Spring</span><span class="tag">React</span><span class="tag">MySQL</span><span class="tag">캡스톤</span></div>
      </div>
      <div class="ocard">
        <div class="oc-head"><div class="t">회의 일정</div><button class="oc-more" onclick="go('schedule')">전체 보기</button></div>
        <div class="sch-item"><span class="dt">08.17 (월)</span><span class="tt">중간 점검 회의</span></div>
        <div class="sch-item"><span class="dt">08.24 (월)</span><span class="tt">2차 스프린트 회의</span></div>
        <div class="sch-item"><span class="dt">08.31 (월)</span><span class="tt">최종 리허설 회의</span></div>
      </div>
      <div class="ocard">
        <div class="oc-head"><div class="t">회의 결과</div><button class="oc-more" onclick="go('meetings')">전체 보기</button></div>
        <div class="mr-item"><div class="dt">08.17 (월)</div><h5>중간 점검 회의</h5><ul><li>정원 동시성은 비관적 락으로 우선 구현</li><li>멘토 중간 피드백 요청</li></ul></div>
        <div class="mr-item"><div class="dt">08.10 (월)</div><h5>킥오프 회의</h5><ul><li>역할 분담(매칭/업무/권한/관리자)</li><li>주 2회 오프라인 회의 · PR 리뷰 후 머지</li></ul></div>
      </div>
    </div>

    <!-- 팀원별 업무 현황 -->
    <div class="ov-grid two">
      <div class="ocard">
        <div class="oc-head"><div class="t">팀원별 업무 현황</div><button class="oc-more" onclick="go('org')">팀원 관리</button></div>
        <table class="ov-tbl">
          <tr><th>팀원</th><th>역할</th><th>담당 업무</th><th>상태</th><th style="width:100px">기여도</th></tr>
          <tr>
            <td><div class="u"><span class="pic" style="background:#c98a12">최</span>최윤서 <span class="badge-me">팀장</span></div></td>
            <td>기획·프론트</td><td class="tk">전체 기획·일정 관리, UI 설계</td>
            <td><span class="chip ing">진행중</span></td>
            <td><span class="mono" style="font-weight:700">완료 3건</span></td>
          </tr>
          <tr>
            <td><div class="u"><span class="pic" style="background:#2b46c8">이</span>이도현</div></td>
            <td>백엔드</td><td class="tk">인증·권한, ERD 설계</td>
            <td><span class="chip ing">진행중</span></td>
            <td><span class="mono" style="font-weight:700">완료 3건</span></td>
          </tr>
          <tr>
            <td><div class="u"><span class="pic" style="background:#2b46c8">민</span>김민재</div></td>
            <td>백엔드</td><td class="tk">매칭·지원 API, 통계</td>
            <td><span class="chip wait">검수 대기</span></td>
            <td><span class="mono" style="font-weight:700">완료 2건</span></td>
          </tr>
          <tr>
            <td><div class="u"><span class="pic" style="background:#0f9d8c">서</span>서지우</div></td>
            <td>기획·디자인</td><td class="tk">화면 설계, 발표 자료</td>
            <td><span class="chip approve">완료</span></td>
            <td><span class="mono" style="font-weight:700">완료 3건</span></td>
          </tr>
        </table>
      </div>

      <div class="ocard">
        <div class="oc-head"><div class="t">다가오는 일정</div><button class="oc-more" onclick="go('schedule')">전체 보기</button></div>
        <div class="sch-item"><span class="dt">08.17<br>(월)</span><div><div class="tt">중간 점검 회의</div><div class="mono" style="font-size:11.5px;color:var(--ink-soft)">19:00 · 회의실 A</div></div></div>
        <div class="sch-item"><span class="dt">08.20<br>(목)</span><div><div class="tt">통계 화면 마감</div><div class="mono" style="font-size:11.5px;color:var(--ink-soft)">업무 · 김민재</div></div></div>
        <div class="sch-item"><span class="dt">08.24<br>(월)</span><div><div class="tt">2차 스프린트 회의</div><div class="mono" style="font-size:11.5px;color:var(--ink-soft)">19:00 · 온라인</div></div></div>
      </div>
    </div>

    <!-- 진행도 세부 / 최근 업데이트 -->
    <div class="ov-grid two">
      <div class="ocard">
        <div class="oc-head"><div class="t">업무 상태 요약</div><button class="oc-more" onclick="go('tasks')">업무 보드</button></div>
        <div class="mp-stats" style="margin-bottom:0">
          <div class="mp-stat"><div class="n mono">12</div><div class="k">전체 업무</div></div>
          <div class="mp-stat"><div class="n mono" style="color:var(--accent)">4</div><div class="k">진행중</div></div>
          <div class="mp-stat"><div class="n mono" style="color:var(--wait)">1</div><div class="k">검수 대기</div></div>
          <div class="mp-stat"><div class="n mono" style="color:var(--ok)">6</div><div class="k">승인 완료</div></div>
        </div>
      </div>
      <div class="ocard">
        <div class="oc-head"><div class="t">최근 업데이트</div><button class="oc-more" onclick="go('results')">결과물</button></div>
        <div class="upd-item"><span class="pic" style="background:#2b46c8">이</span><div class="ut"><b>이도현</b>님이 security_config.zip 을 제출했어요.</div><span class="uw">2시간 전</span></div>
        <div class="upd-item"><span class="pic" style="background:#0f9d8c">서</span><div class="ut"><b>서지우</b>님이 midterm_deck.pptx 를 제출했어요.</div><span class="uw">5시간 전</span></div>
        <div class="upd-item"><span class="pic" style="background:#2b46c8">민</span><div class="ut"><b>김민재</b>님이 지원·승인 API 를 제출했어요.</div><span class="uw">어제 18:30</span></div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>