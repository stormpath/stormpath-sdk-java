.. _apiKeyConfig:

TODO: this file is just a placeholder and was trimmed down to clean up the quickstart.  However, it contains more exhaustive information that will be included into the doc as soon as the configuration section is written...

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