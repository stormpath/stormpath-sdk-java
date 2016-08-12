.. _forgot password:

Forgot Password
===============

.. contents::
   :local:
   :depth: 2

Overview
--------

Most web applications allow a user to reset their password if they forget it and cannot login.  This is most commonly performed by supporting the following workflow:

#. The user visits a 'forgot password' web page and submits the email address they used when they registered their user account.
#. An email that contains a secure 'password reset' link is sent to the user.
#. The user opens their email inbox, reads the email, and clicks the secure password reset link.
#. The user is directed to a web page that allows them to specify and confirm a brand new password for their account.
#. Optionally, a 'success' email is sent to the user to let them know the password was set successfully.  This is a security best practice to ensure that users are always notified when their password is changed.

Stormpath of course implements this entire workflow for you!  You just have to configure the workflow to ensure the user can visit the correct URIs within your application.

Configure the Workflow
----------------------

#. Login to the `Stormpath Admin Console`_.

#. Click on the ``Directories`` tab and click the name of the Directory that will store accounts for your application.  If you created an application and automatically created a directory for it, the Directory name will be the same name as the application name + ' Directory'.

   .. image:: /_static/console-directories-ann.png

#. On the resulting Directory screen, click on the Directory's ``Workflows`` menu item.

   .. image:: /_static/console-directory-ann.png

#. On the Directory Workflows screen, select the ``Password Reset`` tab at the top and enable the workflow.

   Ensure you change the ``Link Base Url`` text field to equal the fully qualified URL of your application's :ref:`password reset link base URL <forgot uri>`.  The default context-relative path for this feature is ``/change``, implying a base URL (for example, during localhost testing) of ``http://localhost:8080/change``.

   .. image:: /_static/console-directory-workflows-pwreset.png

   Click the 'Save Changes' button on the bottom right.

#. Optionally, also enable the "Password Reset Success Email".  It is usually considered a security best practice to let the user know when their password has been changed.

   .. image:: /_static/console-directory-workflows-pwreset-success-email.png

   Click the 'Save Changes' button on the bottom right.

Try it!
-------

#. Visit ``http://localhost:8080/forgot`` and you'll see the form accepting an email address to kick off the workflow:

   .. image:: /_static/forgot.png

#. Fill out the form and click submit you'll be shown a success view:

   .. image:: /_static/forgot-result.png

#. Open your email and, depending on your "Password Reset Email" template, you should see an email that looks like the following:

   .. image:: /_static/forgot-password-email.png

#. Click the link in the email and it will take you to a ``/change`` path.  This will verify the secure token in the password reset link and allow you to enter a new password:

   .. image:: /_static/forgot-password-change.png

#. Click the submit button.  Your new password will be applied to your account and you will be redirected to the login screen by default, with a success message explaining that the password was changed successfully:

   .. image:: /_static/login-changed.png

#. If you also enabled the "Password Reset Success Email", check your email inbox.  Depending on your template, you should see an email that looks like the following:

   .. image:: /_static/password-changed-email.png

.. _forgot uri:

Forgot URI
----------

Users can initiate the password reset workflow by visiting ``/forgot``

If you want to change this path, set the ``stormpath.web.forgotPassword.uri`` configuration property:

.. code-block:: properties

    # The context-relative path to the 'forgot password' view:
    stormpath.web.forgotPassword.uri = /forgot

Forgot Next URI
---------------

After the user submits their email address to send a 'reset password' email, they will automatically be redirected to a 'next' URI.  By default, this URI is the :ref:`login page <login>` as controlled by the ``stormpath.web.forgotPassword.nextUri`` configuration property:

.. code-block:: properties

    stormpath.web.forgotPassword.nextUri = /login?status=forgot

As you can see, this URI has a ``status=forgot`` query parameter.  The default login view will recognize the query parameter and show the user a nice message explaining that they should check their email inbox to look for the password reset email with instructions:

.. image:: /_static/forgot-result.png

Forgot View
-----------

When the Forgot URI is visited a default template view named ``stormpath/forgot`` is rendered by default.  If you wanted to render your own template instead of the default, you can set the name of the template to render with the ``stormpath.web.forgotPassword.view`` property:

.. code-block:: properties

    stormpath.web.forgotPassword.view = stormpath/forgot

Remember that the property value is the *name* of a view, and the effective Spring ``ViewResolver`` will resolve that name to a template file.  See the :ref:`Custom Views <views>` chapter for more information.

Change Password URI
-------------------

The Password Reset 'Link Base URL' mentioned above is the fully qualified base URL used to generate a unique link the user will click when reading the password reset email.  For example, during development, this is often something like ``http://localhost:8080/change`` and in production, something like ``https://myapp.com/change``.

When a user clicks the link in the email, the |project| will automatically process the resulting request;  By default, the context-relative path that will process these requests is ``/change`` as the above link examples show.  This path is controlled via the ``stormpath.web.changePassword.uri`` configuration property:

.. code-block:: properties

   stormpath.web.changePassword.uri = /change

You can change the value to reflect a different path if you wish.

.. caution::
   The fully qualified Password Reset Link Base URL configured in the Stormpath Admin Console must always reflect the path configured via ``stormpath.web.changePassword.uri``.  If you change one, you must change the other.

Change Password Next URI
------------------------

After the user successfully specifies their new password, they will be redirected to a `next` URI.  By default, this URI is the :ref:`login page <login>` as controlled by the ``stormpath.web.changePassword.nextUri`` configuration property:

.. code-block:: properties

    stormpath.web.changePassword.nextUri = /login?status=changed

As you can see, this URI has a ``status=changed`` query parameter.  The default login view will recognize the query parameter and show the user a nice message explaining that their account has been verified and that they can log in:

.. image:: /_static/login-changed.png

Users can login to your web application by visiting ``/login``

If you want to change this path, set the ``stormpath.web.login.uri`` configuration property:

.. code-block:: properties

    # The context-relative path to the login view:
    stormpath.web.login.uri = /login

Change Password View
--------------------

When the Change Password URI is visited a default template view named ``stormpath/change`` is rendered by default.  If you wanted to render your own template instead of the default, you can set the name of the template to render with the ``stormpath.web.changePassword.view`` property:

.. code-block:: properties

    stormpath.web.changePassword.view = stormpath/change

Remember that the property value is the *name* of a view, and the effective Spring ``ViewResolver`` will resolve that name to a template file.  See the :ref:`Custom Views <views>` chapter for more information.

i18n
----

The :ref:`i18n` message keys used in the forgot password view have names prefixed with ``stormpath.web.forgotPassword.``:

.. literalinclude:: ../../../../servlet/src/main/resources/com/stormpath/sdk/servlet/i18n.properties
   :language: properties
   :lines: 88-101

For more information on customizing i18n messages and adding bundle files, please see :ref:`i18n`.

.. _Stormpath Admin Console: https://api.stormpath.com
