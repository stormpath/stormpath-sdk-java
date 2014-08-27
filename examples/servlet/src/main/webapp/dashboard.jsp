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
    <jsp:attribute name="title">Dashboard</jsp:attribute>
    <jsp:body>
        <div class="dashboard">
            <div class="row">
                <div class="col-lg-12">
                    <div class="jumbotron">
                        <h1>Dashboard</h1>

                        <br/>
                        <br/>

                        <p>Welcome to your user dashboard!</p>

                        <p>This page displays some of your account information and also allows you to change custom
                            data.</p>

                        <p>If you click the Logout link in the navbar at the top of this page, you'll be logged out
                            of your account and redirected back to the main page of this site.</p>
                        <br/>
                        <br/>

                        <h2>Your Account Custom Data</h2>
                        <br/>
                        <br/>

                        <p>Your Email: <span class="data">${account.email}</span></p>

                        <c:set var="noBirthday" value="You haven't entered a birthday yet!"/>
                        <p>Your Birthday: <span class="data">${!empty account.customData['birthday'] ? account.customData['birthday'] : noBirthday}</span></p>

                        <c:set var="noColor" value="You haven't entered a color yet!"/>
                        <p>Your Favorite Color: <span class="data">${!empty account.customData['color'] ? account.customData['color'] : noColor}</span></p>

                        <br/>
                        <br/>

                        <p>Stormpath allows you to store up to 10MB of custom user data on
                            each user account. Data can be anything (in JSON format). The above
                            example shows two custom fields (<code>birthday</code> and
                            <code>color</code>), but you can add whatever fields you'd like.</p>

                        <p>You can also store complicated nested JSON documents!</p>
                        <br/>
                        <br/>

                        <h2>Update Custom Data</h2>
                        <br/>
                        <br/>

                        <p>If you enter values below, we'll send and store these
                            values with your user account on Stormpath.</p>

                        <p>Please note, we are not doing any validation in this simple
                            example -- in a real world scenario, you'd want to check user input on the server side!</p>
                        <br/>
                        <br/>

                        <form method="post" class="bs-example form-horizontal" action="${pageContext.request.contextPath}/dashboard">
                            <div class="form-group">
                                <label for="birthday" class="col-lg-2 control-label">Birthday</label>

                                <div class="col-lg-4">
                                    <input type="text" class="form-control" id="birthday" name="birthday" placeholder="mm/dd/yyyy"
                                           value="${!empty account.customData['birthday'] ? account.customData['birthday'] : ''}">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="color" class="col-lg-2 control-label">Favorite Color</label>
                                <div class="col-lg-4">
                                    <input type="text" class="form-control" id="color" name="color" placeholder="color"
                                           value="${!empty account.customData['color'] ? account.customData['color'] : ''}">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-lg-10 col-lg-offset-2">
                                    <button type="submit" class="btn btn-primary">Update Custom Data</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:page>
