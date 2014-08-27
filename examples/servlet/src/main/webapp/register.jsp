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
    <jsp:attribute name="title">Register</jsp:attribute>
    <jsp:body>
        <div class="register">
            <div class="row">
                <div class="col-lg-6 col-lg-offset-3">
                    <form class="bs-example form-horizontal" method="post" action="">
                        <fieldset>
                            <legend>Create a New Account</legend>
                            <c:if test="${not empty error}">
                                <div class="alert alert-dismissable alert-danger register-fail">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <c:out value="${error}"/>
                                </div>
                            </c:if>
                            <p>
                                Registering for this site will create a new user account for you via
                                <a href="https://stormpath.com">Stormpath</a>, then log you in
                                automatically.
                            </p>
                            <div class="alert alert-dismissable alert-info">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                <strong>NOTE</strong> Your password must be between 8 and 100
                                characters, and must contain lowercase letters, uppercase letters,
                                and numbers.
                            </div>
                            <div class="form-group">
                                <label for="email" class="col-lg-4 control-label">Email</label>
                                <div class="col-lg-4">
                                    <input type="email" class="form-control" id="email" name="email" placeholder="Email" autofocus>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="password" class="col-lg-4 control-label">Password</label>
                                <div class="col-lg-4">
                                    <input type="password" class="form-control" id="password" name="password" placeholder="Password">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-lg-10 col-lg-offset-4">
                                    <button type="submit" class="btn btn-primary">Register</button>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                    <h2>A Note About Stormpath Security</h2>
                    <p>
                        In a real environment, you should <b>only deploy your application</b>
                        behind SSL / TLS to avoid having user account credentials sent in plain text
                        over the network.
                    </p>
                    <p>
                        Stormpath user account creation is incredibly secure, is not susceptible to any
                        known vulnerabilities (MITM, hashing issues, replay attacks, etc.), and
                        provides you with a number of options to improve security (including
                        built-in email verification, among other things).  We will not verify
                        your account by email in this sample app, but this can be easily enabled in the Stormpath
                        Admin Console.
                    </p>
                    <p class="last-p">
                        Stormpath can also be configured to allow for weaker (or force stronger)
                        passwords, so it can fit into any application workflow.
                    </p>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page>
