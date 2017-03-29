package com.stormpath.sdk.impl.http.authc;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.client.PairedApiKey;
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.support.RequestAuthenticationException;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;

import java.nio.charset.Charset;

public class SswsAuthenticator implements RequestAuthenticator {

    public static final String AUTHENTICATION_SCHEME = "SSWS";

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private final ApiKeyCredentials apiKeyCredentials;
    private final PairedApiKey pairedApiKey;

    public SswsAuthenticator(ApiKeyCredentials apiKeyCredentials) {
        Assert.notNull(apiKeyCredentials, "apiKeyCredentials must be not be null.");
        this.apiKeyCredentials = apiKeyCredentials;

        ApiKey apiKey = apiKeyCredentials.getApiKey();
        Assert.notNull(apiKey, "apiKeyCredentials argument cannot have a null apiKey");

        Assert.isInstanceOf(PairedApiKey.class, apiKey, "apiKeyCredentials.getApiKey() must be a PairedApiKey instance");
        this.pairedApiKey = (PairedApiKey) apiKey;
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {

        if (request.getResourceUrl().getPath().startsWith("/oauth2/")) {

            ApiKey oauthClientCreds = this.pairedApiKey.getSecondaryApiKey();
            Assert.notNull(oauthClientCreds, "PairedApiKey credentials must have a secondary api key when invoking an Okta /oauth2/ endpoint.");

            String basicCreds = oauthClientCreds.getId() + ":" + oauthClientCreds.getSecret();
            String base64 = Base64.encodeBase64String(basicCreds.getBytes(UTF8));


            request.getHeaders().set(AUTHORIZATION_HEADER, "Basic " + base64);

            return;
        }


        request.getHeaders().set(AUTHORIZATION_HEADER, AUTHENTICATION_SCHEME + " " + apiKeyCredentials.getSecret());
    }
}
