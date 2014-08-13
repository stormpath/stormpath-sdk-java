<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome!</title>
</head>
<body>

    <h3>Your Referenced Stormpath Application:</h3>
    <table border="1">
        <thead>
            <tr>
                <th align="left">Property Name</th>
                <th align="left">Property Value</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>HREF</td>
                <td>${app.href}</td>
            </tr>
            <tr>
                <td>Name</td>
                <td>${app.name}</td>
            </tr>
            <tr>
                <td>Description</td>
                <td>${app.description}</td>
            </tr>
            <tr>
                <td>Status</td>
                <td>${app.status}</td>
            </tr>
        </tbody>
    </table>

</body>
</html>