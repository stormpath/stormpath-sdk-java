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
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:page>
    <jsp:attribute name="title">Ajax Login</jsp:attribute>
    <jsp:body>
        <div class="jumbotron" id="welcome">

            <form id="ajaxLoginForm" method="post" action="${pageContext.request.contextPath}/oauth/token" role="form" class="login-form form-horizontal">

                <div form-group="true" class="form-group group-username">
                    <label class="<c:out value="col-sm-4"/>">Username</label>
                    <div class="col-sm-8">
                        <input name="username" type="text"
                               placeholder="username"
                               autofocus="autofocus"
                               required="required"
                               class="form-control">
                    </div>
                </div>

                <div form-group="true" class="form-group group-password">
                    <label class="<c:out value="col-sm-4"/>">Username</label>
                    <div class="col-sm-8">
                        <input name="password" type="password"
                               placeholder="password"
                               autofocus="autofocus"
                               required="required"
                               class="form-control">
                    </div>
                </div>

                <div>
                    <button type="submit" class="login btn btn-login btn-sp-green">Log In</button>
                </div>
            </form>

        <script type="text/javascript">
            var frm = $('#ajaxLoginForm');
            frm.append('<input type="hidden" name="grant_type" value="password"/>');
            frm.submit(function (ev) {
                $.ajax({
                    type: frm.attr('method'),
                    url: frm.attr('action'),
                    data: frm.serialize(),
                    success: function (data) {
                        window.location = "${pageContext.request.contextPath}/dashboard";
                    },
                    error: function(jqXHR, statusString, err) {
                        alert('login attempt failed.  Please try again.');
                    }
                });

                ev.preventDefault();
            });
        </script>

    </jsp:body>
</t:page>
