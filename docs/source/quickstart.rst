.. _setup:

Quickstart
==========

#if( $servlet )

This quickstart demonstrates the fastest way to enable Stormpath in a Servlet 3.0 (or later) Java web application.
It should take about 5 minutes start to finish.  Let's get started!

#elseif( $sczuul )

This quickstart demonstrates the fastest way to enable Stormpath in a Spring Cloud Zuul gateway / reverse proxy.
It should take about 5 minutes start to finish.  Let's get started!

#else

This quickstart demonstrates the fastest way to enable Stormpath in a Spring Boot web application.  It should take
about 5 minutes start to finish.  Let's get started!

#end

Topics:

.. contents::
     :local:
     :depth: 2

.. _get-api-key:

.. include:: stormpath-setup.txt

Add the |project|
-----------------

.. _dependency-jar:

#if( $servlet )

This step allows you to use Stormpath *without a single line of code or configuration*.  How amazing is that?

Using your favorite dependency resolution build tool like Maven or Gradle, ensure your web (.war) project/module depends on ``${maven.project.artifactId}-${maven.project.version}.jar``. For example:

**Maven**:

.. code-block:: xml

    <dependency>
        <groupId>${maven.project.groupId}</groupId>
        <artifactId>${maven.project.artifactId}</artifactId>
        <version>${maven.project.version}</version>
    </dependency>

**Gradle**:

.. code-block:: groovy

    dependencies {
        compile '${maven.project.groupId}:${maven.project.artifactId}:${maven.project.version}'
    }

Ensure that all resolved dependencies are in your web application's ``/WEB-INF/lib`` directory.

That's it!  You're ready to start using Stormpath in your web application!  Can you believe how easy that was?

#elseif( $springboot )

This step allows you to enable Stormpath in a Spring Boot web app with *very minimal* configuration.
It includes Stormpath Spring Security, Stormpath Spring WebMVC and Stormpath Thymeleaf templates.

Using your favorite dependency resolution build tool like Maven or Gradle, add the ``${maven.project.artifactId}-${maven.project.version}.jar`` to your project dependencies. For example:

**Maven**:

.. code-block:: xml

    <dependency>
        <groupId>${maven.project.groupId}</groupId>
        <artifactId>${maven.project.artifactId}</artifactId>
        <version>${maven.project.version}</version>
    </dependency>

**Gradle**:

.. code-block:: groovy

    dependencies {
        compile '${maven.project.groupId}:${maven.project.artifactId}:${maven.project.version}'
    }

#elseif( $sczuul )

This step allows you to enable Stormpath in a Spring Cloud Zuul ${apptype} project with *very minimal* configuration.
It includes Stormpath Spring Security, Stormpath Spring WebMVC and Stormpath Thymeleaf templates.

Using your favorite dependency resolution build tool like Maven or Gradle, add the ``${maven.project.artifactId}-${maven.project.version}.jar`` to your project dependencies. For example:

**Maven**:

.. code-block:: xml

    <dependency>
        <groupId>${maven.project.groupId}</groupId>
        <artifactId>${maven.project.artifactId}</artifactId>
        <version>${maven.project.version}</version>
    </dependency>

**Gradle**:

.. code-block:: groovy

    dependencies {
        compile '${maven.project.groupId}:${maven.project.artifactId}:${maven.project.version}'
    }

That's it!  Stormpath is now enabled in your Spring Cloud Zuul gateway!

#end

#if( !$servlet )

Spring Security
^^^^^^^^^^^^^^^

The |project| assumes Spring Security will be used to secure your ${apptype} by default.  To ensure this
works correctly, you will need a Spring Security configuration class and apply the ``stormpath()`` hook:

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

.. code-block:: yaml

   # disable Stormpath's Spring Security support:
   stormpath:
     spring:
       security:
         enabled: false

   # disable Spring Security entirely:
   security:
     basic:
       enabled: false

#end

#if( $springboot )
That's it!  You're ready to start using Stormpath in your Spring Boot web application!
#end

#if( $sczuul )

Configure Zuul
--------------

.. include:: config-zuul.rst.inc

#end

Try it!
-------

If you followed the steps above you will now have fully functional registration, login, logout, forgot password workflows, api authentication and more active on your site!

#if( !$sczuul )

Don’t believe it? Try it! Start up your web application, and we'll walk you through the basics:

* Navigate to ``/register``. You will see a registration page. Go ahead and enter some information. You should be able to create a user account. Once you’ve created a user account, you’ll be automatically logged in, then redirected back to the root URL (``/`` by default).
* Submit a ``POST`` (not a ``GET``) to ``/logout``. You will be logged out of your account and then redirected back to ``/login`` by default.  You can learn more about ``POST`` for logout on the :ref:`Logout <logout>` page.
* After logging out, navigate to ``/login``. On the lower-right, click the **Forgot Password?** link, and you'll be shown a form to enter your email.  Enter in your email address and it will send you an email.  Wait for the email and click the link and you'll be able to set a new password!

#else

Don’t believe it? Try it!

#. Start up your Stormpath-enabled Spring Cloud Gateway project, which will run on port 8000 ('localhost eight thousand')
#. Start up your web application that 'sits behind' the gateway, running on port 8080 ('localhost eighty eighty')

Then:

* Navigate to ``http://localhost:8000/register``. You will see a registration page. Go ahead and enter some information. You should be able to create a user account. Once you’ve created a user account, you’ll be automatically logged in, then redirected back to the root URL (``/`` by default).
* Submit a ``POST`` (not a ``GET``) to ``http://localhost:8000/logout``. You will be logged out of your account and then redirected back to ``/login`` by default.  You can learn more about ``POST`` for logout on the :ref:`Logout <logout>` page.
* After logging out, navigate to ``http://localhost:8000/login``. On the lower-right, click the **Forgot Password?** link, and you'll be shown a form to enter your email.  Enter in your email address and it will send you an email.  Wait for the email and click the link and you'll be able to set a new password!

#end

Wasn't that easy?!

.. note::

  You probably noticed that you couldn't register a user account without specifying a sufficiently strong password.
  This is because, by default, Stormpath enforces certain password strength rules.

  If you'd like to change these password strength rules, you can do so easily by visiting the `Stormpath Admin Console`_,
  navigating to your your application's user ``Directory``, and then changing the "Password Strength Policy".

Any Problems?
^^^^^^^^^^^^^

Did you experience any problems with this quickstart?  It might not have worked perfectly for you if:

#if( $servlet )

* you have more than one Application registered with Stormpath.  If this is the case, you'll need to configure your
  web application's Stormpath ``href``, found in the admin console. Once you get the ``href``, add the following to
  your ``stormpath.properties`` file (where ``YOUR_APPLICATION_ID`` is your web app's actual Stormpath
  Application ID):

  .. code-block:: properties

     stormpath.application.href = https://api.stormpath.com/v1/applications/YOUR_APPLICATION_ID

* your web app already uses web frameworks that make heavy use of servlet filters, like Spring or Apache Shiro.
  These could cause filter ordering conflicts, but the fix is easy - you'll need to manually add a few lines to your
  web app's ``/WEB-INF/web.xml`` file.  Ensure the following chunk is at or near the top of your filter mapping
  definitions:

  .. code-block:: xml

     <filter-mapping>
         <filter-name>StormpathFilter</filter-name>
         <url-pattern>/*</url-pattern>
     </filter-mapping>

#else

* you have more than one Application registered with Stormpath.  If this is the case, you'll need to configure your
  ${apptype}'s Stormpath ``href``, found in the admin console. Once you get the ``href``, add the following to your
  ${apptype}'s Spring Boot application properties or yaml file (where ``YOUR_APPLICATION_ID`` is your ${apptype}'s
  actual Stormpath Application ID):

  .. code-block:: yaml

     stormpath:
       application:
         href: 'https://api.stormpath.com/v1/applications/YOUR_APPLICATION_ID'

* your ${apptype} already uses web frameworks that make heavy use of servlet filters, like Spring Security or
  Apache Shiro. These could cause filter ordering conflicts, but the fix is easy - you just need to specify the
  specific order where you want the Stormpath filter relative to other filters.  You do this by adding the following
  to your ${apptype}'s Spring Boot application properties (where ``preferred_value`` is your preferred integer value):

  .. code-block:: yaml

     stormpath:
       web:
         stormpathFilter:
           order: preferred_value #must be an integer

  By default, the ``StormpathFilter`` is ordered as ``Ordered.HIGHEST_PRECEDENCE``, but if you have multiple
  filters with that same order value, you might have to change the order of the other filters as well.


* you're using the ``spring-boot-starter-parent`` as a ``parent`` and you are getting errors related to Spring
  Security. The Stormpath starter relies on Spring Security 4.1.x. The current release at the time of this writing
  of the ``spring-boot-starter-parent`` is 1.4.0 and it also relies on Spring Security 4.1.x. Prior versions of
  the ``spring-boot-starter-parent`` rely on Spring Security 3.2.x. Our first recommendation is to use the latest
  version of the ``spring-boot-starter-parent``. However, if you must use earlier versions, there is a simple
  solution to this, which is to override the Spring Security version in your ``pom.xml``

  .. code-block:: xml
     :emphasize-lines: 18

     <?xml version="1.0" encoding="UTF-8"?>
     <project xmlns="http://maven.apache.org/POM/4.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                                  http://maven.apache.org/xsd/maven-4.0.0.xsd">
         <modelVersion>4.0.0</modelVersion>

         <parent>
             <groupId>org.springframework.boot</groupId>
             <artifactId>spring-boot-starter-parent</artifactId>
             <version>1.4.0.RELEASE</version>
             <relativePath/> <!-- lookup parent from repository -->
         </parent>

         <properties>
             <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
             <java.version>1.8</java.version>
             <spring-security.version>4.1.2.RELEASE</spring-security.version>
         </properties>

         <dependencies>

             <dependency>
                 <groupId>${maven.project.groupId}</groupId>
                 <artifactId>${maven.project.artifactId}</artifactId>
                 <version>${maven.project.version}</version>
             </dependency>

             <!-- Other dependencies... -->

         </dependencies>

     </project>

#end

If there is anything else, please let us know!  Our `Support Team`_ is always happy to help!

Next Steps
----------

That was just a little example of how much functionality is ready right out of the box.  You get so much more, like:

* View customization with your own look and feel
* Internationalization (i18n) for all views
* Token authentication for Javascript Single Page Applications (SPAs) and mobile clients like those on iOS and Android.
* Account email verification (verify an email address is valid before enabling a user account)
* Secure CSRF protection on views with forms
#if( $servlet )* A simple security assertion/authorization framework
#end
* Events to react to registration, login, logout, etc
* Session-free (stateless) secure user account identification
* HTTP Basic and OAuth2 authentication
* and more!

#if( $springboot )

Dig in to our `examples`_ to see more Stormpath Spring Boot in action.

#end

Continue on to find out how to leverage this functionality and customize it for your own needs.

.. _sign up for Stormpath for free: https://api.stormpath.com/register
.. _Stormpath Admin Console: https://api.stormpath.com
.. _set file permissions similarly: http://msdn.microsoft.com/en-us/library/bb727008.aspx
.. _Support Team: https://support.stormpath.com
