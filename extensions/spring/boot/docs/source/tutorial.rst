.. _tutorial:

Tutorial
========

This tutorial will take from you zero to a Stormpath enabled Spring Boot, Spring Security, Spring WebMVC application.

It should take about 30 minutes start to finish. If you are looking for a barebones intro to using Stormpath and
Spring Boot, check out the :doc:`quickstart`.

If you are already familiar with Spring Boot and Spring Security, jump right to the :ref:`spring-security-refined` section
to see how Stormpath integrates with Spring Security.

We will be referring to the tutorial code found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot-default>`_.

Topics:

.. contents::
  :local:
  :depth: 1

.. include:: stormpath-spring-boot-setup.txt

.. _spring-boot-meet-stormpath:

Spring Boot: Meet Stormpath
---------------------------

Let's fire up a basic Spring Boot Web application. The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot-default/00-the-basics>`_.

For a maven build and run, do this:

.. code-block:: bash

    mvn clean package

    STORMPATH_API_KEY_FILE=<path to your key file> \
    java -jar target/stormpath-sdk-tutorials-spring-boot-default-the-basics-1.0.RC5-SNAPSHOT.jar

For a gradle build and run, do this:

.. code-block:: bash

    gradle clean build

    STORMPATH_API_KEY_FILE=<path to your key file> \
    java -jar build/libs/00-the-basics-0.1.0.jar

You should now be able to browse to `<http://localhost:8080>`_ and see a welcome message with your Stormpath application's name.

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
    :emphasize-lines: 7,8

    @RestController
    public class HelloController {
        @RequestMapping("/")
        public String hello(HttpServletRequest req) {
            String greeting = "World!";

            Application app = ApplicationResolver.INSTANCE.getApplication(req);
            if (app != null) { greeting = app.getName(); }

            return "Hello, " + greeting;
        }
    }

Here we have our first taste of Stormpath in action. On line 7 we are getting hold of the Stormpath application and on
line 8 we are obtaining its name for display.

Pretty simple setup, right? In the next section, we'll layer in some flow logic based on logged-in state. And then we
will refine that to make use of all the Spring Security has to offer in making our lives easier.

.. _some-access-controls:

Some Access Controls
--------------------

I am going to say something a little radical here: Don't use the code in this section in real life! "Why have it at all?", you may ask.

In the next section, we will talk about having access controls the "right" way by using Stormpath's integration with Spring Security.

The purpose of this section is to demonstrate the manual labor required in "rolling your own" permissions assertion layer.
Feel free to skip right over to the :ref:`spring-security-meet-stormpath` section.

The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot-default/01-some-access-controls>`_.

Let's say there's a restricted page that you only want authenticated users to have access to. We can determine than someone
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

Build and run the application as before an try it out!

The code from this section also incorporates some other cool features of ``stormpath-default-spring-boot-starter``.

It makes use of Thymeleaf templates. Support for Thymeleaf is built in to ``stormpath-default-spring-boot-starter``. In
fact, the default views for login, register and forgot, change and verify are all Thymeleaf templates.

It also makes use of some settings in ``application.properties``.

By default, the next page after ``/logout`` is ``/login?status=logout``. For this example, an alter is set using:

``stormpath.web.logout.nextUri = /?status=logout``

So, in this case, when you logout you will be redirected to the homepage with a message that will be shown on the page.

You can see this in action by running this example:

.. code-block:: bash

    gradle clean build

    STORMPATH_API_KEY_FILE=<path to your key file> \
    STORMPATH_AUTHORIZED_GROUP_USER=<href to your group in Stormpath> \
    java -jar build/libs/01-some-access-controls-0.1.0.jar

Now, let's take a look at using Spring Security to restrict access.

.. _spring-security-meet-stormpath:

Spring Security: Meet Stormpath
-------------------------------

With the Stormpath Spring Security integration, you can use the standard syntax to restrict access to endpoints and
methods.

The official Spring Security documentation is `here <http://projects.spring.io/spring-security/>`_.

Let's take a look at the additions and changes to the project.
The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot-default/02-spring-security-ftw>`_.

We've added a service called ``HelloService.java``:

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
URL to identify it. Line 5 above ensures that only members of the group (identified by the URL you put in for the
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

    gradle clean build

    STORMPATH_API_KEY_FILE=<path to your key file> \
    STORMPATH_AUTHORIZED_GROUP_USER=<href to your group in Stormpath> \
    java -jar build/libs/02-spring-security-ftw-0.1.0.jar


In the next section, we'll add a small amount of code to be able to dynamically set the Group reference and make the code more readable.

.. _spring-security-refined:

Spring Security Refined
-----------------------

The code for this section can be found `here <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot-default/03-spring-security-refined>`_.

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
        @PreAuthorize("hasRole(@Roles.USER)")
        public String sayHello(Account account) {
            return "Hello, " + account.getGivenName() +
                ". You have the required permissions to access this restricted resource.";
        }
    }

Spring has an expression language called SpringEL, that let's you reference objects and properties found in your Spring
app in a number of ways.

The ``@`` symbol is used to refer to a bean that is in the Spring context. On line 3 above, we are referencing the
``USER`` property of the ``Roles`` bean. The implication is that there is a ``USER`` role defined somewhere that Spring
can resolve.

Remember, in the context of Stormpath, that must ultimately resolve to a fully qualified href that refers to a Stormpath group.

Let's now take a look at this ``Roles`` bean.

.. code-block:: java
    :linenos:
    :emphasize-lines: 1,3,5,7

    @Component("Roles")
    public class Roles {
        public final String USER;

        @Autowired
        public Roles(Environment env) {
            USER = env.getProperty("stormpath.authorized.group.user");
        }
    }

Line 1 uses the standard Spring ``@Component`` annotation to instantiate this object and make it available as a bean in
the application.

Notice that it explicitly calls out the bean name: ``@Component("Roles")``. This is because, by default, Spring will name
a bean in context using camelcase conventions. Without the explicit name, the bean would be named ``roles``.

We use some Spring magic on line 4 to pass the ``Environment`` into the constructor.

In the constructor, we set the ``USER`` variable to the value of the environment property called ``stormpath.authorized.group.user``.

The expectation is that the ``stormpath.authorized.group.user`` environment variable will hold the fully qualified href to the
Stormpath group that backs the ``USER`` role.

Ok - now for the Stormpath magic. You can define the ``stormpath.authorized.group.user`` in the ``application.properties`` file.

You can also have a system environment variable named ``STORMPATH_AUTHORIZED_GROUP_USER``. Behind the scenes, Stormpath will
convert that system environment variable to a Spring environment variable name ``stormpath.authorized.group.user``.

This makes it very easy to change the group ``USER`` role href in different deployment environments without having to
reconfigure and recompile your code.

Additionally, the ``Roles`` class is very easily extended. Let's say you want to add an ``ADMIN`` role into the mix. You
could update the ``Roles`` class like so:

.. code-block:: java
    :linenos:
        :emphasize-lines: 4,9

        @Component("Roles")
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

    gradle clean build

    STORMPATH_API_KEY_FILE=<path to your key file> \
    STORMPATH_AUTHORIZED_GROUP_USER=<href to your group in Stormpath> \
    java -jar build/libs/03-spring-security-refined-0.1.0.jar

.. _a-finer-grain-of-control:

A Finer Grain of Control
------------------------


