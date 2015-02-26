<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
  <link rel="stylesheet" href="/resources/css/main.css">
  <link rel="stylesheet" href="/resources/css/pure-min.css">
  <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
  <title>DB Actions</title>
</head>

<header>
  <div style="position: absolute; top: 0px; left:-8px;">
    <span class="purple header-line header-line-first"/>
    <span class="purple header-line"/>
    <span class="red header-line"/>
    <span class="green header-line"/>
    <span class="aqua header-line"/>
  </div>

  <div class="header-title purple-text">Database actions</div><div class="header-icon purple db-light"/><a class="header-back" href="/"></a>
</header>



<body class="body">


<form:form method="post" enctype="multipart/form-data" action="../runInitDB">
    <input type="submit" class="pure-button pure-button-primary" style="width: 360px; font-size: 20px;" value="Initialize DB"/>
</form:form>

<div class="next-block">
<h2 style="display: inline-block;">- Export to CSV -</h2><br/>
<form action="/dbactions/exportTestAreas" style="display: inline-block; margin-top:5px; margin-right: 10px;">
  <input type="submit" class="pure-button" value="export Test Areas">
</form>
<form action="/dbactions/exportTestResults" style="display: inline-block; margin-top:5px;">
  <input type="submit" class="pure-button" value="export Test Results">
</form>
</div>




<div class="next-block">
<h2 style="display: inline-block;">- Import from CSV -</h2><br/>
<form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="/dbactions/importTestAreas">
            <table>
                <tr>
                    <td><input type="file" name="file" /></td>
                    <td style="color: red; font-style: italic;"><form:errors path="file" /></td>
                    <td><input type="submit" class="pure-button" value="import Test Areas" /></td>
                </tr>
            </table>
        </form:form>

        <form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="/dbactions/importTestResults">
            <table>
                <tr>
                    <td><div class="pure-button pure-button-primary success" style="width:445px;">Test Results import : success</div></td>
                </tr>
            </table>
        </form:form>
</div>














</body>
</html>