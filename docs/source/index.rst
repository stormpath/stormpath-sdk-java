|project|
=========

.. only:: springboot

  The |project| allows you to use Stormpath almost instantly in any Spring Boot application. It makes it *incredibly* simple to add user management, authentication and authorization to your application.

.. only:: servlet

  The |project| is a drop-in plugin for web applications deployed to a `Servlet`_ container, like Tomcat or Jetty.  It makes it *incredibly* simple to add user management, authentication and authorization to your application.

|project| helps automate all user registration, login, authentication and authorization workflows as well as properly secure the web app.  It is very flexible - use only what you need or leverage the entire feature set.

And the best part? **You don't even need a database!**

User Guide
----------

.. only:: servlet

  This part of the documentation will show you how to get started with the Stormpath Java Servlet Plugin.  If you're new to the plugin, start here!

.. only:: springboot

  This part of the documentation will show you how to get started right away.  If you are building a Spring Boot application and are new to Stormpath, start here!

.. toctree::
  :maxdepth: 2

  about
  quickstart
  tutorial
  config
  registration
  login
  social
  authorization
  forgot-password
  logout
  request-authentication
  request
  access-control
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
