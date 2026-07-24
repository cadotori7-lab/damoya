<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${mode == 'update' ? '프로젝트 수정' : '프로젝트 모집 등록'}</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
    <section id="v-form" style="max-width: 800px; margin: 40px auto; padding: 0 20px;">
      <div class="eyebrow">${mode == 'update' ? 'Edit Project' : 'New Project'}</div>
      <h1 class="page" style="margin-bottom: 24px;"><em>${mode == 'update' ? '프로젝트 수정' : '프로젝트 모집'}</em></h1>

      <!-- 등록일 때는 /project/register, 수정일 때는 /project/update로 동적 설정 -->
      <form id="projectForm" action="${ctx}${mode == 'update' ? '/project/update' : '/project/register'}" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <!-- 수정 모드일 때만 고유 ID(projectId)를 숨겨서 전송 -->
        <c:if test="${mode == 'update'}">
            <input type="hidden" name="projectId" value="${project.projectId}">
        </c:if>

        <!-- 상태값(RECRUITING 등) 유지 -->
        <input type="hidden" name="status" id="projectStatus" value="${not empty project.status ? project.status : 'RECRUITING'}">

        <div class="panel" style="display: flex; flex-direction: column; gap: 20px;">
          
          <!-- 제목 -->
          <div class="fld">
            <label>프로젝트 제목</label>
            <input type="text" id="title" name="title" value="${project.title}" placeholder="예: 2026 캡스톤 경진대회 팀원 모집" required style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
          </div>

          <!-- 카테고리 -->
          <div class="fld">
            <label>카테고리</label>
            <select name="category" style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
              <option value="CONTEST" ${project.category == 'CONTEST' ? 'selected' : ''}>공모전</option>
              <option value="DEPARTMENT" ${project.category == 'DEPARTMENT' ? 'selected' : ''}>학과</option>
              <option value="LIBERAL" ${project.category == 'LIBERAL' ? 'selected' : ''}>교양</option>
              <option value="CLUB" ${project.category == 'CLUB' ? 'selected' : ''}>교내활동</option>
            </select>
          </div>

          <!-- 매칭 범위 -->
          <div class="fld">
            <label>매칭 범위</label>
            <select name="matchScope" style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
              <option value="교내" ${project.matchScope == '교내' ? 'selected' : ''}>교내</option>
              <option value="학과" ${project.matchScope == '학과' ? 'selected' : ''}>학과</option>
              <option value="전국" ${project.matchScope == '전국' ? 'selected' : ''}>전국</option>
            </select>
          </div>

          <!-- 모집 인원 -->
          <div class="fld">
            <label>모집 인원 (명)</label>
            <input type="number" name="capacity" value="${project.capacity}" placeholder="예: 4" required style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
          </div>

        <!-- 대상 학년 선택 (버튼 형식) -->
          <div class="fld">
            <label>대상 학년</label>
            <input type="hidden" name="targetGrade" id="targetGrade" value="${not empty project.targetGrade ? project.targetGrade : '1'}">
            
            <!-- 버튼 그룹 영역 -->
            <div class="grade-group" style="display: flex; gap: 8px;">
              <button type="button" class="btn-grade ${project.targetGrade == '1' ? 'active' : ''}" onclick="selectGrade('1', this)">1학년</button>
              <button type="button" class="btn-grade ${project.targetGrade == '2' ? 'active' : ''}" onclick="selectGrade('2', this)">2학년</button>
              <button type="button" class="btn-grade ${project.targetGrade == '3' ? 'active' : ''}" onclick="selectGrade('3', this)">3학년</button>
              <button type="button" class="btn-grade ${project.targetGrade == '4' ? 'active' : ''}" onclick="selectGrade('4', this)">4학년</button>
              <button type="button" class="btn-grade ${project.targetGrade == '무관' ? 'active' : ''}" onclick="selectGrade('무관', this)">학년 무관</button>
            </div>
          </div>

          <!-- 모집 마감일 -->
          <div class="fld">
            <label>모집 마감일</label>
            <input type="date" name="endDate" value="${project.endDate}" required style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
          </div>

          <!-- 소개 및 상세 내용 -->
          <div class="fld">
            <label>프로젝트 소개</label>
            <textarea name="summary" rows="5" placeholder="프로젝트에 대해 자유롭게 소개해주세요." style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px; resize:vertical;"><c:out value="${project.summary}"/></textarea>
          </div>

          <!-- 태그 입력 영역 -->
          <div class="fld one">
            <label>태그</label>
            <div class="tag-input" id="tagBox" onclick="this.querySelector('input').focus()" style="display:flex; flex-wrap:wrap; gap:6px; padding:8px; border:1px solid #ddd; border-radius:6px; background:#fff; cursor:text;">
              <input type="text" id="tagInput" placeholder="태그 입력 후 Enter" onkeydown="handleTagInput(event)" style="border:none; outline:none; flex-grow:1; padding:4px;">
            </div>
            <div class="hint" style="font-size:12px; color:var(--ink-soft); margin-top:4px;">기술·분야 태그로 검색 노출이 잘 돼요. (Enter로 추가)</div>
            <!-- 기존에 저장된 태그 문자열이 이 hidden input에 들어가며, JS가 이를 읽어 화면에 뱃지로 복원해줍니다 -->
            <input type="hidden" name="tags" id="hiddenTags" value="${project.tags}">
          </div>

          <!-- 버튼 영역 -->
          <div style="display: flex; gap: 10px; margin-top: 10px;">
            <button type="button" class="btn pri" style="flex: 1; padding: 12px; justify-content: center;" onclick="submitProject('${mode == 'update' ? project.status : 'RECRUITING'}')">${mode == 'update' ? '수정 완료' : '등록하기'}</button>
            <a href="${ctx}/project/list" class="btn ghost" style="flex: 1; padding: 12px; text-align: center; justify-content: center; display: flex; align-items: center; text-decoration: none;">취소</a>
          </div>

        </div>
      </form>
    </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />

  <script src="${ctx}/resources/js/projectForm.js"></script>
</body>
</html>