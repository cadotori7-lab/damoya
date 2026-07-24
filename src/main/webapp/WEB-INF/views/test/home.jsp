<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>테스트 - 홈</title>
</head>
<body>
    <a href="/test/mentor/register">멘토 등록</a>
    <a href="${pageContext.request.contextPath}/test/chat">챗봇 테스트</a>
    <a href="${pageContext.request.contextPath}/test/project/register">더미 프로젝트 테스트</a>
</body>
</html>
