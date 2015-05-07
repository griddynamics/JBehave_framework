<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <title>Edit test area</title>
</head>


<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="aqua header-line header-line-first"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
    <span class="red header-line"/>
    <span class="green header-line"/>
  </div>

  <div class="header-title aqua-text">Edit existing test area</div><div class="header-icon aqua window-light"/><a class="header-back" href="/"></a>
</header>

<body class="body">


<div class="panel">Edit properties for <b>${testArea.areaId} test area</b></div>
<form class="pure-form pure-form-aligned" method="post" modelAttribute="testArea" action="testAreaUpdated">
    <fieldset>
            <input type="hidden" name="areaId" value="${testArea.areaId}"/>
        <div class="pure-control-group">
            <label for="posX">posX</label>
            <input name="posX" value="${testArea.posX}"/>
        </div>
        <div class="pure-control-group">
            <label for="posY">posY</label>
            <input name="posY" value="${testArea.posY}"/>
        </div>
        <div class="pure-control-group">
            <label for="countFromTop">count from top</label>
            <input name="countFromTop" value="${testArea.countFromTop}"/>
        </div>
        <div class="pure-control-group">
            <label for="width">width</label>
            <input name="width" value="${testArea.width}"/>
        </div>
        <div class="pure-control-group">
            <label for="height">height</label>
            <input name="height" value="${testArea.height}"/>
        </div>
        <div class="pure-control-group">
            <label for="page.pageID">page id</label>
            <input name="page.pageID" value="${testArea.page.pageID}"/>
        </div>
        <div class="pure-control-group">
            <label for="areaName">area name</label>
            <input name="areaName" value="${testArea.areaName}"/>
        </div>
        <div class="pure-control-group">
            <label for="submit"></label>
            <input type="submit" class="pure-button pure-button-primary" value="Update" />
        </div>
    <fieldset> 
</form>
</body>
</html>