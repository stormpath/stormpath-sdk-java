.. _setup:

Quickstart
==========

This quickstart demonstrates the fastest way to enable Stormpath in a Spring Boot web application.  It should take about 5 minutes start to finish.  Let's get started!

Topics:

.. contents::
     :local:
     :depth: 1

.. include:: stormpath-spring-boot-setup.txt

.. _dependency-jar:

Add the Stormpath Default Spring Boot Starter
---------------------------------------------

This step allows you to enable Stormpath in a Spring Boot web app with *very minimal* configuration.
It includes Stormpath Spring Security, Stormpath Spring WebMVC and Stormpath Thymeleaf templates. How amazing is that? Here's how.

Using your favorite dependency resolution build tool like Maven or Gradle, add the stormpath-default-spring-boot-starter-|version|.jar to your project dependencies. For example:

**Maven**:

.. parsed-literal::

    <dependency>
        <groupId>com.stormpath.spring</groupId>
        <artifactId>stormpath-default-spring-boot-starter</artifactId>
        <version>\ |version|\ </version>
    </dependency>

**Gradle**:

.. parsed-literal::

    dependencies {
        compile 'com.stormpath.spring:stormpath-default-spring-boot-starter:\ |version|\ '
    }


Spring Security
^^^^^^^^^^^^^^^

The Stormpath Default Spring Boot starter assumes Spring Security by default.  But to ensure they work
together, you will need a Spring Security configuration class and apply the ``stormpath()`` hook:

.. code-block:: java
    :emphasize-lines: 7

    import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

    @Configuration
    public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.apply(stormpath());
        }
    }

Without this, you will see a browser popup prompting for authentication, which is the default basic authentication behavior for Spring Security.

Also, by default, all paths are locked down with Spring Security. Stormpath's Spring Security integration follows this idiomatic behavior.

Disabling Spring Security
"""""""""""""""""""""""""

If you do not want to use Spring Security, do not add the ``SpringSecurityWebAppConfig`` class shown above (or just comment out the ``http.apply(stormpath())`` line if you do not want to remove the class).  Also set the following two config properties:

  .. code-block:: properties

      # disable Stormpath's Spring Security support:
      stormpath.spring.security.enabled = false

      # disable Spring Security entirely:
      security.basic.enabled = false

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

* you're using the ``spring-boot-starter-parent`` as a ``parent`` and you are getting errors related to Spring Security. The ``stormpath-default-spring-boot-starter`` relies on Spring Security 4.0.x. The current release of the ``spring-boot-starter-parent`` is 1.3.0 and it also relies on Spring Security 4.0.x. Prior versions of the ``spring-boot-starter-parent`` rely on Spring Security 3.2.x. Our first recommendation is to use the latest version of the ``spring-boot-starter-parent``. However, if you must use earlier versions, there is a simple solution to this, which is to override the Spring Security version in your ``pom.xml``

  .. code-block:: xml
      :emphasize-lines: 15

      <?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
          <modelVersion>4.0.0</modelVersion>
          ...
          <parent>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-parent</artifactId>
              <version>1.3.3.RELEASE</version>
              <relativePath/> <!-- lookup parent from repository -->
          </parent>

          <properties>
              <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
              <java.version>1.8</java.version>
              <spring-security.version>4.0.3.RELEASE</spring-security.version>
          </properties>

          <dependencies>
              ...
              <dependency>
                  <groupId>com.stormpath.spring</groupId>
                  <artifactId>stormpath-default-spring-boot-starter</artifactId>
              </dependency>
              ...
          </dependencies>
          ...
      </project>

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

Dig in to our `examples`_ to see more Stormpath Spring Boot in action.

Continue on to find out how to leverage this functionality and customize it for your own needs.
