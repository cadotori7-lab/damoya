<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
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
    <!-- ========== 프로젝트 상세 ========== -->
  <section id="v-detail">
    <a class="back" href="${ctx}/project/list">← 목록으로</a>
    <div class="detail">
      <div>
        <div class="panel d-head">
          <div class="cat">${project.category} · ${project.matchScope}</div>
          <h2>${project.title}</h2>
          <div style="display:flex;gap:8px;flex-wrap:wrap">
            <span class="chip recruit">${project.status == 'RECRUTING' ? '모집중': '임시저장'}</span>
              <c:if test="${not empty project.tags}">
                <c:forEach var="tag" items="${fn:split(project.tags, ',')}">
                  <span class="tag"><c:out value="${tag}" /></span>
                </c:forEach>
              </c:if>
          </div>
          <div class="d-meta">
            <div><div class="k">카테고리</div><div class="v">${project.category}</div></div>
            <div><div class="k">대상 학년</div><div class="v">${project.targetGrade}</div></div>
            <div><div class="k">모집 인원</div><div class="v">${project.capacity}명</div></div>
            <div><div class="k">모집 기간</div><div class="v mono">${project.startDate}~ ${project.endDate}</div></div>
            <div><div class="k">대학 / 학과</div><div class="v">대진대 · 컴퓨터공학</div></div>
          </div>
          <div class="prose">
            <p><c:out value="${project.summary}" /></p>
           </div>
        </div>
            

        <div class="panel">
          <h5 style="font-size:16px;font-weight:800;margin-bottom:4px">댓글 <span class="mono" style="color:var(--ink-soft);font-size:14px">3</span></h5>
          <div class="cmt">
            <div class="pic" style="background:#0f9d8c">서</div>
            <div class="body"><div class="nm">서지우 <span>경영학 · 3학년</span></div><p>혹시 디자인만 담당해도 지원 가능할까요? 피그마 사용 가능합니다!</p></div>
          </div>
          <div class="cmt">
            <div class="pic" style="background:#e07a45">이</div>
            <div class="body"><div class="nm">이도현 <span>컴퓨터공학 · 4학년</span></div><p>백엔드 지원하고 싶습니다. Spring, MyBatis 경험 있어요.</p></div>
          </div>
          <div class="cmt">
            <div class="pic" style="background:#2b46c8">팀</div>
            <div class="body"><div class="nm">팀장 <span>답글</span></div><p>두 분 다 환영합니다 🙌 지원서 넣어주시면 면접 일정 잡을게요.</p></div>
          </div>

          <div class="cmt-form">
            <div class="pic">민</div>
            <div class="cf-input">
              <textarea id="cmtInput" placeholder="궁금한 점이나 지원 관련 문의를 남겨보세요."></textarea>
              <div class="cf-foot"><button class="btn pri sm" onclick="addComment()">댓글 등록</button></div>
            </div>
          </div>
        </div>
      </div>

      <div class="side">
        <div class="apply-card">
          <div class="num">2<small> / 4명 모집</small></div>
          <div class="bar"><span style="width:50%"></span></div>
          <div class="team-need mono" style="font-size:12px;color:var(--ink-soft)">백엔드 1 · 프론트 1 · 기획 1 남음</div>
          <div class="lead">
            <div class="pic">최</div>
            <div class="nm">최윤서 <small>팀장 · 컴퓨터공학 4학년</small></div>
          </div>
          <div style="font-family:var(--mono);font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:var(--ink-soft);margin-bottom:6px">지원 절차</div>
          <div class="stepline">
            <div class="act">지원</div><div>면접</div><div>승인</div>
          </div>
          <button class="btn pri" style="width:100%;justify-content:center;margin-top:18px">지원하기</button>
          <button class="btn ghost" style="width:100%;justify-content:center;margin-top:9px">♥ 관심 등록</button>
        </div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
  <script src="../resources/js/detail.js"></script>
</body>
</html>