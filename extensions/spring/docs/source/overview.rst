.. _overview:

Stormpath-Spring Overview
=========================

Stormpath-Spring support is provided by 4 different dependency modules, available via any Maven-compatible dependency tool you prefer (e.g. Maven, Gradle, Ant+Ivy, etc).  You only need to specify the dependency based on your application's specific needs:

======================== =========================================== ========================================================================================================
GroupId                  ArtifactId                                  Description
======================== =========================================== ========================================================================================================
``com.stormpath.spring`` ``stormpath-spring``                        Enables Stormpath in traditional Spring (non-web) applications.
``com.stormpath.spring`` ``stormpath-spring-webmvc``                 Enables Stormpath plus default MVC controllers and views for many user management workflows in traditional Spring Web MVC applications.
``com.stormpath.spring`` ``spring-boot-starter-stormpath``           Automatically enables Stormpath in a Spring Boot (non-web) application.
``com.stormpath.spring`` ``spring-boot-starter-stormpath-thymeleaf`` Automatically enables Stormpath and default MVC controllers and views in a Spring Boot Web application.
======================== =========================================== ========================================================================================================

Configuration
-------------

Wherever possible, sane default configuration values are used to automatically configure Stormpath beans loaded by Spring.

If you wish to override any of these defaults, you can do so by overriding configuration values via Spring's property placeholder override capabilities or by re-defining a specific Stormpath bean entirely.  In most cases, setting a configuration property will be all that is necessary - most of all of the default Stormpath bean implementations are highly configurable with property values.  If you need even finer control, you may wish to re-define a Stormpath bean entirely to provide your own implementation.

The following sections indicate which dependencies your application may need as well as how to configure them.



