<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="ws-nav">
    <button class="on"><a href="${ctx}/workspace/${project_id}/overview">개요</a></button>
    <button><a href="${ctx}/workspace/${project_id}/board">업무 보드</a></button>
    <button><a href="${ctx}/workspace/${project_id}/schedule">일정</a></button>
    <button><a href="${ctx}/workspace/${project_id}/meetings">회의</a></button>
    <button><a href="${ctx}/workspace/${project_id}/members">팀원 관리</a></button>
    <button><a href="${ctx}/workspace/${project_id}/results">결과물</a></button>
    <button><a href="${ctx}/workspace/${project_id}/complete">완료</a></button>
</div>