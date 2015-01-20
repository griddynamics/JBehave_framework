<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<h1>Test Results List</h1>

<c:forEach items="${testResultList}" var="testResult">
    <a href="/testCases?sessionId=${testResult.testSessionTs}">${testResult.testSessionTs}</a>

    <br />
</c:forEach>

<br/><br/><br/><a href="/">Home Page</a><br/>

</body>
</html>