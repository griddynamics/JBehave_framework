<%@ taglib prefix="form"   uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
  <title>Add test area</title>
</head>


<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="aqua header-line header-line-first"/>
    <span class="aqua header-line"/>
    <span class="purple header-line"/>
    <span class="red header-line"/>
    <span class="green header-line"/>
  </div>

  <div class="header-title aqua-text">Create new test area</div><div class="header-icon aqua window-light"/><a class="header-back" href="/"></a>
</header>

<body class="body">


<div class="panel">Please set properties for new test area:</div>
<form class="pure-form pure-form-aligned" method="post" modelAttribute="testArea" action="testAreaCreated">
    <input type="hidden" name="croppedFromFileName" value="${testArea.croppedFromFileName}"/>
    <fieldset>
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
            <input name="countFromTop" value="true"/>
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
            <label for="areaName">area name</label>
            <input name="areaName" value=""/>
        </div>
        <input type="hidden" name="page.pageID" value="${page.pageID}"/>
        <div class="pure-control-group">
            <label for="submit"></label>
            <input type="submit" class="pure-button pure-button-primary" value="Create area" />
        </div>
    <fieldset> 
</form>
</body>
</html>