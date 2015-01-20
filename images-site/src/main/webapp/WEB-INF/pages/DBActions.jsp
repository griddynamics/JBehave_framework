<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <script type="text/javascript" src="jquery-1.2.6.min.js"></script>
    <title>DB Actions</title>
</head>
<body>

<form:form method="post" enctype="multipart/form-data" action="runInitDB">
    <table>
        <tr>
            <td>Initialize DB:</td>
            <td><input type="submit" value="Init" /></td>
        </tr>
    </table>
</form:form>
<table>
    <tr>
        <td>Export Test Areas to CSV:</td>
        <td>
            <form action="/dbactions/exportTestAreas">
                <input type="submit" value="Export Test Areas">
            </form>
        </td>
    </tr>

    <tr>
        <td>Export Test Results to CSV:</td>
        <td>
            <form action="/dbactions/exportTestResults">
                <input type="submit" value="Export Test Results">
            </form>
        </td>
    </tr>
    <table>


        <form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="dbactions/importTestAreas">
            <table>
                <tr>
                    <td>Import Test Areas from CSV to DB:</td>
                    <td><input type="file" name="file" /></td>
                    <td style="color: red; font-style: italic;"><form:errors path="file" /></td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" value="Upload file to import" /></td>
                    <td></td>
                </tr>
            </table>
        </form:form>

        <form:form method="post" enctype="multipart/form-data" modelAttribute="uploadedFile" action="dbactions/importTestResults">
            <table>
                <tr>
                    <td>Import Test Results from CSV to DB:</td>
                    <td><input type="file" name="file" /></td>
                    <td style="color: red; font-style: italic;"><form:errors path="file" /></td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" value="Upload file to import" /></td>
                    <td></td>
                </tr>
            </table>
        </form:form>

    </table>

</body>
</html>

