.. _tutorial:

Tutorial
========

This tutorial will take you from zero to a Stormpath enabled application featuring Spring Boot WebMVC and Spring Security integration.

It should take about 30 minutes from start to finish. If you are looking for a barebones intro to using Stormpath and
Spring Boot, check out the :doc:`quickstart`.

If you've already gone through the quickstart, jump over to the :ref:`spring-boot-meet-stormpath` section.

If you are already familiar with Spring Boot and Spring Security, jump right to the :ref:`spring-security-refined` section
to see how nicely Stormpath integrates with Spring Security.

We will be referring to the tutorial code found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot>`_.

Topics:

.. contents::
  :local:
  :depth: 1

.. include:: stormpath-spring-boot-setup.txt

.. _spring-boot-meet-stormpath:

Spring Boot: Meet Stormpath
---------------------------

Let's fire up a basic Spring Boot Web application. The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/00-the-basics>`_.

Note: This assumes you have your ``apiKey.properties`` file in the standard location: ``~/.stormpath/apiKey.properties``.

For a maven build and run, do this:

.. code-block:: bash

    mvn clean package
    mvn spring-boot:run

For a gradle build and run, do this:

.. code-block:: bash

    gradle clean build
    java -jar build/libs/00-the-basics-0.1.0.jar

You should now be able to browse to `<http://localhost:8080>`_ and see a welcome message with your Stormpath application's name.

The Stormpath library used for this example is: ``stormpath-webmvc-spring-boot-starter``. This provides basic integration with
Stormpath and Spring Boot WebMVC apps. It does NOT (yet) include the Stormpath Thymeleaf views or the Stormpath Spring Security
integration. Stay tuned for more on those integrations!

This application has just two code files in it. Here's the structure:

.. code-block:: bash
    :emphasize-lines: 9, 10

    .
    `-- src
        `-- main
            `-- java
                `-- com
                    `-- stormpath
                        `-- tutorial
                            |-- controller
                            |   `-- HelloController.java
                            `-- Application.java

``Application.java`` is a most basic Spring Boot application file with a ``main`` method:

.. code-block:: java

    @SpringBootApplication
    public class Application  {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }

``HelloController.java`` is a little more interesting:

.. code-block:: java
    :linenos:
    :emphasize-lines: 5,6

    @RestController
    public class HelloController {
        @RequestMapping("/")
        public String hello(HttpServletRequest req) {
            Application app = ApplicationResolver.INSTANCE.getApplication(req);
            return "Hello, " + app.getName();
        }
    }

Here we have our first taste of Stormpath in action. On line 5 we are getting hold of the Stormpath application and on
line 6 we are obtaining its name for display.

Pretty simple setup, right? In the next section, we'll layer in some flow logic based on logged-in state. And then we
will refine that to make use of all the Spring Security has to offer in making our lives easier.

.. _some-access-controls:

Some Access Controls
--------------------

I am going to say something a little radical here: Don't use the code in this section in real life! "Why have it at all?", you may ask.

In the next section, we will talk about having access controls the "right" way by using Stormpath's integration with Spring Security.

The purpose of this section is to demonstrate the manual labor required in "rolling your own" permissions assertion layer.
Feel free to skip right over to the :ref:`spring-security-meet-stormpath` section.

The Stormpath libraries used for this example are: ``stormpath-webmvc-spring-boot-starter`` and
``stormpath-thymeleaf-spring-boot-starter``. This provides basic integration with Stormpath and Spring Boot WebMVC apps.
Additionally, it provides a default set of thymeleaf views for common auth workflows, such as ``/login``, ``/register`` and
``/forgot`` (for dealing with forgotten passwords). It does NOT (yet) include Stormpath Spring Security integration. In the next
section, we will jump into the Stormpath Spring Security integration.

The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/01-some-access-controls>`_.

Let's say there's a restricted page that you only want authenticated users to have access to. We can determine that someone
is logged in simply by obtaining an ``Account`` object. If it's ``null``, the user is not logged in. If it resolves to an
object, then the user is logged in.

Let's take a look at the updated ``HelloController.java`` file:

.. code-block:: java
    :linenos:
    :emphasize-lines: 12

    @Controller
    public class HelloController {

        @RequestMapping("/")
        String home(HttpServletRequest req, Model model) {
            model.addAttribute("status", req.getParameter("status"));
            return "home";
        }

        @RequestMapping("/restricted")
        String restricted(HttpServletRequest req) {
            if (AccountResolver.INSTANCE.getAccount(req) != null) {
                return "restricted";
            }

            return "redirect:/login";
        }
    }

If we are able to get an account via ``AccountResolver.INSTANCE.getAccount(req)``, then we return the ``restricted``
template. Otherwise, we redirect to ``/login``.

Build and run the application as before and try it out!

The code from this section also incorporates some other cool features of
`stormpath-default-spring-boot-starter <https://github.com/stormpath/stormpath-sdk-java/tree/master/extensions/spring/boot/stormpath-default-spring-boot-starter>`_.

It makes use of Thymeleaf templates. Support for Thymeleaf is built in to ``stormpath-default-spring-boot-starter``. In
fact, the default views for login, register and forgot, change and verify are all Thymeleaf templates.

It also makes use of some settings in ``application.properties``.

By default, the next page after ``/logout`` is ``/login?status=logout``. For this example, an alternate is set using:

``stormpath.web.logout.nextUri = /?status=logout``

So, in this case, when you logout you will be redirected to the homepage with a message that will be shown on the page.

You can see this in action by running this example:

.. code-block:: bash

    mvn clean package
    mvn spring-boot:run

Now, let's take a look at using Spring Security to restrict access.

.. _spring-security-meet-stormpath:

Spring Security: Meet Stormpath
-------------------------------

With the Stormpath Spring Security integration, you can use its standard syntax to restrict access to endpoints and
methods.

The official Spring Security documentation is `here <http://projects.spring.io/spring-security/>`_.

Let's take a look at the additions and changes to the project.
The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/02-spring-security-ftw>`_.

The only Stormpath library used for this example (and those that follow) is: ``stormpath-default-spring-boot-starter``. This is the
library that we expect you will most often use. It includes the Stormpath Spring Boot, Spring Boot WebMVC, Spring Boot Spring Security, and
Spring Boot Thymeleaf template engine integrations.

We've added a configuration file called ``SpringSecurityWebAppConfig.java``. How does Spring know it's a configuration file?
It has the ``@Configuration`` annotation:

.. code-block:: java
    :linenos:

    @Configuration
    public class SpringSecurityWebAppConfig extends StormpathWebSecurityConfigurerAdapter {
        @Override
        protected void doConfigure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                .antMatchers("/**").permitAll();
        }
    }

Why have this configuration? Spring Security expects things to be, well - secured. If there is not a class that extends
``WebSecurityConfigurerAdapter`` in the application, Spring Security will protect *every* pathway and will provide a default
basic authentication popup in your browser. In order to properly hook into the Stormpath Spring Security integration, you
need to extend ``StormpathWebSecurityConfigurerAdapter`` which itself extends ``WebSecurityConfigurerAdapter``. NOTE: Please
refer to the :ref:`advanced-spring-security-integration` section for how to configure your app *without* extending
``StormpathWebSecurityConfigurerAdapter``.

Based on the ``SpringSecurityWebAppConfig`` above, we will permit access to all paths. We are going to protect access to a
service by requiring group membership with the ``@PreAuthorize`` annotation (as you'll see below).

Next, we've added a service called ``HelloService.java``:

.. code-block:: java
    :linenos:
    :emphasize-lines: 5

    @Service
    public class HelloService {
        private static final String MY_GROUP = "GROUP_HREF_HERE";

        @PreAuthorize("hasRole('" + MY_GROUP + "')")
        public String sayHello(Account account) {
            return "Hello, " + account.getGivenName() +
                ". You have the required persmissions to access this restricted resource.";
        }
    }

With the Stormpath Spring Security integration, roles are tied to Stormpath Groups. A Stormpath Group has a unique
URL (aka href) to identify it. Line 5 above ensures that only members of the group (identified by the URL you put in for the
``MY_GROUP`` variable) can access the ``sayHello`` method.

If the authenticated user is not in the specified group, a ``403`` (forbidden) status will be returned. This will
automatically redirect to ``/error``, which gets handled by our ``RestrictedErrorController.java``.
This returns a nicely formatted Thymeleaf template.

With the service defined, we can incorporate it into our controller, ``HellController.java``:

.. code-block:: java
    :linenos:
    :emphasize-lines: 4,5,15-17

    @Controller
    public class HelloController {

        @Autowired
        private HelloService helloService;

        @RequestMapping("/")
        String home(HttpServletRequest req, Model model) {
            model.addAttribute("status", req.getParameter("status"));
            return "home";
        }

        @RequestMapping("/restricted")
        String restricted(HttpServletRequest req, Model model) {
            String msg = helloService.sayHello(
                AccountResolver.INSTANCE.getAccount(req)
            );
            model.addAttribute("msg", msg);
            return "restricted";
        }

    }

Lines 4 and 5 use the Spring Autowiring capability to make the ``HelloService`` available in the ``HelloController``.

Lines 15 - 17 attempt to call the ``sayHello`` method.

Give this a spin yourself. Make sure that you replace the ``MY_GROUP`` value in ``HelloService`` with the actual URL to the group you've
setup in the Stormpath Admin Console.

.. code-block:: bash

    mvn clean package
    mvn spring-boot:run

In the next section, we'll add a small amount of code to be able to dynamically set the Group reference and make the code more readable.

.. _spring-security-refined:

Spring Security Refined
-----------------------

The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/03-spring-security-refined>`_.

In the previouse section, we hard-coded the Stormpath group href into ``HelloService``.

This is cumbersome in a real world situation where you may have multiple environments (dev, stage, prod, etc.).
You don't want to have to change source, recompile and deploy for a new environment or when you change a group in Stormpath.

With a little bit of Spring magic and Stormpath's super flexible configuration mechanism, we can make this much more dynamic.

Take a look at the updated ``HelloService`` class:

.. code-block:: java
    :linenos:
    :emphasize-lines: 3

    @Service
    public class HelloService {
        @PreAuthorize("hasRole(@roles.USER)")
        public String sayHello(Account account) {
            return "Hello, " + account.getGivenName() +
                ". You have the required permissions to access this restricted resource.";
        }
    }

Spring has an expression language called
`SpringEL <http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html>`_, that let's you
reference objects and properties found in your Spring app in a number of ways.

The ``@`` symbol is used to refer to a bean that is in the Spring context. On line 3 above, we are referencing the
``USER`` property of the ``roles`` bean. The implication is that there is a ``USER`` role defined somewhere that Spring
can resolve.

Remember, in the context of Stormpath, that must ultimately resolve to a fully qualified href that refers to a Stormpath group.

Let's now take a look at this ``roles`` bean.

.. code-block:: java
    :linenos:
    :emphasize-lines: 1,7

    @Component
    public class Roles {
        public final String USER;

        @Autowired
        public Roles(Environment env) {
            USER = env.getProperty("stormpath.authorized.group.user");
        }
    }

Line 1 uses the standard Spring ``@Component`` annotation to instantiate this object and make it available as a bean in
the application.

By default, Spring will name the bean in context using camelcase conventions. Therefore the bean will be named ``roles``.

We use some Spring magic on lines 5 and 6 to pass the ``Environment`` into the constructor.

In the constructor, we set the ``USER`` variable to the value of the environment property called ``stormpath.authorized.group.user``.

The expectation is that the ``stormpath.authorized.group.user`` environment variable will hold the fully qualified href to the
Stormpath group that backs the ``USER`` role.

With Spring, you can define the ``stormpath.authorized.group.user`` in the ``application.properties`` file and that property will be available
to your app.

Now for the Stormpath magic. You can also have a system environment variable named ``STORMPATH_AUTHORIZED_GROUP_USER``.
Behind the scenes, Stormpath will convert that system environment variable to a Spring environment variable named
``stormpath.authorized.group.user``.

This makes it very easy to change the group ``USER`` role href in different deployment environments without having to
reconfigure and recompile your code.

Additionally, the ``Roles`` class is very easily extended. Let's say you want to add an ``ADMIN`` role into the mix. You
could update the ``Roles`` class like so:

.. code-block:: java
    :linenos:
    :emphasize-lines: 4,9

        @Component
        public class Roles {
            public final String USER;
            public final String ADMIN;

            @Autowired
            public Roles(Environment env) {
                USER = env.getProperty("stormpath.authorized.group.user");
                ADMIN = env.getProperty("stormpath.authorized.group.admin");
            }
        }

You can try this out for yourself by running this example like so:

.. code-block:: bash

    mvn clean package

    STORMPATH_AUTHORIZED_GROUP_USER=<href to your group in Stormpath> \
    mvn spring-boot:run

In this example, we are also taking advantage of Stormpath's configuration mechanism. This reduces boilerplate code.

If you take a look at our ``SpringSecurityWebAppConfig``, you'll see that it's empty:

.. code-block:: java

    @Configuration
    public class SpringSecurityWebAppConfig extends StormpathWebSecurityConfigurerAdapter {}

We said earlier, that by default all paths would be protected, as is the Spring Security way. So, what's different here?

If you look at the ``application.properties`` file, you will see:

.. code-block:: properties

   stormpath.spring.security.fullyAuthenticated.enabled = false

This line disables the default behavior of protecting all paths. Without this line, our ``SpringSecurityWebAppConfig`` would
need to look like it did in previous examples, if we did want to make it so that all paths are permitted:

.. code-block:: java

    @Configuration
    public class SpringSecurityWebAppConfig extends StormpathWebSecurityConfigurerAdapter {
        @Override
        protected void doConfigure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                .antMatchers("/**").permitAll();
        }
    }

The one line of configuration in ``application.properties`` saved us six lines of boilerplate java code.

.. _a-finer-grain-of-control:

A Finer Grain of Control
------------------------

The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/04-a-finer-grain-of-control>`_.

So far, everything we've done with Spring Security has been through the `@PreAuthorize` annotation. In this section, we are going to look at examples
that give a finer grain of control and demonstrate how Stormpath hooks into Spring Security.

As documented by the Spring team, it's common to have a ``WebSecurityConfigurerAdapter`` and to use a fluent interface to build security rules.

Stormpath hooks into that architecture to enable you to build security rules while protecting access to the built-in Stormpath authentication flows.

Let's take a look at the new file in the example, ``SpringSecurityWebAppConfig``:

.. code-block:: java
    :linenos:
    :emphasize-lines: 2,4

    @Configuration
    public class SpringSecurityWebAppConfig extends StormpathWebSecurityConfigurerAdapter {

        @Override
        protected void doConfigure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                .antMatchers("/me").fullyAuthenticated()
                .antMatchers("/**").permitAll();
        }

    }

Notice on line 2, we are extending ``StormpathWebSecurityConfigurerAdapter`` which itself extends ``WebSecurityConfigurerAdapter``.

The ``configure`` method of ``StormpathWebSecurityConfigurerAdapter`` does some housekeeping to ensure that all of the Stormpath
views (such as /login) remain available and then it calls your ``doConfigure`` method.

In this case, we are expressing that anyone trying to get to ``/me`` should be fully authenticated. And, we are saying that
access to any other path will be permitted.

For more on ``HttpSecurity`` with Spring Security, look `here <http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-httpsecurity>`_.

The new method to handle ``me`` requests in our ``HelloController`` does not call out any other authorizaton requirements. Anyone logged in will be able to
access ``/me``. Furthermore, anyone NOT logged in trying to access ``/me`` will automatically be redirected to the ``/login`` view.

.. code-block:: java
    :linenos:

    @Controller
    public class HelloController {

        ...

        @RequestMapping("/me")
        String me() {
            return "me";
        }

        ...
    }

Try it out. Launch the application as before, and then browse to: ``http://localhost:8080/me``. You will be redirected to the ``/login``
and then after you login to a valid Stormpath Account, you will automatically be brought back to ``/me``. That's the StormPath magic at work!

The last thing we'll look at here is fine grained controls using Spring Security permissions connected to Stormpath custom data.

Every first class object in Stormpath can have custom data associated with it. For instance, you can have custom data at the Group level as well
as at the Account level.

In general, custom data can be completely arbitrary JSON data of any kind. There's a special key that can be used at the top level of custom data
that the Spring Security ``hasPermission`` clause will respond to. Note: this does not preclude you in any way from having other custom data.

Let's take a look at the updated ``HelloService``:

.. code-block:: java
    :linenos:
    :emphasize-lines: 3

    @Service
    public class HelloService {
        @PreAuthorize("hasRole(@roles.USER) and hasPermission('say', 'hello')")
        public String sayHello(Account account) {
            return "Hello, " + account.getGivenName() +
                ". You have the required permissions to access this restricted resource.";
        }
    }

Notice that in addition to the ``hasRole`` clause, we now have added: ``hasPermission('say', 'hello')``. We are saying that
in addition to the user being in the group identified by ``@roles.USER``, they must also have the permission to say hello.

This is connected to Stormpath by having the following custom data present:

.. code-block:: javascript

    {
        "springSecurityPermissions": ["say:hello"]
    }

``springSecurityPermissions`` is the special key we talked about above. Its value is an array of strings each of which
conforms to the following format: ``target:permission``. In this case, the target is *say* and the permission is *hello*.

Note that you can put this custom data at the group level, in which case it would apply to everyone in the group or you
could have it present for individual accounts. As long as the condition declared by the ``hasPermission`` clause is met
(as well as the user belonging to the right group), the user will have access to the ``sayHello`` method.

You could have many specific permissions attached to the ``say`` target. If you wanted to grant a user or group access to
any permission in the ``say`` target, you could take advantage of Stormpath's wildcard permissions syntax. That looks like:

.. code-block:: javascript

    {
        "springSecurityPermissions": ["say:*"]
    }

If you had one method protected by: ``hasPermission('say', 'hello')`` and another method protected by:
``hasPermission('say', 'goodbye')``, the above ``customData`` would grant the user or group entry into both methods.

Try this out for yourself. In the Stormpath Admin Console, add two user accounts to the group you've been using in the
previous examples.

.. image:: /_static/group.png

Add the custom data to one of the users, but not the other.

.. image:: /_static/user-custom-data.png

.. image:: /_static/user-no-custom-data.png

You will find that, although both users are in the right group, only the one with the ``springSecurityPermissions`` custom data
will be able to get to the ``/restricted`` page.

.. _advanced-spring-security-integration:

Advanced Spring Security Integration
------------------------------------

There are times when you will want to hook into the Stormpath Spring Security Integration at a deeper level. Or, perhaps you
have a scenario where you are already extending a class from another library and do not have the option of extending ``StormpathWebSecurityConfigurerAdapter``.

In these situations, your ``@Configuration`` class can interact with the ``StormpathWebSecurityConfigurer`` directly. Take a
look what this looks like:

.. code-block:: java
    :linenos:
    :emphasize-lines: 5,9,18,23

    @Configuration
    public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        StormpathWebSecurityConfigurer stormpathWebSecurityConfigurer;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            stormpathWebSecurityConfigurer.configure(http);
            http
                .authorizeRequests()
                .antMatchers("/restricted").fullyAuthenticated()
                .antMatchers("/**").permitAll();
        }

        @Override
        public final void configure(AuthenticationManagerBuilder auth) throws Exception {
            stormpathWebSecurityConfigurer.configure(auth);
        }

        @Override
        public final void configure(WebSecurity web) throws Exception {
            stormpathWebSecurityConfigurer.configure(web);
        }
    }

On lines 4 - 5, we use Spring's ``@Autowired`` capability to get access to the ``StormpathWebSecurityConfigurer``.

It's critical that you implement each of the three ``configure`` methods above and that you call Stormpath's ``configure`` method
within before you call any methods for your own configuration.

Notice above that on line 9 we call ``stormpathWebSecurityConfigurer.configure(http)`` and only then do we call methods
on the ``HttpSecurity`` object.

Again, in most situations, you will want to simply extend the ``StormpathWebSecurityConfigurerAdapter``. The above code
would look like this:

.. code-block:: java
    :linenos:

    @Configuration
    public class SpringSecurityWebAppConfig extends StormpathWebSecurityConfigurerAdapter {

        @Override
        protected void doConfigure(HttpSecurity http) throws Exception {
            http
                .authorizeRequests()
                .antMatchers("/restricted").fullyAuthenticated()
                .antMatchers("/**").permitAll();
        }
    }

In this case, you are overriding the ``doConfigure`` method. Stormpath's ``StormpathWebSecurityConfigurerAdapter.configure`` method
does the housekeeping it needs to for the Stormpath configuration and then calls the ``doConfigure`` method that you've
overridden.

.. _wrapping-up:

Wrapping Up
-----------

We hope this tutorial has been of value to you in learning about Stormpath's integration with Spring Security for Spring Boot
applications.

You can use the Stormpath Spring Security integration in contexts other than Spring Boot as well. For instance, you could
write a REST API that makes use of Spring Security that has no web layer.

Take a look at the `javadocs </java/apidocs>`_ for a more in depth look at Spring Security as well as the other
code examples `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/examples>`_.