.. _setup:


Setup
=====

This section covers the basic setup you need to perform in order to get started with the Stormpath Servlet Plugin.

Topics:

.. contents:: :local:
   :depth: 2


Register with Stormpath
-----------------------

Now that you've decided to use Stormpath, the first thing you'll want to use is sign up for Stormpath if you haven't already: https://api.stormpath.com/register

Fill out the form and hit submit, and we'll send you an email with further instructions.


Get an API Key
--------------

Once you've registered, create a new API key pair by logging into your dashboard and clicking the "Create an API Key" button.  This will generate a new API key for you, and prompt you to download your key in a file.  This key is required to communicate with the Stormpath REST API.

.. warning::

    Please keep this API key file super private!  This key uniquely identifies you and only you - it should never be shared or viewable by anyone else, not even your co-workers.

    Stormpath employees will never ask you for your API Key.

.. _api-key-file-location:

Recommended API Key File Location
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Once you've downloaded your ``apiKey.properties`` file, save it in your home directory in the following location:

* ``~/.stormpath/apiKey.properties`` on Unix, Linux and Mac OS
* ``C:\Users\YOUR_USERNAME\.stormpath\apiKey.properties`` on Windows (where ``YOUR_USERNAME`` is your actual Windows login username).

Many Stormpath libraries and tools look for your API Key in this location automatically, simplifying your setup work.

To ensure no other users on your system can access the file, you'll also want to change the file's permissions.  For example, on Unix, Linux or Mac OS:

.. code-block:: bash

    $ chmod go-rwx ~/.stormpath/apiKey.properties

To be safe, you might also want to prevent yourself from accidentally writing/modifying the file:

.. code-block:: bash

    $ chmod u-w ~/.stormpath/apiKey.properties

On Windows, you can `set file permissions similarly`_.

Next, we'll cover how to add Stormpath to your web application project.

Add Stormpath Servlet Support
-----------------------------

Stormpath can be added to a web application project in one of two ways:

.. contents:: :local:
   :depth: 1

The plugin is recommended for most projects, especially new projects.  If you have an established project and you notice that adding the plugin might alter or break existing functionality, manually editing ``web.xml`` will give you the flexibility to add Stormpath successfully.

Option 1: Automatic plugin jar
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is the easiest and most 'hands off' approach to adding Stormpath to a web application, and it allows you to deploy Stormpath *without a single line of code or configuration*.  How amazing is that? Here's how.

Using your favorite dependency resolution build tool like Maven or Gradle, ensure your web (.war) project/module depends on stormpath-servlet-plugin-|version|.jar. For example:

**Maven**:

.. parsed-literal::

    <dependency>
        <groupId>com.stormpath.sdk</groupId>
        <artifactId>stormpath-servlet-plugin</artifactId>
        <version>\ |version|\ </version>
    </dependency>

**Gradle**:

.. parsed-literal::

    dependencies {
        compile 'com.stormpath.sdk:stormpath-servlet-plugin:\ |version|\ '
    }

Ensure that all resolved dependencies are in your web application's ``/WEB-INF/lib`` directory.

That's it!  You're ready to start using Stormpath, and optionally customize things based on some configuration.  We'll cover configuration options soon.

Option 2: Manually edit web.xml
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you do not want to use the automatic plugin, you will need to edit your web application's ``web.xml`` file manually.  But it's ok! There are only a few lines to add.

The Stormpath Servlet support uses servlet filters to provide much of its functionality.  If you use your own servlet filters or rely on frameworks that also use servlet filters (like Spring or Apache Shiro), servlet filter order of execution is often important and can be a bit finicky.  In these environments its sometimes easier to define the Stormpath Servlet support components in ``web.xml`` yourself so you have explicit control over filter execution order.

If you are going to modify ``web.xml`` yourself, you **should not use the plugin .jar mentioned above**.  Instead, you'll depend on a different .jar and then modify ``web.xml``.


Dependency
""""""""""

Using your favorite dependency resolution build tool like Maven or Gradle, ensure your web (``.war``) project/module depends on stormpath-sdk-servlet-|version|.jar. For example:

**Maven**:

.. parsed-literal::

    <dependency>
        <groupId>com.stormpath.sdk</groupId>
        <artifactId>stormpath-sdk-servlet</artifactId>
        <version>\ |version|\ </version>
    </dependency>

**Gradle**:

.. parsed-literal::

    dependencies {
        compile 'com.stormpath.sdk:stormpath-sdk-servlet:\ |version|\ '
    }

Ensure that all resolved dependencies are in your web application's ``/WEB-INF/lib`` directory.

Edit web.xml
""""""""""""

Copy and paste the following lines in to your web application's ``/WEB-INF/web.xml`` file:

.. code-block:: xml

    <listener>
        <!-- Load the Stormpath config. Config is most likely defined in /WEB-INF/stormpath.properties: -->
        <listener-class>com.stormpath.sdk.servlet.config.DefaultConfigLoaderListener</listener-class>
    </listener>
    <listener>
        <!-- Load the Stormpath client w/ caching enabled. Customize client config in /WEB-INF/stormpath.properties: -->
        <listener-class>com.stormpath.sdk.servlet.client.DefaultClientLoaderListener</listener-class>
    </listener>
    <listener>
        <!-- Load the webapp's Stormpath Application resource, referenced during various request flows: -->
        <listener-class>com.stormpath.sdk.servlet.application.DefaultApplicationLoaderListener</listener-class>
    </listener>
    <filter>
        <filter-name>StormpathFilter</filter-name>
        <filter-class>com.stormpath.sdk.servlet.filter.StormpathFilter</filter-class>
    </filter>
    <!-- Make sure any request you want accessible to Stormpath is filtered. /* catches all
        requests.  This filter mapping is usually defined in front of other filters to ensure
        that Stormpath authentication can work in subsequent filters in the filter chain: -->
    <filter-mapping>
        <filter-name>StormpathFilter</filter-name>
        <url-pattern>/*</url-pattern>
        <dispatcher>REQUEST</dispatcher>
        <dispatcher>FORWARD</dispatcher>
        <dispatcher>INCLUDE</dispatcher>
        <dispatcher>ERROR</dispatcher>
    </filter-mapping>


Notice the final ``<filter-mapping>`` definition.  In most servlet containers, you specify filter ordering (relative to other filters) by moving the ``<filter-mapping>`` definition above or below other ``<filter-mapping>`` definitions.

The Stormpath ``<filter-mapping>`` should generally be the first in the filter chain, but it could potentially sit behind others.  If you later find that Stormpath-supported URLs in your application do not work, you will likely need to move the Stormpath ``<filter-mapping>`` declaration higher in the file, above other filters.

Specify your API Key
--------------------

If you saved your API Key file to the :ref:`recommended file location <api-key-file-location>` and your web application can read this file at startup, you're all done - you don't have to do anything! The file will be read automatically.

However, if you did not save your API Key file to the recommended location, or your web application does not have access to the file system (for example, some production environments do not have file system access), then you will need to specify your API Key in another way.

API Key Configuration Options
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you do not use the :ref:`recommended file location <api-key-file-location>`, you can specify your API Key (and we'll see later, configuration options in general) in many ways, depending on your needs.

The following locations are checked *in order* - any values found in later locations will automatically override any previously discovered values:

1. Environment Variables
""""""""""""""""""""""""

* ``STORMPATH_API_KEY_ID=your_api_key_id``
* ``STORMPATH_API_KEY_SECRET=your_api_key_secret``

2. classpath:stormpath.properties
"""""""""""""""""""""""""""""""""

If a file named ``stormpath.properties`` exists at the root of the classpath, the following two properties will be used if found:

* ``stormpath.apiKey.id = your_api_key_id``
* ``stormpath.apiKey.secret = your_api_key_secret``

**\*** While ``stormpath.apiKey.secret`` will be used if specified, it is **strongly** recommended that you do not hard code your API Key secret into text files (like ``classpath:stormpath.properties``) that are checked in to version control or shared with other developers.  Environment variables or a private local file are a safer alternative.

3. /WEB-INF/stormpath.properties
""""""""""""""""""""""""""""""""

If a file ``/WEB-INF/stormpath.properties`` exists in your web application, the following two properties will be used if found:

* ``stormpath.apiKey.id = your_api_key_id``
* ``stormpath.apiKey.secret = your_api_key_secret``

**\*** While ``stormpath.apiKey.secret`` will be used if specified, it is **strongly** recommended that you do not hard code your API Key secret into text files (like ``/WEB-INF/stormpath.properties``) that are checked in to version control or shared with other developers.  Environment variables or a private local file are a safer alternative.

4. Servlet Context Parameters
"""""""""""""""""""""""""""""

If you define the following servlet context parameters in your web application's ``/WEB-INF/web.xml`` file, they will be used (and override any previously discovered value):

.. code-block:: xml

    <context-param>
        <param-name>stormpath.apiKey.id</param-name>
        <param-value>your_api_key_id</param-value>
    </context-param>
    <context-param>
        <param-name>stormpath.apiKey.secret</param-name>
        <param-value>your_api_key_secret</param-value> <!-- * See note below -->
    </context-param>

**\*** While ``stormpath.apiKey.secret`` will be used if specified, it is **strongly** recommended that you do not hard code your API Key secret into text files (like ``/WEB-INF/web.xml``) that are checked in to version control (like git) or shared with other developers.  Environment variables or a private local file are a safer alternative.


5. JVM System Properties
""""""""""""""""""""""""

* ``-Dstormpath.apiKey.id=your_api_key_id_here``
* ``-Dstormpath.apiKey.secret=your_apiKey_secret_here``

**\*** Even if the ``stormpath.apiKey.secret`` system property is not visible to other developers, it can still be seen as a security risk in some environments: system property values are visible to anyone performing a process listing on a production machine (e.g. ``ps aux | grep java``).  Environment variables or a private local file are usually a safer alternative.

.. _set file permissions similarly: http://msdn.microsoft.com/en-us/library/bb727008.aspx

