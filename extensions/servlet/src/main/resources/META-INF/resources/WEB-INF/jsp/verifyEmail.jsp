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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="lang" value="${not empty param.lang ? param.lang : not empty lang ? lang : pageContext.request.locale}"/>
<fmt:setLocale value="${lang}" />
<fmt:setBundle basename="com.stormpath.sdk.servlet.i18n"/>

<t:page>
    <jsp:attribute name="title"><fmt:message key="stormpath.web.verify.title"/></jsp:attribute>
    <jsp:attribute name="bodyCssClass">login</jsp:attribute>
    <jsp:body>
        <div class="container custom-container">

            <div class="va-wrapper">

                <div class="view login-view container">

                    <div class="box row">

                        <div class="email-password-area col-xs-12 large col-sm-12">

                            <div class="header">
                                <span><fmt:message key="stormpath.web.verify.body.title"/></span>
                                <p><fmt:message key="stormpath.web.verify.body.instructions"/>
                                <p><fmt:message key="stormpath.web.verify.body.instructions2"/></p>
                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    </jsp:body>
</t:page>



