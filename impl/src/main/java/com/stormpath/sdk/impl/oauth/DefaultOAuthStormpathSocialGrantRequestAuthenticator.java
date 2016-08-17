package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthenticator;

/**
 * @since 1.1.0
 */
public class DefaultOAuthStormpathSocialGrantRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthStormpathSocialGrantRequestAuthenticator {

    private final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultOAuthStormpathSocialGrantRequestAuthenticator(Application application, DataStore dataStore) {
        super(application, dataStore);
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthStormpathSocialGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of OAuthStormpathSocialGrantRequestAuthentication.");
        OAuthStormpathSocialGrantRequestAuthentication oAuthStormpathSocialGrantRequestAuthentication = (OAuthStormpathSocialGrantRequestAuthentication) authenticationRequest;

        OAuthStormpathSocialGrantAuthenticationAttempt oAuthStormpathSocialGrantAuthenticationAttempt = new DefaultOAuthStormpathSocialGrantAuthenticationAttempt(dataStore);
        oAuthStormpathSocialGrantAuthenticationAttempt.setGrantType(oAuthStormpathSocialGrantRequestAuthentication.getGrantType());
        oAuthStormpathSocialGrantAuthenticationAttempt.setApiKeyId(oAuthStormpathSocialGrantRequestAuthentication.getApiKeyId());
        oAuthStormpathSocialGrantAuthenticationAttempt.setApiKeySecret(oAuthStormpathSocialGrantRequestAuthentication.getApiKeySecret());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, oAuthStormpathSocialGrantAuthenticationAttempt, GrantAuthenticationToken.class, httpHeaders);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthStormpathSocialGrantRequestAuthenticationResultBuilder(grantResult);

        return builder.build();
    }
}
