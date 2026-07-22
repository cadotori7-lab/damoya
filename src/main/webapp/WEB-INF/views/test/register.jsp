<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>테스트 - 멘토 등록</title>
</head>
<body>
    <form action="/test/mentor/register" method="post" enctype="multipart/form-data">
        <input type="text" name="name" placeholder="이름" required>
        <input type="file" name="file" accept="image/*" required>
        <input type="submit" value="등록">
    </form>
</body>
</html>