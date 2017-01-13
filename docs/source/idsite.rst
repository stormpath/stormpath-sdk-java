.. _idsite:

ID Site
=======

Stormpath's *ID Site* feature is a hosted website that automates user login, registration, and password reset outside of your application - all hosted on Stormpath's infrastructure. ID Site is really useful for a number of reasons:

* You don't have to code or host any of the user login, registration or password reset screens if you don't want to.
* You don't have to worry about any of the hosting infrastructure or security policies to keep user registration and login safe
* You don't need to change and re-deploy your applications when you want to change the registration or login experience.
* You can maintain your ID site codebase independently of your applications - a change in one does not force a change in another.
* **You can use one ID site for all of your applications, providing a feature-rich Single-Sign-On (SSO) experience across your apps!**

How Does It Work?
-----------------

1. If a user wants to register for your web site, they’ll be redirected to your ID site URL hosted by Stormpath. This can be something like: ``https://login.mysite.com``
2. Stormpath will then serve login / registration / forgot password views automatically, depending on what the user wants to do, allowing the user to do any of these things **without touching your application!** These pages can be completely customized however you like, of course.
3. Once Stormpath has registered or logged the user in, they’ll be redirected back to a URI in your application (``/idSiteResult`` by default). This URI (and its controller) will then verify that the user was successfully logged in, and the user ``Account`` will be immediately be accessible in the :ref:`request context <request>` as expected.

For more information on ID Site, please read the official `ID Site documentation`_.

Enable ID Site
--------------

If you want to use ID Site, the first thing you'll need to do is to `enable the ID Site functionality <https://api.stormpath.com/v#!idSite>`_ in the Stormpath Admin Console.

.. note::

   These instructions will only cover using the built-in hosted login site – if you’d like to customize your ID Site URL or look and feel (theme), please see the official `ID Site documentation`_.

In the box labeled “Authorized Redirect URIs”, enter your redirect URL – this should be set to something like: ``https://www.my-site.com/stormpathCallback``. If you’re testing locally, you might want to set this to: ``http://localhost:${port}/stormpathCallback``.

If you are taking advantage of Stormpath's Multi-Tenancy features, you can use wildcards, like: ``https://*.my-multi-tenant-app.com/stormpathCallback``. This would authorize any subdomain of ``my-multi-tenant-app.com``, such as ``https://fun-co.my-multi-tenant-app.com/stormpathCallback``.

For more information on using Stormpath in a multi-tenant configuration, read our `Multi-Tenant SaaS Guide`_.

If you’d like to support both production and local environments, you can add multiple URLs (*just click the “Add another” button and enter as many URLs as you’d like*).

Lastly, make sure to click the “Save” button at the bottom of the page to save your changes.

In the end, it should look something like this:

.. image:: /_static/idsite-config.png

Configure Your Application
--------------------------

Once you've configured Stormpath properly, you just need to enable the ID Site functionality by setting the ``stormpath.web.idSite.enabled`` configuration property:

.. code-block:: properties

    stormpath.web.idSite.enabled = true

This setting tells the |project| to use the hosted ID Site for user registration, login, and password reset instead of the built-in local functionality.  This is good if you have multiple apps that should have the same login experience.

Try It!
-------

After configuring both Stormpath and your application properties as covered above, start up your application.

If you visit your application's login, registration or forgot password URIs (which by default are ``/login``, ``/register`` and ``/forgot`` respectively), you will be redirected to your ID Site, where you can either create an account or login.

Once you you've logged in, you'll be redirected back to your application in a logged-in state! Nice!

Here's a screenshot of the default ID Site login page to show an example of what it looks like:

.. image:: /_static/idsite-login.png

.. _ID Site documentation: http://docs.stormpath.com/guides/using-id-site/
.. _Multi-Tenant SaaS Guide: https://docs.stormpath.com/rest/product-guide/latest/multitenancy.html