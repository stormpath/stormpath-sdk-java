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
    <jsp:attribute name="title">Login</jsp:attribute>
    <jsp:body>
        <div class="login">
            <div class="row">
                <div class="col-lg-6 col-lg-offset-3">
                    <form class="bs-example form-horizontal" method="post" action="">
                        <fieldset>
                            <legend>Login</legend>
                            <c:if test="${!empty error}">
                                <div class="alert alert-dismissable alert-danger login-fail">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <c:out value="${error}"/>
                                </div>
                            </c:if>
                            <p class="last-p">
                                Once you enter your credentials and hit the Login button below,
                                your credentials will be securely sent to
                                <a href="https://stormpath.com">Stormpath</a> and verified. If
                                your credentials are valid, you'll be logged in using a secure
                                session -- otherwise, you'll get a user friendly error message.
                            </p>

                            <div class="form-group">
                                <label for="email" class="col-lg-4 control-label">Email</label>
                                <div class="col-lg-4">
                                    <input type="email" class="form-control" id="email" name="email"
                                           placeholder="Email" autofocus>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="password" class="col-lg-4 control-label">Password</label>
                                <div class="col-lg-4">
                                    <input type="password" class="form-control" id="password" name="password"
                                           placeholder="Password">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-lg-10 col-lg-offset-4">
                                    <button type="submit" class="btn btn-primary">Login</button>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page>
