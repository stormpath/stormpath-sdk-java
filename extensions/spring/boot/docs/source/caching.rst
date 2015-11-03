.. _caching:

Caching
=======

The Stormpath SDK Client supports caching to reduce round-trips to the Stormpath API servers and to improve performance.  By default, a single (non-clustered) in-process memory cache is enabled.

This behavior is relevant for applications that are deployed to a single JVM only.  If your application is deployed on multiple hosts/nodes simultaneously (i.e. a striped or clustered web application), then you should :ref:`configure a shared cache <cache config>` instead to ensure that cached data remains coherent across all application nodes.

Disable Caching
---------------

If you want to disable caching for the Stormpath SDK ``Client`` entirely, you can set the ``stormpath.cache.enabled`` property to ``false``:

.. code-block:: properties

    stormpath.cache.enabled = false

It is generally not recommended to disable caching, but it might be useful in certain testing scenarios or when multiple applications can frequently modify the same Stormpath data.

.. _cache config:

Cache Configuration
-------------------

If you want to control caching behavior, such as specific cache regions and time-to-live or time-to-idle timeouts, you just need to enable Spring's caching support.  If you configure a Spring ``CacheManager``, it will automatically be used for the Stormpath SDK Client's needs as well.  This ensures that the same cache mechanism is used in your project, ensuring consistent cache config for your application.

For example, specify the `@EnableCaching <http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/EnableCaching.html>`_ annotation on a Java Config class.  See Spring's `caching chapter <http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html>`_ and particularly, `configuring a cache store <http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html#cache-store-configuration>`_.

You can enable any of Spring's supported cache mechanisms and configure each cache region or TTL and TTI values accordingly.

.. caution::
    If your application is deployed on multiple hosts/nodes simultaneously, it is important to configure a Spring ``CacheManager`` that supports `coherent <http://en.wikipedia.org/wiki/Cache_coherence>`_ distributed memory, like Hazelcast, Redis, Gemfire, etc.  This ensures that all of your application nodes 'see' the same data and minimizes the likelihood of any one node seeing stale data.

Cache Regions
~~~~~~~~~~~~~

Resources retrieved from the Stormpath API servers are cached in regions named after the fully-qualified name of the *interface* (not concrete implementation class) of the resource type, for example:

* ``com.stormpath.sdk.application.Application``
* ``com.stormpath.sdk.account.Account``
* etc.

This allows you to configure region-specific TTL and TTI values based on type.  We typically recommend a minimum TTL and TTI of 5 minutes, potentially moving up to 1 hour or more if you have enough cache memory space.

Nonce Cache Region
^^^^^^^^^^^^^^^^^^

In addition to the type-specific regions mentioned above, another region exists to cache nonce values (nonce = 'number used once') for certain cryptographic values that should not be repeated at runtime. By default, the cache region is named ``com.stormpath.sdk.servlet.nonces`` and each nonce value will be cached in that region.

If you want to change the name of the region, you can set the ``stormpath.web.nonce.cache.name`` configuration property and specify your own region name.

.. caution::

    Because the nonce cache region is used for CSRF (Cross Site Request Forgery) attack prevention, any TTL or TTI specified for this region *must* be longer than the CSRF token TTL.  By default, the CSRF token TTL is 1 hour (3,600,000 milliseconds), so you must ensure that the nonce cache region has the same or longer TTL and TTI.

    Also, if you change the CSRF token TTL (via the ``stormpath.web.csrf.token.ttl`` property), you *must* ensure the nonce cache region TTL and TTI are both longer than the CSRF token TTL (even if it is just by 1 second).
