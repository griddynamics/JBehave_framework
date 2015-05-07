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


<div class="next-block">
    <div class="panel success">Test area '${testArea.areaName}' successfully created.</div>
    <br/>
    <img src="${fileStructure.baselineImagePath}${testArea.fileName}" alt="Created Baseline image" >
</div>
</body>
</html>