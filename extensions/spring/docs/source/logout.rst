.. _logout:

Logout
======

Logging out a user is simple: just redirect the user to ``/logout``. This will:

* Trigger a ``LogoutRequestEvent``.  You can listen for this event and perform custom cleanup logic if desired.
* Clear any authentication state that may be associated with the logged in account (like identity cookies or JWT tokens, etc).
* Clear any request identity.
* Terminate the user's HTTP session if one exists as it could be a security risk to allow the session to live.

URI
---

Users will be logged-out if they visit ``/logout``.

If you want to change this path, set the ``stormpath.web.logout.uri`` configuration property:

.. code-block:: properties

    # The context-relative path that will log out the user if visited:
    stormpath.web.logout.uri = /logout

Next Query Parameter
^^^^^^^^^^^^^^^^^^^^

The logout controller supports a ``next`` query parameter.  If present in the request, the value must be a context-relative path to where the user should be redirected after the current request completes.

If the logout URI is visited with a ``next`` query parameter, the user will be redirected to the ``next`` path instead of the default ``nextUri``.

Next URI
--------

If the request to the logout URI does not have a ``next`` query parameter, the user will be redirected to the application's web context root path ('home page') by default.

If you want the user to visit a different default post-logout path, set the ``stormpath.web.logout.nextUri`` configuration property:

.. code-block:: properties

    # The default context-relative path where the user will be redirected after logout:
    stormpath.web.logout.nextUri = /

If the request to the logout URI has a ``next`` query parameter, that parameter value will be used as the context-relative path instead and the ``stormpath.web.logout.nextUri`` value will be ignored.

Events
------

If you implement a :ref:`Request Event Listener <events>`, you can listen for the ``LogoutRequestEvent``.

The ``LogoutRequestEvent`` is published when processing an HTTP request that will logout the request's associated ``Account``.  After the request is complete, the account will be logged out.
