.. _config:

Configuration
=============

.. contents::
   :local:
   :depth: 1

No Config?
----------

Refreshingly, the Stormpath Servlet Plugin doesn't require any configuration at all as long as the following conditions
apply:

#. You added the stormpath-servlet-plugin .jar and its transitive dependencies to your web application's ``/WEB-INF/lib`` directory.  This happens automatically if you use a Maven-compatible build tool like Maven or Gradle to :ref:`specify the stormpath-servlet-plugin dependency <servlet-plugin-jar>` in your project build configuration.

#. Your web application can read the ``$HOME/.stormpath/apiKey.properties`` file :ref:`mentioned in the Quickstart <get-api-key>`.

#. You have only one Application registered with Stormpath.

If all of these conditions cannot be met, then you will have to specify some minimal configuration (but not much!) as
described below.

web.xml
-------

Most plugin users do not need to modify the web application ``/WEB-INF/web.xml`` file to enable the plugin - just adding the plugin .jar to your web application's ``lib`` directory is usually sufficient.

However, some applications might experience a filter chain conflict that causes problems.

At application startup, the Stormpath Servlet Plugin automatically enables a ``StormpathFilter`` to handle various request flows.  If your web application uses frameworks that make heavy use of servlet filters, like Spring MVC or Apache Shiro, these existing filters might cause an ordering conflict with the ``StormpathFilter``.

If you are experiencing problems after adding the stormpath-servlet-plugin .jar to your web app's classpath, you'll need to explicitly specify where the ``StormpathFilter`` should reside in your application's filter chain.  Luckily the fix is really easy:

Simply specify the following XML chunk in ``/WEB-INF/web.xml`` relative to other filter mappings that are already enabled in your application:

  .. code-block:: xml

      <filter-mapping>
          <filter-name>StormpathFilter</filter-name>
          <url-pattern>/*</url-pattern>
      </filter-mapping>

It is often easiest to specifying this at or near the top of your other filter mappings.  The ``StormpathFilter`` will ignore all filtered requests that do not match recognized URL rules, allowing other frameworks to filter requests as necessary.

stormpath.properties
--------------------

If you need to customize behavior, the Stormpath Servlet Plugin uses a very simple ``.properties`` based configuration format and supports a  convenient overrides mechanism using various property definition locations.

All stormpath configuration properties are prefixed with ``stormpath.`` and take the following form (for example)

.. code-block:: properties

    stormpath.some.property.name = aValue
    stormpath.another.property.name = anotherValue

etc.

Property Locations
~~~~~~~~~~~~~~~~~~

You can define stormpath property values in a number of locations.  This allows you to define a core set of properties in a primary configuration file and override values as necessary using other locations.

Configuration property values are read from the following locations, *in order*.  Values discovered in locations later (further down in the list) will automatically override values found in previous locations:

.. contents::
   :local:
   :depth: 2

If you're just starting out, we recommend that your configuration be specified in ``/WEB-INF/stormpath.properties`` and you use Environment Variables to specify password or secret values (e.g. for production environments).

Defining properties in these locations is covered more in detail next.

1. Plugin default.stormpath.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This file resides in the stormpath-servlet-plugin-|version|.jar at:

 ``/META-INF/com/stormpath/sdk/servlet/default.stormpath.properties``

It includes all of the plugin's default configuration and is not modifiable.  The default values within can be overridden by specifying properties in locations read later during the startup process.

2. classpath:stormpath.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If a ``stormpath.properties`` file exists at the root of your web application's classpath (typically in ``/WEB-INF/classes`` or at the root of one of your .jar files in ``/WEB-INF/lib``), ``stormpath.*`` properties will be read from that file and override any identically-named properties discovered previously.

.. NOTE::
   Because this is not a web-specific location, it is only recommended to use this location if you wish to share stormpath properties configuration across multiple projects in a 'resource .jar' that is used in such projects.

3. /WEB-INF/stormpath.properties
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If a file ``/WEB-INF/stormpath.properties`` exists in your web application, properties will be read from this file and override any indentically-named properties discovered in previous locations.

.. TIP::
   This is the recommended primary configuration location for most web applications.

4. Servlet Context Parameters
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you define ``stormpath.*`` servlet context parameters in your web application's ``/WEB-INF/web.xml`` file, they will override any identically-named properties discovered in previous locations.  For example:


.. code-block:: xml

    <context-param>
        <param-name>stormpath.foo.bar</param-name>
        <param-value>myValue</param-value>
    </context-param>

5. Environment Variables
^^^^^^^^^^^^^^^^^^^^^^^^

You may use Environment Variables to specify or override your application's ``stormpath.*`` properties using an all uppercase + underscore convention.

For example, let's assume there is a property named ``stormpath.foo.bar`` that you would might specify in a file:

.. code-block:: properties

    stormpath.foo.bar = myValue

If you wanted to specify this property as an environment variable, you would change all characters to uppercase and replace all period characters ``.`` with underscores ``_``. The above example then becomes:

``STORMPATH_FOO_BAR=myValue``

For example, using the bash shell on a \*nix operating system:

.. code-block:: bash

    $ export STORMPATH_FOO_BAR=myValue


6. JVM System Properties
^^^^^^^^^^^^^^^^^^^^^^^^

If you define ``stormpath.*`` system properties (using ``-D`` flags when starting the java process), they will override any identically-named properties discovered in previous locations.  For example:

``-Dstormpath.foo.bar=myValue``

Security Considerations: Passwords and secret values
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

It is **strongly** recommended that you do not specify the ``stormpath.apiKey.secret`` property - or any other password or secret property - in shared files such as ``classpath:stormpath.properties``, ``/WEB-INF/stormpath.properties``, or ``web.xml``. These files are usually committed to version control (like git) and shared with other developers.

Because Stormpath API Keys are always assigned to an individual person, they should never be shared with or visible to anyone else, not even other co-workers or even Stormpath employees.  Anyone that has your API Key id and secret can alter the data in your Stormpath tenant.

Also, it should also be noted that, while JVM System Properties are not usually visible to other developers, using System Properties for secrets and passwords can also be seen as a security risk: system property values are visible to anyone performing a process listing on a production machine (e.g. ``ps aux | grep java``).

If you cannot rely on accessing the default ``$HOME/.stormpath/apiKey.properties`` file, Environment Variables or a different private local file (with restricted read permissions) is usually a safer alternative when defining passwords or secret values than shared files or JVM System Properties.

Filters and Routes
------------------

The Stormpath Servlet Plugin works largely by intercepting requests to certain *routes* or paths (URIs) in your application and then executing one or more servlet filters based on the route/path being accessed.

.. _filters:

Filters
~~~~~~~

All of the Servlet Filters needed by the plugin are already configured, but if you wanted to, you could define your own Servlet Filters (or even override the plugin's defaults) in ``stormpath.properties`` locations via the following convention:

.. code-block:: properties

    stormpath.web.filters.FILTER_NAME = FULLY_QUALFIED_CLASS_NAME

where:

* ``FILTER_NAME`` is a unique String name of the filter.
* ``FULLY_QUALIFIED_CLASS_NAME`` is your ``javax.servlet.Filter`` implementation fully qualified class name, for example, ``com.whatever.foo.MyFilter``.

You control which filters are executed, and the order they are executed, by declaring routes.

.. _routes:

Routes
~~~~~~

You can control which functionality is executed for any application route (URI path) by defining your own paths in ``stormpath.properties`` locations via the following convention:

.. code-block:: properties

    stormpath.web.routes.ROUTE_PATTERN = FILTER_CHAIN_DEFINITION

where:

* ``ROUTE_PATTERN`` is an `Ant-style path expression`_ that represents a URI path or path hierarchy (via wildcard ``*`` matching) relative to the web application's `context path`_.
* ``FILTER_CHAIN_DEFINITION`` is a comma-delimited list of filter names that match the the names of any previously defined filters as described  :ref:`above <filters>`.

For example:

``stormpath.web.routes./admin/** = foo, bar, baz``

This configuration line indicates that any request to the `/admin` path or any of its children paths (via the ant-style wildcard of `/admin/**`), the ``foo`` filter should execute, then the ``bar`` filter should execute, then the ``baz`` filter should execute.  If the filters all allow the request to continue, then a servlet handler or controller will receive and process the request.

Therefore, the comma-delimited list of filter names defines a *filter chain* that should execute for that specific route/path.  You can define as many routes (filter chains) as you wish based on your applications needs.

We'll see later on that this technique will be very useful to easily define authentication and authorization rules for your web application.

.. TIP::
    Because route patterns are relative to your web application's `context path`_, you can deploy your application to ``http://localhost:8080/myapp`` and then later deploy it to ``https://myapp.com`` without changing your route configuration.


.. _Ant-style path expression: https://ant.apache.org/manual/dirtasks.html#patterns
.. _context path: http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getContextPath()