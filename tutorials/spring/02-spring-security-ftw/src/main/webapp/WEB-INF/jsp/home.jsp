<%--
  ~ Copyright 2016 Stormpath, Inc.
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
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>
<%@ taglib prefix="sp" uri="http://stormpath.com/jsp/tags" %>

<t:page>
    <jsp:attribute name="title">Restricted</jsp:attribute>
    <jsp:body>
        <style>
            body {
                margin-top: 60px;
            }
            .box {
                padding: 50px;
                text-align: center;
                vertical-align: middle;
                border: 0px;
                box-shadow: none;
            }
            .stormpath-header {
                background-color: #161616;
            }
        </style>

        <div class="container-fluid">
            <div class="row">
                <div class="box col-md-6 col-md-offset-3">
                    <div class="stormpath-header">
                        <img src="https://stormpath.com/images/template/logo-nav.png"/>
                    </div>

                    <c:if test="${!empty status and empty errors}">
                        <div class="alert alert-dismissable alert-success">
                            <button type="button" class="close" data-dismiss="alert">&times;</button>
                            <p>${status}</p>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${account != null}">
                            <h1>Hello, ${account.givenName}</h1>
                            <form action="${pageContext.request.contextPath}/logout" method="post">
                                <a href="/restricted" class="btn btn-primary">Restricted</a>
                                <input type="submit" class="btn btn-danger" value="Logout"/>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <h1>Who are you?</h1>
                            <a href="/restricted" class="btn btn-primary">Restricted</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page>
