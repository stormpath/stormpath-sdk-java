stormpath.client.baseUrl
~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``https://api.stormpath.com/v1``

The base URL the Stormpath Client will use when communicating with the Stormpath REST API.  If unspecified, the default value is https://api.stormpath.com/v1 but may be overridden when communicating with enterprise or private Stormpath deployments with custom domain names.  Note the '/v1' path at the end of the URL - a custom URL will almost always specify a version path after the domain name.

stormpath.client.connectionTimeout
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``0``

Sets both the timeout until a Stormpath Client connection is established as well as the connection socket timeout (i.e. a maximum period of inactivity between two consecutive data packets).  A timeout value of zero (the default) is interpreted as an infinite timeout.

stormpath.enabled
~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

A boolean flag that can disable all Stormpath Spring Boot starters.  This is mostly useful during testing or debugging, or if you want to compare behavior when Stormpath is enabled or disabled.

stormpath.client.apiKey.id
~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

The Stormpath API Key ID used by the Client to authenticate with the Stormpath REST API.  If unspecified, the value will be attempted to be read from the $HOME/.stormpath/apiKey.properties file.  If this file is not available or cannot be read, you must specify this configuration value in one of Spring Boot's accessible property definition locations as described here: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html.

stormpath.client.apiKey.secret
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

The Stormpath API Key Secret used by the Client to authenticate with the Stormpath REST API.  IT IS STRONGLY RECOMMENDED THAT YOU DO NOT STORE THIS VALUE IN SOURCE CODE AS DOING SO IS A SECURITY RISK.  If unspecified, the value will be attempted to be read from the $HOME/.stormpath/apiKey.properties file.  If this file is not available or cannot be read, you must specify this configuration value in one of Spring Boot's accessible property definition locations as described here: http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html.  If external config is necessary, it is strongly recommended to use the STORMPATH_API_KEY_SECRET environment variable so this value is not committed to version control.

stormpath.client.apiKey.file
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

A Spring resource URL that points to the apiKey.properties file that contains the Client API Key ID and/or Secret.  This can be used to specify a configuration file for obtaining apiKey.properties instead of configuring them as Spring configuration properties if desired.

stormpath.client.apiKey.fileIdPropertyName
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``apiKey.id``

The name of the API Key ID property to read in the referenced apiKey.properties file.  Only used if reading a apiKey.properties resource specified via the stormpath.client.apiKey.file property, defaults to apiKey.id.

stormpath.client.apiKey.fileSecretPropertyName
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``apiKey.secret``

The name of the API Key Secret property to read in the referenced apiKey.properties file.  Only used if reading a apiKey.properties resource specified via the stormpath.client.apiKey.file property, defaults to apiKey.secret.

stormpath.client.cacheManager.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

A boolean flag that can be used to disable the client's cache, defaults to true.

stormpath.client.cacheManager.defaultTtl
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``300``

A value that sets the default time to live (in seconds) for the client's cache, defaults to 300.

stormpath.client.cacheManager.defaultTti
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``300``

A value that sets the default time to idle (in seconds) for the client's cache, defaults to 300.

stormpath.application.href
~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

The fully qualified URL of the Application REST Resource in Stormpath that corresponds to the Spring Boot application being deployed.  If unspecified, the Client will attempt to find this Application for you automatically.  However, if you have more than one Application registered in Stormpath, you MUST specify this value otherwise the Client will not know which application to use.

stormpath.client.authenticationScheme
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``SAUTHC1``

The authentication scheme the Client should use when communicating with the Stormpath REST API, either BASIC or SAUTHC1.  If unspecified, the default value is SAUTHC, the most secure optiona available.  BASIC is NOT recommended and should only be used in environments where SAUTCH1 is not possible (for example, users have indicated that BASIC is required when running on Google App Engine). 

stormpath.client.proxy.host
~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

The fully qualified host name or IP address of the proxy server to connect through when communicating with the Stormpath REST API.  This is disabled by default and only needs to be specified if you require outbound HTTP traffic to flow through a proxy server first (for example, in a corporate intranet).

stormpath.client.proxy.port
~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``80``

The port on the proxy server to connect to when communicating with the Stormpath REST API.  This value is only used if stormpath.client.proxy.host is configured, and defaults to 80.

stormpath.client.proxy.username
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

Tbe username to use when connecting to the proxy server used when communicating with the Stormpath REST API.  Do not set this value if username/password authentication to the proxy server is not required.  This value is only read if stormpath.client.proxy.host is set.

stormpath.client.proxy.password
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

Tbe password to use when connecting to the proxy server used when communicating with the Stormpath REST API.  Do not set this value if username/password authentication to the proxy server is not required.  This value is only read if stormpath.client.proxy.host is set.  It is STRONGLY recommended that passwords not be set in files that are committed to version control - if you need to set this property, it is recommended to set the STORMPATH_PROXY_PASSWORD environment variable instead.

