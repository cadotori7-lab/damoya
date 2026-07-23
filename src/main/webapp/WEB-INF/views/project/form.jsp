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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
<main>
    <section id="v-form" style="max-width: 800px; margin: 40px auto; padding: 0 20px;">
      <div class="eyebrow">${mode == 'update' ? 'Edit Project' : 'New Project'}</div>
      <h1 class="page" style="margin-bottom: 24px;"><em>${mode == 'update' ? '프로젝트 수정' : '프로젝트 모집'}</em></h1>

      <form id="projectForm" action="${ctx}${mode == 'update' ? '/project/update' : '/project/register'}" method="post" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
        <c:if test="${mode == 'update'}">
            <input type="hidden" name="projectId" value="${project.projectId}">
        </c:if>

        <!-- 상태값 유지 (기본값 RECRUITING) -->
        <input type="hidden" name="status" id="projectStatus" value="${not empty project.status ? project.status : 'RECRUITING'}">
        <input type="hidden" id="selectedCategory" value="${project.category}">

        <div class="panel" style="display: flex; flex-direction: column; gap: 20px;">
          
      <c:if test="${mode == 'update'}">
            <div class="fld" style="background: #f8f9fa; padding: 14px 16px; border-radius: 8px; border: 1px solid #e9ecef;">
              <label style="font-weight: 700; font-size: 14px; margin-bottom: 8px; display: block; color: var(--ink);">모집 상태 설정</label>
              <div style="display: flex; gap: 24px; align-items: center;">
                <label style="cursor: pointer; display: inline-flex; align-items: center; gap: 6px; font-weight: 600; font-size: 14px; margin-bottom: 0;">
                  <input type="radio" name="statusRadio" value="RECRUITING" ${project.status == 'RECRUITING' or empty project.status ? 'checked' : ''} onclick="document.getElementById('projectStatus').value='RECRUITING'" style="margin: 0;"> 모집중
                </label>
                <label style="cursor: pointer; display: inline-flex; align-items: center; gap: 6px; font-weight: 600; font-size: 14px; margin-bottom: 0;">
                  <input type="radio" name="statusRadio" value="CLOSED" ${project.status == 'CLOSED' ? 'checked' : ''} onclick="document.getElementById('projectStatus').value='CLOSED'" style="margin: 0;"> 모집마감
                </label>
              </div>
            </div>
          </c:if>

          <!-- 제목 -->
          <div class="fld">
            <label>프로젝트 제목</label>
            <input type="text" id="title" name="title" value="${project.title}" placeholder="예: 2026 캡스톤 경진대회 팀원 모집" required style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
          </div>

          <!-- 매칭 범위 -->
          <div class="fld">
            <label>매칭 범위</label>
            <select name="matchScope" id="matchScope" onchange="changeCategoryOptions()" style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
              <option value="교내" ${project.matchScope == '교내' or empty project.matchScope ? 'selected' : ''}>교내</option>
              <option value="전국" ${project.matchScope == '전국' ? 'selected' : ''}>전국</option>
            </select>
          </div>

          <!-- 카테고리 -->
          <div class="fld">
            <label>카테고리</label>
            <select name="category" id="categorySelect" style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
            </select>
          </div>

          <!-- 모집 인원 -->
          <div class="fld">
            <label>모집 인원 (명)</label>
            <input type="number" name="capacity" value="${project.capacity}" placeholder="예: 4" required style="width:100%; padding:10px; border:1px solid #ddd; border-radius:6px;">
          </div>

          <!-- 대상 학년 선택 -->
          <div class="fld">
            <label>대상 학년</label>
            <input type="hidden" name="targetGrade" id="targetGrade" value="${not empty project.targetGrade ? project.targetGrade : '1'}">
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

          <!-- 프로젝트 소개 영역 -->
          <div class="fld">
            <label>프로젝트 소개</label>
            <div class="editor-container">
              <div class="editor-toolbar">
                <button type="button" title="굵게"><i class="fa-solid fa-bold"></i></button>
                <button type="button" title="기울임"><i class="fa-solid fa-italic"></i></button>
                <button type="button" title="취소선"><i class="fa-solid fa-strikethrough"></i></button>
                <button type="button" title="링크"><i class="fa-solid fa-link"></i></button>
                <div class="divider"></div>
                <label for="projectImageFile" title="사진 첨부" style="cursor: pointer;">
                  <i class="fa-regular fa-image"></i>
                </label>
                <input type="file" id="projectImageFile" name="uploadFile" accept="image/*" style="display: none;" onchange="handleImageUpload(this)">
                <div class="divider"></div>
                <button type="button" title="코드 블록"><i class="fa-solid fa-code"></i></button>
                <button type="button" title="인용구"><i class="fa-solid fa-quote-right"></i></button>
                <div class="divider"></div>
                <button type="button" title="글머리 기호"><i class="fa-solid fa-list-ul"></i></button>
                <button type="button" title="번호 매기기"><i class="fa-solid fa-list-ol"></i></button>
              </div>
              <textarea name="summary" id="summaryArea" class="editor-textarea" placeholder="프로젝트에 대해 자유롭게 소개해주세요."><c:choose><c:when test="${not empty project.summary}"><c:out value="${project.summary}"/></c:when><c:otherwise>[개발 프로젝트 모집 내용 예시]

• 프로젝트 주제 : 
• 프로젝트 목표 : 
• 예상 프로젝트 일정(횟수) : 
• 예상 커리큘럼 간략히 : 
• 예상 모집인원 : 
• 프로젝트 소개와 개설 이유 : 
• 프로젝트 관련 주의사항 : 
• 프로젝트에 지원할 수 있는 방법을 남겨주세요. (이메일, 카카오 오픈채팅방, 구글폼 등) : </c:otherwise></c:choose></textarea>
            </div>
          </div>

          <!-- 태그 입력 영역 -->
          <div class="fld one">
            <label>태그</label>
            <div class="tag-input" id="tagBox" onclick="this.querySelector('input').focus()" style="display:flex; flex-wrap:wrap; gap:6px; padding:8px; border:1px solid #ddd; border-radius:6px; background:#fff; cursor:text;">
              <input type="text" id="tagInput" placeholder="태그 입력 후 Enter" onkeydown="handleTagInput(event)" style="border:none; outline:none; flex-grow:1; padding:4px;">
            </div>
            <div class="hint" style="font-size:12px; color:var(--ink-soft); margin-top:4px;">기술·분야 태그로 검색 노출이 잘 돼요. (Enter로 추가)</div>
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