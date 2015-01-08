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

URI
---

Users can login to your web application by visiting ``/login``

If you want to change this path, set the ``stormpath.web.login.url`` configuration property:

.. code-block:: properties

    # The context-relative path to the login view:
    stormpath.web.login.url = /login

Next Query Parameter
^^^^^^^^^^^^^^^^^^^^

The login controller supports a ``next`` query parameter.  If present in the request, the value must be a context-relative path to where the user should be redirected after successful login.

If the login URL is visited with a ``next`` query parameter, the user will be redirected to the ``next`` path instead of the default ``nextUrl``.

Next URI
--------

If the request to the login URL does not have a ``next`` query parameter, a successful login will redirect the user to the web application's `context path`_ ('home page') by default.

If you want the user to visit a different default post-login path, set the ``stormpath.web.login.nextUrl`` configuration property:

.. code-block:: properties

    # The default context-relative path where the user will be redirected after logging in:
    stormpath.web.login.nextUrl = /

If the request to the login URL has a ``next`` query paramter, that parameter value will be used as the context-relative path instead and the ``stormpath.web.login.nextUrl`` value will be ignored.

i18n
----

The :ref:`i18n` message keys used in the default login view have names prefixed with ``stormpath.web.login.``:

.. literalinclude:: ../../extensions/servlet/src/main/resources/com/stormpath/sdk/servlet/i18n.properties
   :language: properties
   :lines: 20-37

For more information on customizing i18n messages and adding bundle files, please see :ref:`i18n`.

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

.. _context path: http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getContextPath()
