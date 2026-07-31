<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${mode == 'update' ? '내 소개 수정' : '내 소개 등록'} - 다모여</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
  <section id="v-talentpost">
    <div class="form-wrap">
      <a class="back" href="${ctx}/talent/list">← 인재풀</a>
      <div class="eyebrow">${mode == 'update' ? 'Edit profile post' : 'New profile post'}</div>
      <h1 class="page"><em>내 소개</em> ${mode == 'update' ? '수정하기' : '올리기'}</h1>
      <p class="sub">프로젝트를 찾고 있다는 걸 알려요. 팀장들이 보고 함께하기를 제의할 수 있어요.</p>

      <!-- mode가 update일 경우 /talent/update로, 아닐 경우 /talent/register로 전송 -->
      <form action="${ctx}/talent/${mode == 'update' ? 'update' : 'register'}" method="post" class="form-card" id="talentForm">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
      
        <!-- 수정 시 게시글 고유번호 전달 -->
        <c:if test="${mode == 'update'}">
            <input type="hidden" name="postId" value="${talent.postId}" />
        </c:if>
        
        <div class="fld one">
          <label>글 유형<span class="req">*</span></label>
          <div class="picker">
            <input type="radio" name="kind" id="tk1" value="MEMBER" ${talent.kind == 'MEMBER' || empty talent.kind ? 'checked' : ''}><label for="tk1">팀원으로 지원</label>
            <input type="radio" name="kind" id="tk2" value="MENTOR" ${talent.kind == 'MENTOR' ? 'checked' : ''}><label for="tk2">멘토로 참여</label>
          </div>
        </div>

        <!-- 매칭 범위 라디오 버튼  -->
        <div class="fld one">
          <label>매칭 범위<span class="req">*</span></label>
          <div class="picker" id="scopePicker">
            <input type="radio" name="matchScope" id="ms1" value="교내" ${talent.matchScope == '교내' || empty talent.matchScope ? 'checked' : ''}><label for="ms1">교내</label>
            <input type="radio" name="matchScope" id="ms2" value="전국" ${talent.matchScope == '전국' ? 'checked' : ''}><label for="ms2">전국</label>
          </div>
        </div>
        
        <div class="fld one">
            <label>한 줄 소개<span class="req">*</span></label>
            <input type="text" name="title" value="${talent.title}" placeholder="예: Spring 백엔드로 함께할 팀을 찾고 있어요" required>
        </div>
        
        <div class="frow">
          <div class="fld" style="grid-column: 1 / -1;">
            <label>희망 분야<span class="req">*</span></label>
            <input type="text" name="field" value="${talent.field}" maxlength="20" placeholder="예: 백엔드 개발 / 서비스 기획 (20자 이내)" required>
          </div>
        </div>

        <div class="frow">
          <div class="fld" style="grid-column: 1 / -1;">
            <label>가능 시간 <span class="req">*</span></label>
            <input type="text" name="availableTime" value="${talent.availableTime}" placeholder="예: 평일 저녁 · 주말 온·오프라인" required>
          </div>
        </div>

        <div class="frow">
          <div class="fld" style="grid-column: 1 / -1;">
            <label>기술 및 태그 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
            <input type="hidden" name="tags" id="tagsHiddenInput" value="${talent.tags}">
            <div class="tag-input" id="tagInputBox">
              <input type="text" id="tagTextInput" placeholder="태그 입력 후 엔터 (예: Spring, React, Figma)">
            </div>
            <div class="hint">엔터(Enter)나 쉼표(,)를 누르면 등록됩니다. (최대 6개, 각 태그 20자 이내)</div>
          </div>         
        </div>
         
        <!-- 카테고리 영역 -->
        <div class="fld one">
         <label>관심 카테고리 (1개 이상 선택) <span class="req">*</span></label>
         <div class="picker" id="categoryWrapper">
            <!-- 자바스크립트가 동적으로 채워줍니다 -->
         </div>
         <div id="categoryError" style="color: #d32f2f; font-size: 13px; margin-top: 6px; display: none;">관심 카테고리를 1개 이상 선택해 주세요.</div>
        </div>
        
        <div class="fld one">
            <label>자기소개<span class="req">*</span></label>
            <textarea name="content" placeholder="경험, 다뤄본 기술, 어떤 팀과 함께하고 싶은지 자유롭게 적어주세요." style="min-height:160px" required>${talent.content}</textarea>
        </div>
        
        <div class="form-foot" style="margin-top: 24px;">
          <a class="btn ghost" href="${ctx}/talent/list">취소</a>
          <button type="submit" class="btn pri">${mode == 'update' ? '수정 완료' : '등록하기'}</button>
        </div>
      </form>

    </div>
  </section>
  </main>
  
  <jsp:include page="../includes/footer.jsp" />
  
  <script>
      window.savedCategory = "${talent.category}";
  </script>
  
  <script src="${ctx}/resources/js/TalentForm.js"></script>
</body>
</html>