
Getting Started With Okta
=========================

Okay, so you have been using Stormpath for a while and now you want to checkout out Okta.  This guide will walk through
setting up a new account, creating an API token, and everything else needed to get you up and running on Okta.

Create an Okta Developer Account
--------------------------------

1. Head on over to: https://www.okta.com/developer/signup/
2. Fill out the signup form, and click the "Get Started" button
3. Within a few minutes you will get a conformation email
4. Follow the instructions in the email to finish setting up your account


Your Okta URLs
--------------

When setting up a developer account, you end up with a couple URL:
An admin console URL that looks something like this: 

https://dev-123456-admin.oktapreview.com/admin/getting-started

Use this one to manually change organization settings, create users, or other general administrative work.  The other URL looks similar, but is missing the `admin` part: 

https://dev-123456.oktapreview.com/

This is the one your yours could interact with, and will be the base URL for any API access.

**Important:** The second URL (the non-admin one) is the one you will need to remember, you will use this one for API access.
 
Setup your Okta 'Organization'
------------------------------

### Create an Application

1. Navigate to your Admin console: i.e. https://dev-123456-admin.oktapreview.com/admin/dashboard
2. On the top menu click on 'Applications'
3. Press the 'Add Application' button
4. Press the 'Create New App' button
5. On the 'Create a New Application Integration' popup fill select the following values, then press the 'Create' button
  - Platform - Native
  - Sign-on Method - OpenID Connect
6. On the 'Create OpenID Connect Integration' page enter the following values, and press the 'Next' button
  - Application Name - 'My Test Application'
7. Use `http://localhost:8080/client/callback` for the Redirect URI's, and press the 'Finish' button
8. Your application has been created, but you still have a few settings to change. On the 'General' tab, click the 'Edit' button on the 'General Settings' panel
9. Select the 'Refresh Token', and 'Resource Owner Password' checkboxes and click the 'Save' button
10. Click the 'Edit' button on the 'Client Credentials' panel
11. Select the 'Use Client Authentication' radio button, and press the 'Save' button
12. Click on the 'Groups' tab
13. Select the 'Assign' button in the 'Everyone' column, and press the 'Done' button
14. Grab the ID portion of the URL of your browsers current page, for example: if my URL was: `https://dev-123456-admin.oktapreview.com/admin/app/oidc_client/instance/00icu81200icu812/#tab-groups` then `00icu81200icu812` would be your application's ID

**Important:** You will need to remember your application's ID.

### Create an Access Token

1. Navigate to your Admin console: i.e. https://dev-123456-admin.oktapreview.com/admin/dashboard
2. On the top menu click on 'Security' -> 'API'
3. Select the 'Tokens' tab
4. Press the 'Create Token' button
5. On the popup, give your new token a name, for example: 'My Test Token', and press the 'Create Token' button

**Important:** You will need to remember this token value, so copy/paste it somewhere safe.

For more information take a look at the official [Create an API token](http://developer.okta.com/docs/api/getting_started/getting_a_token.html) guide.


Run an Example Application
--------------------------

Since you are reading this page on Github, I'm going to assume you know how to clone this repo, and switch to the `okta` branch, once you have done that, build the current SNAPSHOT with Apache Maven.

/Users/briandemers/dev/source/stormpath/stormpath-sdk-java

``` bash
$ mvn clean install
```

This should not take more then a couple minutes.

Once complete change directories to examples/spring-boot-default

``` bash
$ cd examples/spring-boot-default
```

The last step before running our example is to set your configuration variables, there are a [few different ways](https://docs.stormpath.com/java/servlet-plugin/config.html) you can do this, but I'll just use environment variables here.

``` bash
$ export STORMPATH_CLIENT_BASEURL=[baseurl_from_above]
$ export OKTA_APPLICATION_ID=[aapplication_id_from_above]
$ export OKTA_API_TOKEN=[api_token_from_above]
```

Start it up!

``` bash
$ mvn spring-boot:run
```

Point your browser to: http://localhost:8080 and start using the example application!

