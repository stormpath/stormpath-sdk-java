stormpath.web.account.jwt.ttl
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``259200``

Time to live for Account JWT. Default is 3 days.

stormpath.web.account.jwt.signatureAlgorithm
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``HS256``

The signature algorithm to use for signing a JWT.

stormpath.web.authc.savers.cookie.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Controls if a successful authentication should be reflected on future HTTP requests by using a digitally-signed account identity cookie.  To ensure HTTP Sessions (which force server state) are not required, this property is true by default.  The cookie's properties are configurable via the stormpath.web.account.cookie.* properties.

stormpath.web.authc.savers.session.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``

Controls if a successful authentication should be reflected by storing account state in the server-side HTTP session.  This property is false by default, preferring instead the performance benefits of a stateless digitally-signed account cookie via the stormpath.web.authc.savers.cookie.enabled property.

stormpath.web.request.remoteUser.strategy
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``username``

Controls the output of httpServletRequest.getRemoteUser() if the user is authenticated.  The value can be one of: email, username, givenName, href, or bypass.  email indicates getRemoteUser() will return account.getEmail(), username returns account.getUsername(), givenName returns account.getGivenName(), href returns account.getHref(), and bypass disables the Stormpath behavior for request.getRemoteUser() and falls back to the Servlet container's implementation.

stormpath.web.request.userPrincipal.strategy
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``account``

Controls the output of httpServletRequest.getUserPrincipal() if the user is authenticated.  The value can be one of: account, email, username, givenName, href, or bypass.  account indicates getUserPrincipal() will return a com.stormpath.sdk.servlet.http.AccountPrincipal instance that represents the entire Account object, email returns a com.stormpath.sdk.servlet.http.EmailPrincipal matching account.getEmail(), username returns a com.stormpath.sdk.servlet.http.UsernamePrincipal matching account.getUsername(), givenName returns a com.stormpath.sdk.servlet.http.GivenNamePrincipal matching account.getGivenName(), href returns a com.stormpath.sdk.servlet.http.HrefPrincipal matching account.getHref() and bypass disables Stormpath behavior for this property and falls back to the Servlet Container's implementation.

stormpath.web.request.client.attributeNames
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``client``

A comma-delimited list of names under which the com.stormpath.sdk.client.Client instance should be available as request attributes.  This allows you to call request.getAttribute("nameHere") to obtain the Client instance easily if desired and is often useful when obtaining the Client in a view model map by placeholder, e.g. ${client}.  The default value is client.

stormpath.web.request.application.attributeNames
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``application``

A comma-delimited list of names under which your application's com.stormpath.sdk.application.Application instance should be available as request attributes.  This allows you to call request.getAttribute("nameHere") to obtain the Application instance easily if desired and is often useful when obtaining the Application in a view model map by placeholder, e.g. ${application.name}.  The default value is application.

stormpath.web.csrf.token.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Wether or not to enable CSRF tokens.

stormpath.web.csrf.token.ttl
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``3600000``

The length of time in milliseconds that CSRF tokens in Stormpath-generated UI forms are valid before the form must be filled out again.  The default value is 3600000 (1 hour).  If Spring Security is enabled, this property is not used as Stormpath will delegate to Spring Security's CSRF generator instead.

stormpath.web.csrf.token.name
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``csrfToken``

The name of CSRF token that will be used in the forms. By default we used 'csrfToken' but Spring Security uses '_csrf'. In the latter case, the name is automatically changed when running in a Spring Security environment so there is no need for developers to change this name themselves in that case.

stormpath.web.nonce.cache.name
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``com.stormpath.sdk.servlet.nonces``

The name of the cache region to store nonce values used in Stormpath-generated security tokens and form tokens.  This cache region MUST have a TTL greater than or equal to the stormpath.web.csrf.token.ttl value.  The default name is com.stormpath.sdk.servlet.nonces to allow specific region configuration independent of other regions.

stormpath.web.stormpathFilter.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Defines if the StormpathFilter should enabled or not. This filter is responsible for providing all the Stormpath-related functionality in a Web environment. The StormpathFilter will ignore all filtered requests that do not match recognized URL rules, allowing other frameworks to filter requests as necessary.  Disabling this filter will prevent Stormpath web support from working correctly, but this property is provided as an option in case you wish to temporarily disable the filter for debugging or request tracing reasons.

stormpath.web.stormpathFilter.order
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``-2147483648``

The order that the StormpathFilter will have in the list of all the available filters. By default, the StormpathFilter has the highest precedence (Integer.MIN_VALUE) to ensure it can ensure identity functionality for subsequent filters.

stormpath.web.stormpathFilter.urlPatterns
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/*``

A comma-delimited list of ant-style patterns that indicate when the StormpathFilter should filter a request.  Any request not matching these patterns will bypass the StormpathFilter.  The default value is everything (/*) to ensure identity behavior can function for all application URIs.

stormpath.web.stormpathFilter.servletNames
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

A comma-delimited list of names that should be given to the StormpathFilter.

stormpath.web.stormpathFilter.dispatcherTypes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``REQUEST, INCLUDE, FORWARD, ERROR``

A comma-delimited list of the servlet dispatcher types that result in StormpathFilter execution.  Valid values are : REQUEST, INCLUDE, FORWARD, ERROR.  Defaults to all 4 values to ensure all requests are filtered.

stormpath.web.stormpathFilter.matchAfter
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``

Defines if the filter mappings for the StormpathFilter should be matched after any declared filter mappings of the ServletContext. Defaults to false, indicating the filters are supposed to be matched before any declared filter mappings of the ServletContext.

stormpath.web.head.view
~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``stormpath/head``

The template view name (Spring view name, not file name) that contains a <head> element that will be rendered in Stormpath views (login, logout, etc).  The DOM fragment to use within this view is defined by the stormpath.web.head.fragmentSelector property.

stormpath.web.head.fragmentSelector
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``head``

The CSS selector that locates the DOM element within the stormpath.web.head.view template that will be used as the actual rendered <head> element.

stormpath.web.head.cssUris
~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``https://fonts.googleapis.com/css?family=Open+Sans:300italic,300,400italic,400,600italic,600,700italic,700,800italic,800 https://netdna.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css /assets/css/stormpath.css``

One or more space-delimited URIs defining the CSS files to be used to style the views. This replaces the default base set of CSS files (Bootstrap + Stormpath defaults).  If you do not wish to completely override this value, and only wish to append your own CSS URIs, you can set the stormpath.web.head.extraCssUris instead.

stormpath.web.head.extraCssUris
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

You can override the default styles by re-defining any of the styles in a CSS file that you specify. This property is used to define extra or additional CSS files beyond the defaults defined via the stormpath.web.head.cssUris property.

stormpath.web.login.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not the Stormpath login view is enabled.

stormpath.web.login.uri
~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/login``

The context-relative path to the login view.

stormpath.web.login.nextUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/``

The context-relative path where the user will be redirected after logging in if a 'next' request parameter is missing.

stormpath.web.login.view
~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``stormpath/login``

The name of the Spring MVC view that should be rendered when visiting the stormpath.web.login.uri.  This is the name provided to the Spring MVC View Resolver.  The default value is 'stormpath/login' which will render a convenient default view provided by the Stormpath starter. If you want to render your own template instead of the default, set the name of your custom view here.

stormpath.web.forgotPassword.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not the Stormpath 'forgot password' view is enabled.

stormpath.web.forgotPassword.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/forgot``

The context-relative path to the 'forgot password' view.

stormpath.web.forgotPassword.nextUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/login?status=forgot``

The context-relative path where the user will be redirected after initiating the 'forgot password' flow if a 'next' request parameter is missing.  This value is '/login?status=fort', indicating the login view will be rendered by default, with a status that indicates why the user is on the login page.  This status can be interpreted by the view controller to customize the view if desired..

stormpath.web.forgotPassword.view
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``stormpath/forgot``

The name of the Spring MVC view that should be rendered when visiting the stormpath.web.forgotPassword.uri.  This is the name provided to the Spring MVC view resolver.  The default value is 'stormpath/forgot' which will render a convenient default view provided by the Stormpath starter. If you want to render your own template instead of the default, set the name of your custom view here.

stormpath.web.register.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not the Stormpath registration view (user self-registration) is enabled.

stormpath.web.register.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/register``

The context-relative path to the 'register' view where a new user can self-register for the application.

stormpath.web.register.nextUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/``

The context-relative path where the user will be redirected after registering, if a 'next' request parameter is missing. If 'email verification' is disabled and the user is directed to the registration view (by clicking a link or via a redirect), and the URI has a 'next' query parameter, the 'next' query parameter value will take precedence as the post-registration redirect location. If email verification is enabled, a page will be rendered asking the user to check their email.

stormpath.web.register.view
~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``stormpath/register``

The name of the Spring MVC view that should be rendered when visiting the stormpath.web.register.uri.  This is the name provided to the Spring MVC view resolver.  The default value is 'stormpath/register' which will render a convenient default view provided by the Stormpath starter.  If you want to render your own template instead of the default, set the name of your custom view here.

stormpath.web.verifyEmail.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not the Stormpath verification view is enabled.

stormpath.web.verifyEmail.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/verify``

The context-relative path to the email verification view. When a user clicks the link in the 'verify your email' email, the Stormpath starter will automatically process the resulting request. Caution: The fully qualified Link Base URL configured in the Stormpath Admin Console must always reflect the path configured via this property. If you change one, you must change the other.

stormpath.web.verifyEmail.nextUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/login?status=verified``

The context-relative path where the user will be redirected after verifying their email address.  The default value is '/login?status=verified', indicating the user will see the login view with a status indicating the user has verified their email.  The default login view will recognize the query parameter and show the user a nice message explaining that their account has been verified and that they can log in now.

stormpath.web.verifyEmail.view
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``stormpath/verify``

The name of the Spring MVC view that should be rendered to explain to the user that a verification email has just been sent out and that they need to click the received link in order to active the account. The default value is 'stormpath/verify' which will render a convenient default view provided by the Stormpath starter, but you may specify your own value to provide a custom view.

stormpath.web.logout.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not the Stormpath logout controller is enabled.

stormpath.web.logout.uri
~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/logout``

The context-relative path to the logout controller.  Logging out a user is as simple as redirecting them to this URI.

stormpath.web.logout.nextUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/``

The context-relative path where the user will be redirected after logout if a 'next' request parameter is missing. The default value is '/login?status=logout'. The default login view will recognize the query parameter and show the user a message confirming that he has successfully been logged out

stormpath.web.logout.invalidateHttpSession
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether the session should be invalidated at logout time or not.  The default is true for security reasons - it is generally never desirable to allow per-user session state to exist after logout.

stormpath.web.changePassword.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not the Stormpath 'change password' view is enabled.

stormpath.web.changePassword.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/change``

The context-relative path to the 'change password' view, where a user can change their password.

stormpath.web.changePassword.nextUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/login?status=changed``

The context-relative path where the user will be redirected after initiating a 'change password' flow.  The default value is '/login?status=changed'.  The default login view will recognize the query parameter and show the user a nice message explaining that their password has been successfully changed and that they can login now..

stormpath.web.changePassword.view
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``stormpath/change``

The name of the Spring MVC view that should be rendered when a user changes their password. The default value is 'stormpath/change' which will render a convenient default view provided by the Stormpath starter, but you may specify your own value to provide a custom view.

stormpath.web.oauth2.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether token authentication should be enabled or not. Token authentication, also called ‘Bearer Token Authentication’, is a convenient authentication mechanism for user interfaces that are not based on traditional server-side rendered pages, for example, Javascript clients or Single Page Applications (SPAs). Under the hood, the Stormpath starter implements this behavior according to the OAuth 2 specification. It implements the entire OAuth 2 ‘password grant’ flow for you.

stormpath.web.oauth2.uri
~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/oauth/token``

The context-relative path an HTTP client may POST to obtain an access token. The access token can be used by the client to authenticate subsequent HTTP requests.

stormpath.web.oauth2.origin.authorizer.originUris
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

This is only used for additional origin URIs that don't already match the server URI.

stormpath.web.idSite.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``

Whether Stormpath ID Site should be enabled or not. This setting tells your project to use the hosted ID Site for user registration, login, and password reset instead of the built-in local functionality. This is good if you have multiple apps that should have the same login experience.

stormpath.web.idSite.loginUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

The context-relative path to ID Site's login page. Null by default as it is assumed the ID Site root is the same as the login page (usually).  Only used when stormpath.web.idSite.enabled is true.

stormpath.web.idSite.registerUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/#/register``

The context-relative path to ID Site's registration page. Only used when stormpath.web.idSite.enabled is true.

stormpath.web.idSite.forgotUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/#/forgot``

The context-relative path to ID Site's 'forgot password' page. Only used when stormpath.web.idSite.enabled is true.

stormpath.web.idSite.resultUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/stormpathCallback``

The context-relative path where ID Site will call back into your application in order to inform about the outcome of the Id Site invocation.  Only used when stormpath.web.idSite.enabled is true.

stormpath.web.idSite.useSubdomain
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Uses ID Site defaults.``

Set it to 'true' to ensure that the user will visit ID Site using a subdomain equal to the Organization, 'false' to ensure that the standard ID Site domain will be used. Assume your ID Site is located at the domain 'id.myapp.com'. If you specify an Organization of 'greatcustomer' and set this property to 'true'', the user will be sent to 'https://greatcustomer.id.myapp.com' instead, providing for a more customized white-labeled URL experience.

stormpath.web.idSite.showOrganizationField
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Uses ID Site defaults.``

Ensures that ID Site will show the Organization field to the end-user in the ID Site user interface. Setting this to 'true' allows the user to see the field and potentially change the value. This might be useful if users can have accounts in different organizations - it would allow the user to specify which organization they want to login to.

stormpath.web.callback.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``

Whether login via a SAML provider will be enabled or not. This setting tells your project to use a 3rd party SAML login page to authenticate users instead of the built-in local functionality.

stormpath.web.callback.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/stormpathCallback``

The context-relative path where the SAML provider will call back into your application to inform about the outcome of the authentication attempt. Only relevant when 'stormpath.web.callback.enabled' is true.

stormpath.web.application.domain
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Inferred based on heuristics by default. However if your application is not deployed to an apex domain, like myapp.com, you *must* specify your application's base domain, e.g. 'myapp.mycompany.com'.``

The base domain of your application. For example if your app resides in 'myapp.mycompany.com', your base domain will be 'mycompany.com'.

stormpath.application.href
~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``



stormpath.web.oauth2.password.validationStrategy
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``local``



stormpath.web.accessTokenCookie.name
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``access_token``



stormpath.web.accessTokenCookie.httpOnly
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.accessTokenCookie.secure
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``



stormpath.web.accessTokenCookie.path
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``



stormpath.web.accessTokenCookie.domain
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``



stormpath.web.refreshTokenCookie.name
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``refresh_token``



stormpath.web.refreshTokenCookie.httpOnly
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.refreshTokenCookie.secure
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``



stormpath.web.refreshTokenCookie.path
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``



stormpath.web.refreshTokenCookie.domain
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``



stormpath.web.produces
~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``application/json``



stormpath.web.register.autoLogin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.register.form.fields.givenName.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.givenName.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.givenName.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``First Name``



stormpath.web.register.form.fields.givenName.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``First Name``



stormpath.web.register.form.fields.givenName.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.givenName.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``text``



stormpath.web.register.form.fields.middleName.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.register.form.fields.middleName.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.middleName.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Middle Name``



stormpath.web.register.form.fields.middleName.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Middle Name``



stormpath.web.register.form.fields.middleName.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.middleName.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``text``



stormpath.web.register.form.fields.surname.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.surname.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.surname.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Last Name``



stormpath.web.register.form.fields.surname.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Last Name``



stormpath.web.register.form.fields.surname.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.surname.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``text``



stormpath.web.register.form.fields.username.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.register.form.fields.username.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.username.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Username``



stormpath.web.register.form.fields.username.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Username``



stormpath.web.register.form.fields.username.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.username.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``text``



stormpath.web.register.form.fields.email.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.email.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.email.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Email``



stormpath.web.register.form.fields.email.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Email``



stormpath.web.register.form.fields.email.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.email.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``email``



stormpath.web.register.form.fields.password.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.password.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.password.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Password``



stormpath.web.register.form.fields.password.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Password``



stormpath.web.register.form.fields.password.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.password.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``password``



stormpath.web.register.form.fields.confirmPassword.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.register.form.fields.confirmPassword.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.confirmPassword.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Confirm Password``



stormpath.web.register.form.fields.confirmPassword.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Confirm Password``



stormpath.web.register.form.fields.confirmPassword.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.register.form.fields.confirmPassword.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``password``



stormpath.web.register.form.fieldOrder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``username``



stormpath.web.login.form.fields.login.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.login.form.fields.login.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.login.form.fields.login.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Username or Email``



stormpath.web.login.form.fields.login.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Username or Email``



stormpath.web.login.form.fields.login.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.login.form.fields.login.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``text``



stormpath.web.login.form.fields.password.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.login.form.fields.password.visible
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.login.form.fields.password.label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Password``



stormpath.web.login.form.fields.password.placeholder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``Password``



stormpath.web.login.form.fields.password.required
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.login.form.fields.password.type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``password``



stormpath.web.login.form.fieldOrder
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``login``



stormpath.web.changePassword.autoLogin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.changePassword.errorUri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/forgot?status=invalid_sptoken``



stormpath.web.social.facebook.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/callbacks/facebook``



stormpath.web.social.github.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/callbacks/github``



stormpath.web.social.google.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/callbacks/google``



stormpath.web.social.linkedin.uri
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/callbacks/linkedin``



stormpath.web.me.enabled
~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``



stormpath.web.me.uri
~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``/me``



stormpath.web.me.expand.apiKeys
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.me.expand.applications
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.me.expand.customData
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.me.expand.directory
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.me.expand.groupMemberships
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.me.expand.groups
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.me.expand.providerData
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.me.expand.tenant
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``false``



stormpath.web.http.authc.challenge
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not a failed HTTP Authentication attempt (via the Authorization header) should result in a HTTP 401 authentication challenge response, setting the WWW-Authenticate header.  This is expected behavior for HTTP Authentication, so the default value is true, but may be set to false you desire an exception be thrown instead.  This is mostly useful in certain testing scenarios, and it is strongly recommended to keep this value set to true to retain HTTP-compliant behavior.

stormpath.web.json.view.resolver.order
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``2147483637``

The resolver order for the InternalResourceViewResolver.

stormpath.web.jsp.view.resolver.order
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``2147483647``

The resolver order for the InternalResourceViewResolver.

stormpath.web.assets.defaultServletName
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``null``

The default servlet name for serving up static assets.

stormpath.web.assets.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not to enable static assets.

stormpath.web.assets.css.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not to enable CSS assets.

stormpath.web.assets.js.enabled
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``true``

Whether or not to enable JavaScript assets.

stormpath.web.assets.handlerMapping.order
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
**Default Value:** ``2147483647``

The resolver order for the static handlerMapping.

