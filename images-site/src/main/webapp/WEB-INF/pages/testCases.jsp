<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
	<h1>Test Cases</h1>
    <table border="1">

        <tr>
            <td>Test ID</td>
            <td>Time</td>
            <td>Test result</td>
            <td>Area tested</td>
            <td>Number of pixels which differ from baseline</td>
        </tr>

	    <c:forEach items="${testResultList}" var="testResult">
		    <tr>
		        <td>
		            <a href="/testResult?getId=${testResult.testId}">${testResult.testId}</a>
                </td>
                <td>
                    ${testResult.testRunTs}
                </td>
                <td>
                    ${testResult.testResult}
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

	<br/><br/><br/><a href="/">Home Page</a><br/>
 
 </body>
</html>