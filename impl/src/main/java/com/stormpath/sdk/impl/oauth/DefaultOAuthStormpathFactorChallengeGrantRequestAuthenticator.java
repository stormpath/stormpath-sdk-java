package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthenticator;

/**
 * @since 1.3.1
 */
public class DefaultOAuthStormpathFactorChallengeGrantRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthStormpathFactorChallengeGrantRequestAuthenticator {

    private final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultOAuthStormpathFactorChallengeGrantRequestAuthenticator(Application application, DataStore dataStore) {
        super(application, dataStore);
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthStormpathFactorChallengeGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of OAuthStormpathFactorChallengeGrantRequestAuthentication.");
        OAuthStormpathFactorChallengeGrantRequestAuthentication authentication = (OAuthStormpathFactorChallengeGrantRequestAuthentication) authenticationRequest;

        OAuthStormpathFactorChallengeGrantAuthenticationAttempt authenticationAttempt = new DefaultOAuthStormpathFactorChallengeGrantAuthenticationAttempt(dataStore);
        authenticationAttempt.setGrantType(authentication.getGrantType());
        authenticationAttempt.setChallenge(authentication.getChallenge());
        authenticationAttempt.setCode(authentication.getCode());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, authenticationAttempt, GrantAuthenticationToken.class, httpHeaders);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);

        return builder.build();
    }
}
