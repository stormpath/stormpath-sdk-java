.. _registration:

Registration
============

.. contents::
   :local:
   :depth: 2

The registration feature of this library allows you to use Stormpath to create new accounts in a Stormpath directory.
You can create traditional password-based accounts, or gather account data from other providers such as
Facebook and Google.

By default, this library will serve an HTML registration page at ``/register``. You can change this URI by setting
``stormpath.web.register.uri`` to a different context-relative path.  You can also disable this feature entirely
by setting ``stormpath.web.register.enabled = false``.

Overview
--------

One of the very first things most web apps need is the ability to create a user account.  So how do you do it?

1. Visit ``http://localhost:${port}/register`` and you'll see the registration view:

.. image:: /_static/register.png

2. Fill out the form and click submit and you'll be redirected back to your application's root path, for example, ``http://localhost:${port}/``.

Pretty nice!  Not a single line of code required. :)

And while the page is automatically rendered with a default look and feel, you have full control over the CSS and HTML
for these pages - we'll discuss customizing them later.

Configuration
-------------

.. contents::
   :local:
   :depth: 2

The registration feature supports several options.  We'll show an example here, and cover their meaning next.

.. code-block:: yaml

   stormpath:
     web:
       register:
         enabled: true
         uri: "/register"
         nextUri: "/"
         view: "register"
         autoLogin: false
         form:
           fields:
             givenName:
               enabled: true
               label: "First Name"
               placeholder: "First Name"
               required: true
               type: "text"
             surname:
               enabled: true
               label: "Last Name"
               placeholder: "Last Name"
               required: true
               type: "text"
             username:
               enabled: false
               label: "Username"
               placeholder: "Username"
               required: true
               type: "text"
             email:
               enabled: true
               label: "Email"
               placeholder: "Email"
               required: true
               type: "email"
             password:
               enabled: true
               label: "Password"
               placeholder: "Password"
               required: true
               type: "password"
             confirmPassword:
               enabled: false
               label: "Confirm Password"
               placeholder: "Confirm Password"
               required: true
               type: "password"
           fieldOrder:
             - "username"
             - "givenName"
             - "middleName"
             - "surname"
             - "email"
             - "password"
             - "confirmPassword"


Enabled
^^^^^^^

Self-registration is enabled by default.  If you don't want users to self-register (perhaps because you will create
or import accounts for your users another way), you can disable registration by setting the
``stormpath.web.register.enabled`` property to ``false``:

.. code-block:: properties

    stormpath.web.register.enabled = false

This means the |project| will not process registration requests to ``stormpath.web.register.uri`` at all and allow any
such requests (if they exist) to fall through to your code.

URI
^^^

If self-registration is enabled, users can self-register by visiting ``/register``.

If you want to change this path, set the ``stormpath.web.register.uri`` configuration property:

.. code-block:: properties

    # The context-relative path to the register ('new user') view:
    stormpath.web.register.uri = /register

Next URI
^^^^^^^^

If :ref:`autoLogin <register-autologin>` is false (and it is false by default), and
:ref:`email verification <email verification>` is disabled, a successfully registered user will be
automatically redirected to the application's context root (home page) by default.

If you want to change this destination, set the ``stormpath.web.register.nextUri`` configuration property:

.. code-block:: properties

    stormpath.web.register.nextUri = /

If :ref:`autoLogin <register-autologin>` is true and :ref:`email verification <email verification>` is disabled, a
successfully registered user will be automatically logged in to the application and redirected to the page
specified in the ``stormpath.web.login.nextUri`` configuration property instead:

.. code-block:: properties

    stormpath.web.login.nextUri = /

Again, these properties are only referenced if email verification is disabled.  If email verification is enabled, a page
will be rendered asking the user to check their email.

Next Query Parameter
""""""""""""""""""""

If :ref:`email verification <email verification>` is disabled and the user is directed to the registration view (by
clicking a link or via a redirect), and the URI has a ``next`` query parameter, the ``next`` query parameter value
will take precedence as the post-registration redirect location. For example:

``https://myapp.com/register?next=/registerSuccess``

This will cause the user to be redirected ``/registerSuccess`` instead of the configured
``stormpath.web.register.nextUri`` path.

Again, this functionality is only executed if email verification is disabled.  If email verification is enabled, a
page will be rendered asking the user to check their email.

#if( !$servlet )

View
^^^^

When the URI is visited a default template view named ``stormpath/register`` is rendered by default.  If you wanted to
render your own template instead of the default, you can set the name of the template to render with the
``stormpath.web.register.view`` property:

.. code-block:: yaml

   stormpath:
     web:
       register:
         view: "stormpath/register"

Remember that the property value is the *name* of a view, and the effective Spring ``ViewResolver`` will resolve that
name to a template file.  See the :ref:`Custom Views <views>` chapter for more information.

#end

.. _register-autologin:

AutoLogin
^^^^^^^^^

It is generally recommended for security reasons to use email verification and require users to login with their
password after clicking an email verification link.

However, if you do not want to use email verification, and instead want users to automatically be considered
authenticated and logged in immediately after they register for a new user account, you can set autoLogin to true:

.. code-block:: properties

   stormpath.web.register.autoLogin = false # set to true if not using email verification

Because this can weaken security, the default value is ``false``.

Form Fields
^^^^^^^^^^^

.. contents::
   :local:
   :depth: 1

The registration form will render the following fields by default (all required):

* First Name (aka Stormpath account ``givenName`` field)
* Last Name (aka Stormpath account ``surname`` field)
* Email
* Password

You can customize the form by simply changing the configuration. For example, while email and password will always be
required, you could make first and last name optional. Or, you can ask the user for both an email address and a
username. You can even specify your own custom fields, no code required!

Form Field Definitions
""""""""""""""""""""""

The ``stormpath.web.register.form.fields`` is a map of ``Field Name`` -to- ``Field Definition`` entries.

Field Name
++++++++++

The name of the field in the field map must be a JSON string without spaces.  If it matches a predefined field name,
the form field value will be automatically set as the associated value in the newly created Stormpath ``Account``.

If the name of the field does not match a predefined field name, the field is treated as a
`custom field <register-custom-form-fields>`_ and saved in the account's ``Custom Data``.

Field Definition
++++++++++++++++

A field definition has properties that control the behavior of a form field.  Here is an example of a form
field definition - in this case, a field that represents a user account's ``givenName``:

.. code-block:: yaml

   stormpath:
     web:
       register:
         form:
           fields:
             givenName:
               enabled: true
               label: "stormpath.web.register.form.fields.givenName.label"
               placeholder: "stormpath.web.register.form.fields.givenName.placeholder"
               required: true
               type: "text"


What do these field definition properties mean?

+------------------------+--------------------------------------------------------------------------------------------+
| Field Property         | Description                                                                                |
|                        |                                                                                            |
+========================+============================================================================================+
| ``enabled``            | A boolean that indicates if the field will be included in the form.                        |
+------------------------+--------------------------------------------------------------------------------------------+
| ``label``              | The text value of the HTML <label> element shown to the left of the form field.  The value |
|                        | can be raw text or an i18n key.  If an i18n key, the resolved internationalized text will  |
|                        | be displayed, not the 18n key itself.  Defaults to                                         |
|                        | ``stormpath.web.register.form.fields.givenName.label``, a key in :ref:`i18n.properties     |
|                        | <i18n>`.                                                                                   |
+------------------------+--------------------------------------------------------------------------------------------+
| ``placeholder``        | The placeholder text value to display within the form field itself.  The value can be raw  |
|                        | text or an i18n key.  If an i18n key, the resolved internationalized text will be          |
|                        | displayed, not the i18n key itself.  Defaults to                                           |
|                        | ``stormpath.web.register.form.fields.givenName.placeholder``, a key in                     |
|                        | :ref:`i18n.properties <i18n>`.                                                             |
+------------------------+--------------------------------------------------------------------------------------------+
| ``required``           | A boolean that indicates if the field must be entered by the user or not.  If ``required`` |
|                        | is ``true`` and the field is blank, the post data will be validated to ensure that the     |
|                        | field is supplied, and an error will be returned if the field is empty.                    |
|                        |                                                                                            |
|                        | The error message displayed is an internationalized message defined in                     |
|                        | :ref:`i18n.properties <i18n>` as a property with the form of                               |
|                        | ``stormpath.web.register.form.fields.[fieldName].required``. If the input is invalid, the  |
|                        | error message is an internationalized message defined i18n.properties as a property with   |
|                        | the form of                                                                                |
|                        | ``stormpath.web.register.form.fields.[fieldName].invalid``.                                |
+------------------------+--------------------------------------------------------------------------------------------+
| ``type``               | The HTML input type of the field.  Most field types will be ``text`` or maybe ``email``,   |
|                        | but the ``password`` or ``confirmPassword`` fields should ideally be a type of ``password``|
|                        | so the browser does not show (masks) password characters as the user types.                |
+------------------------+--------------------------------------------------------------------------------------------+


Standard Form Fields
""""""""""""""""""""

Standard Form Fields already supported:

+---------------------+-----------------------------------------------------------------------------------------------+
| Field Name          | Description                                                                                   |
|                     |                                                                                               |
+=====================+===============================================================================================+
| ``givenName``       | A person's given name, also known as the 'first name' in Western countries. This will be      |
|                     | saved in the new Stormpath ``Account`` record's ``givenName`` field.                          |
+---------------------+-----------------------------------------------------------------------------------------------+
| ``middleName``      | Any middle name(s).  This will be saved in the new Stormpath ``Account`` record's             |
|                     | ``middleName`` field.                                                                         |
+---------------------+-----------------------------------------------------------------------------------------------+
| ``surname``         | A person's family name, also known as 'last name' in Western countries.  This will be saved   |
|                     | in the new Stormpath ``Account`` record's ``surname`` field.                                  |
+---------------------+-----------------------------------------------------------------------------------------------+
| ``username``        | The user's desired username.  If unspecified, Stormpath defaults the username to the          |
|                     | ``email`` value.  This will be saved in the new Stormpath ``Account`` record's ``username``   |
|                     | field.                                                                                        |
+---------------------+-----------------------------------------------------------------------------------------------+
| ``email``           | The user's email address.  This field is always required.  This will be saved in the new      |
|                     | Stormpath ``Account`` record's ``email`` field.                                               |
+---------------------+-----------------------------------------------------------------------------------------------+
| ``password``        | The user's password, always required.                                                         |
+---------------------+-----------------------------------------------------------------------------------------------+
| ``confirmPassword`` | An additional field that, if enabled, must have a value equal the ``password`` value upon     |
|                     | form submission. This field is used to help ensure the user doesn't accidentally type their   |
|                     | password incorrectly, which may help in usability after registration.                         |
|                     |                                                                                               |
|                     | If the submitted value                                                                        |
|                     | does not equal the ``password`` value, an internationalized error will be shown to the user   |
|                     | to ensure that they enter in the password correctly.  The :ref:`i18n.properties <i18n>` key   |
|                     | for this message is ``stormpath.web.register.form.errors.passwordMismatch``.                  |
+---------------------+-----------------------------------------------------------------------------------------------+

If these fields are not sufficient for your needs, you can add custom form fields, covered next.  But please note:

.. note::
   The ``email`` and ``password`` fields are always required. If you modify the form fields, ensure that at least these
   two are present.

.. _register-custom-form-fields:

Custom Form Fields
""""""""""""""""""

The above Standard Form Fields are the ones that are supported by default and match directly to a Stormpath ``Account``
attribute.

If you wanted to collect information that does not directly match a Stormpath account attribute, you can still add
your own custom fields to the Form Field definition map.  Any values entered in these fields will be automatically
added to the user account's ``CustomData`` object when they register successfully.

For example, let's suppose we want to add a custom field to capture a user's birthday during registration:

#if( $servlet )

.. code-block:: properties

    stormpath.web.register.form.fields.birthday.type = text
    stormpath.web.register.form.fields.birthday.enabled = true
    stormpath.web.register.form.fields.birthday.visible = true
    stormpath.web.register.form.fields.birthday.required = true
    stormpath.web.register.form.fields.birthday.label = Birthday
    stormpath.web.register.form.fields.birthday.placeholder = Birthday

#else

.. code-block:: yaml

   stormpath:
     web:
       register:
         form:
           fields:
             # ... other standard fields here ...
             birthday:
               enabled: true
               label: "Birthday" # or i18n key
               placeholder: "Birthday" # or i18n key
               required: true
               type: "text"

#end

When the registration form is rendered, this field will be added:

   .. image:: /_static/register-with-birthday.png

When the form is submitted, the field's name and value will be added automatically to the account's custom data.

.. caution:: Clear Text for Custom Fields

    If the ``type`` of a custom form field is ``password``, input on the field will be masked as usual.
    However, the value input into any custom form field will be stored in Custom Data as clear text.

If you want to provide users with a good internationalization experience, then you should define the label and
placeholder properties like this:

#if( $servlet )

.. code-block:: properties

    stormpath.web.register.form.fields.birthday.label = stormpath.web.register.form.fields.birthday.label
    stormpath.web.register.form.fields.birthday.placeholder = stormpath.web.register.form.fields.birthday.placeholder

#else

.. code-block:: yaml

   stormpath:
     web:
       register:
         form:
           fields:
             birthday:
               label: "stormpath.web.register.form.fields.birthday.label"
               placeholder: "stormpath.web.register.form.fields.birthday.placeholder"

#end

And then, in your ``i18n_en.properties`` file you would add:

.. code-block:: properties

    stormpath.web.register.form.fields.birthday.label = Birthday
    stormpath.web.register.form.fields.birthday.placeholder = 4/1/1980

While in your alternative ``i18n_es.properties`` file you would add:

.. code-block:: properties

    stormpath.web.register.form.fields.birthday.label = Fecha de nacimiento
    stormpath.web.register.form.fields.birthday.placeholder = 1/4/1980


Optional Form Fields
""""""""""""""""""""

If you want to make a form field visible, but optional, set the form field definition's ``required`` property to
``false``.  For example, if you wanted to make First Name (surname) and Last Name (givenName) optional:

.. code-block:: yaml
   :emphasize-lines: 11,17

   stormpath:
     web:
       register:
         # ... truncated for brevity ...
         form:
           fields:
             givenName:
               enabled: true
               label: "First Name"
               placeholder: "First Name"
               required: false
               type: "text"
             surname:
               enabled: true
               label: "Last Name"
               placeholder: "Last Name"
               required: false
               type: "text"
             # ... truncated for brevity ...


Disabling Form Fields
"""""""""""""""""""""

If you want to disable a form field entirely, set the form field definition's ``enabled`` property to
``false``.  For example, if you wanted to remove First Name (surname) and Last Name (givenName) fields from the form
entirely:

If you want to remove fields entirely, you can set enabled to false:

.. code-block:: yaml
   :emphasize-lines: 8,14

       stormpath:
         web:
           register:
             # ... truncated for brevity ...
             form:
               fields:
                 givenName:
                   enabled: false
                   label: "First Name"
                   placeholder: "First Name"
                   required: false
                   type: "text"
                 surname:
                   enabled: false
                   label: "Last Name"
                   placeholder: "Last Name"
                   required: false
                   type: "text"
                 # ... truncated for brevity ...

.. note::

   Because the Stormpath API currently requires Account records to have ``givenName`` and ``surname`` values, if you
   make these fields optional or disable them entirely in your registration form, the |project| will auto-fill these
   fields with a String value of ``UNKNOWN``.


Form Field Order
""""""""""""""""

You can control the order in which the fields are rendered by setting the ``stormpath.web.register.form.fieldOrder``
property.  The fields will be rendered with the first named field at the top, the 2nd named field under the first, and
so on until the fields are fully rendered.

#if( $servlet )

.. code-block:: properties

    stormpath.web.register.form.fieldOrder = username,givenName,middleName,surname,email,password,confirmPassword

#else

.. code-block:: yaml

   stormpath:
     web:
       register:
         form:
           fieldOrder:
             - "username"
             - "givenName"
             - "middleName"
             - "surname"
             - "email"
             - "password"
             - "confirmPassword"

#end

Any fields not listed in the ``fieldOrder`` will be rendered under the listed fields in the order they are declared
in the ``fields`` map.

.. _password strength:

Password Strength
^^^^^^^^^^^^^^^^^

When you first fill out the registration form, you probably noticed that you couldn't register a user account without specifying a sufficiently strong password.  This is because, by default, Stormpath enforces certain password strength rules.

If you'd like to change these password strength rules, you can do so easily. Visit the `Stormpath Admin Console`_,
navigate to your your application's user ``Directory``, and then choose the ``Password Policy`` tab on the ``Policies`` page.

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
   #. Change the 'Link Base URL' text field to equal the fully qualified URL of your application's :ref:`verify link base URL <verify link base url>`.  The default context-relative path for this feature is ``/verify``, implying a base URL (for example, during localhost testing) of ``http://localhost:${port}/verify``.

   .. image:: /_static/console-directory-workflows-ann.png

#. Click the 'Save Changes' button on the bottom right.

Try it!
^^^^^^^

#. Visit ``http://localhost:${port}/register`` and you'll see the registration view:

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

The Verify 'Link Base URL' mentioned above is the fully qualified base URL used to generate a unique link the user will click when reading the email.  For example, during development, this is often something like ``http://localhost:${port}/verify`` and in production, something like ``https://myapp.com/verify``.

When a user clicks the link in the email, the |project| will automatically process the resulting request.  By default, the context-relative path that will process these requests is ``/verify`` as the above link examples show.  This path is controlled via the ``stormpath.web.verifyEmail.uri`` configuration property:

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

    * If ``autoLogin`` is true, the verification workflow will login the user and redirect to ``login.nextUri``

        .. code-block:: properties

            stormpath.web.verifyEmail.autoLogin = true
            stormpath.web.login.nextUri = /


Internationalization (i18n)
---------------------------

The :ref:`i18n` message keys used in the default register view have names prefixed with ``stormpath.web.register.``:

.. literalinclude:: ../../../../extensions/servlet/src/main/resources/com/stormpath/sdk/servlet/i18n.properties
   :language: properties
   :lines: 49-87

For more information on customizing i18n messages and adding bundle files, please see :ref:`i18n`.


Events
------

If you implement a :ref:`Request Event Listener <events>`, you can listen registration-related events and execute custom logic if desired.

Registered Account
^^^^^^^^^^^^^^^^^^

A ``RegisteredAccountRequestEvent`` will be published when processing an HTTP request that results in a newly registered ``Account``.  If the newly registered account requires email verification before it can login, ``event.getAccount().getStatus() == AccountStatus.UNVERIFIED`` will be ``true``.

Verified Account
^^^^^^^^^^^^^^^^

A ``VerifiedAccountRequestEvent`` will be published when processing an HTTP request that verifies an account's email address.  The event's associated account is considered verified and may login to the application.

Naturally, **this event is only published if** :ref:`email verification <email verification>` **is enabled.**

.. _Stormpath Admin Console: https://api.stormpath.com
.. _Custom Data: http://docs.stormpath.com/java/product-guide/#custom-data
