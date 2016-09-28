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

The plugin will not enforce HTTPS so that you may easily test during development. It is expected that you will enable HTTPS when you deploy your application to production.

If your application is available on the public internet and you feel setting up your own TLS certificate is too bothersome, you can use a free `Cloudflare`_ account and they will provide you one for *free*.  There is simply no reason anymore to not enable TLS for all logged in user sessions. (Stormpath has no incentive to recommend Cloudflare. Free TLS for everyone is just too good to pass up).


URI
---

Users can login to your web application by visiting ``/login``

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

i18n
----

The :ref:`i18n` message keys used in the default login view have names prefixed with ``stormpath.web.login.``:

.. literalinclude:: ../../extensions/servlet/src/main/resources/com/stormpath/sdk/servlet/i18n.properties
   :language: properties
   :lines: 17-48

For more information on customizing i18n messages and adding bundle files, please see :ref:`i18n`.

.. _login events:

Events
------

If you implement a :ref:`Request Event Listener <events>`, you can listen for login-related events and execute custom logic if desired.

There are two events that can be triggered during login attempts:

* ``SuccessfulAuthenticationRequestEvent``: published after a successful login attempt
* ``FailedAuthenticationRequestEvent``: published after a failed login attempt

.. note::
   These authentication events are published during login attempts to the login view, but *also* during REST requests that might be serviced by your application.

   Because most REST architectures are stateless, typically every REST HTTP reuqest must be individually authenticated.  Each authenticated (or failed authentication) REST request, then, will result in publishing one of these two ``AuthenticationRequestEvents``.

   You can determine which type of authentication occurred (login form post, REST API call, etc) by inspecting the event's ``AuthenticationResult`` object (``event.getAuthenticationResult()``).  You can use an ``AuthenticationResultVisitor`` to determine which type of AuthenticationResult occurred: ``event.getAuthenticationResult().accept(authenticationResultVisitor);``

.. _login authentication-state:

Authentication State
--------------------

When a user authenticates successfully during a request, how does the web application know who the authenticated user is during future requests?

HTTP is a stateless protocol, so there must be a way to represent the *state* of an authenticated user - the user identity, when they authenticated, etc - across requests.

The plugin supports retaining authentication state across requests by delegating to an ``AuthenticationResult`` ``Saver``.  Upon a successful authentication, the SDK generates an ``AuthenticationResult``.  This ``AuthenticationResult`` is relayed to one or more ``Saver`` instances to persist this state however might be necessary so it is available during future requests.

By default, a Cookie-based ``Saver`` is enabled.  This is a nice default because it it ensures that all state is maintained by the HTTP client ('user agent') and sent on all future requests automatically.

.. tip::

   The default HTTP Cookie-based ``Saver`` ensures that server side state storage - like a session - is not required at all.  This is beneficial to server-side applications that wish to remain stateless to achieve better performance, scalability, and fault tolerance.

Even though the cookie approach is the default, you can choose server-side session storage if you prefer, or enable both, or implement your own ``Saver`` implementation to do whatever you like.

Saving Authentication State
^^^^^^^^^^^^^^^^^^^^^^^^^^^

You can enable any number of AuthenticationResult savers as a comma-delimited list by setting the ``stormpath.web.authc.savers`` configuration property.  For example, the default value is the following:

.. code-block:: properties

   # 'cookie' and 'session' are supported out of the box.  Default to 'cookie' for server statelessness:
   stormpath.web.authc.savers = cookie

The value can be a comma delimited list of names.

This property reflects a convention: each name in the list corresponds to another configuration property that specifies the ``Saver`` implementation to use.  Each named saver will be invoked after a successful authentication to allow it to persist state as desired.

You specify saver implementations based on the following convention:

.. code-block:: properties

    stormpath.web.authc.savers.SAVER_NAME = SAVER_FULLY_QUALIFIED_CLASS_NAME

where:

* ``SAVER_NAME`` is a simple string name that represents the ``Saver`` implementation.
* ``SAVER_FULLY_QUALIFIED_CLASS_NAME`` is the fully qualified class name of a class that implements the ``com.stormpath.sdk.servlet.http.Saver`` interface.

For example, two saver implementations are pre-configured by default:

.. code-block:: properties

   stormpath.web.authc.savers.cookie = com.stormpath.sdk.servlet.filter.account.config.CookieAuthenticationResultSaverFactory
   stormpath.web.authc.savers.session = com.stormpath.sdk.servlet.filter.account.config.SessionAuthenticationResultSaverFactory

So if we look at the default configuration value again:

.. code-block:: properties

   stormpath.web.authc.savers = cookie

we can see that only the cookie-based ``Saver`` implementation is to be used to ensure server statelessness out of the box.

If you wanted to enable both cookie and session storage, for example:

.. code-block:: properties

   stormpath.web.authc.savers = cookie, session

or if you wish, you may specify your own name that corresponds to a property that reflects a custom implementation.

Cookie Storage
^^^^^^^^^^^^^^

The ``CookieAuthenticationResultSaver`` is the default enabled saver for storing authentication state across requests.

Authentication state is represented as a compact `cryptographically-signed JSON Web Token`_ (JWT), stored as the cookie value.  This ensures that the user identity, login timestamp, etc. is stored in an efficient standards-compliant - and most importantly - immutable manner.

Additionally, the JWT is cryptographically signed by a key known only to your application (by default, your Stormpath API Key Secret).  This means it is sufficiently impossible for anything outside of your web application (like a browser or javascript client) to tamper with the token and misrepresent information or 'fake' a login.  As long as HTTPS is enabled :ref:`during login and for the duration of user interaction <https required>`, you can be reasonably assured that the user identity associated with the request is accurate and set only by your application.

Cookie Config
~~~~~~~~~~~~~

You can control the authentication cookie behavior by setting various ``stormpath.web.accessTokenCookie``.\* configuration properties:

============================================================================================================ ================================================ ==========================================================
`Cookie <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html>`_ property                      Config Property Name                             Default Value
============================================================================================================ ================================================ ==========================================================
`name <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#getName()>`_                       ``stormpath.web.accessTokenCookie.name``         ``access_token``
`domain <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setDomain(java.lang.String)>`_   ``stormpath.web.accessTokenCookie.domain``       ``null``
`path <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setPath(java.lang.String)>`_       ``stormpath.web.accessTokenCookie.path``         ``null`` (assumes web app context path)
`httpOnly <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setHttpOnly(boolean)>`_        ``stormpath.web.accessTokenCookie.httpOnly``     ``true``
`secure <http://docs.oracle.com/javaee/6/api/javax/servlet/SessionCookieConfig.html#setSecure(boolean)>`_    ``stormpath.web.accessTokenCookie.secure``       ``null``
============================================================================================================ ================================================ ==========================================================

You need to set the refresh token behavior by setting various ``stormpath.web.refreshTokenCookie``.\* configuration properties:

============================================================================================================ ================================================ ==========================================================
`Cookie <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html>`_ property                      Config Property Name                             Default Value
============================================================================================================ ================================================ ==========================================================
`name <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#getName()>`_                       ``stormpath.web.refreshTokenCookie.name``        ``refresh_token``
`domain <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setDomain(java.lang.String)>`_   ``stormpath.web.refreshTokenCookie.domain``      ``null``
`path <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setPath(java.lang.String)>`_       ``stormpath.web.refreshTokenCookie.path``        ``null`` (assumes web app context path)
`httpOnly <http://docs.oracle.com/javaee/7/api/javax/servlet/http/Cookie.html#setHttpOnly(boolean)>`_        ``stormpath.web.refreshTokenCookie.httpOnly``    ``true``
`secure <http://docs.oracle.com/javaee/6/api/javax/servlet/SessionCookieConfig.html#setSecure(boolean)>`_    ``stormpath.web.refreshTokenCookie.secure``      ``null``
============================================================================================================ ================================================ ==========================================================

Some notes about the default values:

.. sidebar:: Secure Cookies

   A ``secure`` cookie will only be sent by the browser over HTTPS connections, otherwise the cookie will not be sent at all.  To eliminate certain security attack vectors, it is important that identity cookies always be ``secure`` in production.

* The ``stormpath.web.accessTokenCookie.secure`` property that controls whether the cookie must be secure or not is a little special.  It does not reflect a direct value.  Instead, it reflects an object that returns a ``Resolver<Boolean>`` implementation.  This ``Resolver<Boolean>`` implementation returns ``true`` or ``false`` as to whether the cookie should be secure or not.

  Because of the security requirements around identity cookies, the default implementation always requires HTTPS *except* when it has been determined that the request is sent and received by ``localhost``.  This allows you to develop on your local machine without requiring a TLS/SSL certificate, but will require one when you deploy to production due to the security attack vectors that would occur otherwise.

  If you want to provide your own condition implementation that returns ``true`` or ``false`` based on request criteria, you can specify your own ``com.stormpath.sdk.servlet.http.Resolver<Boolean>`` implementation - for example:

  .. code-block:: properties

     stormpath.web.account.cookie.secure.resolver = my.impl.class.that.implements.ResolverThatReturnsBoolean


HttpSession Storage
^^^^^^^^^^^^^^^^^^^

The ``SessionAuthenticationResultSaver`` is available but not enabled by default.  This saver will save an efficient compact representation of the authenticated ``Account`` to the associated request's `HttpSession <http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpSession.html>`_.  This implementation assumes you are using sessions managed by the servlet container.

You can enable this saver by adding the name ``session`` to the ``stormpath.web.authc.savers`` comma-delimited list.  For example:

.. code-block:: properties

   stormpath.web.authc.savers = cookie, session

The plugin does not require use of the ``HttpSession`` at all to remain compatible with stateless architectures.  It will not use the ``HttpSession`` unless you explicitly enable the ``session`` name in the above list.

Finally, if the default HttpSession-based ``Saver`` implementation is not sufficient, you can specify a different implementation with the ``stormpath.web.authc.savers.session`` configuration property.  The value must be the full qualified class name of an implementation of the ``com.stormpath.sdk.servlet.http.Saver`` interface.  For example:

.. code-block:: properties

   stormpath.web.authc.savers.session = com.my.httpsession.based.Saver


.. _context path: http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getContextPath()
.. _Cloudflare: https://www.cloudflare.com/
.. _cryptographically-signed JSON Web Token: https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-40
