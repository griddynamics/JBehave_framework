<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>


<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <script type="text/Javascript">
     function confirmRedirect(pageID)
     {
        if(confirm('Delete test page?'))
        {
           window.location = 'deletePage?id=' + pageID;
        }
     }
  </script>
  <title>Test Pages</title>
</head>

<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="green header-line header-line-first"/>
    <span class="green header-line"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
    <span class="red header-line"/>
  </div>

  <div class="header-title green-text">Test Pages</div><div class="header-icon green cone-light"/><a class="header-back" href="/"></a>
</header>

<body class="body">


<div class="panel">Create new page</div>

<form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="uploadNewPage">
            <table>
                <tr>
                    <td><input type="file" name="file" /></td>
                    <td style="color: red; font-style: italic;"><form:errors path="file" /></td>
                    <td><input type="submit" class="pure-button" value="Create new page" style="width: 190px;"/></td>
                </tr>
            </table>
</form:form>


<br/><br/><br/>

<div class="panel">Existing pages</div>

<table class="pure-table">
    <thead><tr>
        <td>ID</td>
        <td>Name</td>
        <td>Browser</td>
        <td>Browser version</td>
        <td>Browser width</td>
        <td>Browser height</td>
        <td>Delete</td>
    </tr></thead>
    <c:forEach items="${testPagesMap}" var="testPageMapEntry">
        <tr>
            <td colspan="7" align="center" style="background-color:#E8E8E8"><b>${testPageMapEntry.key}</b></td>
        </tr>
        <c:forEach items="${testPageMapEntry.value}" var="testPage">
            <tr>
                <td><a href="/testPage?testPageId=${testPage.pageID}">${testPage.pageID}</a></td>
                <td>${testPage.pageName}</td>
                <td>${testPage.browserName}</td>
                <td>${testPage.browserVersion}</td>
                <td>${testPage.browserWidth}</td>
                <td>${testPage.browserHeight}</td>
                <td><a href="#" onclick="confirmRedirect('${testPage.pageID}')">Delete</a></td>
            </tr>
        </c:forEach>
    </c:forEach>
</table>

</body>
</html>