<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="workspacePath" value="${pageContext.request.requestURI}"/>
<div class="ws-nav">
    <button class="${fn:contains(workspacePath, '/overview') ? 'on' : ''}"><a href="${ctx}/workspace/${project_id}/overview">개요</a></button>
    <button class="${fn:contains(workspacePath, '/board') ? 'on' : ''}"><a href="${ctx}/workspace/${project_id}/board">업무 보드</a></button>
    <button class="${fn:contains(workspacePath, '/schedule') ? 'on' : ''}"><a href="${ctx}/workspace/${project_id}/schedule">일정</a></button>
    <button class="${fn:contains(workspacePath, '/meetings') ? 'on' : ''}"><a href="${ctx}/workspace/${project_id}/meetings">회의</a></button>
    <c:if test="${canViewTeamManagement}">
        <button class="${fn:contains(workspacePath, '/members') ? 'on' : ''}"><a href="${ctx}/workspace/${project_id}/members">팀원 관리</a></button>
    </c:if>
    <button class="${fn:contains(workspacePath, '/results') ? 'on' : ''}"><a href="${ctx}/workspace/${project_id}/results">결과물</a></button>
    <button class="${fn:contains(workspacePath, '/complete') ? 'on' : ''}"><a href="${ctx}/workspace/${project_id}/complete">완료</a></button>
</div>
