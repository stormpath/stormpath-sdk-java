package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.*;

/**
 * @since 1.0.0
 */
public class DefaultOAuthClientCredentialsGrantRequestAuthenticator extends AbstractOAuthRequestAuthenticator implements OAuthClientCredentialsGrantRequestAuthenticator {

    private final static String OAUTH_TOKEN_PATH = "/oauth/token";

    public DefaultOAuthClientCredentialsGrantRequestAuthenticator(Application application, DataStore dataStore) {
        super(application, dataStore);
    }

    @Override
    public OAuthGrantRequestAuthenticationResult authenticate(OAuthRequestAuthentication authenticationRequest) {
        Assert.notNull(this.application, "application cannot be null or empty");
        Assert.isInstanceOf(OAuthClientCredentialsGrantRequestAuthentication.class, authenticationRequest, "authenticationRequest must be an instance of OAuthClientCredentialsGrantRequestAuthentication.");
        OAuthClientCredentialsGrantRequestAuthentication oAuthClientCredentialsGrantRequestAuthentication = (OAuthClientCredentialsGrantRequestAuthentication) authenticationRequest;

        OAuthClientCredentialsGrantAuthenticationAttempt oAuthClientCredentialsGrantAuthenticationAttempt = new DefaultOAuthClientCredentialsGrantAuthenticationAttempt(dataStore);
        oAuthClientCredentialsGrantAuthenticationAttempt.setGrantType(oAuthClientCredentialsGrantRequestAuthentication.getGrantType());
        oAuthClientCredentialsGrantAuthenticationAttempt.setApiKeyId(oAuthClientCredentialsGrantRequestAuthentication.getApiKeyId());
        oAuthClientCredentialsGrantAuthenticationAttempt.setApiKeySecret(oAuthClientCredentialsGrantRequestAuthentication.getApiKeySecret());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        GrantAuthenticationToken grantResult = dataStore.create(application.getHref() + OAUTH_TOKEN_PATH, oAuthClientCredentialsGrantAuthenticationAttempt, GrantAuthenticationToken.class, httpHeaders);

        OAuthGrantRequestAuthenticationResultBuilder builder = new DefaultOAuthClientCredentialsGrantRequestAuthenticationResultBuilder(grantResult);

        return builder.build();
    }
}
