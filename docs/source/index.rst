|project|
=========

#if( $sczuul )

.. include:: about_sczuul.rst

#elseif( $springboot )

The |project| allows you to use Stormpath almost instantly in any Spring Boot application. It makes it *incredibly* simple to add user management, authentication and authorization to your application.

#elseif( $spring )

The |project| allows you to use Stormpath almost instantly in any Spring application. It makes it *incredibly* simple to add user management, authentication and authorization to your application.

#else

The |project| is a drop-in plugin for web applications deployed to a `Servlet`_ container, like Tomcat or Jetty.  It makes it *incredibly* simple to add user management, authentication and authorization to your application.

#end

And the best part? **You don't even need a database!**

User Guide
----------

#if( $servlet )

This part of the documentation will show you how to get started with the Stormpath Java Servlet Plugin. If you're new to the plugin, start here!

#elseif( $springboot )

This part of the documentation will show you how to get started right away. If you are building a Spring Boot application and are new to Stormpath, start here!

#elseif( $spring )

This part of the documentation will show you how to get started right away. If you are building a Spring application and are new to Stormpath, start here!

#end

.. toctree::
  :maxdepth: 2

  about
  quickstart
  #if($spring or $springboot)tutorial#end

  config
  registration
  login
  social
  authorization
  forgot-password
  logout
  request-authentication
  request
  #if($sczuul)forwarded-request#end

  #if($servlet)access-control#end

  i18n
  events
  caching
  idsite
  views
  appendix

Open Source License
-------------------

The |project| and the Stormpath SDK for Java are made available under the business-friendly `Apache License, Version 2.0`_.

.. _Servlet: https://jcp.org/aboutJava/communityprocess/final/jsr315/
.. _Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0.html
