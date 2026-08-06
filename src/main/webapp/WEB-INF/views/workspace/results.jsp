<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${project.title}"/> 결과물</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
<jsp:include page="../includes/header.jsp" />
<main>
  <section id="v-results">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em><c:out value="${project.title}"/></em></h1>
    <p class="sub">승인 완료된 업무 결과물을 한곳에서 확인하고 내려받을 수 있어요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <div class="res-summary" aria-live="polite">
      <span id="resScope">전체</span> 승인 결과물
      <b id="resCount"><c:out value="${fn:length(results)}"/></b>건
      <span class="res-summary-note">· 팀이 완성한 산출물을 모아봤어요.</span>
    </div>

    <div class="res-filter" id="resFilter" aria-label="팀원별 결과물 필터">
      <button type="button" class="on" data-member-id="all" aria-pressed="true">
        전체 <span class="filter-count"><c:out value="${fn:length(results)}"/></span>
      </button>
      <c:forEach var="teamMember" items="${teamMembers}">
        <button type="button"
                data-member-id="${teamMember.memberId}"
                data-member-name="${fn:escapeXml(teamMember.memberName)}"
                aria-pressed="false">
          <c:out value="${teamMember.memberName}"/>
          <span class="filter-count"></span>
        </button>
      </c:forEach>
    </div>

    <div class="results-scroll" id="resultsScroll" data-scroll-threshold="6">
      <div class="results-grid" id="resGrid">
        <c:forEach var="result" items="${results}">
          <article class="res-card" data-member-id="${result.assigneeId}">
            <div class="rc-file">
              <div class="ficon ${result.fileTypeClass}">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <path d="M14 2v6h6"/>
                </svg>
              </div>
              <div class="fmeta">
                <div class="fn">
                  <c:choose>
                    <c:when test="${not empty result.originalFileName}">
                      <c:out value="${result.originalFileName}"/>
                    </c:when>
                    <c:otherwise>첨부 파일 없음</c:otherwise>
                  </c:choose>
                </div>
                <div class="fsz">
                  승인 완료<c:if test="${not empty result.submittedDateLabel}"> · 제출 <c:out value="${result.submittedDateLabel}"/></c:if>
                </div>
              </div>
            </div>

            <div class="rc-task">
              <div class="tt"><c:out value="${result.submitTitle}"/></div>
              <div class="task-name"><c:out value="${result.taskName}"/></div>
              <c:if test="${not empty result.submitContent}">
                <p class="desc"><c:out value="${result.submitContent}"/></p>
              </c:if>
            </div>

            <div class="rc-foot">
              <span class="rc-who">
                <span class="pic" data-avatar-id="${result.assigneeId}"><c:out value="${result.assigneeInitial}"/></span>
                <c:out value="${result.assigneeName}"/>
                <span class="chip approve">승인</span>
              </span>
              <c:if test="${not empty result.submitFile}">
                <a class="dl"
                   href="${ctx}/workspace/${project_id}/tasks/${result.taskId}/file"
                   title="${fn:escapeXml(result.originalFileName)} 다운로드"
                   aria-label="${fn:escapeXml(result.originalFileName)} 다운로드">
                  <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/>
                  </svg>
                </a>
              </c:if>
            </div>
          </article>
        </c:forEach>
      </div>

      <div class="res-empty" id="resEmpty" hidden>
        <div class="res-empty-icon" aria-hidden="true">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <path d="M14 2v6h6M8 15h8M8 11h3"/>
          </svg>
        </div>
        <strong id="resEmptyTitle">승인된 결과물이 아직 없어요</strong>
        <span id="resEmptyDescription">업무가 승인되면 이곳에 자동으로 표시됩니다.</span>
      </div>
    </div>
  </section>
</main>
<jsp:include page="../includes/footer.jsp" />
<script src="${ctx}/resources/js/workspace/results.js"></script>
</body>
</html>
