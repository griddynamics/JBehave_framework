<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title>Add test area</title>
</head>
<body>
<h2>Create test area with properties:</h2>
<br/><br/>
<form method="post" modelAttribute="testArea" action="testAreaCreated">
    <input type="hidden" name="croppedFromFileName" value="${testArea.croppedFromFileName}"/>
    <table>
        <tr>
            <td>posX</td><td><input name="posX" value="${testArea.posX}"/></td>
        </tr>
        <tr>
            <td>posY</td><td><input name="posY" value="${testArea.posY}"</td>
        </tr>
        <tr>
            <td>width</td><td><input name="width" value="${testArea.width}"</td>
        </tr>
        <tr>
            <td>height</td><td><input name="height" value="${testArea.height}"</td>
        </tr>
        <tr>
            <td>browser width</td><td><input name="browserWidth" value="${testArea.browserWidth}"</td>
        </tr>
        <tr>
            <td>browser height</td><td><input name="browserHeight" value="${testArea.browserHeight}"</td>
        </tr>
        <tr>
            <td>browser name</td><td><input name="browserName" value=""/></td>
        </tr>
        <tr>
            <td>browser version</td><td><input name="browserVersion" value=""/></td>
        </tr>
        <tr>
            <td>area name</td><td><input name="areaName" value=""/></td>
        </tr>
        <tr>
            <td><input type="submit" value="Create area" /></td>
        </tr>
    </table>
</form>
</body>
</html>