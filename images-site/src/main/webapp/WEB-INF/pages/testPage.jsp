<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
  <script type="text/Javascript">
       function confirmRedirect(areaID)
       {
          if(confirm('Delete test area?'))
          {
             window.location = 'deleteArea?id=' + areaID;
          }
       }
    </script>

  <title>Test Page</title>
</head>

<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="green header-line header-line-first"/>
    <span class="green header-line"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
    <span class="red header-line"/>
  </div>

  <div class="header-title green-text">Test Page</div><div class="header-icon green cone-light"/><a class="header-back" href="/"></a>
</header>


<body class="body">

<div class="panel">Page info</div>

<table class="pure-table">
            <tr>
                <td>ID:</td>
                <td>${testPage.pageID}</td>
            </tr>
            <tr>
                <td>Page name: </td>
                <td>${testPage.pageName}</td>
            </tr>
            <tr>
                <td>Browser: </td>
                <td>${testPage.browserName}</td>
            </tr>
            <tr>
                <td>Browser version: </td>
                <td>${testPage.browserVersion}</td>
            </tr>
            <tr>
                <td>Browser width: </td>
                <td>${testPage.browserWidth}</td>
            </tr>
            <tr>
                <td>Browser height: </td>
                <td>${testPage.browserHeight}</td>
            </tr>
        </table>
        <br/>
        <br/>
        <img src="${fileStructure.testPagesPath}${testPage.fileName}" alt="Page image" >


<br/><br/><br/>
<div class="panel">Add new area for a page</div>
<div class="pure-button pure-button-primary" value="Select area from the page"><a href="/addTestAreaForPage?id=${testPage.pageID}" style="color:white">Add new test area</a></div>


<br/><br/><br/>
<div class="panel">Areas on the page</div>
<table class="pure-table">
    <thead><tr>
        <td>Image</td>
        <td>Name</td>
        <td>Area width</td>
        <td>Area height</td>
        <td>Pos X</td>
        <td>Pos Y</td>
        <td>Edit</td>
        <td>Delete</td>
    </tr></thead>
    <c:forEach items="${testObjects}" var="testObject">

            <tr>
                <td><img style="border:1px solid grey" src="${fileStructure.baselineImagePath}${testObject.fileName}" alt="Baseline image" ></td>
                <td><a href="/testObject?testObjectId=${testObject.areaId}">${testObject.areaName}</a></td>
                <td>${testObject.width}</td>
                <td>${testObject.height}</td>
                <td>${testObject.posX}</td>
                <td>${testObject.posY}</td>
                <td><a href="editTestArea?id=${testObject.areaId}">Edit</a></td>
                <td><a href="#" onclick="confirmRedirect('${testObject.areaId}')">Delete</a></td>
            </tr>

    </c:forEach>
</table>


</body>
</html>