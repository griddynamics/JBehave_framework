<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
  <title>Test Objects</title>
</head>

<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="green header-line header-line-first"/>
    <span class="green header-line"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
    <span class="red header-line"/>
  </div>

  <div class="header-title green-text">Test Objects</div><div class="header-icon green cone-light"/><a class="header-back" href="/"></a>
</header>


<body class="body">
<table class="pure-table">
    <thead><tr>
        <td>Name</td>
        <td>Browser</td>
        <td>Browser version</td>
        <td>Browser width</td>
        <td>Browser height</td>
        <td>Area width</td>
        <td>Area height</td>
        <td>Pos X</td>
        <td>Pos Y</td>
    </tr></thead>
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
</body>
</html>