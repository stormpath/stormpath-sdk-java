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
<%@tag description="Default Page template" pageEncoding="UTF-8" %>
<%@taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@attribute name="title" required="false" %>
<%@attribute name="description" required="false" %>
<%@attribute name="bodyCssClass" required="false" %>
<%@attribute name="timedRedirectSeconds" required="false" %>
<%@attribute name="timedRedirectUrl" required="false" %>

<!DOCTYPE html>
<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]> <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]> <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--><html lang="en" class="no-js"><!--<![endif]-->
<head>
<meta charset="utf-8">
<c:if test="${!empty title}"><title>${title}</title></c:if>
<c:if test="${!empty description}"><meta name="description" content="${description}"></c:if>
<c:if test="${!empty timedRedirectUrl}"><meta http-equiv="refresh" content="${timedRedirectSeconds};url=${timedRedirectUrl}"/></c:if>
<meta content="width=device-width" name="viewport">
<link href="//fonts.googleapis.com/css?family=Open+Sans:300italic,300,400italic,400,600italic,600,700italic,700,800italic,800"
      rel="stylesheet" type="text/css">
<link href="//netdna.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">
<c:if test="${!empty accountStores}">
<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.6.3/css/font-awesome.min.css" rel="stylesheet">
<link href="//cdnjs.cloudflare.com/ajax/libs/bootstrap-social/5.0.0/bootstrap-social.min.css" rel="stylesheet">
</c:if>
<link href="${pageContext.request.contextPath}/assets/css/stormpath.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/assets/css/custom.stormpath.css" rel="stylesheet">
<!--[if lt IE 9]>
<script src='https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js'></script>
<script src='https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js'></script>
<![endif]-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="//netdna.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>
<body<c:if test="${!empty bodyCssClass}"> class="${bodyCssClass}"</c:if>>
    <jsp:doBody/>
</body>
</html>