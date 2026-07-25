<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로젝트 상세</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
    
    <style>
        .modal-overlay { display: none; }
    </style>
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  
  <main>
    <section id="v-detail">
      <a class="back" href="${ctx}/project/list">← 목록으로</a>
      <div class="detail">
        
        <!-- 좌측 상세 내용 영역 -->
        <div>
          <div class="panel d-head">
            <div class="cat">${project.category} · ${project.matchScope}</div>
            <h2><c:out value="${project.title}" /></h2>
            <div style="display:flex;gap:8px;flex-wrap:wrap">
              <span class="chip recruit">${project.status == 'RECRUITING' ? '모집중' : '모집마감'}</span>
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
              <div><div class="k">모집 마감일</div><div class="v mono"> ${project.endDate}</div></div>
              <div><div class="k">대학 / 학과</div><div class="v">대진대 · 컴퓨터공학</div></div>
            </div>
            
            <div class="prose" style="white-space: pre-wrap; line-height: 1.6;">
              <p><c:out value="${project.summary}" /></p>
            </div>
          </div>

          <!-- 프로젝트 수정 및 삭제 버튼 영역 -->
          <div class="d-actions" style="margin-bottom: 16px; display: flex; gap: 8px;">
            <a class="btn ghost sm" href="${ctx}/project/edit?id=${project.projectId}">수정하기</a>
            <button type="button" class="btn ghost sm" style="color: #e07a45; border-color: #e07a45;" onclick="deleteProject(${project.projectId})">삭제하기</button>
          </div>

          <div class="panel">
            <h5 style="font-size:16px;font-weight:800;margin-bottom:4px">댓글 <span class="mono" style="color:var(--ink-soft);font-size:14px">3</span></h5>
            <div class="cmt-form">
              <div class="pic">민</div>
              <div class="cf-input">
                <textarea id="cmtInput" placeholder="궁금한 점이나 지원 관련 문의를 남겨보세요."></textarea>
                <div class="cf-foot"><button class="btn pri sm" onclick="addComment()">댓글 등록</button></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 우측 사이드바 영역 -->
        <div class="side">
          <div class="apply-card">
            <div class="num">2<small> / ${project.capacity}명 모집</small></div>
            <div class="bar"><span style="width:50%"></span></div>
            <div class="team-need mono" style="font-size:12px;color:var(--ink-soft)">팀원 모집 진행 중</div>
            <div class="lead">
              <div class="pic">최</div>
              <div class="nm">최윤서 <small>팀장 · 컴퓨터공학 4학년</small></div>
            </div>
            <div style="font-family:var(--mono);font-size:11px;letter-spacing:.06em;text-transform:uppercase;color:var(--ink-soft);margin-bottom:6px">지원 절차</div>
            <div class="stepline">
              <div class="act">지원</div><div>면접</div><div>승인</div>
            </div>
            
            <!-- 자바스크립트 마감일 체크용 숨김 데이터 -->
            <div id="projectEndDate" data-end="${project.endDate}" style="display:none;"></div>
            
            <!-- 우측 사이드바 버튼 영역 -->
            <c:choose>
                <c:when test="${isOwner}">
                    <div style="background:#f8f9fa;border-radius:12px;padding:14px;text-align:center;margin-top:18px;border:1px solid #e5e7eb;">
                        <div style="font-size:13px;font-weight:600;color:var(--ink);margin-bottom:4px;">내가 등록한 프로젝트입니다</div>
                        <div style="font-size:11.5px;color:var(--ink-soft);">본문 하단의 수정/삭제 버튼을 이용해주세요.</div>
                    </div>
                </c:when>

                <c:when test="${hashApplied}">
                    <button type="button" class="btn ghost" style="width:100%;justify-content:center;margin-top:18px;background:#f3f4f6;color:#6b7280;cursor:not-allowed;" disabled>
                        이미 지원한 프로젝트예요
                    </button>
                </c:when>

                <c:when test="${project.status eq 'CLOSED'}">
                    <button type="button" class="btn ghost" style="width:100%;justify-content:center;margin-top:18px;background:#f3f4f6;color:#9ca3af;cursor:not-allowed;" disabled>
                        모집이 마감된 프로젝트입니다
                    </button>
                </c:when>

                <c:otherwise>
                    <button type="button" id="applyBtn" class="btn pri" style="width:100%;justify-content:center;margin-top:18px" onclick="openModal('applyModal')">
                        지원하기
                    </button>
                    <button class="btn ghost" style="width:100%;justify-content:center;margin-top:9px">♥ 관심 등록</button>
                </c:otherwise>
            </c:choose>
          </div>
        </div>

      </div>
    </section>
  </main>
  
  <jsp:include page="apply_form.jsp" />
  <jsp:include page="../includes/footer.jsp" />
  
  <script>
      const ctx = '${pageContext.request.contextPath}';
      <c:if test="${not empty msg}">
          alert("${msg}");
      </c:if>
  </script>
  
  <script src="${ctx}/resources/js/detail.js"></script>
</body>
</html>