<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<h1>Test Objects List</h1>
<table>
    <tr>
        <td>Name</td>
        <td>Browser</td>
        <td>Browser version</td>
        <td>Browser width</td>
        <td>Browser height</td>
        <td>Area width</td>
        <td>Area height</td>
        <td>Pos X</td>
        <td>Pos Y</td>
    </tr>
    <c:forEach items="${testObjectsList}" var="testObject">
        <tr>
            <td><a href="/testObject?testObjectId=${testObject.areaId}">${testObject.areaName}</a></td>
            <td>${testObject.browserName}</td>
            <td>${testObject.browserVersion}</td>
            <td>${testObject.browserWidth}</td>
            <td>${testObject.browserHeight}</td>
            <td>${testObject.width}</td>
            <td>${testObject.height}</td>
            <td>${testObject.posX}</td>
            <td>${testObject.posY}</td>
        </tr>




    </c:forEach>
</table>
<br/><br/><br/><a href="/">Home Page</a><br/>

</body>
</html>