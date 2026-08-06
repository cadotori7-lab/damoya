<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
        <h2><c:out value="${member.name}" />(<c:out value="${member.login_id}" />)</h2>
        <div class="line"><c:out value="${univ.univ_name}" /> · <c:out value="${univ.dept_name}" />
           ·<c:if test="${member.grade ne null && member.grade ne ''}"><c:out value="${member.grade}" />학년</c:if></div>
        <div class="badges">
          <c:if test="${member.approved}">
            <span class="b">✓ 학교 인증됨</span>
          </c:if>
          <c:if test="${isMentor}">
            <span class="b">멘토</span>
            <c:if test="${not empty mentor.field}"><span class="b">${mentor.field}</span></c:if>
            <c:if test="${not empty mentor.cert}"><span class="b">${mentor.cert}</span></c:if>
          </c:if>
        </div>
        <c:if test="${isMentor && not empty mentor.career}">
          <div class="line" style="margin-top:4px">경력 ${mentor.career}</div>
        </c:if>
        <div></div>
        <c:if test="${member.intro != null && !member.intro.isEmpty()}">
          <div class="bio">"${member.intro}"</div>
        </c:if>
        <c:if test="${member.intro == null || member.intro.isEmpty()}">
          <div class="bio" style="color:var(--ink-soft);font-weight:500">한 줄 소개를 작성해보세요</div>
        </c:if>
      </div>
      <button class="btn ghost edit" onclick="openModal('profileModal')">프로필 수정</button>
    </div>

    <div class="mp-stats">
      <div class="mp-stat"><div class="n">${ongoingCount}</div><div class="k">진행 중</div></div>
      <div class="mp-stat"><div class="n">${doneCount}</div><div class="k">완료</div></div>
      <div class="mp-stat"><div class="n">${pendingCount}</div><div class="k">지원 대기</div></div>
      <div class="mp-stat"><div class="n">${likedList.size()}</div><div class="k">관심</div></div>
    </div>

    <div class="mp-tabs" id="mpTabs">
      <button class="on" data-t="joined">참여 중인 프로젝트</button>
      <button data-t="applied">내 지원 현황</button>
      <button data-t="offers">받은 제의</button>
      <button data-t="liked">관심 목록</button>
      <button data-t="done">참여 완료 프로젝트</button>
    </div>

    <!-- 참여 중인 프로젝트 -->
    <div class="mp-list" data-tab="joined">
      <c:choose>
        <c:when test="${empty participationList}">
          <p style="color:var(--ink-soft);padding:16px">참여 중인 프로젝트가 없어요.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="p" items="${participationList}">
            <div class="mp-item" style="--c:var(--cat-${p.category})" onclick="location.href='${ctx}/workspace/${p.projectId}/overview'">
              <div class="m-main">
                <div class="m-cat">${p.category}</div>
                <h4>${p.title}</h4>
              </div>
              <div class="m-right">
                <span class="role-tag ${p.projectRole == 'LEADER' ? 'lead' : 'member'}">${p.projectRole == 'LEADER' ? '팀장' : p.projectRole =='MENTOR' ? '멘토' : '팀원'}</span>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- 내 지원 현황 -->
    <div class="mp-list" data-tab="applied" style="display:none">
      <c:choose>
        <c:when test="${empty applicationList}">
          <p style="color:var(--ink-soft);padding:16px">지원한 프로젝트가 없어요.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="a" items="${applicationList}">
            <c:choose>
              <c:when test="${a.joinStatus == 'WAITING'}"><c:set var="chipClass" value="wait"/><c:set var="chipLabel" value="승인 대기"/></c:when>
              <c:when test="${a.joinStatus == 'INTERVIEW'}"><c:set var="chipClass" value="interview"/><c:set var="chipLabel" value="면접 예정"/></c:when>
              <c:when test="${a.joinStatus == 'REJECTED'}"><c:set var="chipClass" value="reject"/><c:set var="chipLabel" value="거절됨"/></c:when>
              <c:otherwise><c:set var="chipClass" value="wait"/><c:set var="chipLabel" value="${a.joinStatus}"/></c:otherwise>
            </c:choose>
            <form action="cancel-application" method="post">
              <input type="hidden" name="projectId" value="${a.projectId}" />
              <input type="hidden" name="memberId" value="${member.member_id}" />
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <div class="mp-item" style="--c:var(--cat-${a.category})">
                <div class="m-main" onclick="location.href='${ctx}/project/detail?id=${a.projectId}'">
                  <div class="m-cat">${a.category}</div>
                  <h4>${a.title}</h4>
                  <div class="m-meta"><span>지원일 ${fn:substring(a.appliedAt, 0, 10)}</span></div>
                </div>
                <div class="m-right">
                  <span class="chip ${chipClass}">${chipLabel}</span>
                  <button type="submit" class="btn sm ghost" onclick="return confirm('정말로 지원을 취소하시겠습니까?')">지원 취소</button>
                </div>
              </div>
            </form>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- 받은 제의 -->
    <div class="mp-list" data-tab="offers" style="display:none">
      <c:choose>
        <c:when test="${not empty offeredProjects}">
          <c:forEach var="o" items="${offeredProjects}">
            <form action="accept-offer" method="post">
              <input type="hidden" name="projectId" value="${o.projectId}" />
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <div class="mp-item" style="--c:var(--cat-${o.category})">
                <div class="m-main" onclick="location.href='${ctx}/project/detail?id=${o.projectId}'">
                  <div class="m-cat">${o.category}</div>
                  <h4>${o.title}</h4>
                  <div class="m-meta"><span>제의일 ${fn:substring(o.appliedAt, 0, 10)}</span></div>
                </div>
                <div class="m-right">
                  <span class="role-tag ${o.projectRole == 'LEADER' ? 'lead' : 'member'}">${o.projectRole == 'LEADER' ? '팀장' : '팀원'}</span>
                  <button type="submit" formaction="reject-offer" class="btn sm ghost" onclick="return confirm('이 프로젝트 제의를 거절하시겠습니까?')">거절</button>
                  <button type="submit" formaction="accept-offer" class="btn sm pri" onclick="return confirm('이 프로젝트 제의를 수락하시겠습니까?')">수락</button>
                </div>
              </div>
            </form>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <p style="color:var(--ink-soft);padding:16px">받은 제의가 없어요.</p>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- 관심 목록 -->
    <div class="mp-list" data-tab="liked" style="display:none">
      <c:choose>
        <c:when test="${empty likedList}">
          <p style="color:var(--ink-soft);padding:16px">관심 등록한 프로젝트가 없어요.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="l" items="${likedList}">
            <div class="mp-item" style="--c:var(--cat-${l.category})" onclick="location.href='${ctx}/project/detail?id=${l.projectId}'">
              <div class="m-main">
                <div class="m-cat">${l.category}</div>
                <h4>${l.title}</h4>
                <div class="m-meta"><span>${l.status == 'RECRUITING' ? '모집중' : '모집마감'}</span></div>
              </div>
              <div class="m-right">
                <button class="btn sm ghost">보기</button>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>
    <!-- 참여 완료된 프로젝트 -->
    <div class="mp-list" data-tab="done" style="display:none">
      <c:choose>
        <c:when test="${empty doneParticipationList}">
          <p style="color:var(--ink-soft);padding:16px">참여 완료된 프로젝트가 없어요.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="p" items="${doneParticipationList}">
            <div class="mp-item" style="--c:var(--cat-${p.category})" onclick="location.href='${ctx}/workspace/${p.projectId}/overview'">
              <div class="m-main">
                <div class="m-cat">${p.category}</div>
                <h4>${p.title}</h4>
              </div>
              <div class="m-right">
                <span class="role-tag ${p.projectRole == 'LEADER' ? 'lead' : 'member'}">${p.projectRole == 'LEADER' ? '팀장' : '팀원'}</span>
              </div>
            </div>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </div>

    <jsp:include page="withdraw-modal.jsp" />
    <jsp:include page="password-modal.jsp" />
    
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
        <div class="fld one"><label>이름</label><form:input path="name" type="text" id="pfName" name="name" value="${member.name}" required="required" minlength="2" /></div>
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
          <c:if test="${!isMentor}">
          <div class="fld"><label>학년</label>
              <select id="pfYear" name="grade">
                <option value="1" <c:if test="${member.grade == 1}">selected</c:if>>1</option>
                <option value="2" <c:if test="${member.grade == 2}">selected</c:if>>2</option>
                <option value="3" <c:if test="${member.grade == 3}">selected</c:if>>3</option>
                <option value="4" <c:if test="${member.grade == 4}">selected</c:if>>4</option>
                <option value="5" <c:if test="${member.grade == 5}">selected</c:if>>5</option>
              </select>
          </div>
          </c:if>
        </div>
        <c:if test="${!isMentor}">
        <div class="frow">
          <div class="fld"><label>복수전공 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label>
              <select name="double_major" id="doubleMajorSelect">
                  <option value="" <c:if test="${empty member.double_major}">selected</c:if>>없음</option>
                  <c:forEach var="dept" items="${univList}">
                    <option value="${dept.dept_name}" data-univ-name="${dept.univ_name}" <c:if test="${dept.dept_name == member.double_major}">selected</c:if>>${dept.dept_name}</option>
                  </c:forEach>
              </select>
          </div>
        </div>
        </c:if>
        <div class="fld one"><label>한 줄 소개 <span style="color:var(--ink-soft);font-weight:500">(선택)</span></label><form:input path="intro" type="text" id="pfBio" name="intro" value="${member.intro}" placeholder="예: Spring 백엔드에 관심 많은 4학년" /></div>

        <div class="frow" style="margin-top:8px;border-top:1px solid var(--line);padding-top:18px">
          <c:if test="${member.provider == 'LOCAL'}">
            <button type="button" class="btn ghost" onclick="closeModal('profileModal');openModal('passwordModal')">비밀번호 변경</button>
          </c:if>
          <button type="button" class="btn ghost danger" onclick="closeModal('profileModal');openModal('withdrawModal')">회원 탈퇴</button>
        </div>

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
  <c:if test="${openPassword}">
    <script>openModal('passwordModal');</script>
  </c:if>
  <c:if test="${openWithdraw}">
    <script>openModal('withdrawModal');</script>
  </c:if>
  
</body>
</html>

