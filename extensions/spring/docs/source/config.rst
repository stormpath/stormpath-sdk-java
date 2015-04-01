.. _config:

Configuration
=============

.. contents::
   :local:
   :depth: 2

No Config?
----------

Refreshingly, the |project| doesn't require any configuration at all as long as the following conditions apply:

#. You added the |project| .jar and its transitive dependencies to your web application's ``/WEB-INF/lib`` directory.  This happens automatically if you use a Maven-compatible build tool like Maven or Gradle to :ref:`specify the spring-boot-starter-stormpath-thymeleaf dependency <dependency-jar>` in your project build configuration.

#. Your web application can read the ``$HOME/.stormpath/apiKey.properties`` file :ref:`mentioned in the Quickstart <get-api-key>`.

#. You have only one Application registered with Stormpath.

If all of these conditions cannot be met, then you will have to specify some minimal configuration (but not much!) as described below.

Property Overrides
------------------

Wherever possible, sane default configuration values are used to automatically configure Stormpath beans loaded by Spring.

If you wish to override any of these defaults, you can do so by overriding properties in your Spring Boot `application.properties`_ locations.  In most cases, setting a configuration property will be all that is necessary - most of all of the default Stormpath bean implementations are highly configurable with property values.  If you need even finer control, you may wish to re-define a Stormpath bean entirely to provide your own implementation as discussed in the next section below.

All Stormpath configuration properties are prefixed with ``stormpath.`` and take the following form (for example):

.. code-block:: properties

    stormpath.some.property.name = aValue
    stormpath.another.property.name = anotherValue

Simply just re-define the relevant ``stormpath.``\* property in one of your Spring Boot `application.properties`_ locations and your value will be used instead of the default.

.. _property security considerations:

Security Considerations: Passwords and secret values
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

It is **strongly** recommended that you do not specify the ``stormpath.apiKey.secret`` property - or any other password or secret property - in shared properties files. These files are usually committed to version control (like git) and shared with other developers.

Because Stormpath API Keys are always assigned to an individual person, they should never be shared with or visible to anyone else, not even other co-workers or even Stormpath employees.  Anyone that has your API Key id and secret can alter the data in your Stormpath tenant.

Also, it should also be noted that, while JVM System Properties are not usually visible to other developers, using System Properties for secrets and passwords can also be seen as a security risk: system property values are visible to anyone performing a process listing on a production machine (e.g. ``ps aux | grep java``).

If you cannot rely on accessing the default ``$HOME/.stormpath/apiKey.properties`` file, Environment Variables or a different private local file (with restricted read permissions) is usually a safer alternative when defining passwords or secret values than shared files or JVM System Properties.


Bean Overrides
--------------

If property overrides are not sufficient or you need even finer control, you may wish to re-define a Stormpath bean entirely and provide your own custom implementation.

To do so, you just need to re-define that bean in your own Spring Boot Java configuration.  Often certain bean names must be retained, so if you re-define a bean, try to use the same name and your bean will be used instead of the default.

For example, assume a bean named ``stormpathJwtFactory`` existed.  To use your own implementation instead of the Stormpath default, just redefine the bean in your project's Java Config.  For example:

.. code-block:: java

    @Bean
    public JwtFactory stormpathJwtFactory() {
        return new MyJwtFactory();
    }

A ``MyJwtFactory`` instance will be used instead of the default.


Stormpath Client
----------------

The |project| depends on a Stormpath SDK ``Client`` instance to communicate with Stormpath for most functionality.  You may configure the client via ``stormpath.*`` properties as necessary.

API Key
~~~~~~~

The API Key used by the SDK Client will be acquired from the following locations.  Locations inspected later override previously discovered values.

* ``$HOME/.stormpath/apiKey.properties`` file
* Any ``stormpath.apiKey.id`` value discovered from Spring property placeholder locations
* Any ``stormpath.apiKey.secret`` value discovered from Spring property placeholder locations **\***

**\*** While ``stormpath.apiKey.secret`` can be configured as a property in a file, please be aware of the :ref:`security considerations <property security considerations>` of files shared with other people.

HTTP Proxy
~~~~~~~~~~

If your application requires communication to Stormpath go through an HTTP Proxy, you can set the following configuration properties as needed:

* ``stormpath.proxy.host``: Proxy server hostname or IP address, e.g. ``proxy.mycompany.com`` or ``10.0.2.88``.
* ``stormpath.proxy.port``: Proxy server port, for example ``8888``.
* ``stormpath.proxy.username``: Username to use when connecting to the proxy server.  Only configure this property if proxy server username/password authentication is required.
* ``stormpath.proxy.password``: Password to use when connecting to the proxy server.  Only configure this property if proxy server username/password authentication is required, but **note**: it is strongly recommended that you don't embed passwords in text files.

Authentication Scheme
~~~~~~~~~~~~~~~~~~~~~

The Stormpath SDK Client communicates with Stormpath using a very secure `cryptographic digest`_-based authentication scheme.

If you deploy your app on Google App Engine however, you might experience some problems.  You can change the scheme to use ``basic`` authentication by setting the following configuration property and value:

.. code-block:: properties

   stormpath.authentication.scheme = basic

If your application is not deployed on Google App Engine, we recommend that you *do not* set this property.

Caching
~~~~~~~

The client caches resources from the Stormpath API server by default in an in-memory, in-process cache to enhance performance.

.. caution::
    If your application is deployed across multiple JVMs (e.g. clustered or striped) the default caching mechanism could cause problems because each application instance would have its *own* cache.  This could cause data consistency problems across the application instances.

You can either disable the cache entirely or configure your own coherent or cluster-friendly Spring ``CacheManager`` and that would be used for the Stormpath Client's needs automatically.

Please see the :ref:`Caching <caching>` chapter for more information.

Usage
~~~~~

You may access the ``Client`` instance via normal Spring autowiring.  For example:

.. code-block:: java

   @Autowired
   private Client client;

You can also :ref:`access the client via a ServletRequest <request sdk client>`.

Stormpath Application
---------------------

The |project| requires that your application correspond to a registered ``Application`` record within Stormpath.

If you only have one registered application with Stormpath, Stormpath-Spring will automatically query Stormpath at startup, find the ``Application`` and use it, and no configuration is necessary.

However, if you have more than one application registered with Stormpath, you must configure the ``href`` of the specific application to access by setting the following configuration property:

.. code-block:: properties

   stormpath.application.href = your_application_href_here

You can find your application's href in the `Stormpath Admin Console`_:

#. Click on the ``Applications`` tab and find your application in the list.  Click on the Application's name:

   .. image:: /_static/console-applications-ann.png

#. On the resulting *Application Details* page, the **REST URL** property value is your application's ``href``:

   .. image:: /_static/console-application-href.png

Usage
~~~~~

You may access the ``Application`` instance if desired (for example, searching your application's user accounts, creating groups, etc) using normal Spring autowiring:

.. code-block:: java

   @Autowired
   private Application application;

You can also :ref:`access the application via a ServletRequest <request application>`.

.. _cryptographic digest: http://en.wikipedia.org/wiki/Cryptographic_hash_function
.. _Stormpath Admin Console: https://api.stormpath.com
.. _application.properties: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html