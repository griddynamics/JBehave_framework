<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <title>Add new page</title>
</head>


<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="aqua header-line header-line-first"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
    <span class="red header-line"/>
    <span class="green header-line"/>
  </div>

  <div class="header-title aqua-text">Add new page</div><div class="header-icon aqua window-light"/><a class="header-back" href="/"></a>
</header>

<body class="body">


<div class="panel success">File name: "<strong>${message}</strong>"<br/>
Uploaded successfully.</div>
<br/>


<div class="panel">Please set properties for new page:</div>
<form class="pure-form pure-form-aligned" method="post" modelAttribute="testPage" action="testPageCreated">
    <fieldset>
        <div class="pure-control-group">
            <label for="pageName">page name</label>
            <input name="pageName" value=""/>
        </div>
        <div class="pure-control-group">
            <label for="browserWidth">browser width</label>
            <input name="browserWidth" value="${testPage.browserWidth}"/>
        </div>
        <div class="pure-control-group">
            <label for="browserHeight">browser height</label>
            <input name="browserHeight" value="${testPage.browserHeight}"/>
        </div>
        <div class="pure-control-group">
            <label for="browserName">browser name</label>
            <input name="browserName" value=""/>
        </div>
        <div class="pure-control-group">
            <label for="browserVersion">browser version</label>
            <input name="browserVersion" value=""/>
        </div>
        <input type="hidden" name="fileName" value="${message}"/>
        <input type="hidden" name="imageHeight" value="${testPage.imageHeight}"/>
        <div class="pure-control-group">
            <label for="submit"></label>
            <input type="submit" class="pure-button pure-button-primary" value="Create page" />
        </div>
    <fieldset>
</form>

<br/><br/>
<img id="lkmvxcvk2" src="${fileStructure.testPagesPath}${message}" alt="Uploaded image" >
<br/>

</body>
</html>

