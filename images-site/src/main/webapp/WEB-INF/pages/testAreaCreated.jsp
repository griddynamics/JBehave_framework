<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:eval expression="@propertyConfigurer.getProperty('baseline.image.path')"  var="baselineImagePath"/>

<html>
    <head>
        <title>Test area successfully created</title>
    </head>
    <body>
        <h2>Test area '${testArea.areaName}' successfully created!</h2>
        <br/>
        <img src="${baselineImagePath}${testArea.fileName}" alt="Created Baseline image" >
        <br/>
        <a href="/">Home Page</a><br/>
    </body>
</html>