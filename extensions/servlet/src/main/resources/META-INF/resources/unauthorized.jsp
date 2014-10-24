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

<t:page>
    <jsp:attribute name="title">Unauthorized</jsp:attribute>
    <jsp:attribute name="description">You are not authorized to view the requested page.</jsp:attribute>
    <jsp:attribute name="bodyCssClass">login</jsp:attribute>
    <jsp:body>
        <div class="container custom-container">

            <div class="va-wrapper">

                <div class="view login-view container">

                    <div class="box row">

                        <div class="email-password-area col-xs-12 large col-sm-12">

                            <div class="header">
                                <span>Unauthorized</span>
                                <p>You are not authorized to view the requested page.</p>
                                <p>If this is unexpected, please contact your site administrator for help, otherwise
                                    click your web browser's back button to return to the previous page.</p>
                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    </jsp:body>
</t:page>
