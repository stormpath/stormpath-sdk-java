.. only:: springboot

  .. _tutorial:

  Tutorial
  ========

  This tutorial will take you from zero to a Stormpath enabled application featuring Spring Boot WebMVC and Spring Security integration.

  It should take about 30 minutes from start to finish. If you are looking for a bare bones intro to using Stormpath and
  Spring Boot, check out the :doc:`quickstart`.

  If you've already gone through the quickstart, jump over to the :ref:`spring-boot-meet-stormpath` section.

  All of the code in the tutorial makes use of the ``stormpath-default-spring-boot-starter``. This starter has it all:
  Spring Boot, Spring Web MVC, Spring Security and the Thymeleaf templating engine - all integrated with Stormpath. Component features,
  such as Spring Security, can easily be disabled through the use of properties or via annotations. (You'll see an example of disabling
  Spring Security with properties in the :ref:`spring-boot-meet-stormpath` section).

  Topics:

  .. contents::
    :local:
    :depth: 1

  .. include:: stormpath-setup.txt

  For the rest of the tutorial, we will be referring to the tutorial code found in `tutorials/spring-boot <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot>`_.

  Each of the tutorial sections is completely standalone and can be used as a starting point for your own applications.

  For instance, if you wanted to build a Spring Boot WebMVC project, including Spring Security integrated with Stormpath, you
  could do the following:

  .. code-block:: bash

      mkdir MyProject
      cd MyProject
      cp -r <path to Stormpath sdk>/tutorials/spring-boot/03-spring-security-refined/* .
      mvn clean package
      java -jar target/*.jar

  .. _spring-boot-meet-stormpath:

  Spring Boot: Meet Stormpath
  ---------------------------

  Let's fire up a basic Spring Boot Web application. The code for this section can be found in `tutorials/spring-boot/00-the-basics <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/00-the-basics>`_.

  Note: This assumes you have your ``apiKey.properties`` file in the standard location: ``~/.stormpath/apiKey.properties``.

  To build and run, do this:

  .. code-block:: bash

      mvn clean package
      mvn spring-boot:run

  You should now be able to browse to `<http://localhost:${port}>`_ and see a welcome message with your Stormpath application's name.

  This application has just two code files and a properties file in it. Here's the structure:

  .. code-block:: bash
      :emphasize-lines: 9, 10

      .
      `-- src
         `-- main
             |-- java
             |   `-- com
             |       `-- stormpath
             |           `-- tutorial
             |               |-- controller
             |               |   `-- HelloController.java
             |               `-- Application.java
             `-- resources
                 `-- application.properties

  ``Application.java`` is a most basic Spring Boot application file with a ``main`` method and the ``@SpringBootApplication``
  annotation:

  .. code-block:: java
      :linenos:
      :emphasize-lines: 1

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

  For this example, we don't want Spring Security locking everything down, which is its default behavior. So, we will simply
  disable it. That's where the ``application.properties`` files comes in:

  .. code-block:: java
      :linenos:

      stormpath.spring.security.enabled = false
      security.basic.enabled = false

  The first line disables Stormpath's hooks into Spring Security and the second line disables Spring Security itself.

  Pretty simple setup, right? In the next section, we'll layer in some flow logic based on logged-in state. And then we
  will refine that to make use of all the Spring Security has to offer in making our lives easier.

  .. _some-access-controls:

  Some Access Controls
  --------------------

  I am going to say something a little radical here: Don't use the code in this section in real life! "Why have it at all?", you may ask.

  In the next section, we will talk about having access controls the "right" way by using Stormpath's integration with Spring Security.

  The purpose of this section is to demonstrate the manual labor required in "rolling your own" permissions assertion layer.
  Feel free to skip right over to the :ref:`spring-security-meet-stormpath` section.

  The code for this section can be found in `tutorials/spring-boot/01-some-access-controls <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/01-some-access-controls>`_.

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

  The official Spring Security documentation is at `http://projects.spring.io/spring-security <http://projects.spring.io/spring-security/>`_.

  Let's take a look at the additions and changes to the project. The code for this section can be found in
  `tutorials/spring-boot/02-spring-security-ftw <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/02-spring-security-ftw>`_.

  We've added a configuration file called ``SpringSecurityWebAppConfig.java``. How does Spring know it's a configuration file?
  It has the ``@Configuration`` annotation:

  .. code-block:: java
      :linenos:
      :emphasize-lines: 8

      import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

      @Configuration
      public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {
          @Override
          protected void doConfigure(HttpSecurity http) throws Exception {
              http
                  .apply(stormpath()).and()
                  .authorizeRequests()
                  .antMatchers("/").permitAll();
          }
      }

  Why have this configuration? Spring Security expects things to be, well - secured. If there is not a class that extends
  ``WebSecurityConfigurerAdapter`` in the application, Spring Security will protect *every* pathway and will provide a default
  basic authentication popup to your browser. In order to easily hook into the Stormpath Spring Security integration, simply
  ``apply`` stormpath! The call to ``stormpath()`` sets up all of the default views and hooks the Stormpath ``AuthenticationManager``
  into your application.

  Based on the ``SpringSecurityWebAppConfig`` above, we will permit access to the homepage. Any other paths will fall back
  to the default of being secured - you would be redirected to the Stormpath login page. We are going to further protect
  access to a service by requiring group membership with the ``@PreAuthorize`` annotation (as you'll see below).

  Next, we've added a service called ``HelloService.java``:

  .. code-block:: java
      :linenos:
      :emphasize-lines: 5

      @Service
      public class HelloService {
          private static final String MY_GROUP = "GROUP_HREF_HERE";

          @PreAuthorize("hasAuthority('" + MY_GROUP + "')")
          public String sayHello(Account account) {
              return "Hello, " + account.getGivenName() +
                  ". You have the required permissions to access this restricted resource.";
          }
      }

  With the Stormpath Spring Security integration, roles are tied to Stormpath Groups. A Stormpath Group has a unique
  URL (aka href) to identify it. Line 5 above ensures that only members of the group (identified by the URL you put in for the
  ``MY_GROUP`` variable) can access the ``sayHello`` method.


  NOTE: In this example, ``hasAuthority`` is used because Spring Security looks for roles with a "ROLE\_" prefix.
  For this reason, we recommend you use ``hasAuthority``. See `this issue <https://github.com/stormpath/stormpath-sdk-java/issues/325#issuecomment-220923162>`_
  for more information.

  If the authenticated user is not in the specified group, a ``403`` (forbidden) status will be returned. This will
  automatically redirect to ``/error``, which gets handled by our ``RestrictedErrorController.java``.
  This returns a nicely formatted Thymeleaf template.

  With the service defined, we can incorporate it into our controller, ``HelloController.java``:

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

  The code for this section can be found in `tutorials/spring-boot/03-spring-security-refined <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/03-spring-security-refined>`_.

  In the previous section, we hard-coded the Stormpath group href into ``HelloService``.

  This is cumbersome in a real world situation where you may have multiple environments (dev, stage, prod, etc.).
  You don't want to have to change source, recompile and deploy for a new environment or when you change a group in Stormpath.

  With a little bit of Spring magic and Stormpath's super flexible configuration mechanism, we can make this much more dynamic.

  Take a look at the updated ``HelloService`` class:

  .. code-block:: java
      :linenos:
      :emphasize-lines: 3

      @Service
      public class HelloService {
          @PreAuthorize("hasAuthority(@groups.USER)")
          public String sayHello(Account account) {
              return "Hello, " + account.getGivenName() +
                  ". You have the required permissions to access this restricted resource.";
          }
      }

  Spring has an expression language called
  `SpringEL <http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html>`_, that let's you
  reference objects and properties found in your Spring app in a number of ways.

  The ``@`` symbol is used to refer to a bean that is in the Spring context. On line 3 above, we are referencing the
  ``USER`` property of the ``groups`` bean. The implication is that there is a ``USER`` group defined somewhere that Spring
  can resolve.

  Remember, in the context of Stormpath, that must ultimately resolve to a fully qualified href that refers to a Stormpath group.

  Let's now take a look at this ``groups`` bean.

  .. code-block:: java
      :linenos:
      :emphasize-lines: 1,7

      @Component
      public class Groups {
          public final String USER;

          @Autowired
          public Groups(Environment env) {
              USER = env.getProperty("stormpath.authorized.group.user");
          }
      }

  Line 1 uses the standard Spring ``@Component`` annotation to instantiate this object and make it available as a bean in
  the application.

  By default, Spring will name the bean in context using camelcase conventions. Therefore the bean will be named ``groups``.

  We use some Spring magic on lines 5 and 6 to pass the ``Environment`` into the constructor using the `@Autowired` annotation.

  In the constructor, we set the ``USER`` variable to the value of the environment property called ``stormpath.authorized.group.user``.

  The expectation is that the ``stormpath.authorized.group.user`` environment variable will hold the fully qualified href to the
  Stormpath group that backs the ``USER`` group.

  With Spring, you can define the ``stormpath.authorized.group.user`` in the ``application.properties`` file and that property will be available
  to your app.

  Now, for the Stormpath magic. You can also have a system environment variable named ``STORMPATH_AUTHORIZED_GROUP_USER``.
  Behind the scenes, Stormpath will convert that system environment variable to a Spring environment variable named
  ``stormpath.authorized.group.user``.

  This makes it very easy to change the group ``USER`` group href in different deployment environments without having to
  reconfigure and recompile your code.

  Additionally, the ``Groups`` class is very easily extended. Let's say you want to add an ``ADMIN`` role into the mix. You
  could update the ``Groups`` class like so:

  .. code-block:: java
      :linenos:
      :emphasize-lines: 4,9

          @Component
          public class Groups {
              public final String USER;
              public final String ADMIN;

              @Autowired
              public Groups(Environment env) {
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

  Next up: An even finer grain of control using Spring Security permissions.

  .. _a-finer-grain-of-control:

  A Finer Grain of Control
  ------------------------

  The code for this section can be found in `tutorials/spring-boot/04-a-finer-grain-of-control <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/04-a-finer-grain-of-control>`_.

  So far, we've restricted access to certain methods with the `hasAuthority` clause of the `@PreAuthorize` annotation. In this
  section, we are going to look at examples that give a finer grain of control and demonstrate how Stormpath hooks into
  Spring Security.

  As before, we allow unauthenticated access to the homepage ``/`` in ``SpringSecurityWebAppConfig.java``.

  For more on ``HttpSecurity`` with Spring Security, see `its HttpSecurity documentation <http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#jc-httpsecurity>`_.

  We've added a new method to our ``HelloController``. It does not call out any other authorizaton requirements. As such,
  anyone logged in will be able to access ``/me``. Furthermore, anyone NOT logged in trying to access ``/me`` will automatically
  be redirected to the ``/login`` view.

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

  Try it out. Launch the application as before, and then browse to: ``http://localhost:${port}/me``. You will be redirected to the ``/login``
  and then after you login to a valid Stormpath Account, you will automatically be brought back to ``/me``. That's the Stormpath magic at work!

  Now, we'll look at fine grained controls using Spring Security permissions connected to Stormpath custom data.

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
          @PreAuthorize("hasAuthority(@groups.USER) and hasPermission('say', 'hello')")
          public String sayHello(Account account) {
              return "Hello, " + account.getGivenName() +
                  ". You have the required permissions to access this restricted resource.";
          }
      }

  Notice that in addition to the ``hasAuthority`` clause, we now have added: ``hasPermission('say', 'hello')``. We are saying that
  in addition to the user being in the group identified by ``@groups.USER``, they must also have the permission to say hello.

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

  .. _token-management:

  Token Management
  ----------------

  The code for this section can be found in `tutorials/spring-boot/05-token-management <https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/05-token-management>`_.

  The Java SDK supports `oauth2 <http://oauth.net/2/>`_ workflows for obtaining and interacting with access tokens and
  refresh tokens. The Token Management feature is included "out of the box" and is used via the `/oauth/token` endpoint.

  The Token Management feature is supported all through the Java SDK stack, including Servlet, Spring, Spring Boot and
  Spring Security (with and without WebMVC).

  This part of the tutorial exercises the Token Magement features using Spring Security Spring Boot WebMVC.

  There's a simple `@RestController` called `MeController` that returns information about the authenticated account.

  .. code-block:: java

      @RestController
      public class MeController {
          @RequestMapping(value="/me", produces = MediaType.APPLICATION_JSON_VALUE)
          public AccountInfo info(HttpServletRequest req) {
              // must be logged in to get here per Spring Security config
              Account account = AccountResolver.INSTANCE.getAccount(req);

              return new AccountInfo(account.getEmail(), account.getFullName(), account.getHref());
          }
      }

  In order to hit the `/me` endpoint, we'll first, we'll get an `access_token` and a `refresh_token` by hitting the
  `/oauth/token` endpoint:

  .. code-block:: bash

      curl -v -X POST \
        -H "Origin: http://localhost:${port}" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=password&username=<valid_email_address>&password=<valid_password>" \
        http://localhost:${port}/oauth/token


  Note: Make sure that the email address and password are URL encoded.

  You will get back a response that looks something like this:

  .. code-block:: javascript

      {
        "access_token":"eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI2M1laa1FBNjRTdEdUQjFhVEhlNGdPIiwiaWF0IjoxNDU0NDM4MTQ3LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU0NDQxNzQ3LCJydGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIn0.-3NNpi7-DTvl2VNCfHHFNwWVikmeCyNPy6KEu--XYjk",
        "refresh_token":"eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIiwiaWF0IjoxNDU0NDM4MTQ3LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU5NjIyMTQ3fQ.yK5twgj3-v51z4pszKXWTX9VtCbs1KxQU4vH1eXvgGo",
        "token_type":"Bearer",
        "expires_in":3600
      }


  The response includes the tokens as well as information on their type (`Bearer` in this case) and when it expires.

  We can now use the `access_token` to hit the `/me` endpoint:


  .. code-block:: bash

      curl \
        -H "Authorization: Bearer eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI2M1laa1FBNjRTdEdUQjFhVEhlNGdPIiwiaWF0IjoxNDU0NDM4MTQ3LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU0NDQxNzQ3LCJydGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIn0.-3NNpi7-DTvl2VNCfHHFNwWVikmeCyNPy6KEu--XYjk" \
        http://localhost:${port}/me

  You will get a response like this:

  .. code-block:: javascript

      {
        "href":"https://api.stormpath.com/v1/accounts/4WScMbAno3V95iiSswkjPX",
        "fullName":"Micah Silverman",
        "email":"micah@stormpath.com"
      }

  Refresh tokens are used to obtain a new access token. This is useful when you want to allow your users to have a longer
  lived session - such as in a mobile application - but you still want to maintain control over how the session is
  managed. Your application could automatically use the `refresh_token` to obtain a new `access_token` when the
  `access_token` expires. With this approach, you could revoke the user's `access_token` and they would be kicked out of
  the system sooner because the `access_token` is short lived.

  Let's use the `refresh_token` above to get a new `access_token`:

  .. code-block:: bash

      curl -v -X POST \
        -H "Origin: http://localhost:${port}" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "grant_type=refresh_token&refresh_token=eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIiwiaWF0IjoxNDU0NDM4MTQ3LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU5NjIyMTQ3fQ.yK5twgj3-v51z4pszKXWTX9VtCbs1KxQU4vH1eXvgGo" \
        http://localhost:${port}/oauth/token

  Notice that in this case the `grant_type` is `refresh_token` and that we are using the `refresh_token` that we obtained
  previously.

  You will get a response like this:

  .. code-block:: javascript

      {
        "access_token":"eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI1eDlxbWlES2U0RmlFMU02alhLSDBMIiwiaWF0IjoxNDU0NDQ0MTU1LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU0NDQ3NzU1LCJydGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIn0.J2NR7MV3OoolYImfUNiu8SCDvaQdresHTnPHgL7mO1Q",
        "refresh_token":"eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIiwiaWF0IjoxNDU0NDM4MTQ3LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU5NjIyMTQ3fQ.yK5twgj3-v51z4pszKXWTX9VtCbs1KxQU4vH1eXvgGo",
        "token_type":"Bearer",
        "expires_in":3600
      }

  While the `refresh_token` is the same, we get a new `access_token`.

  By default, when you logout, both the `access_token` and the `refresh_token` will be revoked. Let's see this in action:

  .. code-block:: bash

      curl -v \
        -H "Authorization: Bearer eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI1eDlxbWlES2U0RmlFMU02alhLSDBMIiwiaWF0IjoxNDU0NDQ0MTU1LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU0NDQ3NzU1LCJydGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIn0.J2NR7MV3OoolYImfUNiu8SCDvaQdresHTnPHgL7mO1Q" \
        http://localhost:${port}/logout

  Now, if you attempt to use the `access_token` again, you will not be granted access as it's been invalidated. You will
  need to login again.


  .. code-block:: bash

      curl \
        -H "Authorization: Bearer eyJraWQiOiJSOTJTQkhKQzFVNERBSU1HUTNNSE9HVk1YIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiI1eDlxbWlES2U0RmlFMU02alhLSDBMIiwiaWF0IjoxNDU0NDQ0MTU1LCJpc3MiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW9ucy82dkZUNEFSZldDbXVIVlY4Vmt0alRvIiwic3ViIjoiaHR0cHM6Ly9hcGkuc3Rvcm1wYXRoLmNvbS92MS9hY2NvdW50cy80V1NjTWJBbm8zVjk1aWlTc3dralBYIiwiZXhwIjoxNDU0NDQ3NzU1LCJydGkiOiI2M1laa01xMTlzYUhxTHZqSDFtbzRLIn0.J2NR7MV3OoolYImfUNiu8SCDvaQdresHTnPHgL7mO1Q" \
        http://localhost:${port}/me

  Here's the response:

  .. code-block:: javascript

      {
        "error":"invalid_client",
        "error_description":"access_token is invalid."
      }

  As you can see from the examples above, Stormpath provides powerful oauth2 Token Management out-of-the-box using the
  `/oauth/token` endpoint. There is no additional coding required on your part to make use of the Token Management
  feature.

  .. _wrapping-up:

  Wrapping Up
  -----------

  We hope this tutorial has been of value to you in learning about Stormpath's integration with Spring Security for Spring Boot
  applications.

  You can use the Stormpath Spring Security integration in contexts other than Spring Boot as well. For instance, you could
  write a REST API that makes use of Spring Security that has no web layer.

  Take a look at the `javadocs </java/apidocs>`_ as well as the `other code examples <https://github.com/stormpath/stormpath-sdk-java/tree/master/examples>`_
  for more information on all that the Stormpath Java SDK has to offer.
