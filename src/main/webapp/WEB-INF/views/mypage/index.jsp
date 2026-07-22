<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>다모여 - 마이페이지</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="../resources/css/style.css">
</head>
<body>
    <jsp:include page="../includes/header.jsp" />
  <main>
  <!-- ========== 마이페이지 ========== -->
  <section id="v-mypage">
    <div class="eyebrow">My page</div>
    <h1 class="page"><em>마이페이지</em></h1>
    <p class="sub" style="margin-bottom:22px">내 프로젝트, 지원 현황, 관심 목록을 한곳에서 확인해요.</p>

    <div class="mp-head">
      <div class="big"><c:out value="${member.name.substring(0, 1)}" /></div>
      <div class="info">
        <h2><c:out value="${member.name}" /></h2>
        <div class="line"><c:out value="${univ.univ_name}" /> · <c:out value="${univ.dept_name}" /> · <c:out value="${member.grade}" />학년</div>
        <div class="badges">
          <c:if test="${member.approved}">
            <span class="b">✓ 학교 인증됨</span>
          </c:if>
        </div>
      </div>
      <button class="btn ghost edit" onclick="openModal('profileModal')">프로필 수정</button>
    </div>

    <div class="mp-stats">
      <div class="mp-stat"><div class="n">3</div><div class="k">진행 중</div></div>
      <div class="mp-stat"><div class="n">5</div><div class="k">완료</div></div>
      <div class="mp-stat"><div class="n">2</div><div class="k">지원 대기</div></div>
      <div class="mp-stat"><div class="n">7</div><div class="k">관심</div></div>
    </div>

    <div class="mp-tabs" id="mpTabs">
      <button class="on" data-t="joined">참여 중인 프로젝트</button>
      <button data-t="applied">내 지원 현황</button>
      <button data-t="offers">받은 제의</button>
      <button data-t="liked">관심 목록</button>
    </div>
    <div class="mp-list" id="mpList"></div>

    <jsp:include page="withdraw-modal.jsp" />
  </section>
  </main>
  <!-- 프로필 수정 모달 -->
<div class="modal-overlay" id="profileModal" onclick="if(event.target===this)closeModal('profileModal')">
  <div class="modal form-modal" role="dialog" aria-modal="true" aria-labelledby="profileTitle">
    <div class="modal-head">
      <div class="mh-info"><h3 id="profileTitle">프로필 수정</h3><div class="role">다른 사람에게 보이는 내 정보예요</div></div>
      <button class="modal-close" onclick="closeModal('profileModal')" aria-label="닫기">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"><path d="M18 6 6 18M6 6l12 12"/></svg>
      </button>
    </div>
    <div class="modal-body" id="profileBody">
      <form:form id="profileForm" modelAttribute="member" action="${ctx}/mypage/update" method="post">
        <div style="display:flex;align-items:center;gap:14px;margin-bottom:18px">
          <span class="big" style="width:56px;height:56px;border-radius:16px;background:linear-gradient(135deg,#2b46c8,#5b45c8);color:#fff;display:grid;place-items:center;font-size:22px;font-weight:800;flex:none"><c:out value="${member.name.substring(0, 1)}" /></span>
          <button class="btn ghost sm">사진 변경</button>
        </div>
        <div class="fld one"><label>이름</label><form:input path="name" type="text" id="pfName" name="name" value="${member.name}" /></div>
        <div class="frow">
          <div class="fld"><label>학교</label>
              <select name="univ_name" id="univSelect" required>
                  <option value="" <c:if test="${empty univ.univ_name}">selected</c:if>>학교를 선택하세요</option>
                  <c:forEach var="u" items="${univList}">
                    <option value="${u.univ_name}" <c:if test="${u.univ_name == univ.univ_name}">selected</c:if>>${u.univ_name}</option>
                  </c:forEach>
              </select>
          </div>
          <div class="fld"><label>학과</label>
              <select name="dept_id" id="deptSelect" required>
                  <option value="" <c:if test="${empty member.dept_id}">selected</c:if>>학과를 선택하세요</option>
                  <c:forEach var="dept" items="${univList}">
                    <option value="${dept.dept_id}" data-univ-name="${dept.univ_name}" <c:if test="${dept.dept_id == member.dept_id}">selected</c:if>>${dept.dept_name}</option>
                  </c:forEach>
              </select>
          </div>
        </div>
        <div class="frow">
          <div class="fld"><label>학년</label>
            <select id="pfYear" name="grade">
              <option>1</option>
              <option>2</option>
              <option>3</option>
              <option>4</option>
              <option>5</option>
            </select>
          </div>
        </div>
        <div class="frow">
          <div class="fld"><label>주전공</label><form:input path="major" type="text" id="pfMajor" name="major" value="${member.major}" /></div>
          <div class="fld"><label>복수전공 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><form:input path="double_major" type="text" id="pfMinor" name="double_major" value="${member.double_major}" placeholder="없으면 비워두세요" /></div>
        </div>
        <div class="fld one"><label>한 줄 소개 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><form:input path="intro" type="text" id="pfBio" name="intro" value="${member.intro}" placeholder="예: Spring 백엔드에 관심 많은 4학년" /></div>
        <div class="form-foot">
          <button class="btn ghost" onclick="closeProfile()">취소</button>
          <button class="btn pri" type="submit">저장하기</button>
        </div>
      </form:form>
    </div>
  </div>
</div>
  <jsp:include page="../includes/footer.jsp" />
  <script src="${ctx}/resources/js/common.js"></script>
  <script src="${ctx}/resources/js/login.js"></script>
  <script src="${ctx}/resources/js/myPage.js"></script>
</body>
</html>

