
**Please remember that this integration is in an early state and not appropriate for production use.**

Getting Started With Okta
=========================

Okay, so you've been using Stormpath for a while and now you want to check out out Okta. This guide will walk you through
setting up a new account, creating an API token, and everything else you'll need to get up and running on Okta.

Create an Okta Developer Account
--------------------------------

1. Head on over to: https://www.okta.com/developer/signup/
2. Fill out the signup form, and click the "Get Started" button
3. Within a few minutes you will get a conformation email
4. Follow the instructions in the email to finish setting up your account


Your Okta URLs
--------------

When setting up a developer account, you end up with a couple URLs.
The first is an admin console URL that looks something like this: 

https://dev-123456-admin.oktapreview.com/admin/getting-started

Use this one to manually change organization settings, create users, or other general administrative work.  

The other URL looks similar, but is missing the `admin` part: 

https://dev-123456.oktapreview.com/

This is the one your users could interact with, and will be the base URL for any API access.

**Important:** The second URL (the non-admin one) is the one you will need to remember, you will use this URL for API access.
 
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

Since you are reading this page on Github, I'm going to assume you know how to clone this repo, and switch to the `okta` branch. Once you have done that, build the current SNAPSHOT with Apache Maven.

``` bash
$ mvn clean install
```

This should not take more then a couple minutes.

Once complete, change directories to examples/spring-boot-default (the Spring and Servlet examples work too!)

``` bash
$ cd examples/spring-boot-default
```

The last step before running the example is to set your configuration variables. There are a [few different ways](https://docs.stormpath.com/java/servlet-plugin/config.html) you can do this, but we'll just use environment variables here.

``` bash
$ export STORMPATH_CLIENT_BASEURL=[baseurl_from_above]
$ export OKTA_APPLICATION_ID=[application_id_from_above]
$ export OKTA_API_TOKEN=[api_token_from_above]
```

Now, start it up...

``` bash
$ mvn spring-boot:run
```

Point your browser to: http://localhost:8080 and you're ready to start using the example application!


## Additional Configuration

| Property | Description | Default Value |
|----------|-------------|---------------|
| stormpath.registration.workflow.enabled | Require email verification before logging in | false |
| stormpath.email.hostname | Email Server Hostname | localhost |
| stormpath.email.port | Email Server Port | 25 |
| stormpath.email.sslEnabled | SSL enabled | false |
| stormpath.email.sslCheckServerIdentityEnabled | SSL Verify Server Identity  | false |
| stormpath.email.tlsEnabled | TLS Enabled | false |
| stormpath.email.username | Email Username | n/a |
| stormpath.email.password | Email Password | n/a |
| stormpath.email.verifyEmailTemplate | JSON file from Stormpath Export used for email verification | /com/stormpath/sdk/mail/templates/verifyEmail.json |
| stormpath.email.forgotPasswordTemplate | SON file from Stormpath Export used for forgot password/reset | /com/stormpath/sdk/mail/templates/forgotPassword.json |
| stormpath.application.allowApiClientCredentials | See notes about client credentials below | false |


### Email Configuration

These integrations use [Apache Commons Email](https://commons.apache.org/proper/commons-email/userguide.html) to send email verification and forgot password/reset emails.

**NOTE:** these work flows require a mail server.

Gmail can be configured with the following settings (generate an [App-Password](https://support.google.com/accounts/answer/185833?hl=en)):

```
-Dstormpath.email.hostname=smtp.gmail.com
-Dstormpath.email.port=587
-Dstormpath.email.tlsEnabled=true
-Dstormpath.email.username=<email@address>
-Dstormpath.email.password=<app-password>
```

Email template json files can be found in the `emailTemplates` folder of your Stormpath data export, just include them on the classpath of your application.


### Required Okta Configuration Change

In order for email verification and password reset workflows to work you will need add additional properties to Okta user profile.

1. Log into your Okta Admin Console
2. Select 'Directory' from the top menu, then select 'Profile Editor'
3. Click on the 'Profile' button on the top Entry in the table for 'User'
4. For each item in the table create new Attributes:

  | Display name | Variable name |
  |--------------|---------------|
  | Email Verification Status | emailVerificationStatus |
  | Email Verification Token | emailVerificationToken |
  | Stormpath Migration Recovery Answer | stormpathMigrationRecoveryAnswer |
  
5. Click the 'Add Attribute' button
6. Enter the 'Display name' and 'Variable name' from above
7. Click the 'Save' or 'Save and Add Another' button when you are finished.

**NOTE:** The Stormpath -> Okta import process will create these fields for you, but the `emailVerificationToken` attribute size will be too small, you can edit this attribute and either remove the min/max attribute length or set the max to 10000.

Read more about the Okta [Profile Editor](https://help.okta.com/en/prod/Content/Topics/Directory/Directory_Profile_Editor.htm).


### Client Credentials

Client credentials are supported by storing a plain text versions of your client credential id:secret in user profile data (similar to Stormpath's Custom Data).  To use this feature you MUST enable it using the 
`stormpath.application.allowApiClientCredentials=true` property. NOTE: the validation of the id and secret is done via the integration and there will be no audit log on Okta's server of these log ins.  Use this feature while migrating only.  Alternatives: Create an Okta Application and use it's client id/secret, or create 'service user' accounts and use basic authentication.


