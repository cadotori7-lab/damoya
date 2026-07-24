<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%-- meeting 이 있으면 수정, 없으면 등록 --%>
<c:set var="isEdit" value="${meeting.meeting_id != null}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>다모여 — ${isEdit ? '회의 수정' : '회의 등록'}</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />

  <main>
  <section id="v-meetingform">
    <div class="eyebrow">${isEdit ? 'Edit meeting' : 'New meeting'}</div>
    <h1 class="page"><em>${isEdit ? '회의 수정' : '회의 등록'}</em></h1>
    <p class="sub">회의 일정을 잡고, 안건과 결정 사항을 기록해요.</p>

    <div class="form-wrap">
      <c:if test="${not empty error}">
        <div style="background:var(--reject-bg);color:var(--reject);border-radius:11px;padding:12px 16px;margin-bottom:16px;font-size:13.5px;font-weight:600">
          ${error}
        </div>
      </c:if>

      <form action="${ctx}/workspace/${project_id}/meetings${isEdit ? '/'.concat(meeting.meeting_id).concat('/edit') : '/form/insert'}"
            method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <div class="form-card">
          <div class="fsec-title"><span>1</span>회의 정보</div>

          <div class="fld one">
            <label>회의명<span class="req">*</span></label>
            <input type="text" name="title" value="${fn:escapeXml(meeting.title)}"
                   placeholder="예: 2차 스프린트 회의" required maxlength="200">
          </div>

          <div class="frow">
            <div class="fld">
              <label>일시<span class="req">*</span></label>
              <input type="datetime-local" name="meet_date" required
                     value="${meeting.meetDateInput}">
            </div>
            <div class="fld">
              <label>한 줄 요약 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
              <input type="text" name="summary" value="${fn:escapeXml(meeting.summary)}"
                     placeholder="예: 관리자·통계 기능 분담" maxlength="300">
              <div class="hint">목록에 표시돼요.</div>
            </div>
          </div>
        </div>

        <div class="form-card">
          <div class="fsec-title"><span>2</span>회의록</div>
          <div class="fld one">
            <label>내용</label>
            <textarea name="content" style="min-height:280px;font-family:inherit"
placeholder="[안건]
- 관리자·통계 기능 담당 분담
- 2차 스프린트 범위 확정

[결정 사항]
- 통계 대시보드는 민재, 신고·계정 관리는 윤서가 담당
- 08/28부터 통합 테스트 시작">${fn:escapeXml(meeting.content)}</textarea>
            <div class="hint">줄바꿈이 그대로 보여요. 안건과 결정 사항을 나눠 적으면 읽기 편해요.</div>
          </div>

          <div class="form-foot">
            <a class="btn ghost"
               href="${ctx}/workspace/${project_id}/meetings<c:if test='${isEdit}'>/${meeting.meeting_id}</c:if>">취소</a>
            <button type="submit" class="btn pri">${isEdit ? '수정 저장' : '회의 등록'}</button>
          </div>
        </div>
      </form>
    </div>
  </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />
</body>
</html>
