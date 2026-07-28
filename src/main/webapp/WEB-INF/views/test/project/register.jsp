<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>테스트 - 더미 프로젝트 등록</title>
  <style>
    body {
      max-width: 720px;
      margin: 40px auto;
      padding: 0 20px;
      font-family: sans-serif;
      line-height: 1.6;
    }
    input[type="text"],
    textarea {
      width: 100%;
      box-sizing: border-box;
      padding: 10px;
      font: inherit;
    }
    textarea {
      min-height: 180px;
      resize: vertical;
    }
    button {
      padding: 10px 18px;
      cursor: pointer;
    }
    .result {
      margin-top: 30px;
      padding: 20px;
      background: #f3f6f9;
      border-radius: 8px;
    }
    .mentor {
      margin: 14px 0;
      padding: 14px;
      background: white;
      border: 1px solid #dfe5eb;
      border-radius: 6px;
    }
    .error {
      margin-top: 30px;
      padding: 16px;
      color: #a40000;
      background: #fff0f0;
      border-radius: 8px;
      white-space: pre-wrap;
    }
  </style>
</head>
<body>
  <h1>MCP 멘토 매칭용 더미 프로젝트</h1>

  <form action="${ctx}/test/project/register" method="post">
    <input
      type="hidden"
      name="${_csrf.parameterName}"
      value="${_csrf.token}"
    />
    <input
      type="hidden"
      name="reference"
      value="${fn:escapeXml(reference)}"
    />

    <p>
      <label for="projectName">프로젝트 이름</label><br>
      <input
        id="projectName"
        type="text"
        name="projectName"
        value="${fn:escapeXml(projectName)}"
        placeholder="예: AI 기반 대학생 프로젝트 매칭 서비스"
        maxlength="200"
        required
      >
    </p>

    <p>
      <label for="projectDescription">프로젝트 설명</label><br>
      <textarea
        id="projectDescription"
        name="projectDescription"
        placeholder="사용 기술, 해결하려는 문제, 필요한 멘토링 분야를 구체적으로 작성하세요."
        minlength="10"
        maxlength="5000"
        required
      ><c:out value="${projectDescription}"/></textarea>
    </p>

    <button type="submit">멘토 추천 요청</button>
  </form>

  <c:if test="${not empty error}">
    <section class="error"><c:out value="${error}"/></section>
  </c:if>

  <c:if test="${submitted and not empty matchResult}">
    <section class="result">
      <h2>멘토 추천 결과</h2>
      <p>reference: <code><c:out value="${matchResult.reference}"/></code></p>
      <p>프로젝트 이름: <strong><c:out value="${projectName}"/></strong></p>
      <p>
        벡터 검색 후보 <c:out value="${matchResult.candidateCount}"/>명 /
        인덱싱 멘토 <c:out value="${matchResult.indexedCount}"/>명
      </p>
      <c:choose>
        <c:when test="${empty matchResult.recommendations}">
          <p>추천된 멘토가 없습니다.</p>
        </c:when>
        <c:otherwise>
          <c:forEach var="mentor" items="${matchResult.recommendations}">
            <article class="mentor">
              <h3>
                <c:out value="${mentor.name}"/>
                (#<c:out value="${mentor.memberId}"/>)
              </h3>
              <p>전문 분야: <c:out value="${mentor.field}"/></p>
              <p>경력: <c:out value="${mentor.career}"/></p>
              <p>자격증: <c:out value="${mentor.cert}"/></p>
              <p>벡터 유사도: <c:out value="${mentor.similarityScore}"/></p>
              <p><strong>추천 이유:</strong> <c:out value="${mentor.reason}"/></p>
            </article>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>
  </c:if>

  <p><a href="${ctx}/test/">테스트 홈으로</a></p>
</body>
</html>
