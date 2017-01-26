.. _logout:

Logout
======

Logging out a user is simple: just hit the ``/logout`` with a ``POST`` (not ``GET``) request. This will:

* Trigger a ``LogoutRequestEvent``.  You can listen for this event and perform custom cleanup logic if desired.
* Clear any authentication state that may be associated with the logged in account (like identity cookies or JWT tokens, etc).
* Clear any request identity.
* Terminate the user's HTTP session if one exists as it could be a security risk to allow the session to live.

The reason why ``POST`` is required is to prevent two problems:

#. Eager fetching by optimistic browsers.  Many modern browsers will execute ``GET`` requests to links discovered
   within a page in order to pre-fetch network resources, like images and CSS.  These browsers will cause users
   to be logged out if ``GET`` was enabled for the ``/login`` URI.

#. Malicious redirects.  If a 3rd party website redirected a user directly to your site's ``/logout`` location, your
   user would be logged out.  This isn't as much as a security risk (since the user's identity will be cleared from
   the browser), but it could be a big pain to users.

Finally, the HTTP specification states that resource state should not be modified on ``GET``.  Because logout
manipulates resource (user) state, ideally logout should never be available via ``GET``.

What does this mean for you?  It simply means that you will need to put a Logout link or button somewhere
in your pages that allows a user to logout safely.

#if( $servlet or $spring )

For example, here is a JSTL snippet that renders a logout that is submitted with a logout button:

.. code-block:: xml

  <!-- Only Show the logout form/button if there is an active account: -->
  <c:if test="${!empty account}">
    <form id="logoutForm" action="${pageContext.request.contextPath}/logout" method="post">
      <input type="submit" value="Logout"/>
    </form>
  </c:if>

#elseif ( $springboot or $sczuul )

For example, here is a Thymeleaf snippet that renders a logout form that is submitted with a logout button:

.. code-block:: xml

  <!-- Only Show the logout form/button if there is an active account: -->
  <div th:if="${account}">
    <form id="logoutForm" th:action="@{/logout}" method="post">
      <input type="submit" class="btn btn-danger" value="Logout"/>
    </form>
  </div>

#end

URI
---

Users will be logged-out if they visit ``/logout`` (only with ``POST``)

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
