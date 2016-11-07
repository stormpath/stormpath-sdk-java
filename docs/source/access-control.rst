.. _access control:

Access Control
==============

The |project| allows you to easily control which of your web application's URI paths a user is allowed to access based on configurable rules.

The rules are implemented via simple Servlet :ref:`filters <filters>` and :ref:`configurable filter chains <uris>`, but in a simpler, easier to read, much more flexible, and more succinct definition format than you might be used to with ``web.xml`` configuration.

It works as follows:

* You can define any number of servlet filter implementation that represents a single control rule, for example, the current requestor must be a known user, or they must be authenticated, or they must belong to a particular group, etc.  The plugin comes with a number of useful ones ready to go out of the box.

* For any URI path that a user might access, you can specify one or more of these control rules (filters).  If all of the control filter conditions pass - that is, each specified filter determines that the current request is allowed - the filters allow the request to continue to the final destination, presumably a Servlet or an MVC controller in your web framework.

  If the access control condition is not satisfied, any filter is allowed to do what it needs: for example, redirect the user to a different location or send back a particular 4XX status code.  This provides a very flexible mechanism to build up rule chains as necessary based on your application requirements.

See the configuration chapter to see how to define the available access control :ref:`filters <filters>` and :ref:`URI-specific filter chains <uris>`

Next, we'll see how uri patterns and filters allow you to define authentication and authorization rules for your web application.

Authentication
--------------

If you need to ensure a user is authenticated before accessing a particular URI path, you can specify the ``authc`` filter for a particular request.  For example, assume that only authenticated users are allowed to access the account dashboard located at ``/account/dashboard``.  We could create a uri path definition:

.. code-block:: properties

   stormpath.web.uris./account/dashboard = authc

This line indicates:

* Is the user associated with the request to ``/account/dashboard`` authenticated?

  * If yes: allow the request to continue, where it will be handled by a Servlet or MVC controller.
  * If no: Was the request made by a browser (i.e. the requester prefers HTML or XHTML content based on the ``Accept`` header) ?

    * If yes, HTML content is preferred, redirect the request to the :ref:`login` view.
    * If no, HTML content is not preferred, send an HTTP authentication challenge response that indicates the types of HTTP authentication that are supported.

As you can see, this authentication assertion behavior supports both 'normal' browser-initiated requests as well as HTTP client libraries.

.. topic:: Security: Require Authentication By Default

   For many web applications, the safest security policy is to always require authentication for any URL that is not explicitly intended for anonymous users.  This is easily achieved by using a 'catch all' rule at the very bottom of your URI filter chains definitions:

   .. code-block:: properties

      # other stormpath.web.uris.* properties above here

      # 'catch all': require authentication for anything not explicitly allowed by anonymous users:
      stormpath.web.uris./** = authc

UnauthenticatedHandler
^^^^^^^^^^^^^^^^^^^^^^

If the above logic does not meet your needs, you can implement the behavior you want to be executed if it is determined that the requester is not authenticated by specifying the ``stormpath.web.authc.unauthenticatedHandler`` configuration property.  The property value must be the name of a fully qualified class name that implements the ``com.stormpath.sdk.servlet.filter.UnauthenticatedHandler`` interface.  For example:

.. code-block:: properties

    stormpath.web.authc.unauthenticatedHandler =  com.my.impl.MyUnauthenticatedHandler

However, this might not be a trivial exercise if you still wish to support both browser and HTTP client semantics.

.. note::

   If you want to execute custom behavior as a result of a failed login, it is generally recommended not to implement a custom ``UnauthenticatedHandler`` and instead react to :ref:`login events <login events>`.  These events are triggered in the event of an authentication by login form or by HTTP headers, so you can react to both scenarios.

Exclusions
----------

Sometimes you might want to exclude a particular path or sub path from an authentication or authorization requirement.  One very common example of this is for static assets, such as images, css or javascript files needed to render a UI before a user can log in.

You can specify an exclusion as a new URI path with the ``anon`` filter and define it *before* the URI(s) that require(s) authentication or authorization.  For example, let's say all of a web app's static assets should always be publicly available under the ``/assets`` path (e.g. ``/assets/images``, ``/assets/css``, ``/assets/js``, etc).  You can define an exclusion like this:

.. code-block:: properties

   stormpath.web.uris./assets/** = anon

   # other uri chains

   # everything else not specified requires authentication:
   stormpath.web.uris./** = authc

The ``anon`` filter allows any anonymous request to continue to the desired handler without performing access control checks at all.

Because the ``stormpath.web.uris./assets/**`` line comes *before* the ``stormpath.web.uris./**`` catch-all line, the first line will match ``/assets/**`` requests first and the catch-all assertion will not be executed.  Remember, URI rule chains are matched and executed based on a 'first match wins' policy.

.. _Ant-style path expression: https://ant.apache.org/manual/dirtasks.html#patterns
.. _context path: http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getContextPath()
