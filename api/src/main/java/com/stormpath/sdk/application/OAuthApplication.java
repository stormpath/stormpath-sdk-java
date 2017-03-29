package com.stormpath.sdk.application;

import com.stormpath.sdk.oauth.IdSiteAuthenticator;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthTokenRevocator;

/**
 * Marks an Application as supporting OAuth and adds required methods for handling tokens.
 */
public interface OAuthApplication extends Application {

    OAuthClientCredentialsGrantRequestAuthenticator createClientCredentialsGrantAuthenticator();

    OAuthStormpathSocialGrantRequestAuthenticator createStormpathSocialGrantAuthenticator();

    OAuthStormpathFactorChallengeGrantRequestAuthenticator createStormpathFactorChallengeGrantAuthenticator();

    OAuthPasswordGrantRequestAuthenticator createPasswordGrantAuthenticator();

    OAuthRefreshTokenRequestAuthenticator createRefreshGrantAuthenticator();

    OAuthBearerRequestAuthenticator createJwtAuthenticator();

    OAuthTokenRevocator createOAuhtTokenRevocator();

    // FIXME: this shouldn't be here, but not sure how much of the IdSite code is just OAuth functionality.
    IdSiteAuthenticator createIdSiteAuthenticator();

}
