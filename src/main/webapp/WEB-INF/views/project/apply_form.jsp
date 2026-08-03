<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%--
  프로젝트 지원 모달.
  project/detail.jsp 맨 아래에 include 한다.

    <jsp:include page="/WEB-INF/views/project/apply-modal.jsp" />

  여는 쪽(상세 페이지의 지원하기 버튼):
    <button type="button" class="btn pri" onclick="openModal('applyModal')">지원하기</button>

  필요한 모델 값: project(대상 프로젝트), myApplication(이미 지원했다면 그 내역, 없으면 null)
--%>

<div class="modal-overlay" id="applyModal">
  <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="applyTitle">
    <div class="modal-head">
      <div class="mh-info">
        <h3 id="applyTitle">이 프로젝트에 지원하기</h3>
        <div class="role">${project.title}</div>
      </div>
      <button type="button" class="modal-close" onclick="closeModal('applyModal')" aria-label="닫기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>

    <form class="modal-body" action="${ctx}/projects/${project.projectId}/apply" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

      <c:if test="${not empty applyError}">
        <div class="form-error"
             style="background:var(--reject-bg);color:var(--reject);border-radius:11px;padding:11px 14px;margin-bottom:16px;font-size:13.5px;font-weight:600">
          ${applyError}
        </div>
      </c:if>

      <%-- 모집 현황 요약 --%>
      <div class="sm-task">
        <div class="k">모집 현황</div>
        <div class="v">${project.category} · ${project.targetGrade}</div>
        <div class="due">${joinedCount} / ${project.capacity}명 모집 중</div>
      </div>
      
    <div class="fld one">
        <label>희망 포지션<span class="req">*</span></label>
        <!-- name="want_position" 을 자바 VO 필드명인 wantPosition 으로 변경! -->
        <input type="text" name="wantPosition" value="${form.wantPosition}"
              placeholder="예: 백엔드 · 인증/권한" required maxlength="100">
        <div class="hint">팀에서 맡고 싶은 역할을 적어주세요.</div>
    </div>

      <div class="fld one">
        <label>지원 동기<span class="req">*</span></label>
        <textarea name="motive" placeholder="이 프로젝트에 왜 참여하고 싶은지 적어주세요."
                  style="min-height:110px" required maxlength="1000">${form.motive}</textarea>
      </div>

      <div class="fld one">
        <label>경력 · 경험 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
        <textarea name="experience"
                  placeholder="관련 프로젝트, 사용해본 기술, 수강한 과목 등을 자유롭게 적어주세요."
                  style="min-height:100px" maxlength="1000">${form.experience}</textarea>
        <div class="hint">팀장이 팀원을 고를 때 참고하는 정보예요.</div>
      </div>

      <div class="role-hint">
        지원하면 <b>팀장이 검토</b>한 뒤 면접 제안 또는 승인을 진행해요.
        진행 상황은 <b>마이페이지 &gt; 내 지원 현황</b>에서 확인할 수 있어요.
      </div>

      <div class="form-foot">
        <button type="button" class="btn ghost" onclick="closeModal('applyModal')">취소</button>
        <button type="submit" class="btn pri">지원서 보내기</button>
      </div>
    </form>
  </div>
</div>