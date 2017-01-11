.. _request authentication:

HTTP Request Authentication
===========================

In addition to supporting a traditional server-side rendered :ref:`login <login>` form, the |project| also supports various HTTP request authentication schemes out of the box.  By default, this includes:

* HTTP Basic Authentication

* Password-based Bearer Token Authentication for Javascript clients and Single Page Applications (SPA) and mobile clients

* API Key authentication for machine clients (other servers or code libraries) that execute on behalf of a user account.

.. contents::
   :local:
   :depth: 2

HTTP Basic Authentication
-------------------------

Any HTTP request that uses the `HTTP Basic Authentication <http://tools.ietf.org/html/rfc2617#section-2>`_ protocol will be authenticated automatically.

For example, using curl:

.. code-block:: bash

   curl -u LOGIN:PASSWORD http://localhost:${port}/

where:

* ``LOGIN`` is the Account ``username`` *or* ``email`` address.
* ``PASSWORD`` is the Account password

.. caution:: HTTPS Required

   HTTP Basic authentication sends the *raw* user password over the network.  Basic Authentication should only be used over HTTPS connections and never over standard HTTP.

Token Authentication
--------------------

Token Authentication, also called 'Bearer Token Authentication', is a convenient authentication mechanism for user interfaces that are not based on traditional server-side rendered pages, for example, Javascript clients or Single Page Applications (SPAs).

Many times, these clients communicate with your application via a REST API and need to authenticate every request.  (Bearer) Token Authentication helps solve this challenge.

A common use case is as follows:

1.  A rich client (JS/HTML/CSS) is loaded in a user's browser or a mobile application starts.
2.  The client-side UI collects the user's username (or email address) and password, for example via a form.
3.  The client-side UI submits the username/email and password pair to your server-side web application, typically as an AJAX HTTPS request.
4.  The |project| will ensure the client is allowed to make the request and then authenticates the submitted credentials.
5.  If the authentication is successful, a 'bearer token' will be returned in the HTTP response to the Javascript application.
6.  The client-side UI will send this bearer token over HTTPS on every future request, either in a cookie, or the request's ``Authorization`` header, or both.  For example:

    .. code-block:: rest

       Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0NTI0ODhlZ ... etc ...

Under the hood, the |project| implements this behavior according to the OAuth 2 specification.  It implements the entire OAuth 2 'password grant' flow for you -  you don't have to implement any of this logic at all!

There is an added benefit to this design: you don't need server-side sessions to retain user identity state.  Server-stateless applications can often scale better than those with sessions since state does not need to be shared or clustered across server nodes.

Get a Token
^^^^^^^^^^^

An http client or user agent (or Single Page Application) must first authenticate to receive an authentication Token.  To do this, send a form ``POST`` request (i.e. a ``Content-Type`` of ``application/x-www-form-urlencoded``) to ``/oauth/token``.

cURL
~~~~

For example with cURL:

.. code-block:: bash

   curl -X POST --data \
      'grant_type=password&username=ACCOUNT_USERNAME&password=ACCOUNT_PASSWORD' \
      -H 'Origin: http://localhost:${port}' http://localhost:${port}/oauth/token

where:

* ``ACCOUNT_USERNAME`` is the username or email address of an account that may login to your application
* ``ACCOUNT_PASSWORD`` is the account's password

Tip: In the ``--data`` argument value, don't forget to URL-encode special characters.  For example, if specifying an email, you would specify ``foo%40bar.com`` (correct) instead of ``foo@bar.com`` (incorrect).

The response body will be an OAuth 2 response, for example:

.. code-block:: json

   {
      "expires_in": 3600,
      "token_type": "Bearer",
      "access_token": "eyJraWQiOiJSOTJTQkhKQz...",
      "refresh_token": "eyJraWQiOiJSOTJTQkhKQz..."
   }


The ``access_token`` value is the Bearer Token to send back on future requests in the ``Authorization`` header.  For example, assuming the above:

.. code-block:: rest

   Authorization: Bearer eyJraWQiOiJSOTJTQkhKQz...

For example, with cURL (the value is shortened for brevity):

.. code-block:: bash

   curl -H 'Authorization: Bearer eyJraWQiOiJSOTJTQkhKQz...' http://localhost:${port}

that the actual ``Authorization`` header value is the string literal ``Bearer``, followed by a space character, followed by the actual token value string.

HTML Form and AJAX
~~~~~~~~~~~~~~~~~~

You can also do the same thing in a browser with a form and ajax:

.. code-block:: html

   <form id="ajaxLoginForm" method="post" action="${pageContext.request.contextPath}/oauth/token">

       <input name="username" type="text" autofocus="autofocus" required="required"/>

       <input name="password" type="password" required="required"/>

       <input name="grant_type" type="hidden" value="password"/>

       <button type="submit">Log In</button>

   </form>

Here is an example JQuery snippet that will process the form submission (ideally this is defined after the form, e.g. at the bottom of the page):

.. code-block:: html

   <script type="text/javascript">

     var frm = $('#ajaxLoginForm');

     frm.submit(function (ev) {

       $.ajax({
         type: frm.attr('method'),
         url: frm.attr('action'),
         data: frm.serialize(),

         success: function (data) {
           window.location = "${pageContext.request.contextPath}/dashboard";
         },

         error: function(jqXHR, statusString, err) {
           alert('login attempt failed.  Please try again.');
         }

       });

       ev.preventDefault();

     });

   </script>

The Bearer Token will be saved as a secure, http-only cookie and sent back to the server on all future requests.  The Starter knows how to look for this cookie to authenticate the request.

Or, if you prefer, your JavaScript code can inspect the HTTP response body and get the ``access_token`` value and then set the ``Authorization`` header with the value on future requests.  For example:

.. code-block:: rest

   Authorization: Bearer ACCESS_TOKEN_VALUE

Note that the actual ``Authorization`` header value is the string literal ``Bearer``, followed by a space character, followed by the actual token value string.

Origin or Referer Required
~~~~~~~~~~~~~~~~~~~~~~~~~~

Requests to obtain a Bearer Token must have an ``Origin`` or ``Referer`` header (Origin is preferred) and these header values must match one or more configured 'base URLs'.

By default, requests will be allowed if the JavaScript client is loaded from the same base URL as your web application (specifically, the client-requested host has the same base URL as what is the browser sets in the ``Origin`` and ``Referer`` header values).

If you want to specify additional hosts that are permitted to run JavaScript that may access your server, you can specify a configuration property:

.. code-block:: properties

   stormpath.web.login.token.authorizedJavaScriptOriginUris = http://localhost https://localhost http://localhost:${port} https://localhost:${port}

The value is a whitespace-delimited list of base URLS.  Each base URL value must be formatted as follows:

* The request scheme (``http`` or ``https``), followed by:
* The protocol separator, i.e. ``://``, followed by:
* The allowed origin host IP or host name
* If a port must be specified:

  * The port separator, i.e. ``:``, followed by:
  * The port number

No additional information such as a URI path or query may be specified.

.. warning::

   Any Browser JavaScript client launched from a URL that matches one of the specified base URIs may communicate with your web application.  Only specify additional web host URLs that you trust to communicate with your web application.

HTTPS Required
~~~~~~~~~~~~~~

It is *not safe* to request a new token over standard HTTP connections and is explicitly forbidden to do so by the OAuth specification.  Therefore, requests to ``/oauth/token`` must be over an HTTPS connection, otherwise the request is rejected.

A convenience allowance for localhost development is enabled however: the HTTPS requirement assertion does not apply if the client and server are both on localhost to allow for convenience while developing and testing.

#if( $servlet )

This is enabled via the following configuration property:

.. code-block:: properties

   stormpath.web.oauth2.authorizer.secure.resolver = com.stormpath.sdk.servlet.config.SecureResolverFactory

The ``com.stormpath.sdk.servlet.config.SecureResolverFactory`` returns a ``Resolver<Boolean>`` instance that will return true or false based on the inbound request.

The default implementation returns a ``com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver`` instance, which, as the name implies, requires HTTPS for all requests except those that are sent from and to localhost.

.. caution::

   If you change this configuration value to specify your own ``com.stormpath.sdk.servlet.http.Resolver<Boolean>`` implementation, please be aware that the OAuth 2 specification *requires* HTTPS.  If your implementation returns ``false`` at any time when you deploy your application to production, your web application *will* be vulnerable to identity hijacking attacks.

#else

This is enabled via the ``stormpathSecureResolver`` bean, an instance of ``Resolver<Boolean>`` that returns true for all scenarios except for localhost development.

If you want to provide your own implementation that reflects other scenarios, you can override the ``stormpathSecureResolver`` bean:

.. code-block:: java

   @Bean
   public Resolver<Boolean> stormpathSecureResolver() {
       return MySecureResolver(); //implement me
   }

.. caution::

   If you change this bean to return your own ``com.stormpath.sdk.servlet.http.Resolver<Boolean>`` implementation, please be aware that the OAuth 2 specification *requires* HTTPS.  If your implementation returns ``false`` at any time when you deploy your application to production, your web application *will* be vulnerable to identity hijacking attacks.

#end

API Key Authentication
----------------------

Any account that may login to your application that also has one or more API Keys may use those API Keys to authenticate requests to your web application using HTTP Basic Authentication.  For example:

.. code-block:: bash

   curl -u ACCOUNT_API_KEY_ID:ACCOUNT_API_KEY_SECRET http://localhost:${port}/

.. caution:: HTTPS Required

   HTTP Basic authentication sends the *raw* credentials over the network.  Basic Authentication should only be used over HTTPS connections and never over standard HTTP.

Determining API Key Authentication
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If a request is authenticated, and you want to know if the authentication was based on an API Key, you can check a request attribute.  For example:

.. code-block:: java

   if (request.getRemoteUser() != null) { //request is authenticated

       ApiKey apiKey = (ApiKey)request.getAttribute(ApiKey.class.getName());

       if (apiKey != null) {

           //request was authenticated by an API Key

       } else {

          //request was not authenticated by an API Key

       }

   }
