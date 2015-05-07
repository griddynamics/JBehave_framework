<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <link rel="stylesheet" href="/resources/css/main.css">
  <link rel="stylesheet" href="/resources/css/pure-min.css">
  <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
  <title>Test Result</title>
</head>


<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="red header-line header-line-first"/>
    <span class="red header-line"/>
    <span class="green header-line"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
  </div>

  <div class="header-title red-text">Test Result</div><div class="header-icon red moon-light"/><a class="header-back" href="/"></a>
</header>

<body class="body">

<h3><c:choose>
    <c:when test="${testResult.testResult eq 'false'}">
        <font color="red">TEST FAILED!</font>
    </c:when>
    <c:when test="${testResult.testResult eq 'true'}">
        <font color="green">TEST PASSED</font>
    </c:when>
</c:choose></h3>


<c:choose>
    <c:when test="${testResult.testResult eq 'false' && testResult.isVerified eq 'false'}">
        <form:form method="POST" action="/testResult?getId=${testResult.testId}">
            <input type="submit" name="acceptNewBaseline" value="Accept new baseline screenshot"">
            <input type="submit" name="acceptFailure" value="Accept failure">
        </form:form>
    </c:when>
</c:choose>
<br/>
<br/>
<table>
    <tr>
        <td>Area tested:</td>
        <td>${testResult.testAreaId}</td>
    </tr>
    <tr>
        <td>Test result: </td>
        <td>${testResult.testResult}</td>
    </tr>
    <tr>
        <td>Number of pixels which differ from baseline: </td>
        <td>${testResult.numberOfDiffPixels}</td>
    </tr>
</table>
<br/>
<br/>
Baseline image:
<br/>
<img src="${fileStructure.baselineImagePath}${testResult.testArea.fileName}" alt="Baseline image" >
<br/>
<c:choose>
    <c:when test="${testResult.testResult eq 'false'}">
        <br/>
        <br/>
        Actual image:
        <br/>
        <img src="${fileStructure.resultImagePath}${testResultImagesFolder}/${testResult.testFolderName}/${fileStructure.croppedScreenshotFileName}" alt="Actual image" >
        <br/>
        <br/>
        <br/>
        Comparison result:
        <br/>
        <img src="${fileStructure.resultImagePath}${testResultImagesFolder}/${testResult.testFolderName}/${fileStructure.comparisonResultFileName}" alt="Comparison result" >
        <br/>
    </c:when>
</c:choose>
</body>
</html>