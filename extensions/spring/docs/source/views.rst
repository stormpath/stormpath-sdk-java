.. _views:

Custom Views
============

The |project| provides out-of-the-box template-based views for common URIs, like `/login`, `/register`, etc.  This page documents how you can easily customize or completely replace the views to have your own look and feel.

All of the default views are all based on the same principle: they are decorated by a common page template, and only the primary content in each rendered page changes.  Internationalization (i18n) is used to represent text on all default views so you can provide a nice translated view for each user based on their locale.  CSS styles largely control the view look and feel.

JSP Views
---------

The default views are JSPs rendered based on a common page template.  The template itself is also a JSP, but represented as a tag library - no 3rd party template libraries are required. This tag library is included in the |project| and available to any JSP view in your project.

A JSP view that should be rendered based on this template should reference this template tag library at the top of the JSP:

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

CSS
---

Views are styled based on CSS files referenced in the view template.  Because each view is decorated by the template, the view is styled based on the definitions in these CSS files:

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

Finally, if this proves too cumbersome or you just want total control, you might want to define your own `view template`_ and reference your own CSS file in the template and ignore any of the plugin default css files.

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

.. _default view files:

Change a Default View
---------------------

If you want to change the structure of any of the included default JSP views, you must redefine them (copy and paste them) in your own project in the following *exact* .war file locations:

============= ================================ =======================================
Default URI   Description                      War File Location
============= ================================ =======================================
/login        Login View                       /WEB-INF/jsp/stormpath/login.jsp
/forgot       Forgot Password Workflow Start   /WEB-INF/jsp/stormpath/forgot.jsp
/change       Forgot Password Set New Password /WEB-INF/jsp/stormpath/change.jsp
/register     New user / registration view     /WEB-INF/jsp/stormpath/register.jsp
/verify       New user please check email view /WEB-INF/jsp/stormpath/verify.jsp
/unauthorized Unauthorized access view         /WEB-INF/jsp/stormpath/unauthorized.jsp
============= ================================ =======================================

If you re-define any of these files at the exact same respective path in your .war project, that file will be used to render the view instead of the plugin file.

.. _view template:

View Template
-------------

Unfortunately the convenient override mechanism where you simply just replace a plugin default file with your own does not work with JSP tag-based templates.  This means that if you want to use your own page template for the plugin's views, you will need to replace *all* of the plugin's default view files.  But the good news is that there are only 6 view files, and they can mostly be copied-and-pasted, so it shouldn't take too long (5 to 10 minutes?).

If you do wish to use your own page template, here is how:

.. _custom template tld:

#. Create a new ``/META-INF/templates.tld`` file in your .war project with the following contents:

   .. code-block:: xml

     <?xml version="1.0" encoding="UTF-8" ?>
     <taglib xmlns="http://java.sun.com/xml/ns/javaee"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd"
             version="2.1">

         <tlib-version>1.0</tlib-version> <!-- whatever version your application is -->
         <short-name>myAppPageTemplate</short-name> <!-- any name will do -->
         <uri>http://mycompany.com/myapp/jsp/tags/templates</uri> <!-- Does not need to resolve to a real view -->

         <tag-file>
             <name>page</name>
             <path>/META-INF/tags/page.tag</path>
         </tag-file>

     </taglib>

#. Create a new ``/META-INF/tags/page.tag`` file in your .war project with your view template markup.  Although this has a ``.tag`` suffix, this is just a standard JSP file.  Here is a basic template example you can use to start:

   .. code-block:: jsp

     <%@tag description="My App page template" pageEncoding="UTF-8"%>
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

   * ``<%@tag description="My App page template" pageEncoding="UTF-8"%>`` must be at the top of the file
   * ``<jsp:doBody/>`` must be somewhere in the template.  This will be substituted at runtime with the actual page content.
   * A ``title`` page attribute is supported.  This can be specified in views that use the template via ``<jsp:attribute name="title">Value Here</jsp:attribute>``

#. Copy and paste :ref:`each stormpath default view file <default view files>` to your own project at the *exact* same path as the plugin files.  That is, each file *must* be in your .war's ``/WEB-INF/jsp/stormpath/`` directory and they *must* have the exact same name as the original files.

#. In each view file, you'll need to replace the following line:

   .. code-block:: jsp

      <%@ taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>

   with your own tag library template uri:

   .. code-block:: jsp

      <%@ taglib prefix="t" uri="http://mycompany.com/myapp/jsp/tags/templates" %>

   (or whatever URI you chose when you created your ``/META-INF/templates.tld`` :ref:`tag library descriptor file <custom template tld>`).


After completing these steps, all plugin views will reflect your custom template.


