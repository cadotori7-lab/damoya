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
    <section id="v-complete">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>AI 헬스케어 웹서비스</em></h1>
    <p class="sub">프로젝트를 마무리하고 완료 처리해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <div id="completeBody">
      <div class="detail" style="align-items:start">
        <div>
          <!-- 완료 체크리스트 -->
          <div class="panel">
            <div class="fsec-title" style="margin-bottom:16px"><span>✓</span>완료 전 확인</div>
            <div class="done-check">
              <div class="done-item">
                <span class="ck ok"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg></span>
                <div class="di-main"><div class="t">모든 업무 검수 완료</div><div class="s">검수 대기 중인 업무가 없어야 해요</div></div>
                <div class="di-val" style="color:var(--ok)">11 / 12</div>
              </div>
              <div class="done-item">
                <span class="ck no">!</span>
                <div class="di-main"><div class="t">검수 대기 1건 남음</div><div class="s">ERD·DB 스키마 설계 · 이도현</div></div>
                <a class="di-val" style="color:var(--accent);cursor:pointer" onclick="go('tasks')">검수하기 →</a>
              </div>
              <div class="done-item">
                <span class="ck ok"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M20 6 9 17l-5-5"/></svg></span>
                <div class="di-main"><div class="t">결과물 등록</div><div class="s">승인된 산출물</div></div>
                <div class="di-val" style="color:var(--ok)">7건</div>
              </div>
            </div>
          </div>

          <!-- 최종 결과물 제출 -->
          <div class="panel">
            <div class="fsec-title" style="margin-bottom:14px"><span>⬆</span>최종 결과물 제출</div>
            <p style="font-size:13.5px;color:var(--ink-soft);margin-bottom:14px;line-height:1.6">프로젝트를 대표하는 최종 산출물(발표 자료, 최종 보고서, 데모 링크 등)을 등록해요. 완료 후 포트폴리오에 기록돼요.</p>
            <div class="final-drop" id="finalDrop" onclick="pickFinal()">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><path d="M17 8l-5-5-5 5"/><path d="M12 3v12"/></svg>
              <div style="font-weight:700;font-size:14px;margin-top:8px">최종 결과물 파일 업로드</div>
              <div style="font-size:12px;color:var(--ink-soft);margin-top:3px">클릭해서 파일 선택</div>
            </div>
            <div id="finalFileWrap"></div>
            <div class="fld one" style="margin-top:14px"><label>데모·저장소 링크 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><input type="text" placeholder="GitHub, 배포 URL 등"></div>
            <div class="fld one"><label>마무리 소감 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><textarea placeholder="프로젝트를 마치며 남길 기록" style="min-height:70px"></textarea></div>
          </div>
        </div>

        <!-- 완료 처리 -->
        <div class="side">
          <div class="apply-card" style="position:sticky;top:86px">
            <div style="font-family:var(--mono);font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:var(--ink-soft);margin-bottom:8px">멘토 검토</div>
            <div class="mentor-status"><span class="chip done" style="background:var(--grey-bg)">멘토 없음</span></div>
            <p style="font-size:12.5px;color:var(--ink-soft);line-height:1.6;margin-bottom:2px">이 프로젝트는 멘토를 초대하지 않아, <b style="color:var(--ink)">팀장이 직접 완료 처리</b>해요. (멘토가 있으면 완료 검토를 거쳐요.)</p>
            <div class="complete-cta" style="border:none;padding:16px 0 0;text-align:left">
              <div class="warn">완료하면 업무·모집이 잠기고 결과물이 포트폴리오에 기록돼요. 진행도가 100%가 아니어도 완료할 수 있어요.</div>
              <button class="btn pri big" style="width:100%;justify-content:center" onclick="completeProject()">프로젝트 완료 처리</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/workspace/complete.js"></script>
</body>
</html>