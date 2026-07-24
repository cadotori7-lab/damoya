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
     <!-- ========== 업무 등록 ========== -->
  <section id="v-taskform">
    <div class="form-wrap">
      <a class="back" href="${ctx}/workspace/{project_id}/board">← 업무 보드</a>
      <div class="eyebrow">New task</div>
      <h1 class="page"><em>업무 등록</em></h1>
      <p class="sub">AI 헬스케어 웹서비스 · 팀장이 업무를 등록하고 담당자를 배정해요.</p>

      <form class="form-card"
          method="post"
          action="${ctx}/workspace/${project_id}/tasks">

        <input type="hidden"
           name="${_csrf.parameterName}"
           value="${_csrf.token}">

        <div class="fld one">
          <label>
              업무명<span class="req">*</span>
          </label>

          <input type="text"
              name="task_name"
              placeholder="예: 프로젝트 지원·승인 API 구현"
              required>
        </div>

        <div class="fld one">
          <label>설명</label>

          <textarea name="description"
                placeholder="업무 내용, 완료 기준, 참고 사항을 적어주세요."
                style="min-height:90px"></textarea>
        </div>

        <div class="frow">
          <div class="fld">
            <label>
                담당자<span class="req">*</span>
            </label>

            <select name="assignee_id" required>
                <option value="">담당자 선택</option>

                <!-- 현재는 임시 ID -->
                <option value="7">김해든</option>
                <option value="16">김해든2</option>
            </select>
          </div>

          <div class="fld">
            <label>
                마감일<span class="req">*</span>
            </label>

            <input type="date"
                   name="due_date"
                   required>
          </div>
        </div>

        <div class="form-foot">
          <a class="btn ghost"
            href="${ctx}/workspace/${project_id}/board">
            취소
          </a>

          <button class="btn pri" type="submit">
            업무 등록
          </button>
        </div>
      </form>
    </div>
  </section>
  </main>
<jsp:include page="../includes/footer.jsp" />
</body>
</html>