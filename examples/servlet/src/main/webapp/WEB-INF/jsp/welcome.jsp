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
    <jsp:attribute name="title">Welcome!</jsp:attribute>
    <jsp:body>
        <div class="jumbotron" id="welcome">

            <h1>Welcome to the Stormpath Web Sample Application!</h1>

            <p class="lead">
                <br/>
                <br/>
                Welcome to this <i>gloriously simple</i>
                <a href="https://github.com/stormpath/stormpath-sdk-java/extensions/servlet">Stormpath Web</a> sample
                application!
            <ul>
                <li>First, take a look through this very basic site.</li>
                <li>Then, check out this project's source code <a
                        href="https://github.com/stormpath/stormpath-sdk-java/examples/servlet">on GitHub</a>.
                </li>
                <li>Lastly, integrate Stormpath into your own sites!</li>
            </ul>
            </p>

            <br/>
            <br/>

            <h2>What This Sample App Demonstrates</h2>

            <br/>
            <br/>

            <p>This simple application demonstrates how easy it is to register, login, and securely authenticate
                users on your website using the Stormpath SDK Servlet container support.</p>

            <p>Not a Stormpath user yet? <a href="https://stormpath.com">Go signup now!</a></p>

            <br/>
            <br/>

            <p>
                <b>NOTE</b>: This application will NOT work until you have gone through
                the bootstrapping instructions found in this project's
                <code>README.md</code> file. For more information, please follow the guide
                on this project's
                <a href="https://github.com/stormpath/stormpath-sdk-java/examples/servlet">GitHub page</a>.
            </p>

            <p class="bigbutton"><a class="bigbutton btn btn-lg btn-danger"
                                    href="${pageContext.request.contextPath}/register" role="button">Register</a></p>
        </div>
    </jsp:body>
</t:page>
