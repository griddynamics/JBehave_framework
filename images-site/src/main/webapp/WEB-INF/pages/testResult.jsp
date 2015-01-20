<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:eval expression="@propertyConfigurer.getProperty('baseline.image.path')"  var="baselineImagePath"/>
<spring:eval expression="@propertyConfigurer.getProperty('result.image.path')"  var="resultImagePath"/>
<spring:eval expression="@propertyConfigurer.getProperty('cropped.image.name')"  var="croppedImageName"/>
<spring:eval expression="@propertyConfigurer.getProperty('comparison.image.name')"  var="comparisonImageName"/>



<html>
<head>
    <title>Test Result</title>
</head>
<body>

<c:choose>
    <c:when test="${testResult.testResult eq 'false'}">
        <font color="red">TEST FAILED!</font>
    </c:when>
    <c:when test="${testResult.testResult eq 'true'}">
        <font color="green">TEST PASSED</font>
    </c:when>
</c:choose>

<br/>
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
<img src="${baselineImagePath}${testResult.testArea.fileName}" alt="Baseline image" >
<br/>

<c:choose>
    <c:when test="${testResult.testResult eq 'false'}">


        <br/>
        <br/>
        Actual image:
        <br/>
        <img src="${resultImagePath}${testResultImagesFolder}/${testResult.testFolderName}/${croppedImageName}" alt="Actual image" >
        <br/>

        <br/>
        <br/>
        Comparison result:
        <br/>
        <img src="${resultImagePath}${testResultImagesFolder}/${testResult.testFolderName}/${comparisonImageName}" alt="Comparison result" >
        <br/>
    </c:when>
</c:choose>
<br/><br/><br/><a href="/">Home Page</a><br/>

</body>

</html>