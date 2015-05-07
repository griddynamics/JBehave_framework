<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>


<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <script type="text/Javascript">
     function confirmRedirect(areaID)
     {
        if(confirm('Delete test area?'))
        {
           window.location = 'deleteArea?id=' + areaID;
        }
     }
  </script>
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
        <td>Page</td>
        <td>Area width</td>
        <td>Area height</td>
        <td>Pos X</td>
        <td>Pos Y</td>
        <td>Edit</td>
        <td>Delete</td>
    </tr></thead>
    <c:forEach items="${testObjectsMap}" var="testObjectMapEntry">
        <tr>
            <td colspan="8" align="center" style="background-color:#E8E8E8"><b>${testObjectMapEntry.key}</b></td>
        </tr>
        <c:forEach items="${testObjectMapEntry.value}" var="testObject">
            <tr>
                <td><a href="/testObject?testObjectId=${testObject.areaId}">${testObject.areaName}</a></td>
                <td>${testObject.page.pageID}</td>
                <td>${testObject.width}</td>
                <td>${testObject.height}</td>
                <td>${testObject.posX}</td>
                <td>${testObject.posY}</td>
                <td><a href="editTestArea?id=${testObject.areaId}">Edit</a></td>
                <td><a href="#" onclick="confirmRedirect('${testObject.areaId}')">Delete</a></td>
            </tr>
        </c:forEach>
    </c:forEach>
</table>

</body>
</html>