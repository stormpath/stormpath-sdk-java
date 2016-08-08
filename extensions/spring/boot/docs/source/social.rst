.. _social:

Social
======

.. contents::
   :local:
   :depth: 2

Overview
--------

Besides username and password :ref:`login <login>`, users can leverage Stormpath
social directory support to single sign on with their favorite social providers
(Facebook, GitHub, Google and LinkedIn).

.. image:: /_static/social-login.png

After clicking on the desire provider the user should follow the usual OAuth2
dance for the given provider, once authenticated it should be redirected back to your
application's `context path`_ ('home page') by default.

Configuration
-------------

This plugin provides some configuration regarding the callback URLs for each provider
so you can create custom controllers or filters to handle the authentication.
It also provides properties to configure the OAuth scope that would be used for
authenticating to each social provider.

.. caution:: Minimal needed scope

   The default value for each provider scope property, is the minimum required scope needed to
   authenticate and create the account inside Stormpath, so you can add scope but not remove
   those default values.

.. code-block:: properties

   #Facebook
   stormpath.web.social.facebook.uri = /callbacks/facebook
   stormpath.web.social.facebook.scope = email
   #GitHub
   stormpath.web.social.github.uri = /callbacks/github
   stormpath.web.social.github.scope = user:email
   #Google
   stormpath.web.social.google.uri = /callbacks/google
   stormpath.web.social.google.scope = email profile
   #LinkedIn
   stormpath.web.social.linkedin.uri = /callbacks/linkedin
   stormpath.web.social.linkedin.scope = r_basicprofile r_emailaddress

To configure the Stormpath social directories check `REST API social authentication guide`_ 

Events
------

See :ref:`Login Events <login Events>`

Authentication State
--------------------

See :ref:`Login Authentication State <login authentication-state>`

.. _context path: http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getContextPath()
.. _REST API social authentication guide: https://docs.stormpath.com/rest/product-guide/latest/auth_n.html#social-authn
