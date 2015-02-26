<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
  <title>Test Sessions</title>
</head>


<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="red header-line header-line-first"/>
    <span class="red header-line"/>
    <span class="green header-line"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
  </div>

  <div class="header-title red-text">Test Sessions</div><div class="header-icon red moon-light"/><a class="header-back" href="/"></a>
</header>

<body class="body">


<c:forEach items="${testResultList}" var="testResult">
    <a href="/testCases?sessionId=${testResult.testSessionTs}">${testResult.testSessionTs}</a>

    <br />
</c:forEach>

</body>
</html>