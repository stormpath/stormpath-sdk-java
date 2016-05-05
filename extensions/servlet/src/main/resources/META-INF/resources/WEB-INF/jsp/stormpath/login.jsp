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

                            <c:if test="${!empty status}">
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
                                                <label class="<c:out value="${!empty accountStores ? 'col-sm-12' : 'col-sm-4'}"/>"><sp:message
                                                        key="${field.label}"/></label>
                                                <div class="<c:out value="${!empty accountStores ? 'col-sm-12' : 'col-sm-8'}"/>">
                                                    <input name="${field.name}" value="${field.value}"
                                                           type="${field.type}"
                                                           placeholder="<sp:message key="${field.placeholder}"/>"
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
                            <div class="provider-area  col-xs-12 col-sm-4">
                                <div class="header">&nbsp;</div>
                                <label>Easy 1-click login:</label>
                                <c:forEach items="${accountStores}" var="accountStore">
                                    <button class="btn btn-social btn-${accountStore.provider.providerId}"
                                            id="${accountStore.provider.clientId}"><c:out
                                            value="${accountStore.provider.providerId}"/></button>
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

            </div>
        </div>

        <c:set var="req" value="${pageContext.request}"/>
        <c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}"/>

        <script type="text/javascript">
            /* <![CDATA[ */

            $('.btn-google').click(function (event) {
                event.preventDefault();
                googleLogin($('.btn-google').attr('id'));
            });

            $('.btn-facebook').click(function (event) {
                event.preventDefault();
                facebookLogin($('.btn-facebook').attr('id'));
            });

            $('.btn-github').click(function (event) {
                event.preventDefault();
                githubLogin($('.btn-github').attr('id'));
            });

            $('.btn-linkedin').click(function (event) {
                event.preventDefault();
                linkedinLogin($('.btn-linkedin').attr('id'));
            });

            function linkedinLogin(clientId) {
                window.location.replace('https://www.linkedin.com/uas/oauth2/authorization' +
                        '?client_id=' + clientId +
                        '&response_type=code' +
                        '&scope=' + encodeURIComponent('r_emailaddress r_basicprofile') +
                        '&redirect_uri=' + encodeURIComponent('${baseURL}/callbacks/linkedin') +
                        '&state=${oauthStateToken}');
            }

            function googleLogin(clientId) {
                window.location.replace('https://accounts.google.com/o/oauth2/auth?response_type=code' +
                        '&client_id=' + clientId + '&scope=email' +
                        '&redirect_uri=' + encodeURIComponent('${baseURL}/callbacks/google'));
            }

            function githubLogin(clientId) {
                window.location.replace('https://github.com/login/oauth/authorize?client_id=' + clientId);
            }

            function samlLogin() {
                window.location.replace('http://localhost:8080/saml');
            }

            function facebookLogin(appId) {
                var FB = window.FB;
                FB.init({
                    appId: appId,
                    cookie: true,
                    xfbml: true,
                    version: 'v2.4'
                });
                FB.login(function (response) {
                    if (response.status === 'connected') {
                        var queryStr = window.location.search.replace('?', '');
                        if (queryStr) {
                            window.location.replace('/callbacks/facebook?queryStr&accessToken=' + FB.getAuthResponse()['accessToken']);
                        } else {
                            window.location.replace('/callbacks/facebook?accessToken=' + FB.getAuthResponse()['accessToken']);
                        }
                    }
                }, {scope: 'email'});
            }

            (function (d, s, id) {
                var js, fjs = d.getElementsByTagName(s)[0];
                if (d.getElementById(id)) {
                    return;
                }
                js = d.createElement(s);
                js.id = id;
                js.src = '//connect.facebook.net/en_US/sdk.js';
                fjs.parentNode.insertBefore(js, fjs);
            }(document, 'script', 'facebook-jssdk'));
            /* ]]> */
        </script>
    </jsp:body>
</t:page>
