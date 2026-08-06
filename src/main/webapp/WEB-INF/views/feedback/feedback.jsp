<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>다모여 — 멘토 피드백</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
  <main>
  <section id="v-feedback">
    <div class="eyebrow">Mentor feedback</div>
    <h1 class="page"><em>멘토 피드백</em></h1>
    <p class="sub">담당 프로젝트의 진행 상황을 확인하고 단계별 피드백을 남겨요.</p>

    <c:if test="${not empty msg}">
      <div class="panel" style="margin-bottom:14px;color:var(--ok)"><c:out value="${msg}"/></div>
    </c:if>
    <c:if test="${not empty error}">
      <div class="panel" style="margin-bottom:14px;color:var(--reject)"><c:out value="${error}"/></div>
    </c:if>

    <div class="detail">
      <div>
        <div class="panel">
          <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px">
            <div>
              <div class="d-head"><span class="cat"><c:out value="${project.category}"/></span></div>
              <h2 style="font-size:20px;font-weight:800;margin-top:4px"><c:out value="${project.title}"/></h2>
            </div>
            <span class="mentor-badge"><span class="pic"><c:out value="${fn:substring(member.name, 0, 1)}"/></span><c:out value="${member.name}"/> 멘토</span>
          </div>
          <div class="fb-summary">
            <div class="box"><div class="n mono">${teamSummary != null ? teamSummary.progressPercent : 0}%</div><div class="k">진행도</div></div>
            <div class="box"><div class="n mono" style="color:var(--ok)">${teamSummary != null ? teamSummary.taskDone : 0}</div><div class="k">승인 완료 업무</div></div>
            <div class="box"><div class="n mono">${dday != null ? (dday >= 0 ? 'D-' : 'D+') : ''}${dday != null ? (dday >= 0 ? dday : -dday) : '—'}</div><div class="k">마감까지</div></div>
          </div>
        </div>

        <div class="panel">
          <div class="fsec-title" style="margin-bottom:14px"><span>✎</span>피드백 작성</div>
          <form action="${ctx}/feedback/${project.projectId}" method="post">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="fld">
              <label>평가 단계<span class="req">*</span></label>
              <div class="picker">
                <input type="radio" name="stage" id="st1" value="MIDTERM" checked><label for="st1">중간 점검</label>
                <input type="radio" name="stage" id="st2" value="FINAL"><label for="st2">최종 평가</label>
                <input type="radio" name="stage" id="st3" value="REVIEW"><label for="st3">완료 검토</label>
              </div>
            </div>
            <div class="fld one">
              <label>내용<span class="req">*</span></label>
              <textarea name="content" required placeholder="진행 상황에 대한 피드백을 남겨주세요. 잘된 점과 보완할 점을 구체적으로 적어주면 팀에 도움이 돼요."></textarea>
            </div>
            <div class="form-foot"><button type="submit" class="btn pri">피드백 등록</button></div>
          </form>
        </div>
      </div>

      <div class="side">
        <div class="panel" style="position:sticky;top:86px">
          <div class="fsec-title" style="margin-bottom:16px"><span>◷</span>지난 피드백</div>
          <c:choose>
            <c:when test="${empty feedbacks}">
              <p style="color:var(--ink-soft);font-size:13.5px">아직 남긴 피드백이 없어요.</p>
            </c:when>
            <c:otherwise>
              <div class="fb-timeline">
                <c:forEach var="f" items="${feedbacks}">
                  <div class="fb-item">
                    <div class="fh"><span class="chip stage"><c:out value="${f.stageLabel}"/></span><span class="date">${f.createdAtDisplay}</span></div>
                    <p><c:out value="${f.content}"/></p>
                  </div>
                </c:forEach>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>
