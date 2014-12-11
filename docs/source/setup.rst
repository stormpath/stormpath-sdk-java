.. _setup:


Setup
=====

This section covers the basic setup you need to perform in order to get started with the Stormpath Servlet Plugin.


Register with Stormpath
-----------------------

Now that you've decided to use Stormpath, the first thing you'll want to use is sign up for Stormpath if you haven't already: https://api.stormpath.com/register


Create an API Key Pair
----------------------

Once you've registered, create a new API key pair by logging into your dashboard and clicking the "Create an API Key" button.  This will generate a new API key for you, and prompt you to download your key in a file.  This key is required to communicate with the Stormpath REST API.

.. note::
    Please keep the API key file you just downloaded safe!  This key pair it contains allows anyone that has it to make Stormpath API requests, and should be properly protected, backed up, etc.

Once you've downloaded your ``apiKey.properties`` file, save it in your home directory in a file named ``~/.stormpath/apiKey.properties``.  To ensure no other users on your system can access the file, you'll also want to change the file's permissions.  For example:

.. code-block:: bash

    $ chmod go-rwx ~/.stormpath/apiKey.properties

Just to be safe, you might also want to prevent yourself from writing/modifying the file:

.. code-block:: bash

    $ chmod u-w ~/.stormpath/apiKey.properties

That's it! You're all ready to go to start using Stormpath in your web application!

Next, we'll cover how to add Stormpath to your web application project.

Add Stormpath Servlet Support
-----------------------------

Stormpath can be added to a web application project in one of two ways:

* Option 1: Using an automatic :ref:`plugin .jar <pluginjar>`
* Option 2: Manually :ref:`editing web.xml <webxml>`

The automatic plugin is recommended for most projects, especially new projects.  However, if you find that adding the plugin might alter existing functionality, manual web.xml changes will give you the flexibility to add Stormpath successfully.

.. _pluginjar:

Option 1: Automatic Plugin jar
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is the easiest and most 'hands off' approach to adding Stormpath to a web application, and it allows you to deploy Stormpath *without a single line of code or configuration*.  How amazing is that? Here's how.

Using your favorite dependency resolution build tool like Maven or Gradle, ensure your web (.war) project/module depends on stormpath-servlet-plugin-|version|.jar. For example:

Maven:

.. code-block:: xml

    <dependency>
        <groupId>com.stormpath.sdk</groupId>
        <artifactId>stormpath-servlet-plugin</artifactId>
        <version>|version|</version>
    </dependency>

Gradle:

.. code-block:: groovy

    dependencies {
        compile 'com.stormpath.sdk:stormpath-servlet-plugin:|version|'
    }

Where ``|version|`` is |version|.

Ensure that all resolved dependencies are in your .war file's ``/WEB-INF/lib`` directory.

That's it!  You're ready to start using Stormpath, and optionally customize things based on some configuration.  We'll cover configuration options soon.


.. _webxml:

Option 2: Edit web.xml
^^^^^^^^^^^^^^^^^^^^^^

If you do not want to use the automatic plugin, you will need to edit your web application's ``web.xml`` file manually.  But it's ok! There are only a few lines to add.

The Stormpath Servlet support uses servlet filters to provide much of its functionality.  If you use your own servlet filters or rely on frameworks that also use servlet filters (like Spring or Apache Shiro), servlet filter order of execution is often important and can be a bit finicky.  In these environments its often easier to define the Stormpath Servlet support components in ``web.xml`` yourself so you have explicit control over which filters are executed in which order.

If you are going to modify ``web.xml`` yourself, you **should not use the plugin .jar mentioned above**.  Instead, you'll depend on a different .jar and then modify ``web.xml``.


Dependency
""""""""""

Using your favorite dependency resolution build tool like Maven or Gradle, ensure your web (``.war``) project/module depends on stormpath-sdk-servlet-|version|.jar. For example:

Maven:

.. code-block:: xml

    <dependency>
        <groupId>com.stormpath.sdk</groupId>
        <artifactId>stormpath-sdk-servlet</artifactId>
        <version>|version|</version>
    </dependency>

Gradle:

.. code-block:: groovy

    dependencies {
        compile 'com.stormpath.sdk:stormpath-sdk-servlet:|version|'
    }

Where ``|version|`` is |version|.

Ensure that all resolved dependencies are in your .war file's ``/WEB-INF/lib`` directory.

Edit ``web.xml``
""""""""""""""""

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

There we go - initial setup is all done.  Let's test it!
