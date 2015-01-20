.. _caching:

Caching
=======

The Stormpath Servlet Plugin delegates to an SDK Client, which supports caching to reduce round-trips to the Stormpath API servers and to improve performance.

The plugin enables a single (non-clustered) in-process memory cache for the SDK Client by default.  This behavior is relevant for web applications that are deployed to a single JVM only.  If your web application is deployed on multiple web hosts/nodes simultaneously (i.e. a striped or clustered application), then you will likely want to :ref:`enable a shared cache <shared cache>` instead to ensure that cached data remains coherent across all web application nodes.

In-Process Cache
----------------

You can configure the out-of-the-box in-memory cache for the single-JVM application use case using :ref:`configuration properties <config>`.

The cache memory space is segmented into regions or 'sections', allowing you to specify different caching behavior for each region.

.. _default ttl:

Default TTL
^^^^^^^^^^^

TTL, or 'Time to Live' is how long a cache entry is allowed to remain in the cache, regardless of how often it is accessed.  After this amount of time, the cache entry will considered invalid and will removed ('evicted') from the cache.

You can specify the default TTL value for all cache regions by setting the ``stormpath.cache.ttl`` value in milliseconds:

.. code-block:: properties

   # 3,600,000 milliseconds = 1 hour
   stormpath.cache.ttl = 3600000

Default TTI
^^^^^^^^^^^

TTI, or 'Time to Idle' is how long a cache entry is allowed to remain idle (unreferenced, not used) in the cache before it can be removed ('evicted') from the cache.

You can specify the default TTI value for all cache regions by setting the ``stormpath.cache.tti`` value in milliseconds:

.. code-block:: properties

   # 3,600,000 milliseconds = 1 hour
   stormpath.cache.ttl = 3600000

Cache Regions
^^^^^^^^^^^^^

Cache Regions are automatically created as they are requested and use the default ttl and tti - you do not need to configure them explicitly.

However, if you want to specify a cache region's TTL or TTI, you can do so using a configuration property convention; prefix the region name ``stormpath.cache.`` and suffix the name with ``.ttl`` or ``tti`` for TTL or TTI respectively.

For example, let's assume we have a cache region named ``My Test Cache Region`` and wanted a 5 minute time-to-idle and a 30 minute time-to-live.  We'd define two configuration properties as follows:

.. code-block:: properties

   # 300,000 millis = 5 minutes:
   stormpath.cache.My Test Cache Region.tti = 300000

   # 1,800,000 millis = 30 minutes:
   stormpath.cache.My Test Cache Region.ttl = 1800000

If a cache region does not have configured ``.tti`` or ``.ttl`` values, the :ref:`default ttl and tti values <default ttl>` are assumed.

Client Cache Regions
^^^^^^^^^^^^^^^^^^^^

The Stormpath Client creates a cache region *per* resource data type.  That is, all cached Accounts are in one region, all cached Groups are in another, etc.  The region names are equal to the fully qualified *interface* name of each resource type.  For example:

* ``com.stormpath.sdk.account.Account``
* ``com.stormpath.sdk.group.Group``
* etc...

If you want to configure caching rules for a particular client resource type, when adding the necessary property prefix and suffix, you might have the following config lines (for example):

.. code-block:: properties

   stormpath.cache.com.stormpath.sdk.account.Account.tti = 3600000

   stormpath.cache.com.stormpath.sdk.group.Group.ttl = 4800000

.. _shared cache:

Shared Cache
------------

Each web application instance will, by default, have its *own* private in-process cache as described above.

However, if your web application .war is deployed on multiple JVMs - for example, you load balance requests across multiple identical web application nodes - you may experience data cache inconsistency problems if the default cache remains enabled: separate private cache instances are often not desirable because each web app instance could see its own 'version' of the cached data.

For example, if a user sends a request that is directed to web app instance A and then a subsequent request is directed to web app instance B, and the two instances do not agree on the same cached data, this could cause data integrity problems in many applications. This can be solved by using a shared or distributed cache to ensure cache consistency, also known as `cache coherence`_.

If you need cache coherency, you will want to specify a ``com.stormpath.sdk.cache.CacheManager`` implementation that can communicate with a shared or distributed cache system, like Hazelcast, Redis, etc.

You can do this by specifying the ``stormpath.cache.manager`` configuration property, for example:

.. code-block:: properties

   stormpath.cache.manager = your.fully.qualified.implementation.of.CacheManager


.. _cache coherence: http://en.wikipedia.org/wiki/Cache_coherence
