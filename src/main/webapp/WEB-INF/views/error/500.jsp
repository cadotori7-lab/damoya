<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>500 - 서버 오류</title>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.css">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="${ctx}/resources/css/style.css">
</head>
<body>
	<main style="min-height:72vh;display:flex;align-items:center;justify-content:center;padding:40px 20px;text-align:center">
		<div>
			<div class="eyebrow">Server error</div>
			<h1 class="page"><em>500</em> 서버에 문제가 생겼습니다</h1>
			<p class="sub">잠시 후 다시 시도해 주세요. 문제가 계속되면 관리자에게 알려주세요.</p>
			<div style="margin-top:24px">
				<a class="btn pri" href="${ctx}/home">홈으로 돌아가기</a>
			</div>
		</div>
	</main>
</body>
</html>
