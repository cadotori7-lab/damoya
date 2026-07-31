<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
  <link rel="stylesheet"
    href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="../resources/css/style.css">
</head>

<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
    <!-- ========== 관리자 ========== -->
    <section id="v-admin">
      <div class="eyebrow">Admin console</div>
      <h1 class="page"><em>관리자</em> 대시보드</h1>
      <p class="sub">학교 인증, 신고 처리, 서비스 통계를 한곳에서 관리해요.</p>
      <div style="margin:-12px 0 22px;display:flex;gap:8px"><a class="btn ghost sm" href="${ctx}/admin/posts">게시물 관리
          →</a><a class="btn ghost sm" href="${ctx}/admin/accounts">계정 관리 →</a></div>

      <div class="stats">
        <div class="stat">
          <div class="k">전체 회원</div>
          <div class="n">${dashboard.memberCount}<small>명</small></div>
        </div>
        <div class="stat">
          <div class="k">진행 중 프로젝트</div>
          <div class="n">${dashboard.ongoingProjectCount}<small>개</small></div>
        </div>
        <div class="stat">
          <div class="k">인증 대기 학교</div>
          <div class="n">${dashboard.approvedRequiredMemberCount}<small>개</small></div>
        </div>
        <div class="stat">
          <div class="k">미처리 신고</div>
          <div class="n">${dashboard.reportList.size()}<small>개</small></div>
        </div>
      </div>

      <div class="admin-grid">
        <div class="tbl-wrap">
          <div class="tbl-h">학교 인증 승인 <span class="cnt">${dashboard.approvedRequiredMemberCount}</span></div>
          <table>
            <tr>
              <th>신청자</th>
              <th>학교 / 학과</th>
              <th>처리</th>
            </tr>

            <c:forEach items="${dashboard.approvedRequiredMembers}" var="approvedRequiredMember">
              <tr>
                <td>
                  <div class="nm">${approvedRequiredMember.name}</div>
                </td>
                <td>
                  <div class="mono">${approvedRequiredMember.univ_name} · ${approvedRequiredMember.dept_name}</div>
                </td>
                <td>
                  <div class="act-btns">
                    <form action="${ctx}/admin/approve-member" method="post" style="display:inline">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                      <input type="hidden" name="memberId" value="${approvedRequiredMember.member_id}"/>
                      <button type="submit" class="btn sm pri">승인</button>
                    </form>
                    <form action="${ctx}/admin/reject-member" method="post" style="display:inline">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                      <input type="hidden" name="memberId" value="${approvedRequiredMember.member_id}"/>
                      <button type="submit" class="btn sm ghost">반려</button>
                    </form>
                  </div>
                </td>
              </tr>
            </c:forEach>

          </table>
        </div>

        <div class="tbl-wrap">
          <div class="tbl-h">신고 처리 <span class="cnt">${dashboard.reportList.size()}</span></div>
          <table>
            <tr>
              <th>대상</th>
              <th>사유</th>
              <th>상태</th>
            </tr>

            <c:forEach items="${dashboard.reportList}" var="report">

              <tr>
                <td>
                  <div class="nm">${report.memberName}</div>
                  <div class="mono">프로젝트</div>
                </td>
                <td>${report.reason}</td>
                <c:choose>
                  <c:when test="${report.status == 'received'}">
                    <td><span class="chip wait">접수</span></td>
                  </c:when>
                  <c:when test="${report.status == 'processing'}">
                    <td><span class="chip processing">처리중</span></td>
                  </c:when>
                  <c:when test="${report.status == 'completed'}">
                    <td><span class="chip completed">처리완료</span></td>
                  </c:when>
                </c:choose>
              </tr>

            </c:forEach>

          </table>
        </div>
      </div>
    </section>
  </main>
  <jsp:include page="../includes/footer.jsp" />
</body>
</html>