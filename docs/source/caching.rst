.. _caching:

Caching
=======

The |project| delegates to an SDK Client, which supports caching to reduce round-trips to the Stormpath API servers and to improve performance. By default, a single (non-clustered) in-process memory cache is enabled.

This behavior is relevant for ${apptype}s that are deployed to a single JVM only.  If your ${apptype} is deployed on multiple web hosts/nodes simultaneously (i.e. striped or clustered ${apptype}s), then you should :ref:`configure a shared cache <shared cache>` instead to ensure that cached data remains coherent across all ${apptype} instances.

#if( $servlet )

In-Process Cache
----------------

You can configure the out-of-the-box in-memory cache for the single-JVM application use case using :ref:`configuration properties <config>`.

The cache memory space is segmented into regions or 'sections', allowing you to specify different caching behavior for each region.

#end

Disable Caching
---------------

If you want to disable caching for the Stormpath SDK ``Client`` entirely, you can set the ``stormpath.client.cacheManager.enabled`` property to ``false``:

.. code-block:: properties

    stormpath.client.cacheManager.enabled = false

It is generally not recommended to disable caching, but it might be useful in certain testing scenarios or when multiple applications can frequently modify the same Stormpath data.

#if( $servlet )

.. _default ttl:

Default TTL
^^^^^^^^^^^

TTL, or 'Time to Live' is how long a cache entry is allowed to remain in the cache, regardless of how often it is accessed.  After this amount of time, the cache entry will considered invalid and will removed ('evicted') from the cache.

You can specify the default TTL value for all cache regions by setting the ``stormpath.client.cacheManager.defaultTtl`` value in milliseconds:

.. code-block:: properties

  # 3,600,000 milliseconds = 1 hour
  stormpath.client.cacheManager.defaultTtl = 3600000

Default TTI
^^^^^^^^^^^

TTI, or 'Time to Idle' is how long a cache entry is allowed to remain idle (unreferenced, not used) in the cache before it can be removed ('evicted') from the cache.

You can specify the default TTI value for all cache regions by setting the ``stormpath.client.cacheManager.defaultTti`` value in milliseconds:

.. code-block:: properties

  # 3,600,000 milliseconds = 1 hour
  stormpath.client.cacheManager.defaultTti = 3600000

Cache Regions
^^^^^^^^^^^^^

Cache Regions are automatically created as they are requested and use the default ttl and tti - you do not need to configure them explicitly.

However, if you want to specify a cache region's TTL or TTI, you can do so using a configuration property convention; prefix the region name ``stormpath.client.cacheManager.caches`` and suffix the name with ``.ttl`` or ``tti`` for TTL or TTI respectively.

For example, let's assume we have a cache region named ``My Test Cache Region`` and wanted a 5 minute time-to-idle and a 30 minute time-to-live.  We'd define two configuration properties as follows:

.. code-block:: properties

   # 300,000 millis = 5 minutes:
   stormpath.client.cacheManager.caches.My Test Cache Region.tti = 300000

   # 1,800,000 millis = 30 minutes:
   stormpath.client.cacheManager.caches.My Test Cache Region.ttl = 1800000

If a cache region does not have configured ``.tti`` or ``.ttl`` values, the :ref:`default ttl and tti values <default ttl>` are assumed.

Client Cache Regions
^^^^^^^^^^^^^^^^^^^^

The Stormpath Client creates a cache region *per* resource data type.  That is, all cached Accounts are in one region, all cached Groups are in another, etc.  The region names are equal to the fully qualified *interface* name of each resource type.  For example:

* ``com.stormpath.sdk.account.Account``
* ``com.stormpath.sdk.group.Group``
* etc...

If you want to configure caching rules for a particular client resource type, when adding the necessary property prefix and suffix, you might have the following config lines (for example):

.. code-block:: properties

   stormpath.client.cacheManager.caches.com.stormpath.sdk.account.Account.tti = 3600000

   stormpath.client.cacheManager.caches.com.stormpath.sdk.group.Group.ttl = 4800000

.. _shared cache:

Shared Cache
------------

Each web application instance will, by default, have its *own* private in-process cache as described above.

However, if your web application .war is deployed on multiple JVMs - for example, you load balance requests across multiple identical web application nodes - you may experience data cache inconsistency problems if the default cache remains enabled: separate private cache instances are often not desirable because each web app instance could see its own 'version' of the cached data.

For example, if a user sends a request that is directed to web app instance A and then a subsequent request is directed to web app instance B, and the two instances do not agree on the same cached data, this could cause data integrity problems in many applications. This can be solved by using a shared or distributed cache to ensure cache consistency, also known as `cache coherence`_.

If you need cache coherency, you will want to specify a ``com.stormpath.sdk.cache.CacheManager`` implementation that can communicate with a shared or distributed cache system, like Hazelcast, Redis, etc.

You can do this by specifying the ``stormpath.client.cacheManager`` configuration property, for example:

.. code-block:: properties

   stormpath.client.cacheManager = your.fully.qualified.implementation.of.CacheManager

Nonce Cache Region
^^^^^^^^^^^^^^^^^^

In addition to the type-specific regions mentioned above, another region exists to cache nonce values (nonce = 'number used once') for certain cryptographic values that should not be repeated at runtime. By default, the cache region is named ``com.stormpath.sdk.servlet.nonces`` and each nonce value will be cached in that region.

If you want to change the name of the region, you can set the ``stormpath.web.nonce.cache.name`` configuration property and specify your own region name.

.. caution::

    Because the nonce cache region is used for CSRF (Cross Site Request Forgery) attack prevention, any TTL or TTI specified for this region *must* be longer than the CSRF token TTL.  By default, the CSRF token TTL is 1 hour (3,600,000 milliseconds), so you must ensure that the nonce cache region has the same or longer TTL and TTI.

    Also, if you change the CSRF token TTL (via the ``stormpath.web.csrf.token.ttl`` property), you *must* ensure the nonce cache region TTL and TTI are both longer than the CSRF token TTL (even if it is just by 1 second).

#else

.. _shared cache:
.. _cache config:

Cache Configuration
-------------------

If you want to control caching behavior, such as specific cache regions and time-to-live or time-to-idle timeouts, you just need to enable Spring's caching support.  If you configure a Spring ``CacheManager``, it will automatically be used for the Stormpath SDK Client's needs as well.  This ensures that the same cache mechanism is used in your project, ensuring consistent cache config for your ${apptype}.

For example, specify the `@EnableCaching <http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/EnableCaching.html>`_ annotation on a Java Config class.  See Spring's `caching chapter <http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html>`_ and particularly, `configuring a cache store <http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html#cache-store-configuration>`_.

You can enable any of Spring's supported cache mechanisms and configure each cache region or TTL and TTI values accordingly.

.. caution::

   If your ${apptype} is deployed on multiple hosts/nodes simultaneously, it is important to configure a Spring ``CacheManager`` that supports `coherent <http://en.wikipedia.org/wiki/Cache_coherence>`_ distributed memory, like Hazelcast, Redis, Gemfire, etc.  This ensures that all of your ${apptype} instances 'see' the same data and minimizes the likelihood of any one instance seeing stale data.

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

#end

.. _cache coherence: http://en.wikipedia.org/wiki/Cache_coherence
