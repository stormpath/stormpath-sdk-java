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
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:page>
    <jsp:attribute name="title">Login</jsp:attribute>
    <jsp:attribute name="bodyCssClass">login</jsp:attribute>
    <jsp:body>
        <div class="container custom-container">

            <div class="va-wrapper">

                <div class="view login-view container">

                    <div class="box row">

                        <div class="email-password-area col-xs-12 <c:out value="${social ? 'small col-sm-8' : 'large col-sm-12'}"/>">

                            <div class="header">
                                <span>Log In or <a href="${pageContext.request.contextPath}${requestScope['stormpath.web.register.url']}">Create Account</a></span>
                            </div>

                            <c:if test="${!empty errors}">
                                <div class="alert alert-dismissable alert-danger bad-login">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <c:forEach items="${errors}" var="error">
                                        <p>${error}</p>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <form method="post" role="form" class="login-form form-horizontal">

                                <c:if test="${!empty csrfToken}">
                                    <input name="_csrf" type="hidden" value="${csrfToken}">
                                </c:if>

                                <div class="form-group group-email">
                                    <label class="<c:out value="${social ? 'col-sm-12' : 'col-sm-4'}"/>">Email</label>
                                    <div class="<c:out value="${social ? 'col-sm-12' : 'col-sm-8'}"/>">
                                        <input autofocus="true" placeholder="Email" required="required"
                                               name="login" type="text" value="" class="form-control">
                                    </div>
                                </div>
                                <div class="form-group group-password">
                                    <label class="<c:out value="${social ? 'col-sm-12' : 'col-sm-4'}"/>">Password</label>
                                    <div class="<c:out value="${social ? 'col-sm-12' : 'col-sm-8'}"/>">
                                        <input placeholder="Password" required="required" type="password"
                                               name="password" class="form-control">
                                    </div>
                                </div>
                                <div>
                                    <button type="submit" class="login btn btn-login btn-sp-green">Log In</button>
                                </div>
                            </form>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    </jsp:body>
</t:page>
