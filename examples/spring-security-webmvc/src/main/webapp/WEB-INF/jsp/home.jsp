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
        <div class="container">
            <h1>${message}</h1>
            <c:choose>
                <c:when test="${account != null}">
                    <h4>Account Store: ${account.directory.name}</h4>
                    <h4>Provider: ${account.providerData.providerId}</h4>
                    <form action="/logout" method="post">
                        <input type="submit" class="btn btn-danger" value="Logout"/>
                    </form>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-primary" href="/login">Login</a>
                </c:otherwise>
            </c:choose>
        </div>
    </jsp:body>
</t:page>
