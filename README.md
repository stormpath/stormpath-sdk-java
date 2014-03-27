[![Build Status](https://api.travis-ci.org/stormpath/stormpath-sdk-java.png?branch=master)](https://travis-ci.org/stormpath/stormpath-sdk-java)

# Stormpath Java SDK #

Copyright &copy; 2013 Stormpath, Inc. and contributors.

This project is open-source via the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

For all additional information, please see the full [Project Documentation](https://www.stormpath.com/docs/java/product-guide).

### Build Instructions ###

This project requires Maven 3.0.3 to build.  Run the following:

`> mvn install`

## Change Log ##

### 0.9.4 ###

- [Issue 36](https://github.com/stormpath/stormpath-sdk-java/issues/36): Client version is now being obtained from version.properties file

### 0.9.3 ###

- [Issue 16](https://github.com/stormpath/stormpath-sdk-java/issues/16): Allow client to use basic authentication
- Backwards-incompatible change: com.stormpath.sdk.impl.http.support.SignatureException has been replaced by com.stormpath.sdk.impl.http.support.RequestAuthenticationException
- New method for Account: isMemberOfGroup(String hrefOrName)

### 0.9.2 ###

This is a bugfix point release that resolves [3 issues](https://github.com/stormpath/stormpath-sdk-java/issues?milestone=4&state=closed):

- [Issue 30](https://github.com/stormpath/stormpath-sdk-java/issues/30): Custom data updates are not cached when calling account/group save()
- [Issue 28](https://github.com/stormpath/stormpath-sdk-java/issues/28): ResourceException getMessage() should return a more descriptive message
- [Issue 31](https://github.com/stormpath/stormpath-sdk-java/issues/31): Provide more detailed ResourceException messages (duplicate of Issue #28).

### 0.9.1 ###

This is a bugfix point release that resolves [1 issue](https://github.com/stormpath/stormpath-sdk-java/issues?milestone=3):

- [Issue 25](https://github.com/stormpath/stormpath-sdk-java/issues/25): account.addGroup and group.addAccount do not work

### 0.9.0 ###

This is a [milestone](https://github.com/stormpath/stormpath-sdk-java/issues?milestone=2&page=1&state=closed) / new feature release.

#### Custom Data! ####

Our most requested feature is now available via the Stormpath Java SDK!

You now have the ability to create, update, delete up to 10 Megabytes of your own custom data per `Account` or `Group` stored within Stormpath.  This is a big deal: any account or group information that you can think of specific to your application(s) can now be stored directly with the account or group.  This allows you to completely remove user tables within your application if you desire.

Read the [Custom Data announcement](http://www.stormpath.com/blog/custom-user-data-stormpath-now-beta) and the [Custom Data REST API documentation](http://docs.stormpath.com/rest/product-guide/#custom-data) for more information and how to safely use Custom Data in your own applications.

`Custom Data` is a SDK Resource: you can save, update and delete it like any other.  But it is also a `java.util.Map<String,Object>` implementation:

```java
CustomData data = account.getCustomData();
data.put("favoriteColor", "blue");
data.remove("favoriteHobby");
data.save();
```
Because `CustomData` extends `Map<String,Object>`, you can store whatever data you want, but *NOTE*: 

The data *MUST* be JSON-compatible.  This means you can store primitives, and Maps, Arrays, Collections, nested as deep as you like.  Ensure the objects you persist can be marshalled to/from JSON via [Jackson](http://jackson.codehaus.org/) (what the Stormpath Java SDK uses for JSON marshalling).  Also a single `Custom Data` resource must be less than 10 Megabytes in size.

##### Persistance by Reachabliity #####

Custom Data is a resource like any other - you can `save()` modifications and `delete()` it if you like.  But, because it it has a 1-to-1 correlation with an owning `Account` or `Group`, it is a little extra special: you can also save, update and delete Custom Data just by saving the owning account or group.

For example, let's say you wanted to create a Starfleet account for [Captain Jean-Luc Picard](http://en.wikipedia.org/wiki/Jean-Luc_Picard).  Because Stormpath has no knowledge of Star Trek-specific needs, we can store this in the account's Custom Data resource:

```java
Application application = getApplication(); //obtain the app instance however you wish

Account account = client.instantiate(Account.class);
account.setGivenName("Jean-Luc");
account.setSurname("Picard");
account.setEmail("captain@starfleet.com");
account.setPassword("Changeme1!");

//let's store some application-specific data:
CustomData data = account.getCustomData();
data.put("rank", "Captain");
data.put("birthDate", "2305-07-13");
data.put("birthPlace", "La Barre, France");
data.put("favoriteDrink", "Earl Grey tea (hot)");

application.createAccount(account);
```
Notice how we did not call `data.save()` - creating the account (or updating it later via `save()`) will automatically persist the account's `customData` resource.  The account 'knows' that the custom data resource has been changed and it will propogate those changes automatically when you persist the account.

Groups work the same way - you can save a group and it's custom data resource will be saved as well.

*NOTE*: Just remember, if you have any secure data or information you don't want searchable, ensure you encrypt it before saving it in an account or group's custom data resource.  Read the [Custom Data announcement](http://www.stormpath.com/blog/custom-user-data-stormpath-now-beta) for usage guidelines.

#### Create Accounts via an Application ####

As a convenience, you may now create `account` and `group` resources directly via an application, without first needing to obtain the intermediate directory or group where they will be persisted:

```java
Application application = getApplication(); //obtain the app instance however you wish

Account account = client.instantiate(Account.class);
account.setGivenName("John");
account.setSurname("Smith");
account.setEmail("john@smith.com");
account.setPassword("Changeme1!");

application.createAccount(account);
```
You can also use the `CreateAccountRequest` concept to control other account creation directives.  For example, using the Fluent API:

```java
import static com.stormpath.sdk.account.Accounts.*;

...

account = application.createAccount(newCreateRequestFor(account).setRegistrationWorkflowEnabled(true).build());
```

Again, this is a convenience: The account will be routed to (and created in) the application's designated [default account store](http://docs.stormpath.com/rest/product-guide/#account-store-mapping-default-account-store), or throw an error if there is no designated account store.  

Because accounts are not 'owned' by applications (they are 'owned' by a directory), this will not make application 'private' accounts - it is merely a convenience mechanism to reduce the amount of work to create an account that may use a particular application.

#### Create Groups via an Application ####

Similar to accounts, as a convenience, you may create `group` resources directly via an application without first needing to obtain the intermediate directory where the group will be persisted:

```java
Application application = getApplication(); //obtain the app instance however you wish

Group group = client.instantiate(Group.class);
group.setName("Directory-unique name here");

application.createGroup(group);
```
You can also use the `CreateGroupRequest` variant to control other group creation directives.  For example, using the fluent API:

```java
import static com.stormpath.sdk.group.Groups.*;

...

group = application.createGroup(newCreateRequestFor(group).withResponseOptions(options().withCustomData()).build());
```

Remember, this is a convenience: The group will be routed to (and created in) the application's designated [default group store](http://docs.stormpath.com/rest/product-guide/#account-store-mapping-default-group-store), or throw an error if there is no designated group store.  

Because groups are not 'owned' by applications (they are 'owned' by a directory), this will not make application 'private' groups - it is merely a convenience mechanism to reduce the amount of work to create a group accessible to a particular application.

#### Create Cloud Directories ####

You may now create and delete 'Cloud' (natively hosted) directories in Stormpath with the Stormpath Java SDK.  LDAP and Active Directory 'Mirrored' directories must still be created in the Stormpath Admin Console UI.  For example:

```java
Directory dir = client.instantiate(Directory.class);
dir.setName("My new 'cloud' Directory");

dir = client.getCurrentTenant().createDirectory(dir);

...
//delete it when no longer useful
dir.delete();
```

#### Manage Account Store Mappings ####

[Account Store Mappings](http://docs.stormpath.com/rest/product-guide/#account-store-mappings) are useful in more advanced usages of Stormpath, for example, if you have more than one directory (or group) to assign to an application to create a merged user base for the application.

The Java SDK now allows you to add, remove, re-order and generally manage an Application's account store mappings for these more advanced use cases.  See the JavaDoc for the `AccountStoreMapping` resource and the following `Application` methods:

```java
AccountStoreMappingList getAccountStoreMappings();
AccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams);
AccountStoreMappingList getAccountStoreMappings(AccountStoreMappingCriteria criteria);
AccountStore getDefaultAccountStore();
void setDefaultAccountStore(AccountStore accountStore);
AccountStore getDefaultGroupStore();
void setDefaultGroupStore(AccountStore accountStore);
AccountStoreMapping createAccountStoreMapping(AccountStoreMapping mapping) throws ResourceException;
AccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException;
```

### 0.8.1 ###

This is a bugfix point release that resolves [1 issue](https://github.com/stormpath/stormpath-sdk-java/issues?milestone=1&page=1&state=closed):

- [Issue #12](https://github.com/stormpath/stormpath-sdk-java/issues/14) application.authenticateAccount fails if caching is enabled

### 0.8.0 ###

#### Bug Fixes ####

##### Collection Iteration (transparent pagination) #####

Collection Resource iteration previously only represented the first page in a collection.  Iteration now transparently iterates over the entire collection, automatically requesting new pages from the server as necessary.  For example:

```java
AccountList accounts = application.getAccounts();

//iterate over the entire account collection:
for (Account account : accounts) {
    //do something with the account
}
```

#### New Features and Improvements ####

##### Caching #####

The SDK now has full caching support, utilizing a CacheManager interface (that produces/manages Cache instances).  If enabled, this improves performance by reducing round-trips to the Stormpath API servers.

An out-of-the-box production-grade CacheManager implementation - complete with default and per-region TTL/TTI configuration - may be configured for single-JVM applications.  Single-JVM app example config:

```java
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
```

Multi-JVM applications (an application deployed across multiple JVMs) would likely want to use a distributed/clustered coherent Cache product like Hazelcast, Ehcache+TerraCotta, Oracle Coherence, Gigaspaces, etc.  To leverage these caching products, you must implement the two interfaces (`CacheManager` and `Cache`) to delegate to your Caching provider API of choice, and you're on your way.  For example:

```java
CacheManager cacheManager = new CacheManagerImplementationThatUsesMyPreferredCachingProduct();
Client client = new ClientBuilder()
    .setApiKeyFileLocation(System.getProperty("user.home") + "/.stormpath/apiKey.properties")
    .setCacheManager(cacheManager);
    .build();
```

In both cases, the Stormpath Java SDK will store resource data in separate cache regions.  Each region is named after a resource interface for which it caches data, e.g. `"com.stormpath.sdk.account.Account"`, allowing for custom caching rules per resource type.  This gives you finer control of resource caching behavior based on your preferences/needs.

##### Query Support #####

Two new query mechanisms were introduced - you choose which you want to use based on your preference and/or JVM language.

1. [Fluent](http://en.wikipedia.org/wiki/Fluent_interface) and type-safe query DSL: If you're using a type-safe language, you will find this convenient, especially when using an IDE that auto-completes.  You'll find writing valid queries fast!  For example:
    ```java
    import static com.stormpath.sdk.account.Accounts.*;
    ...
    
    application.getAccounts(where(
        surname().containsIgnoreCase("Smith"))
        .and(givenName().eqIgnoreCase("John"))
        .orderBySurname().descending()
        .withGroups(10, 10) //eager fetching
        .offsetBy(20).limitTo(25)); //pagination
    ```
2. Map-based query methods.  These are not type safe, but might be desirable for some developers, maybe those using dynamically typed languages.  The map key/value pairs are simply REST API query parameters and values.  For example, the same results of the above fluent query could be achieved as follows in Groovy:
    ```groovy
    application.getAccounts [surname: '*Smith*', givenName: 'John',
                             orderBy: 'surname desc', expand: 'groups(offset:10,limit:10)'
                             offset: 20, limit: 25]
    ```

##### JavaDoc Enhancements #####

JavaDoc has been improved significantly.  But please don't hesitate to send us a Pull Request with fixes or enhancements!
