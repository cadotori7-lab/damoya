<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>계정 관리 | 다모여</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
  <jsp:include page="../includes/header.jsp" />
  <main>
    <section id="v-accounts">
      <a class="back" href="${ctx}/admin/dashboard">← 관리자 대시보드</a>
      <div class="eyebrow">Account management</div>
      <h1 class="page"><em>계정 관리</em></h1>
      <p class="sub">회원 계정을 조회하고, 신고·규정 위반 계정을 제재할 수 있어요.</p>

      <div class="acc-toolbar">
        <form action="${ctx}/admin/accounts" method="get" id="accountSearchForm" style="display:flex;gap:8px;flex-wrap:wrap;width:100%">
          <input type="text" name="search" value="${fn:escapeXml(search)}" placeholder="이름·아이디·학교로 검색" style="flex:1;min-width:180px">
          <select name="status" id="status">
            <option value="all" ${status eq 'all' ? 'selected' : ''}>전체 상태</option>
            <option value="ACTIVE" ${status eq 'ACTIVE' ? 'selected' : ''}>정상</option>
            <option value="SUSPENDED" ${status eq 'SUSPENDED' ? 'selected' : ''}>정지</option>
            <option value="WITHDRAWN" ${status eq 'WITHDRAWN' ? 'selected' : ''}>탈퇴</option>
            <option value="PENDING" ${status eq 'PENDING' ? 'selected' : ''}>승인대기</option>
          </select>
          <select name="role" id="role">
            <option value="all" ${role eq 'all' ? 'selected' : ''}>전체 역할</option>
            <option value="USER" ${role eq 'USER' ? 'selected' : ''}>일반</option>
            <option value="MENTOR" ${role eq 'MENTOR' ? 'selected' : ''}>멘토</option>
            <option value="ADMIN" ${role eq 'ADMIN' ? 'selected' : ''}>관리자</option>
          </select>
          <button type="submit" class="btn ghost">검색</button>
        </form>
      </div>

      <div class="tbl-wrap">
        <table id="accTable">
          <tr><th>회원</th><th>학교 / 학과</th><th>역할</th><th>가입일</th><th>상태</th><th>관리</th></tr>
        </table>
      </div>

      <nav aria-label="page" style="margin-top: 32px;">
        <ul class="pagination" style="display: flex; justify-content: center; gap: 8px; list-style: none; padding: 0;">
          <c:if test="${pageBean.min > 1}">
            <li class="page-item">
              <a href="${ctx}/admin/accounts?page=${pageBean.prevPage}&search=${fn:escapeXml(search)}&status=${status}&role=${role}"
                 style="padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none; color: var(--ink-soft);">이전</a>
            </li>
          </c:if>

          <c:forEach begin="${pageBean.min}" end="${pageBean.max}" var="pageNum">
            <li class="page-item ${pageNum == pageBean.currentPage ? 'active' : ''}">
              <a href="${ctx}/admin/accounts?page=${pageNum}&search=${fn:escapeXml(search)}&status=${status}&role=${role}"
                 style="padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none;
                 ${pageNum == pageBean.currentPage ? 'background-color: var(--ink); color: #fff; border-color: var(--ink);' : 'color: var(--ink-soft);'}">
                ${pageNum}
              </a>
            </li>
          </c:forEach>

          <c:if test="${pageBean.max < pageBean.pageCnt}">
            <li class="page-item">
              <a href="${ctx}/admin/accounts?page=${pageBean.nextPage}&search=${fn:escapeXml(search)}&status=${status}&role=${role}"
                 style="padding: 8px 14px; border: 1px solid #ddd; border-radius: 6px; text-decoration: none; color: var(--ink-soft);">다음</a>
            </li>
          </c:if>
        </ul>
      </nav>
    </section>
  </main>

  <jsp:include page="../includes/footer.jsp" />
  <script type="application/json" id="members-data"><c:out value="${members}" escapeXml="false"/></script>
  <script type="application/json" id="query-data"><c:out value="${queryJson}" escapeXml="false"/></script>
  <script>
    const members = JSON.parse(document.getElementById('members-data').textContent || '[]');
    const query = JSON.parse(document.getElementById('query-data').textContent || '{}');
    const ctx = '${pageContext.request.contextPath}';
    const csrfParameter = '${_csrf.parameterName}';
    const csrfToken = '${_csrf.token}';
    const currentPage = query.page || 1;
    const currentSearch = query.search || '';
    const currentStatus = query.status || 'all';
    const currentRole = query.role || 'all';
    <c:if test="${not empty msg}">
      alert('<c:out value="${msg}"/>');
    </c:if>
  </script>
  <script src="${ctx}/resources/js/common.js"></script>
  <script src="${ctx}/resources/js/adminAccounts.js"></script>
</body>
</html>
