.. _overview:

Overview
========

The Stormpath Servlet Plugin is a lightweight plugin that will - without *any* coding or mandatory configuration whatsoever - automatically enable the following behavior in any Servlet 3 (or later) Java web application:

* User account registration view and controller with CSRF protection
* User account email verification workflow (with secure links and one-time-use tokens)
* Verify account controller
* Login view and controller with CSRF protection
* Forgot password view with CSRF protection
* Forgot password email workflow (secure link and the ability to reset their password)
* Change password view with CSRF protection
* Logout controller to clean up identity state
* *Stateless* cryptographically verified user identity.  No HTTP sessions required!
* 'Unauthorized' view and controller
* OAuth 2 Token Authentication! An embedded OAuth provider implementation allows your Single Page Applications (SPAs) to login and logout directly and securely - no HTTP session required.
* Filter-based access control framework: easily control which users can visit what URLs based on any account data or group (role) assignments.
* HTTP Basic and OAuth Bearer authentication

All of the above features support:

* Best practices security for modern web apps
* Automatic CSRF protection for all supported forms with secure one-time-use tokens
* Completely offloaded user authentication - never worry about hashing or storing passwords ever again
* Internationalization (i18n)! All views and user messages can be represented in any language.
* Delivery speed: don't waste your time building these things anymore
* Complete view customization: change the look and feel of all views or use the clean/elegant defaults.
* Custom behavior: change any behavior via simple configuration or plug-in your own logic easily and simply.

And you don't even need a database to support any of this!  Stormpath can take care of *all* of it!

Think of how much *time* we developers spend on this same exact logic on almost every single web application we build.  Why do we re-invent the wheel on every project?  With Stormpath, we can stop wasting time on these things and build the things that are important to our web applications.

In summary, there is simply no better option available to Java web developers anywhere today that provides completely hands-off (yet customizable) support for total user management.

How does it work?
-----------------


