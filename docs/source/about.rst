.. _about:

About
=====

Are you building a web $apptype, but not sure if the |project| is right for you?  This page will help you decide
if is a good fit!


What is Stormpath?
------------------

`Stormpath`_ is a reliable, scalable API service for managing users, authentication and authorization. Stormpath allows
your web application to do things like:

- Create user accounts.
- Edit user accounts.
- Store custom user data with each account.
- Create groups and roles.
- Automate account email verification and password reset email workflows.
- Assign users various permissions (groups, roles, etc.).
- Handle complex authentication and authorization patterns.
- Log users in via social login with `Facebook`_ and `Google`_ OAuth.
- Cache user information for quick access.
- Scale your application as you get more users.
- Securely store your users and user data in a central location.
- Support token-based authentication for your Single Page Applications (SPAs)
- Allow users to authenticate with your application with OAuth
- and more!

Stormpath provides a simple REST API for all of the above.  For instance, if you wanted to create a new user account given an email address and password, you could send Stormpath an ``HTTP POST`` request and Stormpath would create a new user account for you, securely hash its password using security best practices and and store it securely in Stormpath's cloud service.

In addition to allowing you to create users and groups, Stormpath also allows you to store custom data with each user account.  Let's say you want to store a user's birthday -- you can send Stormpath an ``HTTP POST`` request to the user's account URL and store *any* variable JSON data (birthdays, images, movies, links, etc.).  This information is cryptographically verifiable and is authenticated end-to-end, ensuring your user data is secure.

What is the |project|?
----------------------

#if( $servlet )

The |project| is a drop-in plugin for Servlet-based web applications that makes it *incredibly* simple to add user management and authentication to your Java-based web application.

#elseif( $sczuul )

.. include:: about_sczuul.rst

#else

The |project| aims to completely automate all user registration, login, authentication and authorization workloads as well as properly secure your web $apptype.  It is completely flexible - use only the functionality you need or leverage the entire feature set.

#end

Who should use Stormpath?
-------------------------

Stormpath is a powerful and secure service.  The |project| makes it even easier to use in ${apptype}s on the JVM, but it might not be for everyone!

You might want to use Stormpath if:

- You want to make user creation, management, and security as simple as possible (you can get started with ZERO lines of code *excluding settings*).
- User security is a top priority.  The Stormpath API, our documents and integrations were built by Java security experts.
- Scaling your userbase is a potential problem (Stormpath handles scaling your users transparently).
- You need to store custom user data along with your user's basic information (email, password).
- You would like to have automatic email verification for new user accounts.
- You would like to configure and customize password strength rules.
- You'd like to keep your user data separate from your other applications to increase platform stability / availability.
- You are building a service oriented application or using a microservices based architecture, in which multiple independent services need access to the same user data.
- You would like to use Stormpath, but need to host it yourself (Stormpath has private and on-premise editions you can use internally).

**Stormpath is a great match for applications of any size where security, development speed, and simplicity are top priorities.**

You might **NOT** want to use Stormpath if:

- You are building an application that does not need user accounts.
- Your application is meant for internal employee-only usage (not exposed to the public web).
- You aren't worried about user data / security much.
- You aren't worried about application availability / redundancy.
- You want to roll your own custom user authentication.

Want to use Stormpath?  OK, great!  Let's get started!

.. _Stormpath: https://stormpath.com/
.. _Facebook: https://www.facebook.com/
.. _Google: https://www.google.com/