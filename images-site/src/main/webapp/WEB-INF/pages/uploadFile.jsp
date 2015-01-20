<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
    <title>Create area to test</title>
</head>
<body>

<center>
    <h2>Create area to test</h2>
    <h3>Please select a file with area to test in it.</h3>


    <form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="showFile">
        <table>
            <tr>
                <td>Upload File:</td>
                <td><input type="file" name="file" /></td>
                <td style="color: red; font-style: italic;"><form:errors path="file" />
                </td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="Upload" />
                <td></td>
                </td>
            </tr>
        </table>
    </form:form>
</center>
</body>
</html>

