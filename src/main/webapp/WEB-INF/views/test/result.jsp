<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>테스트 - 자격증 검증 결과</title>
</head>
<body>
    <h1>멘토 자격증 이름 검증 결과</h1>

    <c:choose>
        <c:when test="${not empty error}">
            <p style="color:red;">${error}</p>
        </c:when>
        <c:otherwise>
            <p>입력한 이름: <strong>${name}</strong></p>
            <p>일치 여부:
                <strong style="color:${result.matched ? 'green' : 'red'};">
                    ${result.matched ? '일치' : '불일치'}
                </strong>
            </p>
            <p>유사도 점수: ${result.score} (임계값: ${result.threshold})</p>
            <p>추출된 텍스트: ${result.extracted_text}</p>
        </c:otherwise>
    </c:choose>

    <p><a href="/test/mentor/register">다시 등록</a></p>
</body>
</html>
