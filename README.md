[![Build Status](https://api.travis-ci.org/stormpath/stormpath-sdk-java.png?branch=master)](https://travis-ci.org/stormpath/stormpath-sdk-java)

# Stormpath Java SDK #

Copyright &copy; 2013 Stormpath, Inc. and contributors.

This project is open-source via the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

For all additional information, please see the full [Project Documentation](https://www.stormpath.com/docs/java/product-guide).

### Build Instructions ###

This project requires Maven 3.0.3 to build.  Run the following:

`> mvn install`

## Change Log ##

### 0.8.0 ###

#### Bug Fixes ####

*Collection Iteration (transparent pagination)*

Collection Resource iteration previously only represented the first page in a collection.  Iteration now transparently iterates over the entire collection, automatically requesting new pages from the server as necessary.  For example:

    AccountList accounts = application.getAccounts();

    //iterate over the entire account collection:
    for (Account account : accounts) {
        //do something with the account
    }


#### New Features and Improvements ####

*Caching*

The SDK now has full caching support, utilizing a CacheManager interface (that produces/manages Cache instances).  If enabled, this improves performance by reducing round-trips to the Stormpath API servers.

An out-of-the-box production-grade CacheManager implementation - complete with default and per-region TTL/TTI configuration - may be configured for single-JVM applications.  Single-JVM app example config:

    import static com.stormpath.sdk.cache.Caches.*;
    ...

    Client client = new ClientBuilder()
        .setApiKeyFileLocation(System.getProperty("user.home") + "/.stormpath/apiKey.properties")
        .setCacheManager(newCacheManager()
            .withDefaultTimeToLive(1, TimeUnit.DAYS) //general default
            .withDefaultTimeToIdle(2, TimeUnit.HOURS) //general default
            .withCache(forResource(Account.class) //Account-specific cache settings
                .withTimeToLive(1, TimeUnit.HOURS)
                .withTimeToIdle(30, TimeUnit.MINUTES))
            .withCache(forResource(Group.class) //Group-specific cache settings
                .withTimeToLive(2, TimeUnit.HOURS))
            .build() //build the CacheManager
        )
        .build(); //build the Client

Multi-JVM applications (an application deployed across multiple JVMs) would likely want to use a distributed/clustered coherent Cache product like Hazelcast, Ehcache+TerraCotta, Oracle Coherence, Gigaspaces, etc.  To leverage these caching products, you must implement the two interfaces (`CacheManager` and `Cache`) to delegate to your Caching provider API of choice, and you're on your way.  For example:

    CacheManager cacheManager = new CacheManagerImplementationThatUsesMyPreferredCachingProduct();
    Client client = new ClientBuilder()
        .setApiKeyFileLocation(System.getProperty("user.home") + "/.stormpath/apiKey.properties")
        .setCacheManager(cacheManager);
        .build();

In both cases, the Stormpath Java SDK will store resource data in separate cache regions.  Each region is named after a resource interface for which it caches data, e.g. `"com.stormpath.sdk.account.Account"`, allowing for custom caching rules per resource type.  This gives you finer control of resource caching behavior based on your preferences/needs.

*Query Support*

Two new query mechanisms were introduced - you choose which you want to use based on your preference and/or JVM language.

1. [Fluent](http://en.wikipedia.org/wiki/Fluent_interface) and type-safe query DSL: If you're using a type-safe language, you will find this convenient, especially when using an IDE that auto-completes.  You'll find writing valid queries fast!  For example:

        import static com.stormpath.sdk.account.Accounts.*;
        ...
         
        application.getAccounts(where(
            surname().containsIgnoreCase("Smith"))
            .and(givenName().eqIgnoreCase("John"))
            .orderBySurname().descending()
            .withGroups(10, 10) //resource expansion
            .offsetBy(20)
            .limitTo(25));

2. Map-based query methods.  These are not type safe, but might be desirable for some, maybe those using dynamically typed languages.  The map key/value pairs are simply REST API query parameters and values.  For example, the same results of the above fluent query could be achieved as follows in Groovy:

        application.getAccounts [surname: '*Smith*', givenName: 'John',
                                 orderBy: 'surname desc', expand: 'groups(10,10)'
                                 offset: 20, limit: 25]

*JavaDoc*

JavaDoc has been improved significantly.  But please don't hesitate to send us a Pull Request with fixes or enhancements!




