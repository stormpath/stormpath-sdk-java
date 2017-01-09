.. _forwarded request:

Forwarded Request
=================

If a user has authenticated, either by using a :ref:`login form <login>` or via
:ref:`Request Authentication <request authentication>`, the user account data will be forwarded to any backend
origin servers behind the gateway in a request header.

The origin server(s) may inspect this request header to discover information about the user.  This information can
be used to customize views, send emails, update user-specific data, or practically anything else you can think of that
might be user-specific.

This page covers how to customize everything related to this header and its contents.

.. contents::
   :local:
   :depth: 2

Forwarded Account Header Name
-----------------------------

If a user account is associated with a request that will be forwarded to an origin server, the account will be
converted to a String and added to the forwarded request as an HTTP request header.  The default header name is
``X-Forwarded-User``, a de-facto HTTP header for forwarding user account information.

If you want to specify a different HTTP header name, set the ``stormpath.zuul.account.header.name`` configuration property:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           name: X-Forwarded-User

.. caution::

   If you change this name, ensure the origin server(s) behind your gateway are updated to look for the new header name as well.

.. note::

   If there is no user account associated with the request, the header will not be present in the forwarded request at all.

Forwarded Account Header Value
------------------------------

If a user ``Account`` is associated with the request, the forwarded account header value will be a String that represents the
account.

You can customize this String value to be anything you like - such as a simple single value like the account's username
or email, or the entire account as a JSON document, or even a cryptographically-safe
:ref:`JSON Web Token (JWT) <forwarded account header jwt>` that represents the account information you choose.

By default, a digitally-signed account :ref:`JWT <forwarded account header jwt>` will be used as the header value.
When an origin server reads the forwarded header value, the origin server can verify the JWT signature.  This allows
the origin server to cryptographically guarantee the account information in the JWT has not been unknowingly changed or
tampered with in transit. JWTs are among the simplest and safest means of secure identity assertion, so the
|project| chooses this approach by default to help ensure best-practices security.

If JWTs are not desirable - perhaps because you implicitly trust the network and machine transmission to your origin
servers - you can :ref:`disable the JWT approach entirely <forwarded account header jwt enabled>` and either send over a
:ref:`single string value <forwarded account single field>` or a :ref:`plain JSON document <forwarded account json>`.
If you don't have a preference however, it is recommended in most scenarios to retain the additional security benefits
JWTs can offer.

Because JWT construction is a secondary concern after you've chosen which account data to represent in the header, we'll
cover account value customization first, and then finish with instructions on how to represent this information as a JWT.

.. _forwarded account single field:

Single Account Field
^^^^^^^^^^^^^^^^^^^^

If you do not want or need the security guarantees of a JWT and want your header value to be a single string value,
such as the account's username or email, you can set the following configuration:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             enabled: false
           value:
             strategy: single
             field: username


This configuration states that :ref:`JWT is disabled <forwarded account header jwt enabled>`, and we'll use a value
conversion strategy of ``single`` which means we want the header to be a single account field value (we'll talk about
:ref:`JWTs <forwarded account header jwt>` and :ref:`conversion strategies <object conversion strategy>` later).  The
account field that we want to use as the value is indicated by the ``stormpath.zuul.account.header.value.field``
config property, which in this case is ``username``.

With the above config, if an account with a username of ``tk421`` was associated with the request, the header sent to
the origin server(s) would look like this:

.. code-block:: properties

   x-forwarded-user: tk421

A similar example using the account email instead is shown in the :ref:`field <object conversion field>` section.

You can set the ``stormpath.zuul.account.header.value.field`` to a name of any `scalar`_ property defined on the
`com.stormpath.sdk.account.Account <https://docs.stormpath.com/java/apidocs/com/stormpath/sdk/account/Account.html>`_
interface.  For example:

* ``email``
* ``givenName``
* ``middleName``
* ``surname``
* ``fullName``
* etc...

That said, a single account field is often not sufficient, nor is it cryptographically signed to guarantee data
integrity, so you may prefer an Account JSON document or a signed JWT instead.

.. _forwarded account json:

Account JSON
^^^^^^^^^^^^

You can serialize the request Account, as well as objects or collections reachable from the
`com.stormpath.sdk.account.Account <https://docs.stormpath.com/java/apidocs/com/stormpath/sdk/account/Account.html>`_
interface (like ``CustomData`` and its ``Groups`` collection and more), as a single JSON
document. The resulting JSON document string will be the forwarded account value.

So how do you specify which of the Account's fields and its reachable objects (the 'graph') should be included in the
final JSON document?  With *conversion rules*.

.. _conversion rules:

Conversion Rules
""""""""""""""""

Account-to-String conversion is performed according to rules that you can specify for the account and
its graph of connected objects.  We call these rules *conversion rules*  and you can specify conversion config for any
object or collection encountered in a graph.

For the purposes of the forwarded account header, the account associated with the
request is always the 'root' of the object graph to convert; its properties and reachable objects/collections may also be
serialized by specifying a parallel graph of conversion rules.  The account object graph will be traversed according to
your rules, and the resulting output will be a single JSON document that has the same graph structure as your
specified conversion graph.

So what are the conversion rules?

Before explaining each possible option in detail, let's see what the default configuration looks like and we'll
explain what it means. Or if you prefer, you can read about the
:ref:`available configuration options <object graph conversion>` first, and then return here to see
them used in context.

Here are the default Account conversion rules, specified as various ``stormpath.zuul.account.header.value.*`` config
properties.  This is the default configuration in effect if you don't specify any conversion rules yourself:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           value:
             strategy: scalars
             fields:
               href:
                 enabled: false
               customData:
                 strategy: scalars
                 fields:
                   href:
                     enabled: false
               groups:
                 strategy: defined
                 elements:
                   enabled: true
                   name: items
                   each:
                     strategy: scalars


So what does this mean?  You can summarize this in English as the following:

.. epigraph::

   When converting the request Account to a JSON value, I want:

   - all of the account's `scalar`_ (i.e. non object/collection) fields to be included, each as a JSON name/value pair.

   - However, I want specific overriding rules for the ``href``, ``customData`` and ``groups`` fields.  For these:

     - don't include the account's ``href`` field.  My origin server(s) behind the gateway probably won't talk
       directly with Stormpath and won't know what to do with that url, so exclude it

     - ``customData`` isn't a scalar, but I want it included anyway, so I'm going to define conversion rules for it
       too.  Those are:

       - Include any of the customData's scalar properties automatically

       - However, don't include the customData's ``href``, since my origin server(s) won't know what to do with it.

     - ``groups`` isn't a scalar (it's a ``GroupsCollection`` object), but I want it included anyway.

       - However, in this case I want to include *only* fields that are explicitly *defined* in its ``fields``
         list.  (In this case, even though the strategy is ``defined``, no actual ``fields`` have been specified.
         This means that *no* fields on the ``GroupsCollection`` object itself, like 'size' and 'limit' will be
         included.  We just want the collection's elements, described next.)

       - The collection ``elements`` are enabled so I do want the elements in the collection.

         - I want any converted collection elements to be represented as a JSON array with the *name* of ``items``.

         - For ``each`` element in the ``GroupsCollection`` instance, I want to convert each ``Group`` object,
           including each group's *scalar* properties.


Here is an example of what the resulting JSON would look like, pretty printed for readability (to reduce the number of
bytes transmitted over the network, the actual value won't be pretty-printed):

.. code-block:: json

   {
     "username": "tk421",
     "email": "tk421@galacticempire.com",
     "givenName": "TK421",
     "middleName": null,
     "surname": "Stormtrooper",
     "fullName": "TK421 Stormtrooper",
     "status":"ENABLED",
     "createdAt": "2016-12-15T19:58:55.272Z",
     "modifiedAt":"2016-12-15T19:59:23.729Z",
     "passwordModifiedAt": "2016-12-15T19:58:55.000Z",
     "emailVerificationToken": null,
     "customData": {
       "createdAt": "2016-12-15T19:58:55.272Z",
       "modifiedAt":"2016-12-15T19:59:23.729Z",
       "favoriteColor": "Blaster Black"
     },
     "groups": {
       "items": [
         {
           "name": "dsguards",
           "description": "Death Star Guards",
           "status": "ENABLED",
           "createdAt": "2016-12-28T00:34:46.453Z",
           "modifiedAt":"2016-12-28T00:34:46.453Z"
         },
         {
           "name": "troopers",
           "description": "All stormtroopers",
           "status": "ENABLED",
           "createdAt": "2016-12-28T00:34:07.222Z",
           "modifiedAt":"2016-12-28T00:34:07.222Z"
         }
       ]
     }
   }


As you can see, the output JSON graph mirrors the conversion rules graph above.

Now that we've seen a good example, let's cover all the possible conversion config properties to explain their
functionality.  You may find the :ref:`strategy <object conversion strategy>` particularly helpful.

.. _object graph conversion:

.. contents::
   :local:
   :depth: 1

.. _collection conversion each:

``each``
""""""""

The ``each`` conversion property may only be specified as a nested property of an ``elements`` block.

If specified, the ``each`` property value is a conversion rule block that indicates how to
convert/serialize each element in the collection.

If you do not specify an ``each`` configuration block, default conversion rules apply for any encountered element
object.

.. _collection conversion elements:

``elements``
""""""""""""

The ``elements`` conversion property may only be specified when the object encountered is a Collection.

Unlike a standard conversion block, it supports only 3 nested config properties:  ``name``, ``enabled`` and ``each``.
Unless overridden, the default ``name`` of the elements array in the rendered JSON is ``items``.

In the following example, the ``groups`` field is a collection, so it can support the ``elements`` definition
(which in turn uses an ``each`` definition):

.. code-block:: yaml
   :emphasize-lines: 9-12

   stormpath:
     zuul:
       account:
         header:
           value:
             fields:
               groups:
                 strategy: defined
                 elements:
                   name: items
                   each:
                     strategy: scalars


This would result in the following JSON (other properties omitted for brevity):

.. code-block:: javascript

   {
     // ... omitted for brevity ...
     "groups": {
       "items": [
         {
           // ... ommitted for brevity ...
         },
         {
           // ... ommitted for brevity ...
         }
       ]
     }
   }

Notice this creates a ``groups`` JSON property, which is an object, and within that object, wraps the elements in
an ``items`` array.

.. _object wraps array:

Object Wraps Array
++++++++++++++++++

We typically recommend keeping this 'object wraps array' strategy for collections - it allows for adding properties
to the collection object itself in the future (like ``size`` or ``limit``, etc) whereas raw JSON arrays cannot
support this.

That said, what if you didn't care about potential additional collection properties in the future, and you just wanted
the collection to be a raw JSON array?  You can use a ``strategy`` of ``list``.  For example:

.. code-block:: yaml
   :emphasize-lines: 8

   stormpath:
     zuul:
       account:
         header:
           value:
             fields:
               groups:
                 strategy: list
                 elements:
                   each:
                     strategy: scalars

This results in the following JSON (other properties omitted for brevity):

.. code-block:: javascript

   {
     // ... omitted for brevity ...
     "groups": [
       {
         // ... ommitted for brevity ...
       },
       {
         // ... ommitted for brevity ...
       }
     ]
   }


Notice the resulting JSON - ``groups`` is not an object with a nested ``items`` array - it is just an array.

.. caution::

   We typically recommend that you *DO NOT* use the ``list`` strategy if forward-compatibility is
   important to you: JSON arrays are inflexible and cannot support additional properties over time, whereas JSON
   objects are flexible and allow for future property expansion.

   However, the ``list`` strategy could be useful if your account JSON must adhere to an existing or legacy structure
   that your origin servers expect.


.. _object conversion enabled:

``enabled``
"""""""""""

The ``enabled`` conversion property indicates if the field will be included in the output sent to the origin server.  If
the value is ``false``, that field will not be included at all in the output sent to the origin server.  The default
value is ``true``.

.. _object conversion field:

``field``
"""""""""

The ``field`` conversion property is only evaluated when using the ``single`` strategy.  It defines which
field on the target object should be used as the value in the rendered output.

For example, the following config says "Use the account's email address as the single value for the forwarded
account header":

.. code-block:: yaml
   :emphasize-lines: 8-9

   stormpath:
     zuul:
       account:
         header:
           jwt:
             enabled: false
           value:
             strategy: single
             field: email

With the above config, if an account with an email of ``tk421@galacticempire.com`` was associated with the request,
the header sent to the origin server(s) would look like this:

.. code-block:: properties

   x-forwarded-user: tk421@galacticempire.com

.. _object conversion fields:

``fields``
""""""""""

``fields`` is a conversion property that is a map of named fields to conversion rules.  Each named field corresponds
to a field on the encountered object being serialized.  Each mapped value is a conversion rule/block that defines
how that named field should be serialized.

Fields explicitly defined in the ``fields`` map always override the default ``strategy``.

In the following example, the Account's ``href`` and ``customData`` fields have explicit conversion rules that override
the specified ``scalars`` strategy:

.. code-block:: yaml
   :emphasize-lines: 7-11

   stormpath:
     zuul:
       account:
         header:
           value:
             strategy: scalars
             fields:
               href:
                 enabled: false
               customData:
                 strategy: scalars



Don't forget that a ``fields`` map can be specified for any reachable object or collection, not just the root account
object.

.. _object conversion name:

``name``
""""""""

The ``name`` conversion property allows you to define a different name for the encountered field if you do not like the
default field name.  Consider the following example:

.. code-block:: yaml
   :emphasize-lines: 8,10

   stormpath:
     zuul:
       account:
         header:
           value:
             fields:
               givenName:
                 name: firstName
               surname:
                 name: lastName


This configuration results in the following example JSON.

.. code-block:: json
   :emphasize-lines: 4,6

   {
     "username": "tk421",
     "email": "tk421@galacticempire.com",
     "firstName": "TK421",
     "middleName": null,
     "lastName": "Stormtrooper",
     "fullName": "TK421 Stormtrooper",
     "status":"ENABLED",
     "createdAt": "2016-12-15T19:58:55.272Z",
     "modifiedAt":"2016-12-15T19:59:23.729Z",
     "passwordModifiedAt": "2016-12-15T19:58:55.000Z",
     "emailVerificationToken": null,
   }

As per the above override configuration, the member that ordinarily would have been named ``givenName`` is now
named ``firstName`` and the member that would have been named ``surname`` is now ``lastName``.

If the ``name`` conversion property is unspecified, the default field name will be used.

**elements name**

If you specify the ``name`` conversion property as a child of an ``elements`` property, that name reflects the name
given to the array member *within* the collection JSON object.  If you do not specify an ``elements name``, the
default value is ``items``, resulting in the following structure:

.. code-block:: javascript

   {
     // ... omitted for brevity ...
     "groups": {
       "items": [
         {
           // ... ommitted for brevity ...
         },
         {
           // ... ommitted for brevity ...
         }
       ]
     }
   }

Here is an example of changing a collection's ``field name`` and its ``elements name`` and the resulting JSON, so
you can see the difference between the two:

.. code-block:: yaml
   :emphasize-lines: 8

      stormpath:
        zuul:
          account:
            header:
              value:
                fields:
                  groups:
                    name: my_groups
                    elements:
                      name: my_array

This config results in the following:

.. code-block:: javascript

   {
     // ... omitted for brevity ...
     "my_groups": {
       "my_array": [
         {
           // ... ommitted for brevity ...
         },
         {
           // ... ommitted for brevity ...
         }
       ]
     }
   }


See the difference?  The JSON member name of the *collection object itself* is now ``my_groups`` and the member name
of the JSON array that contains just the elements is now ``my_array``.

So in summary, a ``field name`` controls the name of the field.  If the field is a collection, the ``elements name``
controls the name of the elements array within the field/object.

Why wrap the array in an object?  Why not just have it be a simple array?  See the
:ref:`elements <collection conversion elements>` and :ref:`Object Wraps Array <object wraps array>` sections for more
information on backwards-compatibility safety.

.. _object conversion strategy:

``strategy``
""""""""""""

The ``strategy`` conversion property specifies the *general* strategy of how to convert an encountered
object or collection.  It would be burdensome to have to specify *every* *single* *field* that you want to include, so
the ``strategy`` concept is a shortcut that allows you to define a general approach to simplify your configuration.

The ``strategy`` property is an enum and may have one of the following values:

===========  ===========================================================================================================
Value        Description
===========  ===========================================================================================================
``DEFINED``  Only fields explicitly defined in the ``fields`` section will be evaluated for inclusion in the JSON
             output.  Any fields not explicitly defined in the ``fields`` section *WILL NOT* be included in the
             converted JSON output.
``SINGLE``   The conversion output should be just one of the source object's field values.  The name of the single
             field to include is defined by the ``field`` configuration property.
``SCALARS``  All of the source object's `scalar`_ values should be included in the output. A scalar value is any single
             value that is not a Collection, Map or compound/complex object.  This is the default strategy if you do
             not specify one.
``LIST``     Only usable only if the source object is a Collection resource, this strategy ensures that the
             converted output is the raw List of the collection's elements only, instead of an Object that reflects the
             Collection itself (and its list of elements).  In other words, the converted output will not reflect any
             properties of the Collection resource itself - only its elements represented as a single List.  If the
             source object is not a Collection resource/instance, this strategy is ignored.
``ALL``      Indicates that *ALL* fields of the source object should be in the output.  Be careful when
             choosing this strategy as the output could be sufficiently larger than desired.  Larger outputs increase
             the amount of data sent to the origin server(s) on every request.
===========  ===========================================================================================================

Unless overridden for a particular/named field, ``SCALARS`` is the default strategy for all encountered objects.

.. _forwarded account header jwt:

Forwarded Account Header JWT
----------------------------

By default, a digitally-signed account `JSON Web Token (JWT)`_ will be used as the HTTP header value.  When an origin
server reads the forwarded HTTP header value, the origin server can verify the JWT's signature.  This allows the origin
server to cryptographically guarantee the account information in the JWT has not been uknowingly changed or tampered
with in transit. JWTs are among the simplest and safest means of secure identity assertion, so the |project| chooses
this approach to ensure best-in-class security by default.

If JWTs are not desirable - perhaps because you implicitly trust the network and machine transmission to your origin
servers - you can disable the JWT approach entirely (see the ``enabled`` property below) and instead send a simple string
value or JSON document as documented above  If you don't have a preference however, it is recommended in most
scenarios to retain the added security that JWTs can offer.

.. tip::

   The JWT will contain the :ref:`Account JSON as defined above <forwarded account json>`, so you have full control
   over the JWT contents.

The remaining part of this page documents which configuration properties are available to you so you can customize
the account JWT sent to origin servers if desired.

.. _forwarded account header jwt enabled:

``enabled``
^^^^^^^^^^^

If you do not want the forwarded account header value to be a JWT, set the
``stormpath.zuul.account.header.jwt.enabled`` property to ``false``:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             enabled: false

This ensures the header value is *NOT* a JWT, but either an :ref:`Account JSON document <forwarded account json>` or a
:ref:`single string value <forwarded account single field>` as documented above, but beware of the security implications.

By default, JWT ``enabled`` is ``true``.

``expirationSeconds``
^^^^^^^^^^^^^^^^^^^^^

You can specify when the JWT expires (and is no longer usable by origin servers) by specifying the
``stormpath.zuul.account.header.jwt.expirationSeconds`` property.

The value is a ``long`` that indicates the number of **seconds** (*not* milliseconds!) that the JWT should be usable
after creation.

This number is added to the JWT's creation timestamp (again in seconds), and the resulting timestamp is converted to a date
and set as the JWT ``exp`` claim.

This value is ``null`` by default, indicating that the forwarded account header JWTs do not expire by default, but
we recommended that you set a value (e.g. one hour = 3600 seconds) as indefinite JWTs are often not desirable in
security contexts.  For example:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             expirationSeconds: 3600


``notBeforeSeconds``
^^^^^^^^^^^^^^^^^^^^

You can specify a minimum timestamp of when the JWT is allowed to be used by the origin servers by
specifying the ``stormpath.zuul.account.header.jwt.notBeforeSeconds`` property.

The value is a ``long`` that indicates the number of **seconds** (*not* milliseconds!) that will be added
(or subtracted if negative) to the JWT's creation timestamp.  The resulting timestamp is converted to a date and set
as the JWT ``nbf`` claim.

This value is ``null`` by default, indicating that the forwarded account header JWTs do not have an ``nbf`` timestamp
by default.

Here is an example that states that an ``nbf`` claim should exist, and it should be the same timestamp as the JWT's
creation timestamp. (This has the effect of ensuring that origin servers do not process JWTs with timestamps that are
out of sync with origin server clocks, which may or may not be desirable depending on your use case):


.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             notBeforeSeconds: 0


``claims``
^^^^^^^^^^

You can set custom name/value pairs that should appear in the JWT's claims via the
``stormpath.zuul.account.header.jwt.claims`` property.  For example:

.. code-block:: yaml
   :emphasize-lines: 6-8

   stormpath:
     zuul:
       account:
         header:
           jwt:
             claims:
               iss: my gateway
               aud: my origin server


This configuration would result in a JWT claims that, if inspected, would have a structure similar to the
following:

.. code-block:: javascript
   :emphasize-lines: 3-4

   {
     "iat": 1482972605,
     "iss": "my gateway",
     "aud": "my origin server",
     // ... other claims/Account fields omitted for brevity ...
   }


Notice that your configured custom name/value pairs are in the claims, in addition to other runtime-specific values.

.. note::

   ``stormpath.zuul.account.header.jwt.claims`` name/value pairs represent JWT claims *default* values.  Any specific
   runtime-determined claim value with the same name (such as ``iat`` or ``exp``) will replace (overwrite) your
   custom defaults.

``header``
^^^^^^^^^^

You can set custom name/value pairs that should appear in the JWT's header via the
``stormpath.zuul.account.header.jwt.header`` property.  For example:

.. code-block:: yaml
   :emphasize-lines: 6-8

      stormpath:
        zuul:
          account:
            header:
              jwt:
                header:
                  foo: bar
                  hello: world


This configuration would result in a JWT header that, if inspected, would have a structure similar to the following:

.. code-block:: javascript
   :emphasize-lines: 3-4

      {
        "alg": "HS256",
        "foo": "bar",
        "hello": "world"
        // ... other header fields omitted for brevity ...
      }


Notice that your configured custom name/value pairs are in the header, in addition to other runtime-specific values.

.. note::

   ``stormpath.zuul.account.header.jwt.header`` name/value pairs represent JWT header *default* values.  Any specific
   runtime-determined header value with the same name (such as ``kid`` or ``alg``) will replace (overwrite) your
   custom defaults.

``key``
^^^^^^^

You may configure the signing key used to cryptographically sign the JWT via various
``stormpath.zuul.account.header.jwt.key.*`` properties.  They are:

.. contents::
   :local:
   :depth: 1

.. tip::

   If you do not specify a signing key, the secret from Stormpath Client API Key used to bootstrap the
   |project| will be used as the default signing key.  In this case, the JWT will have a ``kid`` (Key ID) header
   value equal to the HREF (URL) of that Stormpath API Key.

   However, it is probably unlikely that your backend origin servers will have this same key configured, so they will
   not be able to verify the JWT's digital signature.

   To avoid JWT key/parsing errors in your origin servers, we recommend that specify your own signing key via
   the :ref:`stormpath.zuul.account.header.jwt.key.k property <forwarded account signing key value>` or by defining the
   :ref:`stormpathForwardedAccountJwtSigningKey <forwarded account signing key bean>` bean.

   Also please see the :ref:`signing key alg <forwarded account signing key alg>` section for more information.


.. _forwarded account signing key alg:

``alg``
"""""""

You can specify which digital signature algorithm is used to sign the JWT by setting the
``stormpath.zuul.account.header.jwt.key.alg`` property to one of the following supported values:

=========  ================  ==============================================
Value      Algorithm Family  Description
=========  ================  ==============================================
``HS256``  HMAC              HMAC using SHA-256
``HS384``  HMAC              HAMC using SHA-384
``HS512``  HMAC              HMAC using SHA-512
``RS256``  RSA               RSASSA-PKCS-v1_5 using SHA-256
``RS384``  RSA               RSASSA-PKCS-v1_5 using SHA-384
``RS512``  RSA               RSASSA-PKCS-v1_5 using SHA-512
``PS256``  RSA               RSASSA-PSS using SHA-256 and MGF1 with SHA-256
``PS384``  RSA               RSASSA-PSS using SHA-384 and MGF1 with SHA-384
``PS512``  RSA               RSASSA-PSS using SHA-512 and MGF1 with SHA-512
``ES256``  Elliptic Curve    ECDSA using P-256 and SHA-256
``ES384``  Elliptic Curve    ECDSA using P-384 and SHA-384
``ES512``  Elliptic Curve    ECDSA using P-512 and SHA-512
=========  ================  ==============================================


For example:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             key:
               alg: HS256


If you are using an HMAC algorithm by specifying ``HS256``, ``HS384``, or ``HS512``, you can provide your HMAC
symmetric key in one of two ways.  Either:

A. Set the ``stormpath.zuul.account.header.jwt.key.k`` and ``stormpath.zuul.account.header.jwt.key.encoding``
   config properties, or

B. Define the :ref:`stormpathForwardedAccountJwtSigningKey <forwarded account signing key bean>` bean.


**If you are not using an HMAC algorithm**, you **must** provide your signing key
by defining the :ref:`stormpathForwardedAccountJwtSigningKey <forwarded account signing key bean>` bean.


``enabled``
"""""""""""

You can disable the JWT signature process entirely (not use a key at all) by setting
``stormpath.zuul.account.header.jwt.key.enabled`` equal to ``false``:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             key:
               enabled: false


This will ensure that the JWT created is *NOT* digitally signed - it will be an
`Unsecured JWT <https://tools.ietf.org/html/rfc7519#section-6>`_. **We strongly recommend that you digitally sign JWTs for the security model that signed JWTs afford**.
However, unsecured JWTs could be useful in very specific circumstances specific to your application.
If you're unsure, we recommend that you *do not* set this property.


``encoding``
""""""""""""

If you specified the text value of your HMAC signing key via the ``stormpath.zuul.account.header.jwt.key.k`` property,
and that string is *not* Base64Url-encoded, you will need to set the ``stormpath.zuul.account.header.jwt.key.encoding``
property to indicate which encoding is used.  For example:

.. code-block:: yaml
   :emphasize-lines: 8

   stormpath:
     zuul:
       account:
         header:
           jwt:
             key:
               k: EQDGRjSpZB87/eWO42XQ7h7mfxk0EmF6ZDY0TDGdAoA=
               encoding: base64


The default/assumed encoding is ``base64url``.  There are two other supported encodings:

* ``base64``: standard Base64 encoding (not URL encoded)
* ``utf8``: direct UTF-8 bytes of the configured string, i.e. ``k.getBytes(StandardCharsets.UTF8)``

**CAUTION**: these 3 text encodings are not cryptographically secure.  Please see the
:ref:`key caution <forwarded account signing key value caution>` concerning key string values.

.. _forwarded account signing key value:

``k``
"""""

If you want to configure your HMAC signing key as a string, you can set the
``stormpath.zuul.account.header.jwt.key.k`` property.  For example:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             key:
               k: EQDGRjSpZB87_eWO42XQ7h7mfxk0EmF6ZDY0TDGdAoA


By default, the value is expected to be a Base64Url string.  The |project| will then base64url-decode this value
at startup to obtain the raw signing key bytes used to compute the JWT signature.

If your string value is not Base64Url, you can specify the ``stormpath.zuul.account.header.jwt.key.encoding``
config property to indicate which encoding is used.

.. _forwarded account signing key value caution:

.. caution::

   **Base64, Base64Url and UTF-8 encoding DOES NOT imply encryption**.

   Anyone that can access the
   ``stormpath.zuul.account.header.jwt.key.k`` string value can use it to sign JWTs as you.  Keep this text string (and
   the configured property value) safe and secret.

   If you are uncomfortable embedding key strings in your configuration due to security concerns, we recommend
   any of three approaches:

   1.  Specify the ``stormpath.zuul.account.header.jwt.key.k`` value as an
       `external Spring Boot property <https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html>`_.
       For example, set the ``STORMPATH_ZUUL_ACCOUNT_HEADER_JWT_KEY_K`` environment variable via an operations
       orchestration mechanism like Chef, Puppet or CloudFoundry that has access to secure/encrypted data store for
       such values.

   2.  Use `Spring Cloud Config Server <https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html#_security>`_
       to securely represent key values as text properties in your config.  Spring Cloud Config Server will decrypt
       the text value just before giving it to the |project| so it may be used correctly.

   3.  Do not configure the ``stormpath.zuul.account.header.jwt.key.k`` property and instead define your own
       :ref:`stormpathForwardedAccountJwtSigningKey <forwarded account signing key bean>` bean.  You can then load the
       key bytes in whatever secure way you prefer.


``kid``
"""""""

When specifying a signing key, it is usually recommended to also specify a string identifier for the key in the JWT
header.  This allows JWT recipients (i.e. your origin servers) the ability to inspect the JWT header and identify which
signing key was used.  Based on this identifier, the JWT recipient can then look up the corresponding key
(or public key) to use in order to correctly verify the JWT's digital signature.

You can specify your signing key's id (the ``kid`` param in the JWT header) by setting the
``stormpath.zuul.account.header.jwt.key.kid`` configuration property.  For example:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             key:
               kid: my signing key id


This will set the JWT's ``kid`` header accordingly.

Note that since it is a header, an alternative approach of accomplishing the same thing is to set it as a
``stormpath.zuul.account.header.jwt.header.kid`` name/value pair:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             header:
               kid: my signing key id


The first approach keeps the key id configuration 'close to' the other key parameters, which might be desirable
depending on preference.  Either approach accomplishes the same thing - feel free to use what you prefer.


``valueClaim``
^^^^^^^^^^^^^^

The ``valueClaim`` config properties allow you to control how the :ref:`Account JSON <forwarded account json>` is
represented inside the JWT.

By default, the :ref:`Account JSON <forwarded account json>` is represented under a single JWT claim named
``user``.  This results in JWT claims that look something like this:

.. code-block:: javascript
   :emphasize-lines: 5-12

   {
     "iat": 1482972605,
     "iss": "my gateway",
     "aud": "my origin server",
     "user": {
       "username": "tk421",
       "email": "tk421@galacticempire.com",
       "givenName": "TK421",
       "middleName": null,
       "surname": "Stormtrooper"
       // ... other Account fields omitted for brevity ...
     }
     // ... other JWT fields omitted for brevity ...
   }


As you can see, the user account JSON is reflected as a single ``user`` claim, and the entire user account can be
retrieved by a single lookup of that claim.  This helps keep your user account information 'clean' and separate from
other JWT claims like ``iat``, ``iss``, ``aud``, etc.

If you prefer, you can :ref:`change the claim name <forwarded account jwt valueclaim name>` or
:ref:`not use a claim at all <forwarded account jwt valueclaim enabled>`
via the respective nested ``name`` and ``enabled`` properties.

.. tip::

   For you JWT experts out there, you might want to know why we didn't represent the user account with the
   `JWT sub claim <https://tools.ietf.org/html/rfc7519#section-4.1.2>`_ .  The ``sub`` claim is the RFC-standard claim
   that defines the target identity of the JWT, and the user account is the identity we care about, right?  So why
   didn't we just use the default ``sub`` claim instead of ``user``?

   The reason is that the JWT RFC (`RFC 7519 <https://tools.ietf.org/html/rfc7519>`_) says that the value of the ``sub``
   claim must be a ``StringOrURI`` data type value, as defined in
   `RFC 7519 section 2 (Terminology) <https://tools.ietf.org/html/rfc7519#section-2>`_.  The user account JSON is a full
   JSON object structure, which is neither a String nor a URI as required by the RFC.  So, we choose a different
   claim name to avoid any parsing/validation errors that JWT libraries might enforce for that claim, and all is well.


.. _forwarded account jwt valueclaim enabled:

``enabled``
"""""""""""

The :ref:`Account JSON <forwarded account json>` is nested in the JWT claims as single claim named ``user`` by
default.

If you don't want to use a specific value claim at all, and instead prefer to have the account properties mixed
together with all other JWT claims (like ``iat``, ``iss`` and ``aud``), you can disable the value claim concept
entirely by setting ``stormpath.zuul.account.header.jwt.valueClaim.enabled`` to ``false``:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             valueClaim:
               enabled: false


After setting this property to ``false``, all user account JSON name/value pairs are added directly to the JWT claims,
making each account property a claim itself.  The account properties and any other JWT-related ones are all
intermixed and 'just claims' as far as the JWT is concerned.  For example:


.. code-block:: javascript

   {
     "iat": 1482972605,
     "iss": "my gateway",
     "aud": "my origin server",
     "username": "tk421",
     "email": "tk421@galacticempire.com",
     "givenName": "TK421",
     "middleName": null,
     "surname": "Stormtrooper"
     // ... any other JWT or Account claims omitted for brevity ...
   }


.. _forwarded account jwt valueclaim name:

``name``
""""""""

The single value claim is named ``user`` by default.  You can change this name if you prefer by setting the
``stormpath.zuul.account.header.jwt.valueClaim.name`` config property.  For example:

.. code-block:: yaml

   stormpath:
     zuul:
       account:
         header:
           jwt:
             valueClaim:
               name: userAccount

This would result in JWT claims that look something like this:

.. code-block:: javascript
   :emphasize-lines: 5

   {
     "iat": 1482972605,
     "iss": "my gateway",
     "aud": "my origin server",
     "userAccount": {
       "username": "tk421",
       "email": "tk421@galacticempire.com",
       "givenName": "TK421",
       "middleName": null,
       "surname": "Stormtrooper"
       // ... other Account fields omitted for brevity ...
     }
     // ... other JWT fields omitted for brevity ...
   }


.. _forwarded account signing key bean:

Signing Key Bean
""""""""""""""""

If you are using an RSA or Elliptic Curve private key to sign the JWT, or you just prefer to specify your signing key
as a bean, you must provide the key by defining a ``stormpathForwardedAccountJwtSigningKey`` bean in your Spring
configuration:

.. code-block:: java

    @Bean
    public java.security.Key stormpathForwardedAccountJwtSigningKey() {
        //load the RSA or Elliptic Curve private key here and return it.
    }


You can also define this bean to provide your symmetric key for HMAC algorithms as well if you prefer not to
configure the HMAC signing key using the ``stormpath.zuul.account.header.jwt.key.k`` config property.


Custom Header Value
-------------------

Finally, if *none* of the above options are sufficient for you, don't worry, we still have you covered.  You can still
create any string you want as the header value with a little custom code.  You have three easy options:

1.  If you don't need access to the HttpServletRequest/Response pair and want to convert the Account to a
    Map that will be automatically turned into JSON or a JWT for you, you can define your own
    :ref:`account-to-map conversion function <forwarded account map function>` bean.

2.  If you don't need access to the HttpServletRequest/Response pair and want to do the full account to final
    header String conversion logic yourself, you can define your own
    :ref:`account-to-string conversion function <forwarded account to string function>` bean.

3.  If you need access to the HttpServletRequest/Response during the account-to-string conversion process, you can
    define your own :ref:`stormpathForwardedAccountHeaderValueResolver` bean.

In any case you will need to add the proper bean in your gateway Spring config.

.. note::

   Remember that adding or changing any of these beans will probably require changes to your origin server(s) -
   the origin server(s) will need to understand how to read the final Account string value created by your
   conversion bean.


.. _forwarded account map function:

Account-to-Map Function
^^^^^^^^^^^^^^^^^^^^^^^

If you don't need access to the HttpServletRequest/Response pair, and you just want to be able to convert an ``Account``
instance to a ``Map<String,?>``, you can define your own ``stormpathForwardedAccountMapFunction`` bean:

.. code-block:: java

   @Bean
   public Function<Account, ?> stormpathForwardedAccountMapFunction() {
       return new MyAccountToMapFunction(); //implement me
   }


This bean/method must be named ``stormpathForwardedAccountMapFunction`` and the bean must implement the
``com.stormpath.sdk.lang.Function<Account,?>`` interface.

When the gateway determines that there is an account to forward to an origin server, your custom function will be
called with an ``Account`` instance and it will return a ``Map<String,?>`` result.

This resulting map will be
converted to a JSON document automatically, and then potentially converted to a JWT depending on the value of the
:ref:`stormpath.zuul.account.header.jwt.enabled <forwarded account header jwt enabled>` property (which is enabled by
default).  If JWT is enabled, you can :ref:`customize the JWT as documented <forwarded account header jwt>` above.

The final resulting JSON or JWT string will be the header value.

.. note::

   If the resulting Map is ``null`` or empty, the header will not be present in the forwarded request at all.


.. _forwarded account to string function:

Account-to-String Function
^^^^^^^^^^^^^^^^^^^^^^^^^^

If you don't need access to the HttpServletRequest/Response pair, and you just want to be able to convert an ``Account``
instance to a String, you can define your own ``stormpathForwardedAccountStringFunction`` bean:

.. code-block:: java

   @Bean
   public Function<Account, String> stormpathForwardedAccountStringFunction() {
       return new MyAccountToStringFunction(); //implement me
   }

This bean/method must be named ``stormpathForwardedAccountStringFunction`` and the bean must implement the
``com.stormpath.sdk.lang.Function<Account,String>`` interface.

When the gateway determines that there is an account to forward to an origin server, your custom function will be
called with an ``Account`` instance and it will return a ``String`` result.  This resulting string will be the
header value sent to your origin server(s).

.. note::

   If the resulting string is ``null`` or empty, the header will not be present in the forwarded request at all.


.. _stormpathForwardedAccountHeaderValueResolver:

Account Header Value Resolver
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you need access to the HttpServletRequest/Response pair during the account-to-string conversion process, you can
define your own ``stormpathForwardedAccountHeaderValueResolver`` bean.  Be sure to autowire the
``accountResolver`` bean so that you can look up the ``Account`` associated with the request.  For example:

.. code-block:: java
   :emphasize-lines: 1,2,14

   @Autowired
   private AccountResolver accountResolver;

   @Bean
   public Resolver<String> stormpathForwardedAccountHeaderValueResolver() {

       //implement me.  For example:

       return new Resolver<String>() {

            @Override
            public String get(HttpServletRequest request, HttpServletResponse response) {

                Account account = accountResolver.getAccount(request);

                //convert this account to a String and return it :)
            }
        }
   }

This bean/method must be named ``stormpathForwardedAccountHeaderValueResolver`` and the the bean must implement the
``com.stormpath.sdk.servlet.http.Resolver<String>`` interface.

When the gateway determines that the request should be filtered and has an account present, your custom Resolver's
``get`` method will be called and you can find the associated account with the autowired ``accountResolver`` bean.  Once
you have an account instance, you can convert it to a String and return it however you like.

.. note::

   If the resulting string is ``null`` or empty, the header will not be present in the forwarded request at all.


.. _JWT: https://stormpath.com/blog/beginners-guide-jwts-in-java
.. _JSON Web Token (JWT): https://stormpath.com/blog/beginners-guide-jwts-in-java
.. _scalar: http://softwareengineering.stackexchange.com/questions/238033/what-does-it-mean-when-data-is-scalar