<%--
  ~ Copyright 2015 Stormpath, Inc.
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
    <jsp:attribute name="title"><sp:message key="stormpath.web.login.title"/></jsp:attribute>
    <jsp:attribute name="bodyCssClass">login</jsp:attribute>
    <jsp:body>
        <div class="container custom-container">

            <div class="va-wrapper">

                <div class="view login-view container">

                    <div class="box row">

                        <div class="email-password-area col-xs-12 <c:out value="${!empty accountStores ? 'small col-sm-8' : 'large col-sm-12'}"/>">

                            <div class="header">
                                <span>
                                    <sp:message key="stormpath.web.login.form.title">
                                        <sp:param><a href="${pageContext.request.contextPath}${registerUri}"><sp:message
                                                key="stormpath.web.login.form.registerLink.text"/></a></sp:param>
                                    </sp:message>
                                </span>
                            </div>

                            <c:if test="${!empty status and empty errors}">
                                <div class="alert alert-dismissable alert-success">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <p>${status}</p>
                                </div>
                            </c:if>

                            <c:if test="${!empty errors}">
                                <div class="alert alert-dismissable alert-danger bad-login">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <c:forEach items="${errors}" var="error">
                                        <p>${error.message}</p>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <form method="post" role="form" class="login-form form-horizontal">

                                <c:forEach items="${form.fields}" var="field">
                                    <c:choose>
                                        <c:when test="${field.type == 'hidden'}">
                                            <input name="${field.name}" value="${field.value}" type="${field.type}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <div form-group="true" class="form-group group-${field.name}">
                                                <label class="<c:out value="${!empty accountStores ? 'col-sm-12' : 'col-sm-4'}"/>">
                                                    ${field.label}</label>
                                                <div class="<c:out value="${!empty accountStores ? 'col-sm-12' : 'col-sm-8'}"/>">
                                                    <input name="${field.name}" value="${field.value}"
                                                           type="${field.type}"
                                                           placeholder="${field.placeholder}"
                                                           <c:if test="${field.required}">required="required" </c:if>
                                                           class="form-control">
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <div>
                                    <button type="submit" class="login btn btn-login btn-sp-green"><sp:message
                                            key="stormpath.web.login.form.button.value"/></button>
                                </div>
                            </form>

                        </div>

                        <c:if test="${!empty accountStores}">
                            <div class="provider-area col-xs-12 col-sm-4">
                                <div class="header">&nbsp;</div>
                                <label>Easy 1-click login:</label>
                                <c:forEach items="${accountStores}" var="accountStore">
                                    <button class="btn btn-social btn-${accountStore.provider.providerId}"
                                            id="${accountStore.provider.providerId == 'saml' ? accountStore.href : accountStore.provider.clientId}">
                                        <c:if test="${accountStore.provider.providerId != 'saml'}">
                                            <span class="fa fa-${accountStore.provider.providerId}"></span>
                                        </c:if>
                                        <c:if test="${accountStore.provider.providerId == 'saml'}">
                                            <span class="fa fa-lock"></span>
                                        </c:if>
                                        <c:out value="${accountStore.provider.providerId == 'saml' ? accountStore.name : accountStore.provider.providerId}"/>
                                    </button>
                                </c:forEach>
                            </div>
                        </c:if>

                    </div>

                    <c:if test="${verifyEnabled}">
                        <a href="${pageContext.request.contextPath}${verifyUri}" class="verify"><sp:message
                                key="stormpath.web.login.form.sendVerificationEmail.text"/></a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}${forgotLoginUri}" class="to-login"><sp:message
                            key="stormpath.web.login.form.resetLink.text"/></a>

                </div>
                <c:set var="req" value="${pageContext.request}"/>
                <input type="hidden" id="baseUrl"
                       value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}"/>
            </div>
        </div>

        <script src='${pageContext.request.contextPath}/assets/js/stormpath.js'></script>
    </jsp:body>
</t:page>
