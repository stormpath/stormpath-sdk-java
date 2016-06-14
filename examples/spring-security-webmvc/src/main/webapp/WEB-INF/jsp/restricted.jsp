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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page import="com.stormpath.spring.examples.HelloService" %>

<c:set var="MY_GROUP" value="<%=HelloService.MY_GROUP%>" scope="application"/>

<t:page>
    <jsp:attribute name="title">Restricted</jsp:attribute>
    <jsp:body>
        <div class="container">
            <sec:authorize access="hasAuthority('${MY_GROUP}')">
                <h1>${message}</h1>
            </sec:authorize>
            <sec:authorize access="!hasAuthority('${MY_GROUP}')">
                <h1>You do not have the proper permissions to view this page.</h1>
            </sec:authorize>
            <form action="/logout" method="post">
                <input type="submit" class="btn btn-danger" value="Logout"/>
            </form>
        </div>
    </jsp:body>
</t:page>
