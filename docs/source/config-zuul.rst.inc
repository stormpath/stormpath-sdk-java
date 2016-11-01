Zuul needs to know where your web application is, so we need to set some zuul configuration properties. As an example,
we'll assume that your webapp is available via ``http://localhost:8080`` ("localhost eighty eighty") and your
Spring Cloud Zuul gateway will be available via ``http://localhost:8000`` ("localhost eight thousand").

This means web traffic will go to ``http://localhost:8000`` to be handled by your Zuul gateway first, and then
the gateway will flow traffic as necessary through to your web application which is available at
``http://localhost:8080``.  Web clients (like browsers or mobile apps) will not communicate directly to
``localhost:8080`` anymore - instead they will only 'see' ``localhost:8000``.

Here is the minimal config to add to your Spring Cloud Zuul gateway project's application configuration:

.. code-block:: yaml

   zuul:
     routes:
       app:
         path: /**
         url: http://localhost:8080

   server:
     port: 8000
     use-forward-headers: true

   logging:
     level:
       root: INFO