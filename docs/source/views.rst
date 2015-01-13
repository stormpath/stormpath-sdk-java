.. _views:

Views
=====

The Stormpath Servlet Plugin provides out-of-the-box template-based views for common URIs, like `/login`, `/register`, etc.  This page documents how you can easily customize or completely replace the views to have your own look and feel.

All of the default views are all based on the same principle: they are decorated by a common page template, and only the primary content in each rendered page changes.  Internationalization (i18n) is used to represent text on all default views so you can provide a nice translated view for each user based on their locale.  CSS styles largely control the view look and feel.

JSP Views
---------

The default views are all rendered based on a common page template, conveniently represented as a JSP tag library - no 3rd party template libraries are required. This tag library is included in the Stormpath Servlet Plugin and available to any JSP view in your project.

Any JSP view that should be rendered based on this template should reference this JSP tag library at the top of the JSP:

.. code-block:: jsp

    <%@ taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>

And the page might be defined as follows:

.. code-block:: jsp

    <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>
    <%@ taglib prefix="sp" uri="http://stormpath.com/jsp/tags" %>
    <%-- Any other taglibs, like the core 'c' library, etc: --%>

    <t:page>
        <jsp:attribute name="title"><!-- Optional title to go in the doc's <meta> section --></jsp:attribute>
        <jsp:attribute name="bodyCssClass"><!-- Optional CSS class to apply to the <body> element --></jsp:attribute>
        <jsp:body>

            <!-- Place your page content here: -->

        </jsp:body>
    </t:page>

Anything within the ``<jsp:body> </jsp:body>`` element will be 'wrapped' by the :ref:`page template <view template>`.

Change a Default View
---------------------

If you want to change the structure of any of the included default JSP views, you must redefine them in your own project in the following *exact* War File Locations:

============= ================================ =======================================
Default URI   Description                      War File Location
============= ================================ =======================================
/login        Login View                       /WEB-INF/jsp/stormpath/login.jsp
/forgot       Forgot Password Workflow Start   /WEB-INF/jsp/stormpath/forgot.jsp
/change       Forgot Password Set New Password /WEB-INF/jsp/stormpath/change.jsp
/register     New user / registration view     /WEB-INF/jsp/stormpath/register.jsp
/verify       New user please check email view /WEB-INF/jsp/stormpath/verifyEmail.jsp
/unauthorized Unauthorized access view         /WEB-INF/jsp/stormpath/unauthorized.jsp
============= ================================ =======================================

If you re-define any of these files at the exact same respective path in your .war project, that file will be used to render the view instead of the plugin file.

Internationalization (i18n)
---------------------------

All of the Stormpath plugin default views are internationalized to support language translation based on the end-user's locale.

In addition to the page template tag library, a regular tag library is included in the plugin and may be used to automatically render i18n messages based on the the ``com.stormpath.sdk.servlet.i18n`` message resource bundle.  You can use the taglib in a jsp by referencing ``<%@ taglib prefix="sp" uri="http://stormpath.com/jsp/tags" %>`` at the top of your JSP file.

For example:

.. code-block:: jsp

    <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>
    <%@ taglib prefix="sp" uri="http://stormpath.com/jsp/tags" %>

    <t:page>
        <jsp:attribute name="title"><sp:message key="stormpath.web.login.title"/></jsp:attribute>
        <jsp:attribute name="bodyCssClass">login</jsp:attribute>
        <jsp:body>

            <h1><sp:message key="stormpath.web.login.title"/></h1>

        </jsp:body>
    </t:page>

The ``<sp:message>`` tag works just like the standard template library's ``<fmt:message>`` tag, but ``<sp:message>`` will automatically use the ``com.stormpath.sdk.servlet.i18n`` message bundle in addition to allowing for a flexible locale resolution strategy in your ``stormpath.properties`` configuration.

If you wish to see all of the predefined message keys available, as well as more information about i18n message value resolution, please see the :ref:`i18n` page.

.. _view template:

View Template
-------------

You can change the template applied to the views if desired.  You simply re-define the template file in your own project at the following .war file location:

.. code-block:: bash

    /WEB-INF/tags/page.tag

Your file *must* have this exact path and name in the .war file, otherwise the override will not occur.

Once you create the file, you can populate it.  Here is a basic template example you can use to start:

.. code-block:: jsp

    <%@tag description="Stormpath Page template" pageEncoding="UTF-8"%>
    <%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%-- Any other taglibs --%>
    <%@attribute name="title" required="false" %>
    <%-- Any other attributes referenced in this template --%>

    <!DOCTYPE html>
    <html>
        <head>
        <meta charset="utf-8">
        <title><c:out value="${!empty title ? title : ''}"/></title>
        <link href="${pageContext.request.contextPath}/assets/css/style.css" rel="stylesheet">
    </head>
    <body>
        <jsp:doBody/>
    </body>
    </html>

The important points to note:

* ``<%@tag description="Default Page template" pageEncoding="UTF-8"%>`` must be at the top of the file
* ``<jsp:doBody/>`` must be somewhere in the template.  This will be substituted at runtime with the actual page content.
* A ``title`` page attribute is supported.  This can be specified in views that use the template via ``<jsp:attribute name="title">Value Here</jsp:attribute>``

Once you've done this, all of the default views will reflect your template instead of the plugin's default template!

CSS
---

The plugin's JSP template references two relevant CSS files:

.. code-block:: jsp

    <link href="${pageContext.request.contextPath}/assets/css/stormpath.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/custom.stormpath.css" rel="stylesheet">

If you wish to easily override only a few styles, it's easiest to re-create the ``custom.stormpath.css`` in your own project at the following *exact* .war path and file name:

.. code-block:: bash

    /assets/css/custom.stormpath.css

If this file is present in your .war, it will override the plugin default file.  You can re-define any definitions you find in the base ``stormpath.css`` file and those will override the defaults in ``stormpath.css``.

If you have a lot of CSS changes, you may wish to re-define the ``stormpath.css`` file entirely.  Just create the following file in your project at the following *exact* .war path and file name:

.. code-block:: bash

    /assets/css/stormpath.css

If this file is present in your .war, it will override the plugin default file.

Finally, if this proves too cumbersome, it might be easier just to re-define the plugin's `view template`_ and reference your own CSS file in the template and ignore any of the plugin default css files.


