<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:eval expression="@propertyConfigurer.getProperty('baseline.image.path')"  var="baselineImagePath"/>
<html>
    <head>
        <title>Test Object</title>
    </head>
    <body>
        <table>
            <tr>
                <td>ID:</td>
                <td>${testObject.areaId}</td>
            </tr>
            <tr>
                <td>Name: </td>
                <td>${testObject.areaName}</td>
            </tr>
            <tr>
                <td>Browser: </td>
                <td>${testObject.browserName}</td>
            </tr>
            <tr>
                <td>Browser version: </td>
                <td>${testObject.browserVersion}</td>
            </tr>
            <tr>
                <td>Browser width: </td>
                <td>${testObject.browserWidth}</td>
            </tr>
            <tr>
                <td>Browser height: </td>
                <td>${testObject.browserHeight}</td>
            </tr>
            <tr>
                <td>Area width: </td>
                <td>${testObject.width}</td>
            </tr>
            <tr>
                <td>Area height: </td>
                <td>${testObject.height}</td>
            </tr>
            <tr>
                <td>Pos Y: </td>
                <td>${testObject.posX}</td>
            </tr>
            <tr>
                <td>Pos Y: </td>
                <td>${testObject.posY}</td>
            </tr>
        </table>
        <br/>
        <br/>
        Baseline image:
        <br/>
        <img src="${baselineImagePath}${testObject.fileName}" alt="Baseline image" >
        <br/>
        <br/><br/><br/><a href="/">Home Page</a><br/>
    </body>
</html>