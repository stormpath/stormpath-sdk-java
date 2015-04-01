.. _views:

Custom Views
============

The |project| provides out-of-the-box template files to render views at supported URIs, like ``/login``, ``/register``, etc.  This page documents how you can easily customize or completely replace the views to have your own look and feel.

All of the default views are all based on the same principle: they are a standard html templates styled with shared `Bootstrap <http://getbootstrap.com/>`_ CSS. Internationalization (i18n) is used to represent text on all default views so you can provide a nice translated view for each user based on their locale.  CSS styles control the view look and feel.

Thymeleaf Views
---------------

The ``spring-boot-starter-stormpath-thymeleaf`` .jar ensures that Spring Boot's `Thymeleaf support <http://blog.codeleak.pl/2014/04/how-to-spring-boot-and-thymeleaf-with-maven.html>`_ is available and also provides the out-of-the-box template files in its ``templates/stormpath`` package.

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

Internationalization (i18n)
^^^^^^^^^^^^^^^^^^^^^^^^^^^

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

.. _default view files:

Change a Default View
---------------------

If you want to change the structure of any of the included default Thymeleaf views, you must redefine them (copy and paste them) in your own project and specify the view name as a Stormpath configuration property.

For example, let's assume you wanted to write a completely different Login view from scratch.  You would do that by re-defining the Thymeleaf .html file in your own project.  Let's assume you put this file in the following location (assuming a Maven/Gradle project structure)::

    src/main/resources/templates/myLoginPage.html

All that is left to do is to specify this view template be used instead of the Stormpath default.  You do that in this particular case (the login page) by setting the ``stormpath.web.login.view`` property with the view name of your template file:

.. code-block:: properties

    stormpath.web.login.view = myLoginPage

Why is the view name value in this case just ``myLoginPage`` when the Stormpath default view name is ``stormpath/login``?

The reason is because the default Thymeleaf view resolver assumes a *classpath* file prefix of ``classpath:templates/`` and a suffix of ``.html``.  The example file above is under the ``src/main/resources`` directory which reflects the root of the classpath.  It is in a ``templates`` package, which is standard for Spring Boot template files.  Finally the ``.html`` suffix finishes the file path.  The Stormpath default views are purposefully in a ``stormpath`` sub-package to reduce the possibility of naming conflicts in your own project.

Once you re-define the view file in your project and set the corresponding ``stormpath.web.VIEWNAME.view`` property (where VIEWNAME is the name of the view you want to override), the Stormpath view controller will render the view with your template instead of the default.

See the :ref:`appendix <appendix>` for a list of the default Thymeleaf view template files that you might wish to copy-and-paste into your project.