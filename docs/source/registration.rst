.. _registration:

Registration
============

.. contents::
   :local:
   :depth: 2

Overview
--------

One of the very first things most web apps need is the ability to create a user account and login.  So how do we create user accounts?

1. Visit ``http://localhost:8080/register`` and you'll see the registration view:

.. image:: /_static/register.png

2. Fill out the form and click submit and you'll be redirected back to your application's root path, for example, ``http://localhost:8080/``.

Pretty nice!  Not a single line of code required :)

And while we think the default look and feel of the pages automatically rendered by the plugin are pretty nice, you have full control over the CSS and HTML for these pages - we'll cover customizing them later.

URI
---

Users can self-register for your web application by visiting ``/register``

If you want to change this path, set the ``stormpath.web.register.uri`` configuration property:

.. code-block:: properties

    # The context-relative path to the register ('new user') view:
    stormpath.web.register.uri = /register

Next URI
--------

If :ref:`email verification <email verification>` is disabled, a successfully registered user will be automatically redirected to the application's context root (home page) by default.  If you want to change this destination, set the ``stormpath.web.register.nextUri`` configuration property:

.. code-block:: properties

    stormpath.web.register.nextUri = /

Again, this property is only referenced if email verification is disabled.  If email verification is enabled, a page will be rendered asking the user to check their email.

Next Query Parameter
^^^^^^^^^^^^^^^^^^^^

If :ref:`email verification <email verification>` is disabled and the user is directed to the registration view (by clicking a link or via a redirect), and the URI has a ``next`` query parameter, the ``next`` query parameter value will take precedence as the post-registration redirect location.  For example:

``https://myapp.com/register?next=/registerSuccess``

This will cause the user to be redirected ``/registerSuccess`` instead of the configured ``stormpath.web.register.nextUri`` path.

Again, this functionality is only executed if email verification is disabled.  If email verification is enabled, a page will be rendered asking the user to check their email.

Form Fields
-----------

You can specify a which form fields will be displayed by editing the ``stormpath.web.register.form.fields`` configuration property.  For example, the default value:

.. code-block:: properties

    stormpath.web.register.form.fields = givenName, surname, email(required), password(required,password)

The value is a comma-delimited list Stormpath ``Account`` field names and optional form field directives.  The currently supported field names:

* ``givenName``: person's given name, also known as 'first name' in Western countries.
* ``middleName``: any middle name(s).
* ``surname``: person's family name, also known as 'last name' in Western countries.
* ``username``: a username.  If unspecified, Stormpath defaults the username to the ``email`` value.
* ``email``: the user's email address.  This field is always required.
* ``password``: the user's password.

Field names may also have directives in parenthesis immediately following the field name:

``fieldName(directive1, directive2, ..., directiveN)``

The currently supported directives are:

* ``required``: the form field must be populated before the form can be submitted
* ``password``: the form field is a password field; show ``*`` characters instead of raw password characters

Fields specified without a directive will be optional (displayed, but not required to be filled in).

So, the above default value indicates that:

1. The ``givenName`` form field (first name) will be shown first, but it is optional (no directives)
2. The ``surname`` form field (last name) will be shown next, but it is optional (no directives)
3. The ``email`` form field will be shown next and it is required (``required`` directive)
4. The ``password`` form field will be shown last, and it is required and a password field (show stars instead of raw characters).

You can customize this list with optional directives as necessary, but note:

**The** ``email`` **form field is always required.  If you customize your form fields, ensure that you always have at least an** ``email(required)`` **list entry.**

.. TIP::
    Re-ordering the comma-delimited list will automatically re-order the fields in the view :)

i18n
----

The :ref:`i18n` message keys used in the default register view have names prefixed with ``stormpath.web.register.``:

.. literalinclude:: ../../extensions/servlet/src/main/resources/com/stormpath/sdk/servlet/i18n.properties
   :language: properties
   :lines: 42-70

For more information on customizing i18n messages and adding bundle files, please see :ref:`i18n`.

.. _email verification:

Email Verification
------------------

Many applications require a newly registered user to verify that they do indeed 'own' the email address specified during registration before the user is allowed to login.  This helps ensure that:

* Email addresses cannot be abused by people that do not own them
* The application has a way of communicating with the user if necessary
* The registration process was completed by a human being (and not a 'bot' performing automatic registration, which could be used for malicious purposes).

If you want to enable email verification for newly registered accounts, you have to explicitly turn on this feature in the Stormpath Administration console:

#. Login to the `Stormpath Admin Console`_.

#. Click on the ``Directories`` tab and click the name of the Directory that will store accounts for your application.  If you created an application and automatically created a directory, the Directory name will be the same as the application name + ' Directory'.

   .. image:: /_static/console-directories-ann.png

#. On the resulting Directory screen, click on the Directory's ``Workflows`` menu item.

   .. image:: /_static/console-directory-ann.png

#. On the Directory Workflows screen, the 'Verification Email' workflow is shown first.  Ensure that you

   #. Enable the workflow, and
   #. Change the 'Link Base URL' text field to equal the fully qualified URL of your application's :ref:`verify link base URL <verify link base url>`.  The plugin's default context-relative path for this feature is ``/verify``, implying a base URL (for example, during localhost testing) of ``http://localhost:8080/verify``.

   .. image:: /_static/console-directory-workflows-ann.png

#. Click the 'Save Changes' button on the bottom right.

Try it!
^^^^^^^

#. Visit ``http://localhost:8080/register`` and you'll see the registration view:

   .. image:: /_static/register.png

#. Fill out the form and click submit, your account will be created with ``UNVERIFIED`` or ``ENABLED`` status depending on your `Directory Workflow <http://docs.stormpath.com/console/product-guide/#directory-workflows>`_:

    * Account status ``UNVERIFIED``:
        You will be redirected to the :ref:`login page <login>` with ``?status=unverified`` and you will receive a verification email.

        A) Open your email and, depending on your "Account Email Verification" email template, you should see an email that looks like the following:

           .. image:: /_static/register-verify-email.png

        B) Click the link in the email and it will take you to a ``/verify`` path.  This will verify your email address and redirect you to the :ref:`Verify Next URI`

            .. image:: /_static/login-verified.png

    * Account status ``ENABLED``:
        If ``autoLogin`` is true the application will log you in and redirect to register 'next' URI.
        If false you will be redirected to the :ref:`login page <login>` with ``?status=created``.

.. _verify link base url:

Verify Link Base URL
^^^^^^^^^^^^^^^^^^^^

The Verify 'Link Base URL' mentioned above is the fully qualified base URL used to generate a unique link the user will click when reading the email.  For example, during development, this is often something like ``http://localhost:8080/verify`` and in production, something like ``https://myapp.com/verify``.

When a user clicks the link in the email, the Stormpath Java Servlet Plugin will automatically process the resulting request.  By default, the context-relative path that will process these requests is ``/verify`` as the above link examples show.  This path is controlled via the ``stormpath.web.verifyEmail.uri`` configuration property:

.. code-block:: properties

    stormpath.web.verifyEmail.uri = /verify

You can change the value to reflect a different path if you wish.

.. caution::
    The fully qualified Link Base URL configured in the Stormpath Admin Console must always reflect the path configured via ``stormpath.web.verifyEmail.uri``.  If you change one, you must change the other.

.. _verify next uri:

Verify Next URI
^^^^^^^^^^^^^^^

When the user clicks the email verification link and the request is processed by the the ``stormpath.web.verifyEmail.uri`` path, the user will be:

    * If ``autoLogin`` is false (this is the default), redirected to a 'next' URI.  By default, this URI is the :ref:`login page <login>` as controlled by the ``stormpath.web.verifyEmail.nextUri`` configuration property:

        .. code-block:: properties

            stormpath.web.verifyEmail.autoLogin = false
            stormpath.web.verifyEmail.nextUri = /login?status=verified

        As you can see, this URI has a ``status=verified`` query parameter.  The plugin's default login view will recognize the query parameter and show the user a nice message explaining that their account has been verified and that they can log in:

        .. image:: /_static/login-verified.png

    * If ``autoLogin`` is true, the verification workflow will log in the user and redirect to login 'next' URI

        .. code-block:: properties

            stormpath.web.verifyEmail.autoLogin = true
            stormpath.web.login.nextUri = /

Events
------

If you implement a :ref:`Request Event Listener <events>`, you can listen registration-related events and execute custom logic if desired.

Registered Account
^^^^^^^^^^^^^^^^^^

A ``RegisteredAccountRequestEvent`` will be published when processing an HTTP request that results in a newly registered ``Account``.  If the newly registered account requires email verification before it can login, ``event.getAccount().getStatus() == AccountStatus.UNDEFINED`` will be ``true``.

Verified Account
^^^^^^^^^^^^^^^^

A ``VerifiedAccountRequestEvent`` will be published when processing an HTTP request that verifies an account's email address.  The event's associated account is considered verified and may login to the application.

Naturally, **this event is only published if** :ref:`email verification <email verification>` **is enabled.**

.. _Stormpath Admin Console: https://api.stormpath.com
