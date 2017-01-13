.. _request:

Request Context
===============

#if( $sczuul )

.. tip::

   This page covers request objects that are available during a request within the ${apptype} itself.  If you are
   looking for information about the request data forwarded to the origin server(s), please see the
   :ref:`Forwarded Request <forwarded request>` page instead.

#end

The |project| ensures important Stormpath objects are available to your ${apptype} code during a request:

* The Stormpath SDK ``Client`` instance, in case you want to communicate with Stormpath for any behavior that the |project| does not automate.
* The Stormpath ``Application`` instance that corresponds to your web application.
* A Stormpath ``Account`` instance that represents the current authenticated user account making the request.

The ``Client`` and ``Application`` will always be available.  The current user ``Account`` is only available if the user making the request is authenticated.

.. contents::
   :local:
   :depth: 2

Current User Account
--------------------

After a user :ref:`registers <registration>` or :ref:`logs in <login>`, you can get the user ``Account`` making the request using an ``AccountResolver``, request attributes, or the ``HttpServletRequest`` API directly.

.. note::
   A request ``Account`` will only be available if a known user has previously authenticated and is making the request.  If the request is made by an unknown user (a 'guest') that has not yet authenticated, the request will not have an associated ``Account``.

.. contents::
   :local:
   :depth: 1

Account Resolver
^^^^^^^^^^^^^^^^

A type-safe way to get an ``Account`` associated with the current request is to use the ``AccountResolver``:

.. code-block:: java

    import com.stormpath.sdk.account.Account;
    //...

    if (AccountResolver.INSTANCE.hasAccount(request)) {

        //a known user has authenticated previously - get the user identity:
        Account account = AccountResolver.INSTANCE.getRequiredAccount(request);

        //do something with the account

    } else {

        //the current user is unknown - either unauthenticated or a guest.

    }

.. caution::
   Notice that the ``AccountResolver.getRequiredAccount`` method is only invoked if it is known that there is indeed an Account instance available.  If an account is not available when calling ``getRequiredAccount`` an exception will be thrown.

   This *check-then-use* pattern helps eliminate NullPointerExceptions and conditional branching bugs when working with user identities - often desirable in sensitive logic.

Request Attributes
^^^^^^^^^^^^^^^^^^

While request attribute lookups are not type-safe (``String`` keys and ``Object`` return types), they can be convenient or preferred in some situations.

Request Attribute Names
~~~~~~~~~~~~~~~~~~~~~~~

If an ``Account`` is associated with the current request, it will be available via the request attribute name equal to the ``Account`` interface's fully qualified class name.  For example:

.. code-block:: java

    Account account = (Account) request.getAttribute(Account.getClass().getName());
    //account will be null if the request is not yet associated with a known user account.

It will also be available via the simpler unqualified name of ``account``.  For example, the following line provides the same exact result as the one above:

.. code-block:: java

    Account account = (Account) request.getAttribute("account");
    //account will be null if the request is not associated with a known user account.

Why two attribute names for the same object?

When writing view templates, it is often easier to reference a request attribute by a simple name rather than being required to import a Class or use the class's fully qualified name.  For example, consider a the following `JSP Expression Language`_ example:

.. code-block:: jsp

    Hello, ${requestScope.account.givenName}! Nice to see you again!

Without the simpler attribute name, you would have to do something like this:

.. code-block:: jsp

    Hello, ${requestScope['com.stormpath.sdk.account.Account'].givenName}! Nice to see you again!

which is less readable and not as convenient.

HTTP Servlet Request API
^^^^^^^^^^^^^^^^^^^^^^^^

If you'd prefer to use the native ``HttpServletRequest`` API to obtain any associated account information, you can!  You can call either of the two following methods:

* HttpServletRequest `getRemoteUser()`_
* HttpServletRequest `getUserPrincipal()`_

Even better, you can customize exactly what is returned from either of these methods.

HttpServletRequest getRemoteUser()
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If there is an ``Account`` associated with the request, invoking ``request.getRemoteUser()`` will return the Account's  ``username`` by default.  If there is no associated Account, ``null`` is returned.

But you can specify what the return value should be via the ``stormpath.web.request.remoteUser.strategy`` configuration property:

.. code-block:: properties

    stormpath.web.request.remoteUser.strategy = username

The property value may be one of the following strings: ``username``, ``email``, ``givenName``, ``href``, or ``bypass``:

* ``username``: ``getRemoteUser()`` will return the Account's username, ``account.getUsername()``.
* ``email``: returns ``account.getEmail()``
* ``givenName``: returns ``account.getGivenName()``
* ``href``: returns ``account.getHref()``
* ``bypass``: disables the behavior for this method and delegates to the Servlet Container implementation.

Again, if there is no Account associated with the request, ``getRemoteUser()`` will return ``null``.

HttpServletRequest getUserPrincipal()
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If there is an ``Account`` associated with the request, invoking ``request.getUserPrincipal()`` will return a ``java.security.Principal`` instance that reflects the associated Account.  If there is no associated ``Account``, ``null`` is returned.

If there is an account, by default, an instance of ``com.stormpath.sdk.servlet.http.AccountPrincipal`` is returned.  This allows you to get the Account object directly by invoking ``accountPrincipal.getAccount()``.

But you can specify what type of ``Principal`` implementation is returned via the ``stormpath.web.request.userPrincipal.strategy`` configuration property:

.. code-block:: properties

    stormpath.web.request.userPrincipal.strategy = account

The property value may be one of the following strings: ``account``, ``email``, ``username``, ``givenName``, ``href``, ``bypass``:

* ``account``: ``getUserPrincipal()`` returns a ``com.stormpath.sdk.servlet.http.AccountPrincipal`` that represents the entire ``Account`` object
* ``email``: returns a ``com.stormpath.sdk.servlet.http.EmailPrincipal`` matching ``account.getEmail()``.
* ``username``: returns a ``com.stormpath.sdk.servlet.http.UsernamePrincipal`` matching ``account.getUsername()``.
* ``givenname``: returns a ``com.stormpath.sdk.servlet.http.GivenNamePrincipal`` matching ``account.getGivenName()``.
* ``href``: returns a ``com.stormpath.sdk.servlet.http.HrefPrincipal`` matching ``account.getHref()``.
* ``bypass``: disables the behavior for this method and delegates to the Servlet Container implementation.

Again, if there is no Account associated with the request, ``getUserPrincipal()`` will return ``null``.

.. _request application:

Stormpath Application
---------------------

The |project| requires that your ${apptype} correspond to a registered ``Application`` record within Stormpath.  You can access this ``Application`` for your own needs (for example, searching your application's user accounts, creating groups, etc.) using Spring autowiring, an ``ApplicationResolver`` or request attributes.

#if( !$servlet )

Spring autowiring
^^^^^^^^^^^^^^^^^

The ``Application`` instance is created at ${apptype} startup and is not request-specific, so the easiest thing to do is to obtain it by normal Spring autowiring:

.. code-block:: java

   @Autowired
   private Application application;

#end

Application Resolver
^^^^^^^^^^^^^^^^^^^^

A type-safe way to lookup the ``Application`` instance is to use the ``ApplicationResolver``:

.. code-block:: java

   import com.stormpath.sdk.servlet.application.ApplicationResolver;
   //...

   Application myApp = ApplicationResolver.INSTANCE.getApplication(request);

Request Attributes
^^^^^^^^^^^^^^^^^^

While request attribute lookups are not type-safe (``String`` keys and ``Object`` return types), they can be convenient or preferred in some situations.

Default Request Attribute Name
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``Application`` will always be available under the request attribute key equal to the ``Application`` interface's fully qualified class name.  For example:

.. code-block:: java

    Application myApp = (Application) request.getAttribute(Application.getClass().getName());

Custom Request Attribute Names
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``Application`` is also available via simpler unqualified attribute names for convenience.  For example, the default convenience attribute name key is just ``application``.  This allows the exact same Application lookup above to be done like this:

.. code-block:: java

    Application myApp = (Application) request.getAttribute("application");

If you want to change this name, or add other names, you can change the ``stormpath.web.request.application.attributeNames`` configuration property and set a comma-delimited list of names.  For example:

.. code-block:: properties

    stormpath.web.request.application.attributeNames = app, application, stormpathApplication, stormpathApp

Why is this supported?

When writing view templates, it is often easier to reference a request attribute by a simple name rather than being required to import a Class or use the class's fully qualified name.  For example, consider a the following `JSP Expression Language`_ example:

.. code-block:: jsp

    My application name is: ${requestScope.application.name}.

Without these simpler attribute names, you would have to do something like this:

.. code-block:: jsp

    My application name is: ${requestScope['com.stormpath.sdk.application.Application'].name}.

which is less readable and not very convenient.

.. _request sdk client:

Stormpath Client
----------------

#if( $servlet )

The |project| uses a Stormpath ``Client`` for all communication to Stormpath. You can access this ``Client`` for your own needs using either the ``ClientResolver`` or request attributes.

#else

The |project| uses a Stormpath ``Client`` for all communication to Stormpath. You can access this ``Client`` for your own needs using Spring autowiring, the ``ClientResolver`` or request attributes.

Spring autowiring
^^^^^^^^^^^^^^^^^

The ``Client`` is created at ${apptype} startup and is not request-specific, so the easiest thing to do is to obtain it by normal Spring autowiring:

.. code-block:: java

   @Autowired
   private Client client;

#end

Client Resolver
^^^^^^^^^^^^^^^

If you want to look up the Client using only the HttpServletRequest, you can do so in a type-safe way using the ``ClientResolver``:

.. code-block:: java

   import com.stormpath.sdk.servlet.client.ClientResolver;
   //...

   Client client = ClientResolver.INSTANCE.getClient(request);

Request Attributes
^^^^^^^^^^^^^^^^^^

While request attribute lookups are not type-safe (``String`` keys and ``Object`` return types), they can be convenient or preferred in some situations.

Default Request Attribute Name
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``Client`` will always be available under the request attribute key equal to the ``Client`` interface's fully qualified class name.  For example:

.. code-block:: java

    Client client = (Client) request.getAttribute(Client.getClass().getName());

Custom Request Attribute Names
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The ``Client`` is also available via simpler unqualified attribute names for convenience.  For example, the default convenience attribute name key is just ``client``.  This allows the exact same Client lookup above to be done like this:

.. code-block:: java

    Client client = (Client) request.getAttribute("client");

If you want to change this name, or add other names, you can change the ``stormpath.web.request.client.attributeNames`` configuration property and set a comma-delimited list of names.  For example:

.. code-block:: properties

    stormpath.web.request.client.attributeNames = client, stormpathClient, awesomeStormpathClient

Why is this supported?

When writing view templates, it is often easier to reference a request attribute by a simple name rather than being required to import a Class or use the class's fully qualified name.  For example, consider a the following `JSP Expression Language`_ example:

.. code-block:: jsp

    My Stormpath tenant name is: ${requestScope.client.currentTenant.name}.

Without these simpler attribute names, you would have to do something like this:

.. code-block:: jsp

    My Stormpath tenant name is: ${requestScope['com.stormpath.sdk.client.Client'].currentTenant.name}.

which is less readable and not very convenient.

.. _JSP Expression Language: http://docs.oracle.com/javaee/1.4/tutorial/doc/JSPIntro7.html
.. _getRemoteUser(): http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getRemoteUser()
.. _getUserPrincipal(): http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getUserPrincipal()