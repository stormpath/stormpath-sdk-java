.. _login:

Login
=====

.. contents::
   :local:
   :depth: 2

Overview
--------

:ref:`Registered <registration>` users may login by visiting ``/login``:

.. image:: /_static/login.png

After entering a valid account username or email address and password, the user will be logged in and redirected back to your application's `context path`_ ('home page') by default.

The default login view also has links to allow the user to :ref:`register a new user account <registration>` or :ref:`reset their password <forgot password>` if they forgot it.

.. _https required:

Security Notice
---------------

.. caution:: HTTPS Required

   **HTTPS must be enabled during a login attempt and for all future requests UNTIL THE USER LOGS OUT**.

   This is the only way to protect your users and prevent password attacks and man-in-the-middle or session hijacking attacks.  Even in internal company (intranet) environments this is required to ensure security.

   Using HTTPS only during login attempts and then returning to HTTP afterwards is an easily exploitable and very serious security hole.  Please do not do this.

The |project| will not enforce HTTPS so that you may easily test during development. It is expected that you will enable HTTPS when you deploy your application to production.

If your ${apptype} is available on the public internet and you feel setting up your own TLS certificate is too bothersome, you can use a free `Cloudflare`_ account and they will provide you one for *free*.  There is simply no reason anymore to not enable TLS for all logged in user sessions. (Stormpath has no incentive to recommend Cloudflare. Free TLS for everyone is just too good to pass up).


URI
---

Users can login by visiting ``/login``

If you want to change this path, set the ``stormpath.web.login.uri`` configuration property:

.. code-block:: properties

    # The context-relative path to the login view:
    stormpath.web.login.uri = /login

Next Query Parameter
^^^^^^^^^^^^^^^^^^^^

The login controller supports a ``next`` query parameter.  If present in the request, the value must be a context-relative path to where the user should be redirected after successful login.

If the login URI is visited with a ``next`` query parameter, the user will be redirected to the ``next`` path instead of the default ``nextUri``.

Next URI
--------

If the request to the login URI does not have a ``next`` query parameter, a successful login will redirect the user to the web application's `context path`_ ('home page') by default.

If you want the user to visit a different default post-login path, set the ``stormpath.web.login.nextUri`` configuration property:

.. code-block:: properties

    # The default context-relative path where the user will be redirected after logging in:
    stormpath.web.login.nextUri = /

If the request to the login URI has a ``next`` query paramter, that parameter value will be used as the context-relative path instead and the ``stormpath.web.login.nextUri`` value will be ignored.

#if( !$servlet )

View
----

When the URI is visited a default template view named ``stormpath/login`` is rendered by default.  If you wanted to render your own template instead of the default, you can set the name of the template to render with the ``stormpath.web.login.view`` property:

.. code-block:: properties

    stormpath.web.login.view = stormpath/login

Remember that the property value is the *name* of a view, and the effective Spring ``ViewResolver`` will resolve that name to a template file.  See the :ref:`Custom Views <views>` chapter for more information.

#end

i18n
----

The :ref:`i18n` message keys used in the default login view have names prefixed with ``stormpath.web.login.``:

.. literalinclude:: ../../../../extensions/servlet/src/main/resources/com/stormpath/sdk/servlet/i18n.properties
   :language: properties
   :lines: 17-48

For more information on customizing i18n messages and adding bundle files, please see :ref:`i18n`.

.. _login events:

Events
------

If you implement a :ref:`Request Event Listener <events>`, you can listen for login-related events in your ${apptype} code and execute custom logic if desired.

There are two events that can be triggered during login attempts:

* ``SuccessfulAuthenticationRequestEvent``: published after a successful login attempt
* ``FailedAuthenticationRequestEvent``: published after a failed login attempt

.. note::
   These authentication events are published during login attempts to the login view, but *also* during REST requests that might be serviced by your ${apptype}.

   Because most REST architectures are stateless, typically every REST HTTP reuqest must be individually authenticated.  Each authenticated (or failed authentication) REST request, then, will result in publishing one of these two ``AuthenticationRequestEvents``.

   You can determine which type of authentication occurred (login form post, REST API call, etc) by inspecting the event's ``AuthenticationResult`` object (``event.getAuthenticationResult()``).  You can use an ``AuthenticationResultVisitor`` to determine which type of AuthenticationResult occurred: ``event.getAuthenticationResult().accept(authenticationResultVisitor);``

.. _login authentication-state:

Authentication State
--------------------

When a user authenticates successfully during a request, how does your ${apptype} know who the authenticated user is during future requests?

HTTP is a stateless protocol, so there must be a way to represent the *state* of an authenticated user - the user identity, when they authenticated, etc - across requests.

The |project| supports retaining authentication state across requests by delegating to an ``AuthenticationResult`` ``Saver``.  Upon a successful authentication, the SDK generates an ``AuthenticationResult``.  This ``AuthenticationResult`` is relayed to one or more ``Saver`` instances to persist this state however might be necessary so it is available during future requests.

By default, a Cookie-based ``Saver`` is enabled.  This is a nice default because it it ensures that all state is maintained by the HTTP client ('user agent') and sent on all future requests automatically.

.. tip::

   The default HTTP Cookie-based ``Saver`` ensures that server side state storage - like a session - is not required at all.  This is beneficial to server-side applications that wish to remain stateless to achieve better performance, scalability, and fault tolerance.

Even though the cookie approach is the default, you can implement your own ``Saver`` implementation to do whatever you like.

Saving Authentication State
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The |project| will automatically save authentication state for access during later requests for you.  By default, a Cookie-based saver is enabled and no ``HttpSession`` access is used.  You can provide your own implementations entirely if the cookie option is not suitable.

#if( $servlet )

You can enable any number of AuthenticationResult savers as a comma-delimited list by setting the ``stormpath.web.authc.savers`` configuration property.  For example, the default value is the following:

.. code-block:: properties

   # Default to 'cookie' for server statelessness:
   stormpath.web.authc.savers = cookie

The value can be a comma delimited list of names.

This property reflects a convention: each name in the list corresponds to another configuration property that specifies the ``Saver`` implementation to use.  Each named saver will be invoked after a successful authentication to allow it to persist state as desired.

You specify saver implementations based on the following convention:

.. code-block:: properties

    stormpath.web.authc.savers.SAVER_NAME = SAVER_FULLY_QUALIFIED_CLASS_NAME

where:

* ``SAVER_NAME`` is a simple string name that represents the ``Saver`` implementation.
* ``SAVER_FULLY_QUALIFIED_CLASS_NAME`` is the fully qualified class name of a class that implements the ``com.stormpath.sdk.servlet.http.Saver`` interface.

For example, one saver implementation is pre-configured by default:

.. code-block:: properties

   stormpath.web.authc.savers.cookie = com.stormpath.sdk.servlet.filter.account.config.CookieAuthenticationResultSaverFactory

So if we look at the default configuration value again:

.. code-block:: properties

   stormpath.web.authc.savers = cookie

we can see that only the Cookie-based ``Saver`` implementation is to be used to ensure server statelessness out of the box.

If you wanted to enable both cookie and your own name that corresponds to a property that reflects a custom implementation ('session' for example):

.. code-block:: properties

   stormpath.web.authc.savers = cookie, session

#end

Cookie Storage
^^^^^^^^^^^^^^

The ``CookieAuthenticationResultSaver`` is the default enabled saver for storing authentication state across requests.

Authentication state is represented as a compact `cryptographically-signed JSON Web Token`_ (JWT), stored as the cookie value.  This ensures that the user identity, login timestamp, etc. is stored in an efficient standards-compliant - and most importantly - immutable manner.

Additionally, the JWT is cryptographically signed by a key known only to your application (by default, your Stormpath API Key Secret).  This means it is sufficiently impossible for anything outside of your web application (like a browser or javascript client) to tamper with the token and misrepresent information or 'fake' a login.  As long as HTTPS is enabled :ref:`during login and for the duration of user interaction <https required>`, you can be reasonably assured that the user identity associated with the request is accurate and set only by your application.

Cookie Config
~~~~~~~~~~~~~

You can control the authentication cookie behavior by setting various ``stormpath.web.accessTokenCookie.*`` configuration properties:

#[[
============================================================================================================ ================================================ ==========================================================
`Cookie <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html>`_ property                      Config Property Name                             Default Value
============================================================================================================ ================================================ ==========================================================
`name <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#getName()>`_                       ``stormpath.web.accessTokenCookie.name``         ``access_token``
`domain <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setDomain(java.lang.String)>`_   ``stormpath.web.accessTokenCookie.domain``       ``null``
`path <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setPath(java.lang.String)>`_       ``stormpath.web.accessTokenCookie.path``         ``null`` (assumes web app context path)
`httpOnly <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setHttpOnly(boolean)>`_        ``stormpath.web.accessTokenCookie.httpOnly``     ``true``
`secure <http://docs.oracle.com/javaee/6/api/javax/servlet/SessionCookieConfig.html#setSecure(boolean)>`_    ``stormpath.web.accessTokenCookie.secure``       ``null``
============================================================================================================ ================================================ ==========================================================

You need to set the refresh token behavior by setting various ``stormpath.web.refreshTokenCookie.*`` configuration properties:

============================================================================================================ ================================================ ==========================================================
`Cookie <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html>`_ property                      Config Property Name                             Default Value
============================================================================================================ ================================================ ==========================================================
`name <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#getName()>`_                       ``stormpath.web.refreshTokenCookie.name``        ``refresh_token``
`domain <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setDomain(java.lang.String)>`_   ``stormpath.web.refreshTokenCookie.domain``      ``null``
`path <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setPath(java.lang.String)>`_       ``stormpath.web.refreshTokenCookie.path``        ``null`` (assumes web app context path)
`httpOnly <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setHttpOnly(boolean)>`_        ``stormpath.web.refreshTokenCookie.httpOnly``    ``true``
`secure <http://docs.oracle.com/javaee/6/api/javax/servlet/SessionCookieConfig.html#setSecure(boolean)>`_    ``stormpath.web.refreshTokenCookie.secure``      ``null``
============================================================================================================ ================================================ ==========================================================
]]#

Some notes about the default values:

.. sidebar:: Secure Cookies

   A ``secure`` cookie will only be sent by the browser over HTTPS connections, otherwise the cookie will not be sent at all.  To eliminate certain security attack vectors, it is important that identity cookies always be ``secure`` in production.

* The ``stormpath.web.accessTokenCookie.secure`` property that controls whether the cookie must be secure or not is a little special.  It does not reflect a direct value.  Instead, it reflects an object that returns a ``Resolver<Boolean>`` implementation.  This ``Resolver<Boolean>`` implementation returns ``true`` or ``false`` as to whether the cookie should be secure or not.

  Because of the security requirements around identity cookies, the default implementation always requires HTTPS *except* when it has been determined that the request is sent and received by ``localhost``.  This allows you to develop on your local machine without requiring a TLS/SSL certificate, but will require one when you deploy to production due to the security attack vectors that would occur otherwise.

  If you want to provide your own condition implementation that returns ``true`` or ``false`` based on request criteria, you can specify your own ``com.stormpath.sdk.servlet.http.Resolver<Boolean>`` implementation - for example:

#if( $servlet )
  .. code-block:: properties

     stormpath.web.account.cookie.secure.resolver = my.impl.class.that.implements.ResolverThatReturnsBoolean

#else
  .. code-block:: java

     @Bean
     public Resolver<Boolean> stormpathSecureResolver() {
         return new MySecureResolver(); //implement me
     }
#end

JWT Creation
~~~~~~~~~~~~

As mentioned above, the cookie value is actually a `cryptographically-signed JSON Web Token`_.  The JWT string itself is created by the ``stormpathAuthenticationJwtFactory`` bean, an instance of the ``AuthenticationJwtFactory`` interface.

The default implementation supports configuring the JWT's TTL (time-to-live) to indicate how long it is valid for.  By default, the JWT itself is valid for 3 days, but it would never be used longer than the cookie's ``maxAge`` value (1 day by default).  If you need to change the JWT TTL, it is configurable via the ``stormpath.web.account.jwt.ttl`` property:

.. code-block:: properties

    # value is in _seconds_ (not milliseconds):
    stormpath.web.account.jwt.ttl = 259200

.. note::
    When a JWT is stored in a cookie, the JWT TTL *must* be greater than or equal to the cookie's ``maxAge`` value (in seconds), otherwise the cookie will retain a stale/unusable JWT.

#if( !$servlet )

Custom AuthenticationJwtFactory
+++++++++++++++++++++++++++++++

If you need greater control over how the JWT is constructed, you can create your own ``AuthenticationJwtFactory`` implementation and override the ``stormpathAuthenticationJwtFactory`` bean to return your instance:

.. code-block:: java

    @Bean
    public AuthenticationJwtFactory stormpathAuthenticationJwtFactory() {
        return new MyAuthenticationJwtFactory(); //implement me
    }

.. tip::
   The default ``AuthenticationJwtFactory`` implementation uses the `JJWT library <https://github.com/jwtk/jjwt>`_ to construct the JWT string.  Because the JJWT library is already available in the runtime classpath, you might find it convenient to use the same library for any custom JWT creation.

#end

Disabling Cookie Storage
~~~~~~~~~~~~~~~~~~~~~~~~

If you are going to use your own ``Saver<AuthenticationResult>`` implementation, you can disable the cookie if desired:

.. code-block:: properties

    stormpath.web.authc.savers.cookie.enabled = false

But be careful: if you disable this, you *must* enable at least one other saver - at least one must be available to handle authentication correctly.

Custom Savers
^^^^^^^^^^^^^

#if( $servlet )

Finally, if the default Cookie-based ``Saver`` implementation is not sufficient. For example, you want an HttpSession-based ``Saver``, then you can create the  ``stormpath.web.authc.savers.session`` configuration property.  The value must be the full qualified class name of an implementation of the ``com.stormpath.sdk.servlet.http.Saver`` interface.  For example:

.. code-block:: properties

   stormpath.web.authc.savers.session = com.my.httpsession.based.Saver

#else

If the default cookie ``Saver`` implementation is not sufficient, you can specify different implementations by overriding the ``stormpathAuthenticationResultSavers`` bean and returning your own ``List`` of ``Saver<AuthenticationResult>`` instances.  For example:

.. code-block:: java

    @Bean
    public List<Saver<AuthenticationResult>> stormpathAuthenticationResultSavers() {

        List<Saver<AuthenticationResult>> savers = new ArrayList<Saver<AuthenticationResult>>();

        //add your custom Saver<AuthenticationResult> instances to the 'savers' list here

        return savers;
    }

Spring Security
---------------

If you are using our `Spring Security integration <https://github.com/stormpath/stormpath-sdk-java/tree/master/extensions/spring/stormpath-spring-security-webmvc>`_ then the ``Authentication`` token will be available in Spring Security's ``SecurityContext`` where you can obtain the Stormpath ``Account`` that is currently authenticated:

.. code-block:: java

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) { //there may be no logged in user.
        String accountHref = authentication.getUsername();
        Account account = client.getAccount(accountHref, Account.class);
        String username = account.getUsername();
    }

The ``Authentication`` token will always be available irrespective of the kind of authentication used to login. It does not matter whether the user authenticated via ``cookie``, ``access_token``, ``credentials``, ``social providers``, etc, the Stormpath Account information will always be available in Spring Security's ``SecurityContext``.

#end

.. _context path: http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getContextPath()
.. _Cloudflare: https://www.cloudflare.com/
.. _cryptographically-signed JSON Web Token: https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-40
