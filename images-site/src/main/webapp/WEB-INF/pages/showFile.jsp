<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/resources/libs/jquery/jquery-1.11.1.min.js" />"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/libs/imgareaselect/css/imgareaselect-default.css" />" />
<script type="text/javascript" src="<c:url value="/resources/libs/imgareaselect/js/jquery.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/libs/imgareaselect/js/jquery.imgareaselect.pack.js" />"></script>

<spring:eval expression="@propertyConfigurer.getProperty('test.areas.image.path')"  var="testAreasImagesPath"/>


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
    <title>Uploaded file</title>

</head>
<body>

<h2>Uploaded file</h2>

File name: "<strong>${message}</strong>"<br/>
Uploaded successfully !<br/><br/>

<img id="lkmvxcvk" src="${testAreasImagesPath}${message}" alt="Uploaded image" >
<br/><br/>

<form method="post" modelAttribute="imageSelection" action="createTestArea">
    <input type="hidden" name="fileName" value="${message}" />
    <input type="hidden" name="x1" value="" />
    <input type="hidden" name="y1" value="" />
    <input type="hidden" name="x2" value="" />
    <input type="hidden" name="y2" value="" />
    <input type="submit" name="submit" value="Submit" />
</form>

</body>
</html>

