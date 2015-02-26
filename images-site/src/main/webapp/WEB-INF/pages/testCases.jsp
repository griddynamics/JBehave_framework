<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

  <div class="header-title red-text">Test Session</div><div class="header-icon red moon-light"/><a class="header-back" href="/"></a>
</header>

<body class="body">

	<table border="1" class="pure-table">

        <thead><tr>
            <td>Test ID</td>
            <td>Time</td>
            <td>Test result</td>
            <td>Area tested</td>
            <td>Number of pixels which differ from baseline</td>
        </tr></thead>

	    <c:forEach items="${testResultList}" var="testResult">
		    <tr>
		        <td>
		            <a href="/testResult?getId=${testResult.testId}">${testResult.testId}</a>
                </td>
                <td>
                    ${testResult.testRunTs}
                </td>
                <td>
                    <c:choose>
                      <c:when test="${testResult.testResult eq 'false'}">
                          <font color="red">FAILED</font>
                      </c:when>
                      <c:when test="${testResult.testResult eq 'true'}">
                          <font color="green">PASSED</font>
                      </c:when>
                    </c:choose>
                </td>
                <td>
                	${testResult.testAreaId}
                </td>
                <td>
                    ${testResult.numberOfDiffPixels}
                </td>
            </tr>
	    </c:forEach>

	</table>
 
 </body>
</html>