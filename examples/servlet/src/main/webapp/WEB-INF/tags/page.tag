<%--
  ~ Copyright 2014 Stormpath, Inc.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@tag description="Default Page template" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@attribute name="title" required="false" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Stormpath Web Sample | <c:out value="${!empty title ? title : ''}"/></title>
    <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/bootstrap.min.js"></script>
</head>
<body>
    <div class="container">
        <div class="header">
            <form id="logoutForm" action="${pageContext.request.contextPath}/logout" method="post">
                <ul class="nav nav-pills pull-right">
                    <c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri']}"/>
                    <li<c:if test="${fn:endsWith(uri,'/')}"> class="active"</c:if>><a
                            href="${pageContext.request.contextPath}/">Home</a></li>
                    <c:choose>
                        <c:when test="${!empty account}">
                            <li<c:if test="${fn:endsWith(uri,'dashboard')}"> class="active"</c:if>><a
                                    href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                            <li><a id="logout" href="">Logout</a></li>
                        </c:when>
                        <c:otherwise>
                            <li<c:if test="${fn:endsWith(uri,'login')}"> class="active"</c:if>><a
                                    href="${pageContext.request.contextPath}/login">Login</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </form>
            <h3 class="text-muted">Stormpath Servlet Plugin</h3>
        </div>
        <jsp:doBody/>
    </div>
    <script type="application/javascript">
        $('#logout').click(function (event) {
            event.preventDefault();
            $('#logoutForm').submit();
        })
    </script>
</body>
</html>