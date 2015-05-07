<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/resources/libs/jquery/jquery-1.11.1.min.js" />"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/libs/imgareaselect/css/imgareaselect-default.css" />" />
<script type="text/javascript" src="<c:url value="/resources/libs/imgareaselect/js/jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/libs/imgareaselect/js/jquery.imgareaselect.pack.js" />"></script>

<script type="text/javascript">
    $(document).ready(function () {
    $('#lkmvxcvk').imgAreaSelect({handles: true, onSelectEnd: function (img, selection) {
    $('input[name="x1"]').val(selection.x1);
    $('input[name="y1"]').val(selection.y1);
    $('input[name="x2"]').val(selection.x2);
    $('input[name="y2"]').val(selection.y2);
    }});
    });
</script>

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


<br/>
<img id="lkmvxcvk" src="${fileStructure.testPagesPath}${testPage.fileName}" alt="Page image" >
<br/>
<form method="post" modelAttribute="imageSelection" action="createTestArea">
    <input type="hidden" name="pageId" value="${testPage.pageID}" />
    <input type="hidden" name="x1" value="" />
    <input type="hidden" name="y1" value="" />
    <input type="hidden" name="x2" value="" />
    <input type="hidden" name="y2" value="" />
    <input type="submit" class="pure-button pure-button-primary" name="submit" value="Submit" />
</form>



</body>
</html>

