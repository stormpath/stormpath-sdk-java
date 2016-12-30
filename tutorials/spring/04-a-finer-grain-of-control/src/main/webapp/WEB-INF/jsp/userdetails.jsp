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
    <jsp:attribute name="title">Groups</jsp:attribute>
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
            td {
                text-align: left;
            }
        </style>

        <div class="container-fluid">
            <div class="row">
                <div class="box col-md-6 col-md-offset-3">
                    <div class="stormpath-header">
                        <img src="https://stormpath.com/images/template/logo-nav.png"/>
                    </div>

                    <h1>Hello, ${account.fullName}!</h1>

                    <h2>You belong to these groups:</h2>
                    <table class="table table-bordered">
                        <tr>
                            <th>Name</th>
                            <th>Description</th>
                        </tr>
                        <c:forEach items="${groups}" var="group">
                        <tr>
                            <td>${group.name}</td>
                            <td>${group.description}</td>
                        </tr>
                        </c:forEach>
                    </table>

                    <h2>You have these permissions:</h2>
                    <table class="table table-bordered">
                        <tr>
                            <th>Source</th>
                            <th>Spring Security Permissions</th>
                        </tr>
                        <c:forEach items="${springSecurityPermissions}" var="permission">
                            <tr>
                                <td>${permission.key}</td>
                                <td><c:forEach items="${permission.value}" var="value">${value}<br/></c:forEach></td>
                            </tr>
                        </c:forEach>
                    </table>

                    <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go Home</a>

                </div>
            </div>
        </div>
    </jsp:body>
</t:page>