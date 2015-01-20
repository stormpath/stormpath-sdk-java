.. _events:

Events
======

The Stormpath Servlet Plugin will trigger events when interesting things happen.  You can listen for these events and implement custom behavior when desired.

RequestEventListener
--------------------

When the plugin publishes various events, a ``RequestEventListener`` will be notified.  If you want to listen for events, you can implement your own ``RequestEventListener`` in one of two ways:

#. Implement the ``com.stormpath.sdk.servlet.event.RequestEventListener`` interface directly and implement all event methods.
#. Subclass the ``com.stormpath.sdk.servlet.event.RequestEventListenerAdapter`` and override only the event methods you are interested in.

You may specify your implementation's fully qualified class name using the ``stormpath.web.event.listener`` configuration property:

.. code-block:: properties

    stormpath.web.request.event.listener = com.stormpath.sdk.servlet.event.RequestEventListenerAdapter

As you can see, the default implementation is an instance of the adapter, which simply just logs each event to the ``debug`` log level.

Events
------

The events currently published by the plugin are:

======================================== ==============================================================================
Event Class                              Published when processing an HTTP request that:
======================================== ==============================================================================
``SuccessfulAuthenticationRequestEvent`` successfully authenticates an ``Account``
``FailedAuthenticationRequestEvent``     attempts to authenticate an ``Account`` but the authentication attempt failed
``RegisteredAccountRequestEvent``        results in a newly registered ``Account``.  If the newly registered account
                                         requires email verification before it can login,
                                         ``event.getAccount().getStatus() == AccountStatus.UNDEFINED`` will be ``true``
``VerifiedAccountRequestEvent``          verifies an account's email address.  The event's associated account is
                                         considered verified and may login to the application.
``LogoutRequestEvent``                   will logout the request's associated ``Account``.  After the request is
                                         complete, the account will be logged out.
======================================== ==============================================================================

Listener Best Practices
-----------------------

Events are sent and consumed *synchronously* during the HTTP request that triggers them.

To ensure requests are responded to quickly, ensure your event listener methods return quickly or dispatch work asynchronously to another thread or ``ExecutorService`` (for example).