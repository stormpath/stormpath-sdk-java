[![Build Status](https://api.travis-ci.org/stormpath/stormpath-sdk-java.png?branch=okta)](https://travis-ci.org/stormpath/stormpath-sdk-java)

# Stormpath is Joining Okta

We are incredibly excited to announce that [Stormpath is joining forces with Okta](https://stormpath.com/blog/stormpaths-new-path?utm_source=github&utm_medium=readme&utm-campaign=okta-announcement). Please visit [the Migration FAQs](https://stormpath.com/oktaplusstormpath?utm_source=github&utm_medium=readme&utm-campaign=okta-announcement) for a detailed look at what this means for Stormpath users.

We're available to answer all questions at [support@stormpath.com](mailto:support@stormpath.com).

## Okta Support Branch

The aim of this branch is to port the Stormpath Java integrations (Spring, Spring-Boot, and Servlet) to work with Okta's API instead of Stormpath.

Take a look at the [Getting Started With Okta](OktaGettingStarted.md) guide for instructions on how to get started.

Grab the `2.0.2-okta` release from [Central](https://search.maven.org/#search%7Cga%7C1%7Cstormpath%20), or build it yourself with Apache Maven: `mvn install`

You will also need to set the following properties (these can be set the same way as your [existing Stormpath configuration properties](https://docs.stormpath.com/java/servlet-plugin/config.html#id10)).

| Key | Description |
|-----|-------------|
| okta.api.token | [An Okta API key](http://developer.okta.com/docs/api/getting_started/getting_a_token.html) |
| okta.application.id | You find your Application's id with an [API call](http://developer.okta.com/docs/api/resources/apps.html), or by opening your 'application' config in the Okta Admin console and grab the ID from your browsers URL |
| stormpath.client.baseUrl | The base url of your Okta organization, for example in a preview enviornment this would be something like: https://dev-123456.oktapreview.com |

# Stormpath Java SDK #

*An advanced, reliable and easy-to-use user management API, built by Java security experts*

[Stormpath](https://stormpath.com) is a complete user management API.  This
library gives your Java app access to all of Stormpath's features:

- Robust authentication and authorization.
- Schemaless user data and profiles.
- A hosted login subdomain, for easy Single Sign-On across your apps.
- Social login with Facebook and Google OAuth.
- Secure API key authentication for your service.
- Servlet support for Java web applications.

If you have feedback about this library, please get in touch and share your
thoughts! support@stormpath.com

## Documentation

Stormpath offers deep documentation and support for Java.

- [Official Stormpath Docs](http://docs.stormpath.com/)
- [API Docs for Java](https://docs.stormpath.com/java/apidocs/)
- [Stormpath Java Product Guide](https://docs.stormpath.com/java/product-guide/)
- [Stormpath Servlet Plugin for Java Webapps](https://docs.stormpath.com/java/servlet-plugin/)
- [Spring Boot Stormpath Web Starter](https://docs.stormpath.com/java/spring-boot-web/)

Please email support@stormpath.com with any errors or issues with the documentation.

## Links

Below are some resources you might find useful!

- [Java Quickstart](http://docs.stormpath.com/java/quickstart/) to get started.
- [Java WebApp Quickstart](https://docs.stormpath.com/java/servlet-plugin/quickstart.html)
- [Spring Boot Webapp Quickstart](https://docs.stormpath.com/java/spring-boot-web/quickstart.html)
- [Stormpath Java SDK](https://github.com/stormpath/stormpath-sdk-java)
- [Stormpath Servlet Plugin for Java Webapps](https://docs.stormpath.com/java/servlet-plugin/)
- [Spring Boot Stormpath Web Starter](https://docs.stormpath.com/java/spring-boot-web/)

**Spring Support**
- [Spring Boot Stormpath Web Starter](https://docs.stormpath.com/java/spring-boot-web/)
- [Example Stormpath Spring App](https://github.com/stormpath/stormpath-sdk-java/tree/master/examples/spring)
- [Example Stormpath Spring Web App](https://github.com/stormpath/stormpath-sdk-java/tree/master/examples/spring-webmvc)
- [Example Stormpath Spring Boot App](https://github.com/stormpath/stormpath-sdk-java/tree/master/examples/spring-boot)
- [Example Stormpath Spring Boot Web App](https://github.com/stormpath/stormpath-sdk-java/tree/master/examples/spring-boot-webmvc)

**Spring Security Support**
- [Stormpath Spring Security Plugin](https://github.com/stormpath/stormpath-sdk-java/tree/master/extensions/spring/stormpath-spring-security)
- [Stormpath Spring Security Sample App](https://github.com/stormpath/stormpath-sdk-java/tree/master/examples/spring-security-webmvc)

**Apache Shiro Support**
- [Stormpath-Shiro Plugin](https://github.com/stormpath/stormpath-shiro)
- [Stormpath-Shiro Sample Webapp](https://github.com/stormpath/stormpath-shiro-web-sample)
- [Shiro Webapp Tutorial](http://shiro.apache.org/webapp-tutorial.html)

**API Authentication with Stormpath**
- [API Authentication for Jersey Applications - Tutorial](https://stormpath.com/blog/jersey-app-key-management/)

## Quickstart

Any Java application can use our general [Stormpath Java API Quickstart](http://docs.stormpath.com/java/quickstart/) to get started.

We also have a [Java WebApp Quickstart](https://docs.stormpath.com/java/servlet-plugin/quickstart.html) for the Stormpath Servlet Plugin - a drop-in plugin for web applications deployed to a Servlet container, like Tomcat or Jetty.  

If you're building a Spring Boot application, the [Spring Boot Webapp Quickstart](https://docs.stormpath.com/java/spring-boot-web/quickstart.html) will get you up and running quickly.

Deploy our Spring Boot Example to Heroku

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/stormpath/heroku-spring-boot-runner&env\[GROUP_ID\]=com.stormpath.spring&env\[ARTIFACT_ID\]=stormpath-sdk-examples-spring-boot-default)

## Build Instructions

This project requires Maven 3.2.1 and JDK 7 to build.  Run the following:

`> mvn install`

Release changes are viewable in the [change log](changelog.md)

## Contributing

Contributions, bug reports and issues are very welcome. Stormpath regularly maintains this repository, and are quick to review pull requests and accept changes!

You can make your own contributions by forking the develop branch of this
repository, making your changes, and issuing pull request on the develop branch.

## Copyright

Copyright &copy; 2013-2015 Stormpath, Inc. and contributors.

This project is open-source via the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

For all additional information, please see the full [Project Documentation](http://docs.stormpath.com/java/product-guide/).
