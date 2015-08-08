.. _setup:

Quickstart
==========

This quickstart demonstrates the fastest way to enable Stormpath in a Spring Boot web application.  It should take about 5 minutes start to finish.  Let's get started!

Topics:

.. contents::
     :local:
     :depth: 1

.. _get-api-key:

Get an API Key
--------------

All communication with Stormpath must be authenticated with an API Key.

#. If you haven’t already, `sign up for Stormpath for free`_.  You’ll be sent a verification email.

#. Click the link in the verification email.

#. Log in to the `Stormpath Admin Console`_ using the email address and password you used during registration.

#. Click the **Create API** Key or **Manage Existing Keys** button in the middle of the page.

#. Under **Security Credentials**, click **Create API Key**.

   This will generate your API Key and download it to your computer as an ``apiKey.properties`` file.

#. Save the file in your home directory in the following location:

   * ``~/.stormpath/apiKey.properties`` on Unix, Linux and Mac OS
   * ``C:\Users\YOUR_USERNAME\.stormpath\apiKey.properties`` on Windows

#. Change the file permissions to ensure only you can read this file and not accidentally write or modify it. For example:

    .. code-block:: bash

     $ chmod go-rwx ~/.stormpath/apiKey.properties
     $ chmod u-w ~/.stormpath/apiKey.properties

On Windows, you can `set file permissions similarly`_.

.. _dependency-jar:

Add the Spring Boot Stormpath Web Starter
-----------------------------------------

This step allows you to enable Stormpath in a Spring Boot web app *without a single line of code or configuration*.  How amazing is that? Here's how.

Using your favorite dependency resolution build tool like Maven or Gradle, add the stormpath-thymeleaf-spring-boot-starter-|version|.jar to your project dependencies. For example:

**Maven**:

.. parsed-literal::

    <dependency>
        <groupId>com.stormpath.spring</groupId>
        <artifactId>stormpath-thymeleaf-spring-boot-starter</artifactId>
        <version>\ |version|\ </version>
    </dependency>

**Gradle**:

.. parsed-literal::

    dependencies {
        compile 'com.stormpath.spring:stormpath-thymeleaf-spring-boot-starter:\ |version|\ '
    }

That's it!  You're ready to start using Stormpath in your Spring Boot web application!  Can you believe how easy that was?

Try it!
-------

If you followed the steps above you will now have fully functional registration, login, logout, forgot password workflows, api authentication and more active on your site!

Don’t believe it? Try it! Start up your Spring Boot web application, and we'll walk you through the basics:

* Navigate to ``/register``. You will see a registration page. Go ahead and enter some information. You should be able to create a user account (but notice how Stormpath helps enforce password strength rules?  You can :ref:`customize these <password strength>` later).  Once you’ve created a user account, you’ll be automatically logged in, then redirected back to the root URL (``/`` by default).
* Navigate to ``/logout``. You will be logged out of your account and then redirected back to ``/login`` by default.
* Navigate to ``/login``. On the lower-right, click the **Forgot Password?** link, and you'll be shown a form to enter your email.  Enter in your email address and it will send you an email.  Wait for the email and click the link and you'll be able to set a new password!

Wasn't that easy?!

Any Problems?
^^^^^^^^^^^^^

Did you experience any problems with this quickstart?  It might not have worked perfectly for you if:

* you have more than one Application registered with Stormpath.  If this is the case, you'll need to configure your application's Stormpath ``href``, found in the admin console.  Once you get the ``href``, add the following to your Spring Boot ``application.properties`` file (where ``YOUR_APPLICATION_ID`` is your application's actual Stormpath Application ID):

  .. code-block:: properties

      stormpath.application.href = https://api.stormpath.com/v1/applications/YOUR_APPLICATION_ID


* your web app already uses web frameworks that make heavy use of servlet filters, like Spring Security or Apache Shiro. These could cause filter ordering conflicts, but the fix is easy - you just need to specify the specific order where you want the Stormpath filter relative to other filters.  You do this by adding the following to your Spring Boot ``application.properties`` file (where ``preferred_value`` is your preferred integer value):


  .. code-block:: properties

      stormpath.web.stormpathFilter.order = preferred_value

  By default, the ``StormpathFilter`` is ordered as ``Ordered.HIGHEST_PRECEDENCE``, but if you have multiple filters with that same order value, you might have to change the order of the other filters as well.


If there is anything else, please let us know!  Our `Support Team`_ is always happy to help!

Next Steps
----------

That was just a little example of how much functionality is ready right out of the box.  You get so much more, like:

* View customization with your own look and feel
* Internationalization (i18n) for all views
* Token authentication for Javascript Single Page Applications (SPAs) and mobile clients like those on iOS and Android.
* Account email verification (verify an email address is valid before enabling a user account)
* Secure CSRF protection on views with forms
* Events to react to registration, login, logout, etc
* Session-free (stateless) secure user account identification
* HTTP Basic and OAuth2 authentication
* and more!

Continue on to find out how to leverage this functionality and customize it for your own needs.

.. _sign up for Stormpath for free: https://api.stormpath.com/register
.. _Stormpath Admin Console: https://api.stormpath.com
.. _set file permissions similarly: http://msdn.microsoft.com/en-us/library/bb727008.aspx
.. _Support Team: https://support.stormpath.com