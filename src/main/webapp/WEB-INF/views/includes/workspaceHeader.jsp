<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="ws-nav">
    <button class="on"><a href="${ctx}/workspace/overview">개요</a></button>
    <button><a href="${ctx}/workspace/board">업무 보드</a></button>
    <button><a href="${ctx}/workspace/schedule">일정</a></button>
    <button><a href="${ctx}/workspace/meetings">회의</a></button>
    <button><a href="${ctx}/workspace/members">팀원 관리</a></button>
    <button><a href="${ctx}/workspace/results">결과물</a></button>
    <button><a href="${ctx}/workspace/complete">완료</a></button>
</div>