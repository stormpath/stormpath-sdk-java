.. _views:

Custom Views
============

The |project| provides out-of-the-box template files to render views at supported URIs, like ``/login``, ``/register``, etc.  This page documents how you can easily customize or completely replace the views to have your own look and feel.

.. tip::

   This page discusses templates for views that are local only to the application where the |project| is enabled.

   If you have multiple applications that need a unified registration, login (SSO - Single Sign On) and forgot-password experience, please see the :ref:`ID Site section <idsite>` instead.

All of the default built-in views are all based on the same principle: they are a standard html templates styled with shared `Bootstrap <http://getbootstrap.com/>`_ CSS. Internationalization (i18n) is used to represent text on all default views so you can provide a nice translated view for each user based on their locale.  CSS styles control the view look and feel.

#if( $servlet )

JSP Views
---------

The default views are JSPs rendered based on a common page template.  The template itself is also a JSP, but represented as a tag library - no 3rd party template libraries are required. This tag library is included in the Stormpath Java Servlet Plugin and available to any JSP view in your project.

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

#else

Thymeleaf Views
---------------

The ``stormpath-thymeleaf-spring-boot-starter`` .jar ensures that Spring Boot's `Thymeleaf support <http://blog.codeleak.pl/2014/04/how-to-spring-boot-and-thymeleaf-with-maven.html>`_ is available and also provides the out-of-the-box template files in its ``templates/stormpath`` package.

Because Spring Boot's Thymeleaf ``ViewResolver`` assumes a view name prefix of ``classpath:templates`` and a suffix of ``.htm``, this implies that Stormpath's default view template files can be referenced with a name like ``stormpath/someName``, ``stormpath/anotherName``, etc.

The default views are Thymeleaf ``.htm`` files that share a common ``<head>`` element to pull in `Bootstrap <http://getbootstrap.com/>`_ CSS and Javascript.  The ``<head>`` content itself is also a Thymeleaf template.  All other data in the template are either i18n message keys that use Spring's MessageSource mechanism for internationalization or model object references for the model made available to the template by the backing view controller.

Here is an extremely simple template file that shows how the others operate:

.. code-block:: html

    <html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title th:text="#{stormpath.web.login.title}">Login Title Here</title>
        <!--/*/ <th:block th:include="${headViewName} :: ${headFragmentSelector}"/> /*/-->
    </head>
    <body>
    <!-- Body content here -->
    </body>
    </html>

#end

Internationalization (i18n)
---------------------------

#if( $servlet )

All of the |project| default views are internationalized to support language translation based on the end-user's locale.

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

.. _default view files_jsp:

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

#. Copy and paste :ref:`each stormpath default view file <default view files_jsp>` to your own project at the *exact* same path as the plugin files.  That is, each file *must* be in your .war's ``/WEB-INF/jsp/stormpath/`` directory and they *must* have the exact same name as the original files.

#. In each view file, you'll need to replace the following line:

   .. code-block:: jsp

      <%@ taglib prefix="t" uri="http://stormpath.com/jsp/tags/templates" %>

   with your own tag library template uri:

   .. code-block:: jsp

      <%@ taglib prefix="t" uri="http://mycompany.com/myapp/jsp/tags/templates" %>

   (or whatever URI you chose when you created your ``/META-INF/templates.tld`` :ref:`tag library descriptor file <custom template tld>`).

After completing these steps, all plugin views will reflect your custom template.

#else

In the above example, you can see one of two meaningful lines:

.. code-block:: html

    <title th:text="#{stormpath.web.login.title}">Login Title Here</title>

This line shows a title using standard `Thymeleaf i18n message key notation <http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#messages>`_.  The Spring Boot Thymeleaf starter automatically ensures that this notation will reference your application's Spring ``MessageSource``, ensuring i18n works the same as in any other Spring application.

Each Stormpath message key is automatically available in any template. The full list of Stormpath's out-of-the-box message keys is listed in the default :ref:`i18n.properties file <i18n-properties-file>`.  You can use them yourself or use any of your own message keys.

.. _head template:

Head Template
-------------

The second interesting line above is this one:

.. code-block:: html

    <!--/*/ <th:block th:include="${headViewName} :: ${headFragmentSelector}"/> /*/-->


While this may look like a commented-out HTML comment, this is actually a special `Thymeleaf directive <http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#thymeleaf-prototype-only-comment-blocks>`_ that will include another template.  As you see, ``${headViewName}`` and ``${headFragmentSelector}`` are themselves values that are substituted at runtime with a template view name and a 'fragment selector' to allow you to control which fragment within the template is included.  These values are configured with the following two configuration properties:

.. code-block:: properties

    stormpath.web.head.view = stormpath/head
    stormpath.web.head.fragmentSelector = head

If you wanted, you could change these values to completely replace the default head template with your own.  See the Thymeleaf documentation for more about `comments and blocks <http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#comments-and-blocks>`_ and `fragment selectors <http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#optional-brackets>`_.

CSS
---

Views are styled based on a configurable set of CSS files referenced in the ``head`` template.  The default CSS files enabled are `Bootstrap <http://getbootstrap.com/>`_ css and a default :ref:`stormpath.css <stormpath.css>` file.

CSS Overrides
^^^^^^^^^^^^^

You can override the default styles by re-defining any of the styles in a CSS file that you specify.  Create the file in your Spring Boot project, override whatever styles you want, and then and reference the runtime URI of this file (where it will reside when the app is online) via the ``stormpath.web.head.extraCssUris`` configuration property.  For example:

.. code-block:: properties

    stormpath.web.head.extraCssUris = /assets/css/override.stormpath.css

The value can be one or more space-delimited URIS.  If a URI value starts with ``http`` or ``//``, the value is considered fully qualified and will be inserted directly into the template.  Any other value is assumed to be relative to the web application's context path.  Also note that the browser will load the CSS files in the order specified, implying that styles in later URIs take precedence (will override identical styles found in previous URIs).

The URI values that you specify assumes that your CSS files reside in one of the static content package locations that `Spring Boot will serve by default <https://spring.io/blog/2013/12/19/serving-static-web-content-with-spring-boot>`_.

Given the above URI example, this implies that the file resides in one of the following locations (assuming you use a Maven/Grade project structure):

* ``src/main/resources/META-INF/resources/assets/css/override.stormpath.css``
* ``src/main/resources/resources/assets/css/override.stormpath.css``
* ``src/main/resources/static/assets/css/override.stormpath.css``
* ``src/main/resources/public/assets/css/override.stormpath.css``

CSS Replacement
^^^^^^^^^^^^^^^

The above ``stormpath.web.head.extraCssUris`` property is used to define extra or additional CSS files after the default base set of CSS files (Bootstrap + Stormpath defaults) are in place.

If you don't want this and instead want to explicitly define every CSS file referenced from scratch, you can set the ``stormpath.web.head.cssUris`` value directly:

.. code-block:: properties

    stormpath.web.head.cssUris = uri1 uri2 ... uriN

The value can be one or more space-delimited URIS.  If a URI value starts with ``http`` or ``//``, the value is considered fully qualified and will be inserted directly into the template.  Any other value is assumed to be relative to the web application's context path.  Also note that the browser will load the CSS files in the order specified, implying that styles in later URIs take precedence (will override identical styles found in previous URIs).

If you set this property, there is no need to set the ``stormpath.web.head.extraCssUris`` property.

Finally, if this proves too cumbersome or you just want total control, you might want to define your own `head template`_ entirely.

.. _default view files_thymeleaf:

Change a Default View
---------------------

If you want to change the structure of any of the included default Thymeleaf views, you must redefine them (copy and paste them) in your own project and specify the view name as a Stormpath configuration property.

For example, let's assume you wanted to write a completely different Login view from scratch.  You would do that by re-defining the Thymeleaf .html file in your own project.  Let's assume you put this file in the following location (assuming a Maven/Gradle project structure):

.. parsed-literal::

   src/main/resources/templates/myLoginPage.html

All that is left to do is to specify this view template be used instead of the Stormpath default.  You do that in this particular case (the login page) by setting the ``stormpath.web.login.view`` property with the view name of your template file:

.. code-block:: properties

    stormpath.web.login.view = myLoginPage

Why is the view name value in this case just ``myLoginPage`` when the Stormpath default view name is ``stormpath/login``?

The reason is because the default Thymeleaf view resolver assumes a *classpath* file prefix of ``classpath:templates/`` and a suffix of ``.html``.  The example file above is under the ``src/main/resources`` directory which reflects the root of the classpath.  It is in a ``templates`` package, which is standard for Spring Boot template files.  Finally the ``.html`` suffix finishes the file path.  The Stormpath default views are purposefully in a ``stormpath`` sub-package to reduce the possibility of naming conflicts in your own project.

Once you re-define the view file in your project and set the corresponding ``stormpath.web.VIEWNAME.view`` property (where VIEWNAME is the name of the view you want to override), the Stormpath view controller will render the view with your template instead of the default.

See the :ref:`appendix <appendix>` for a list of the default Thymeleaf view template files that you might wish to copy-and-paste into your project.

#end
