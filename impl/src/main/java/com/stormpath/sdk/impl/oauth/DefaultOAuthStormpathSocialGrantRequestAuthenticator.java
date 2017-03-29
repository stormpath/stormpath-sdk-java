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

    private final String oauthTokenPath;

    public DefaultOAuthStormpathSocialGrantRequestAuthenticator(Application application, DataStore dataStore) {
        this(application, dataStore, OAUTH_TOKEN_PATH);
    }

    public DefaultOAuthStormpathSocialGrantRequestAuthenticator(Application application, DataStore dataStore, String oauthTokenPath) {
        super(application, dataStore);
        this.oauthTokenPath = oauthTokenPath;
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthStormpathSocialGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of OAuthStormpathSocialGrantRequestAuthentication.");
        OAuthStormpathSocialGrantRequestAuthentication authentication = (OAuthStormpathSocialGrantRequestAuthentication) authenticationRequest;

        OAuthStormpathSocialGrantAuthenticationAttempt authenticationAttempt = new DefaultOAuthStormpathSocialGrantAuthenticationAttempt(dataStore);
        authenticationAttempt.setGrantType(authentication.getGrantType());
        authenticationAttempt.setProviderId(authentication.getProviderId());
        if (authentication.getAccessToken() != null) {
            authenticationAttempt.setAccessToken(authentication.getAccessToken());
        } else if (authentication.getCode() != null) {
            authenticationAttempt.setCode(authentication.getCode());
        } else {
            throw new IllegalArgumentException("An accessToken or code is required for grant type 'stormpath_social'.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + oauthTokenPath, authenticationAttempt, GrantAuthenticationToken.class, httpHeaders);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthGrantRequestAuthenticationResultBuilder(grantResult);

        return builder.build();
    }
}
