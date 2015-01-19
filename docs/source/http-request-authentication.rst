.. _http request authentication:

HTTP Request Authentication
===========================

In addition to supporting a traditional server-side rendered :ref:`login <login>` form, the Stormpath Servlet Plugin also supports various HTTP request authentication schemes out of the box.  By default, this includes:

* HTTP Basic Authentication

* Password-based Bearer Token Authentication for Javascript clients and Single Page Applications (SPA)

* API Key authentication for machine clients (other servers or code libraries) that execute on behalf of a user account.

HTTP Basic Authentication
-------------------------

Any HTTP request that uses the `HTTP Basic Authentication <http://tools.ietf.org/html/rfc2617#section-2>`_ protocol will be authenticated by the plugin automatically.

For example, using curl:

.. code-block:: bash

   curl -u LOGIN:PASSWORD http://localhost:8080/

where:

* ``LOGIN`` is the Account ``username`` *or* ``email`` address.
* ``PASSWORD`` is the Account password

.. caution:: HTTPS Required

   HTTP Basic authentication sends the *raw* user password over the network.  Basic Authentication should only be used over HTTPS connections and never over standard HTTP.

Bearer Token Authentication
---------------------------

Bearer Token authentication is a convenient authentication mechanism for Javascript clients or Single Page Applications (SPAs), especially those that communicate with your application via a REST API.

The most common use case is as follows:

1.  A client-side user interface built with HTML, CSS and Javascript is loaded in the user's browser.
2.  When the user logs in, the client-side user interface collects the user's username (or email address) and password.
3.  The client-side application submits the username/email and password pair to your server-side application, typically as an AJAX HTTPS request.
4.  The Stormpath Servlet Plugin will ensure the client is allowed to make the request and then authenticates the submitted credentials.
5.  If the authentication is successful, the plugin will return a 'bearer token' in the HTTP response to the Javascript application.
6.  The Javascript application will send this bearer token over HTTPS on every future request, either in a cookie, or the request's ``Authorization`` header, or both.  For example:

    .. code-block:: http

       Authorization: Bearer mF_9.B5f-4.1JqM

Under the hood, the Stormpath Servlet Plugin implements this behavior according to the OAuth 2 specification.  It implements the entire OAuth 2 'password grant' flow for you -  you don't have to implement any of this logic at all!
