<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${project.title}"/> 완료</title>
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
    <h1 class="page"><em><c:out value="${project.title}"/></em></h1>
    <p class="sub">모든 업무를 완료하고 프로젝트의 최종 결과물을 제출해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <c:if test="${not empty completeMessage}">
      <div class="board-notice board-notice-success"><c:out value="${completeMessage}"/></div>
    </c:if>
    <c:if test="${not empty completeError}">
      <div class="board-notice board-notice-error"><c:out value="${completeError}"/></div>
    </c:if>

    <c:choose>
      <c:when test="${completion.completed}">
        <div class="complete-done completion-result-card">
          <div class="big-ic">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
          </div>
          <span class="chip done">프로젝트 종료</span>
          <h2>최종 결과물 제출을 완료했어요</h2>
          <p><c:out value="${project.title}"/> 프로젝트가 완료 처리되었습니다.</p>

          <div class="completion-result-detail">
            <div class="result-detail-row">
              <span>제목</span>
              <strong><c:out value="${completion.finalResult.submit_title}"/></strong>
            </div>
            <div class="result-detail-row result-content-row">
              <span>설명 및 소감</span>
              <div><c:out value="${completion.finalResult.submit_content}"/></div>
            </div>
            <div class="result-detail-row">
              <span>제출 파일</span>
              <a class="completion-download"
                 href="${ctx}/workspace/${project_id}/tasks/${completion.finalResult.task_id}/file">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/></svg>
                <c:out value="${completion.finalFileName}"/>
              </a>
            </div>
          </div>

          <div class="completion-result-actions">
            <a class="btn ghost" href="${ctx}/workspace/${project_id}/overview">개요로 이동</a>
            <a class="btn pri" href="${ctx}/project/my">내 프로젝트</a>
          </div>
        </div>
      </c:when>

      <c:otherwise>
        <div class="completion-layout">
          <section class="panel completion-check-panel">
            <div class="completion-section-head">
              <div>
                <div class="fsec-title"><span>✓</span>업무 완료 확인</div>
                <p>최종 결과물을 제출하려면 모든 일반 업무가 완료되어야 합니다.</p>
              </div>
              <div class="completion-count ${completion.allTasksCompleted ? 'ready' : ''}">
                <strong><c:out value="${completion.completedTaskCount}"/></strong>
                <span>/ <c:out value="${completion.totalTaskCount}"/> 완료</span>
              </div>
            </div>

            <c:choose>
              <c:when test="${completion.allTasksCompleted}">
                <div class="completion-ready">
                  <span class="ready-icon">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>
                  </span>
                  <div><strong>모든 업무가 완료되었습니다.</strong><span>최종 결과물을 제출할 수 있어요.</span></div>
                </div>
              </c:when>
              <c:otherwise>
                <div class="completion-blocked-note">
                  완료되지 않은 업무 <b><c:out value="${completion.incompleteTasks.size()}"/></b>건을 먼저 처리해주세요.
                </div>
                <div class="completion-table-scroll">
                  <table class="completion-task-table">
                    <thead>
                      <tr><th>업무 대상</th><th>업무 제목</th><th>마감일</th><th>업무 상태</th></tr>
                    </thead>
                    <tbody>
                      <c:forEach var="task" items="${completion.incompleteTasks}">
                        <tr>
                          <td><c:out value="${task.assigneeName}"/></td>
                          <td>
                            <a href="${ctx}/workspace/${project_id}/board?task=${task.taskId}">
                              <c:out value="${task.taskName}"/>
                            </a>
                          </td>
                          <td class="mono"><c:out value="${task.dueDateLabel}"/></td>
                          <td><span class="chip ${task.statusClass}"><c:out value="${task.statusLabel}"/></span></td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>
                <div class="completion-board-link">
                  <a class="btn ghost sm" href="${ctx}/workspace/${project_id}/board">업무 보드에서 확인 →</a>
                </div>
              </c:otherwise>
            </c:choose>
          </section>

          <section class="panel completion-form-panel ${completion.allTasksCompleted ? '' : 'is-locked'}">
            <div class="completion-section-head">
              <div>
                <div class="fsec-title"><span>⬆</span>최종 결과물 제출</div>
                <p>파일과 프로젝트를 대표하는 제목, 설명 및 소감을 기록해주세요.</p>
              </div>
            </div>

            <c:choose>
              <c:when test="${not completion.allTasksCompleted}">
                <div class="completion-lock-message">
                  <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                  <strong>아직 제출할 수 없어요</strong>
                  <span>위의 미완료 업무를 모두 완료하면 제출 양식이 열립니다.</span>
                </div>
              </c:when>
              <c:when test="${not isLeader}">
                <div class="completion-lock-message">
                  <strong>팀장만 최종 결과물을 제출할 수 있어요.</strong>
                  <span>모든 업무가 완료되었습니다. 팀장에게 최종 제출을 요청해주세요.</span>
                </div>
              </c:when>
              <c:otherwise>
                <form id="finalResultForm"
                      class="completion-form"
                      method="post"
                      enctype="multipart/form-data"
                      action="${ctx}/workspace/${project_id}/complete">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                  <div class="completion-field">
                    <label for="submitTitle">최종 결과물 제목 <span>*</span></label>
                    <input id="submitTitle" name="submitTitle" type="text" maxlength="200" required placeholder="예: 다모여 프로젝트 최종 산출물">
                  </div>

                  <div class="completion-field">
                    <label for="submitDescription">최종 결과물 설명 <span>*</span></label>
                    <textarea id="submitDescription" name="submitDescription" maxlength="5000" required placeholder="최종 결과물의 구성과 주요 내용을 설명해주세요."></textarea>
                  </div>

                  <div class="completion-field">
                    <label for="reflection">프로젝트 소감 <span>*</span></label>
                    <textarea id="reflection" name="reflection" maxlength="5000" required placeholder="프로젝트를 진행하며 느낀 점과 배운 점을 남겨주세요."></textarea>
                  </div>

                  <div class="completion-field">
                    <label for="finalFile">최종 결과물 파일 <span>*</span></label>
                    <input id="finalFile"
                           name="finalFile"
                           type="file"
                           required
                           accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.hwp,.hwpx,.txt,.md,.zip,.png,.jpg,.jpeg">
                    <small>문서·이미지·ZIP 파일, 최대 20MB</small>
                    <div class="final-file" id="finalFilePreview" hidden>
                      <span class="fi">
                        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/></svg>
                      </span>
                      <div class="fmeta"><div class="fn" id="finalFileName"></div><div class="fs" id="finalFileSize"></div></div>
                      <button type="button" class="rm" id="removeFinalFile" aria-label="선택 파일 제거">×</button>
                    </div>
                  </div>

                  <div class="completion-submit-area">
                    <p>제출하면 최종 결과물이 저장되고 프로젝트 상태가 종료로 변경됩니다.</p>
                    <button type="submit" class="btn pri big" id="completeSubmitButton">최종 결과물 제출</button>
                  </div>
                </form>
              </c:otherwise>
            </c:choose>
          </section>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</main>
<jsp:include page="../includes/footer.jsp" />
<script src="${ctx}/resources/js/workspace/complete.js"></script>
</body>
</html>
