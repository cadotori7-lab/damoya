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
  <!-- ========== 업무 보드 ========== -->
  <section id="v-tasks">
    <div class="eyebrow">Team workspace</div>
    <h1 class="page"><em>AI 헬스케어 웹서비스</em></h1>
    <p class="sub">모집이 끝난 팀의 작업 공간이에요. 업무·일정·회의를 한곳에서 관리해요.</p>

    <jsp:include page="../includes/workspaceHeader.jsp" />

    <div class="seg-toggle" id="taskToggle">
      <button class="on" data-p="all">전체 업무</button>
      <button data-p="mine">내 업무</button>
      <button data-p="team">팀 현황</button>
    </div>

    <div class="proj-switch" id="boardTools" style="gap:10px">
      <input type="text"
       id="taskSearch"
       class="board-search"
       placeholder="업무 검색 (업무명·설명)"
       oninput="onTaskSearch(this.value)">
      <c:if test="${isLeader}">
        <a class="btn pri sm"
          style="margin-left:auto"
          href="${ctx}/workspace/${project_id}/taskform">
          + 업무 등록
        </a>
      </c:if>
    </div>

<!-- Controller에서 받은 실제 업무 데이터 -->
<div id="taskData" hidden>
  <c:forEach var="task" items="${taskList}">
    <div class="task-source"
         data-task-id="${task.task_id}"
         data-status="${task.status}"
         data-assignee-id="${task.assignee_id}">

      <span class="task-name">
        <c:out value="${task.task_name}"/>
      </span>

      <span class="task-description">
        <c:out value="${task.description}"/>
      </span>

      <span class="task-due-date">
        <c:out value="${task.due_date}"/>
      </span>

      <span class="task-submit-title">
        <c:out value="${task.submit_title}"/>
      </span>

      <span class="task-submit-content">
        <c:out value="${task.submit_content}"/>
      </span>

      <span class="task-submit-file">
        <c:out value="${task.submit_file}"/>
      </span>

      <span class="task-reject-reason">
        <c:out value="${task.reject_reason}"/>
      </span>
    </div>
  </c:forEach>
</div>

<div id="boardPanel">
  <div class="kanban" id="kanban"></div>
</div>

    <!-- 팀 현황 (팀장) -->
    <div id="teamPanel" style="display:none">
      <div class="mp-stats">
        <div class="mp-stat"><div class="n mono">12</div><div class="k">전체 업무</div></div>
        <div class="mp-stat"><div class="n mono" style="color:var(--accent)">4</div><div class="k">진행중</div></div>
        <div class="mp-stat"><div class="n mono" style="color:var(--wait)">1</div><div class="k">검수 대기</div></div>
        <div class="mp-stat"><div class="n mono" style="color:var(--ok)">6</div><div class="k">승인 완료</div></div>
      </div>
      <div class="sec-label">팀원별 업무 현황 <span style="font-family:var(--sans);font-weight:500;color:var(--ink-soft);font-size:12px;text-transform:none;letter-spacing:0">· 이름을 누르면 담당 업무를 볼 수 있어요</span></div>
      <div class="member-row clickable" onclick="openMember('이도현')" tabindex="0" onkeydown="if(event.key==='Enter')openMember('이도현')">
        <span class="pic" style="background:#2b46c8">이</span>
        <div class="mr-info"><div class="nm">이도현 <span class="role">· 백엔드</span></div></div>
        <div class="mr-stat">완료 <b>3</b> / 배정 5</div>
        <svg class="chev" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>
      <div class="member-row clickable" onclick="openMember('김민재')" tabindex="0" onkeydown="if(event.key==='Enter')openMember('김민재')">
        <span class="pic" style="background:#2b46c8">민</span>
        <div class="mr-info"><div class="nm">김민재 <span class="role">· 백엔드</span></div></div>
        <div class="mr-stat">완료 <b>2</b> / 배정 4</div>
        <svg class="chev" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>
      <div class="member-row clickable" onclick="openMember('서지우')" tabindex="0" onkeydown="if(event.key==='Enter')openMember('서지우')">
        <span class="pic" style="background:#0f9d8c">서</span>
        <div class="mr-info"><div class="nm">서지우 <span class="role">· 기획/디자인</span></div></div>
        <div class="mr-stat">완료 <b>3</b> / 배정 3</div>
        <svg class="chev" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M9 18l6-6-6-6"/></svg>
      </div>
    </div>
  </section>
  </main>
 
 <!-- ===== 팀원 업무 상세 모달 ===== -->
<div class="modal-overlay" id="memberModal" onclick="if(event.target===this)closeMember()">
  <div class="modal" role="dialog" aria-modal="true" aria-labelledby="mmName">
    <div class="modal-head">
      <span class="pic" id="mmPic"></span>
      <div class="mh-info"><h3 id="mmName"></h3><div class="role" id="mmRole"></div></div>
      <button class="modal-close" onclick="closeMember()" aria-label="닫기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>
    <div class="modal-body">
      <div class="modal-stats" id="mmStats"></div>
      <div class="sec-label" style="margin-top:0">담당 업무</div>
      <div id="mmTasks"></div>
    </div>
  </div>
</div>

<!-- ===== 결과물 제출 모달 ===== -->
<div class="modal-overlay"
     id="submitModal"
     onclick="if(event.target===this) closeSubmit()">

  <div class="modal form-modal"
       role="dialog"
       aria-modal="true"
       aria-labelledby="smTitle">

    <div class="modal-head">
      <div class="mh-info">
        <h3 id="smTitle">결과물 제출</h3>
        <div class="role">
          <span id="smTaskName">업무명</span>
          ·
          <span id="smTaskDue">마감일</span>
        </div>
      </div>

      <button type="button"
              class="modal-close"
              onclick="closeSubmit()"
              aria-label="닫기">
        ×
      </button>
    </div>

    <form id="submitForm"
      class="submit-form"
      method="post"
      enctype="multipart/form-data">

      <input type="hidden"
         name="${_csrf.parameterName}"
         value="${_csrf.token}">

      <div class="field">
        <label for="submitTitle">제출 제목</label>
        <input id="submitTitle"
             name="submit_title"
             type="text"
             maxlength="200"
             required>
      </div>

      <div class="field">
        <label for="submitContent">제출 내용</label>
        <textarea id="submitContent"
                name="submit_content"
                rows="6"
                required></textarea>
      </div>

      <div class="field">
        <label for="submitFile">첨부 파일 (선택, 최대 20MB)</label>
        <input id="submitFile"
             name="submitFile"
             type="file"
             accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.hwp,.hwpx,.txt,.zip,.png,.jpg,.jpeg">
      </div>

      <div class="modal-actions">
        <button type="button"
              class="btn ghost"
              onclick="closeSubmit()">취소</button>
        <button type="submit"
              class="btn pri">제출</button>
      </div>
    </form>
  </div>
</div>

<!-- ===== 업무 상세 + 검수 모달 ===== -->
<div class="modal-overlay" id="taskModal" onclick="if(event.target===this)closeTask()">
  <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="taskModalTitle">
    <div class="modal-head">
      <div class="mh-info"><h3 id="taskModalTitle"></h3><div class="role">업무 상세</div></div>
      <button class="modal-close" onclick="closeTask()" aria-label="닫기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>
    <div class="modal-body" id="taskModalBody"></div>
  </div>
</div>

  <jsp:include page="../includes/footer.jsp" />
  <script>
    window.APP_CONTEXT = '${ctx}';
    window.PROJECT_ID = Number('${project_id}');
    window.LOGIN_MEMBER_ID = Number('${currentMemberId}');
  </script>
  <script src="${ctx}/resources/js/workspace/board.js"></script>
  <script src="${ctx}/resources/js/common.js"></script>
</body>
</html>