<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
  <link rel="stylesheet" href="resources/css/main.css">
  <link rel="stylesheet" href="resources/css/pure-min.css">
  <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
  <title>Create an area to test</title>
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



    <div class="panel">Please select a file with area to test in it.</div>
<form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="showFile">
        <table>
            <tr>
                <td><input type="file" name="file" /></td>
                <td style="color: red; font-style: italic;"><form:errors path="file" />
                </td>
                <td><input type="submit" class="pure-button pure-button-primary" value="Upload" />
            </tr>
        </table>
    </form:form>

</body>
</html>

