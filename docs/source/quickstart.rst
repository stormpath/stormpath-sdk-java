.. _setup:


Quickstart
==========

This quickstart demonstrates the fastest way to enable Stormpath in a Servlet 3.0 (or later) Java web application.  It should take about 5 minutes start to finish.  Let's get started!

Topics:

.. contents:: :local:
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

#. Change the file permissions to ensure only you can read this file. For example:

    .. code-block:: bash

     $ chmod go-rwx ~/.stormpath/apiKey.properties

#. To be safe, you might also want to prevent yourself from accidentally writing/modifying the file:

    .. code-block:: bash

     $ chmod u-w ~/.stormpath/apiKey.properties

On Windows, you can `set file permissions similarly`_.

.. _servlet-plugin-jar:

Add the Stormpath Java Servlet Plugin
--------------------------------

This step allows you to deploy Stormpath *without a single line of code or configuration*.  How amazing is that? Here's how.

Using your favorite dependency resolution build tool like Maven or Gradle, ensure your web (.war) project/module depends on stormpath-servlet-plugin-|version|.jar. For example:

**Maven**:

.. parsed-literal::

    <dependency>
        <groupId>com.stormpath.sdk</groupId>
        <artifactId>stormpath-servlet-plugin</artifactId>
        <version>\ |version|\ </version>
    </dependency>

**Gradle**:

.. parsed-literal::

    dependencies {
        compile 'com.stormpath.sdk:stormpath-servlet-plugin:\ |version|\ '
    }

Ensure that all resolved dependencies are in your web application's ``/WEB-INF/lib`` directory.

That's it!  You're ready to start using Stormpath in your web application!  Can you believe how easy that was?

Try it!
-------

If you followed the steps above you will now have fully functional registration, login, logout, forgot password workflows, api authentication and more active on your site!

Don’t believe it? Try it! Start up your web application, and we'll walk you through the basics:

* Navigate to ``/register``. You will see a registration page. Go ahead and enter some information. You should be able to create a user account. Once you’ve created a user account, you’ll be automatically logged in, then redirected back to the root URL (``/`` by default).
* Navigate to ``/logout``. You will be logged out of your account and then redirected back to ``/login`` by default.
* Navigate to ``/login``. On the lower-right, click the **Forgot Password?** link, and you'll be shown a form to enter your email.  Enter in your email address and it will send you an email.  Wait for the email and click the link and you'll be able to set a new password!

Wasn't that easy?!

.. note::

    You probably noticed that you couldn't register a user account without specifying a sufficiently strong password.  This is because, by default,
    Stormpath enforces certain password strength rules.

    If you'd like to change these password strength rules, you can do so easily by visiting the `Stormpath Admin Console`_, navigating to your your application's user ``Directory``, and then changing the "Password Strength Policy".


Any Problems?
^^^^^^^^^^^^^

Did you experience any problems with this quickstart?  It might not have worked perfectly for you if:

* you have more than one Application registered with Stormpath.  If this is the case, you'll need to configure your application's Stormpath ``href``, found in the admin console.

* your web app already uses web frameworks that make heavy use of servlet filters, like Spring or Apache Shiro. These could cause filter ordering conflicts, but the fix is easy - you'll need to manually add a few lines to your web app's ``/WEB-INF/web.xml`` file.  Ensure the following chunk is at or near the top of your filter mapping definitions:

  .. code-block:: xml

      <filter-mapping>
          <filter-name>StormpathFilter</filter-name>
          <url-pattern>/*</url-pattern>
      </filter-mapping>

* If there is anything else, please let us know!  Our `Support Team`_ is always happy to help!

Next Steps
----------

That was just a little example of how much functionality is ready right out of the box.  You get so much more, like:

* View customization with your own look and feel
* Internationalization (i18n) for all views
* Token authentication for Single Page Applications (SPAs)
* Account email verification (verify an email address is valid before enabling a user account)
* Secure CSRF protection on views with forms
* A simple security assertion/authorization framework
* Events to react to registration, login, logout, etc
* Session-free (stateless) secure user account identification
* HTTP Basic and OAuth2 authentication
* and more!

Continue on to find out how to leverage this functionality and customize it for your own needs.

.. _sign up for Stormpath for free: https://api.stormpath.com/register
.. _Stormpath Admin Console: https://api.stormpath.com
.. _set file permissions similarly: http://msdn.microsoft.com/en-us/library/bb727008.aspx
.. _Support Team: https://support.stormpath.com
