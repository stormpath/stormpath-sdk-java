.. _forwarded request:

Forwarded Request
=================

If a user has authenticated, either by using a :ref:`login form <login>` or via :ref:`Request Authentication <request authentication>`,
the user account's data will be forwarded to any backend origin servers behind the gateway in a request header.

The origin server(s) may inspect this request header to discover information about the account.  This information can
be used to customize views, send emails, update user-specific data, or practically anything else you can think of that
might be user specific.

This page covers how to customize everything related to this header and its contents if desired.

.. contents::
   :local:
   :depth: 2

Forwarded Account Header Name
-----------------------------

If a user ``Account`` is associated with a request that will be forwarded to an origin server, the account will be
converted to a String and added to the forwarded request as a request header.  The default header name is
``X-Forwarded-Account``.

If you want to specify a different header name, set the ``stormpath.zuul.account.header.name`` configuration property:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           name: "X-Forwarded-Account"


If you change this name, ensure the origin server(s) behind your gateway know to look for the new header name as well.

.. note::

   If there is no account associated with the request, the header will not be present in the forwarded request at all.

Default Header Value
--------------------

The value set in the forwarded account header must be a String.

By default, the ``Account`` will be converted into a JSON string and that string will be the header value.  The default
JSON value includes all of the following:

* all scalar Account properties (all non-array and non-Object properties like strings, integers, booleans, etc)
* all of the Account's ``CustomData`` as a nested ``customData`` JSON object.
* all of the Account's groups in a nested ``groups`` array.  Each element in the array is a ``Group`` JSON object.
* For each ``Group`` in the ``groups`` array, the group's ``CustomData`` as nested ``customData`` a JSON object.

This means that origin servers behind the gateway can simply parse the JSON result and acccess the JSON structure as
desired.  Origin servers **do not** need a Stormpath SDK - as long as they can parse JSON, they can access
the forwarded user account information!

Here is an example of a raw header value for a `jsmith` user account that is a member of one 'admin' group:

.. parsed-literal::

   {"href":"https://api.stormpath.com/v1/accounts/1FSlmxExAmPlEiq65h4A5L","username":"jsmith","email":"john.smith@example.com","givenName":"John","middleName":null,"surname":"Smith","fullName":"John Smith","status":"ENABLED","createdAt":"2016-09-29T17:35:48.976Z","modifiedAt":"2016-10-26T21:02:43.694Z","emailVerificationToken":null,"customData":{"href":"https://api.stormpath.com/v1/accounts/1FSlmxExAmPlEiq65h4A5L/customData","createdAt":"2016-09-29T17:35:48.976Z","modifiedAt":"2016-10-26T21:02:43.694Z"},"groups":[{"href":"https://api.stormpath.com/v1/groups/l21iFIPqBbgVyHExAmPLe","name":"admin","description":null,"status":"ENABLED","createdAt":"2016-09-29T17:42:31.414Z","modifiedAt":"2016-09-29T17:43:03.887Z","customData":{"href":"https://api.stormpath.com/v1/groups/l21iFIPqBbgVyHExAmPLe/customData","createdAt":"2016-09-29T17:42:31.414Z","modifiedAt":"2016-09-29T17:43:03.887Z"}}]}

As you can see, there is no extraneous whitespace in order to reduce the overall header value size.  If you were to
pretty-print the header value, it would look like this:

.. code-block:: json

   {
     "href": "https://api.stormpath.com/v1/accounts/1FSlmxExAmPlEiq65h4A5L",
     "username": "jsmith",
     "email": "john.smith@example.com",
     "givenName": "John",
     "middleName": null,
     "surname": "Smith",
     "fullName": "John Smith",
     "status": "ENABLED",
     "createdAt": "2016-09-29T17:35:48.976Z",
     "modifiedAt": "2016-10-26T21:02:43.694Z",
     "emailVerificationToken": null,
     "customData": {
       "href": "https://api.stormpath.com/v1/accounts/1FSlmxExAmPlEiq65h4A5L/customData",
       "createdAt": "2016-09-29T17:35:48.976Z",
       "modifiedAt": "2016-10-26T21:02:43.694Z"
     },
     "groups": [
       {
         "href": "https://api.stormpath.com/v1/groups/l21iFIPqBbgVyHExAmPLe",
         "name": "admin",
         "description": null,
         "status": "ENABLED",
         "createdAt": "2016-09-29T17:42:31.414Z",
         "modifiedAt": "2016-09-29T17:43:03.887Z",
         "customData": {
           "href": "https://api.stormpath.com/v1/groups/l21iFIPqBbgVyHExAmPLe/customData",
           "createdAt": "2016-09-29T17:42:31.414Z",
           "modifiedAt": "2016-09-29T17:43:03.887Z"
         }
       }
     ]
   }

Again, the header value in an actual request is not pretty-printed to save header space.

This string representation should be ok in many cases, but if you have a lot of groups or custom data per account,
be careful:

.. caution::

   If an ``Account`` has a lot of custom data or is assigned to many groups, or the groups themselves may have a lot of
   custom data, the ``X-Forwarded-Account`` header could be very large.

   Many web servers reject requests where any header is over 4 KB (or sometimes 8 KB) in size, and many web servers
   reject requests if the entire request is over 16 KB in size.

   If your accounts or groups have a lot of data and/or you see your origin web servers rejecting requests, you will
   need to customize the header value to meet your size needs instead of using the above default format.


Choosing Account Fields
^^^^^^^^^^^^^^^^^^^^^^^

If the above default format does not meet your needs, you can configure which account and group properties are included
or excluded in the resulting JSON String.

Included Account Fields
"""""""""""""""""""""""

By default, all scalar fields (strings, booleans, integers, etc) are included.  If you want to include non-scalars, i.e.
objects or collections, you can specify which ones to include in the JSON representation by setting the
``stormpath.zuul.account.header.includedProperties`` property as a list of string names.  The default value includes
``groups`` and ``customData`` field names:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           includedProperties:
             - "groups"
             - "customData"


Add or remove non-scalar field names as desired.

.. note::

   Included properties override excluded properties.  If you have the same property listed in both
   ``includedProperties`` and ``excludedProperties``, the property will be included.

Excluded Account Fields
"""""""""""""""""""""""

By default all scalar fields are included and all non-scalar (Object or Collection) fields except ``groups`` and
``customData`` are excluded from the JSON representation.

If you wanted to exclude any property (scalar or not), you can set the
``stormpath.zuul.account.header.includedProperties`` property as a list of property names:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           excludedProperties:
             - "onePropertyHere" # whatever you prefer.
             - "anotherHere"

Any property name found here will not be included in the JSON representation

.. note::

   Included properties override excluded properties.  If you have the same property listed in both
   ``includedProperties`` and ``excludedProperties``, the property will be included.


Custom Header Value
-------------------

If none of the above options are sufficient for you, and you want to represent an entirely different string as the
header value (and maybe not even JSON!), you can specify your own Account-to-String conversion function in your
gateway's Spring config as a Spring Bean override.

.. code-block:: java

   @Bean
   public Function<Account, String> stormpathForwardedAccountStringFunction() {
       return new MyAccountToStringFunction(); //implement me
   }

This bean/method must be named ``stormpathForwardedAccountStringFunction``.  The object returned must implement the
``com.stormpath.sdk.lang.Function<Account,String>`` interface.

When the gateway determines that there is an account to forward to an origin server, your custom function will be
called with an ``Account`` instance and it will return a ``String`` result.  This resulting string will be the
header value sent to your origin server(s).

.. note::

   If the resulting string is ``null`` or empty, the header will not be present in the forwarded request at all.
