.. _authentication:

Authentication
==============

In a web application, you can use one of Spring Security's existing authentication filters to automatically handle authentication requests
(e.g. `UsernamePasswordAuthenticationFilter`_, `BasicAuthenticationFilter`_) and you won't have to code anything; authentication attempts
will be processed as expected by the ``StormpathAuthenticationProvider`` automatically.

However, if you want to execute the authentication attempt yourself (e.g. you have a more complex login form or UI technology) or your
application is not a web application, this is easy as well:

Create a Spring Security ``UsernamePasswordAuthenticationToken`` to wrap your user's submitted username and password and then call Spring
Security's ``AuthenticationManager``\ 's ``authenticate`` method:

.. code:: java

    //Field
    private AuthenticationManager am = new MyAuthenticationManager();

    //...
    //...

    //authenticate
    String username = //get from a form or request parameter (OVER SSL!)
    String password = //get from a form or request parameter (OVER SSL!)

    UsernamePasswordToken request = new UsernamePasswordToken(username, password);
    Authentication result = am.authenticate(request);
    SecurityContextHolder.getContext().setAuthentication(result);

    //From that point on, the user is considered to be authenticated

That's it, a standard Spring Security authentication attempt. If the authentication attempt fails, an ``AuthenticationException`` will be thrown as expected.

In Stormpath, you can add, remove and enable accounts for your application and Spring Security will reflect these changes instantly!

.. _UsernamePasswordAuthenticationFilter: http://docs.spring.io/spring-security/site/docs/current/apidocs/org/springframework/security/web/authentication/UsernamePasswordAuthenticationFilter.html
.. _BasicAuthenticationFilter: http://docs.spring.io/spring-security/site/docs/current/apidocs/org/springframework/security/web/authentication/www/BasicAuthenticationFilter.html