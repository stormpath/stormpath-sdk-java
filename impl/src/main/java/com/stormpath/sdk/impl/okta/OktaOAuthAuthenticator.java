package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.OAuthAuthenticator;
import com.stormpath.sdk.impl.authc.DefaultOktaAuthNAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.oauth.DefaultOAuthTokenRevocator;
import com.stormpath.sdk.impl.oauth.OktaOAuthPasswordGrantRequestAuthenticator;
import com.stormpath.sdk.impl.oauth.OktaOAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.impl.oauth.OktaOAuthRequestAuthenticator;
import com.stormpath.sdk.oauth.IdSiteAuthenticator;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthTokenRevocator;
import com.stormpath.sdk.okta.OktaOIDCWellKnownResource;

/**
 */
public class OktaOAuthAuthenticator implements OAuthAuthenticator {

    private final InternalDataStore dataStore;
    private final Application application;
    private final OktaSigningKeyResolver oktaSigningKeyResolver;

    private OktaOIDCWellKnownResource wellKnownResource;

    public OktaOAuthAuthenticator(String authorizationServerId,
                                  Application application,
                                  InternalDataStore dataStore) {
        this.dataStore = dataStore;
        this.application = application;

        String wellKnownUrlBaseUrl = authorizationServerId != null ? "/oauth2/"+authorizationServerId : "/";
        wellKnownResource = dataStore.getResource(wellKnownUrlBaseUrl + "/.well-known/openid-configuration", OktaOIDCWellKnownResource.class);

        this.oktaSigningKeyResolver = new DefaultOktaSigningKeyResolver(dataStore, authorizationServerId);
    }

    // TODO: remove the need for these
    public String getTokenEndpoint() {
        return wellKnownResource.getTokenEndpoint();
    }

    // TODO: remove the need for these
    public String getIntrospectionEndpoint() {
        return wellKnownResource.getIntrospectionEndpoint();
    }

    @Override
    public OAuthPasswordGrantRequestAuthenticator createPasswordGrantAuthenticator() {
        return new OktaOAuthPasswordGrantRequestAuthenticator(dataStore,
                                                              wellKnownResource.getTokenEndpoint(),
                                                              new DefaultOktaAuthNAuthenticator(
                                                                      dataStore,
                                                                      wellKnownResource.getTokenEndpoint(),
                                                                      wellKnownResource.getIntrospectionEndpoint()),
                                                              createOAuthTokenRevocator(),
                                                              createRefreshGrantAuthenticator());
    }

    @Override
    public OAuthRefreshTokenRequestAuthenticator createRefreshGrantAuthenticator() {
        return new OktaOAuthRefreshTokenRequestAuthenticator(dataStore,
                                                             wellKnownResource.getTokenEndpoint(),
                                                             new DefaultOktaAuthNAuthenticator(
                                                                    dataStore,
                                                                    wellKnownResource.getTokenEndpoint(),
                                                                    wellKnownResource.getIntrospectionEndpoint()),
                                                             createOAuthTokenRevocator());
    }

    @Override
    public OAuthBearerRequestAuthenticator createJwtAuthenticator() {
        return new OktaOAuthRequestAuthenticator(application,
                                                 dataStore,
                                                 new DefaultOktaAuthNAuthenticator(
                                                         dataStore,
                                                         wellKnownResource.getTokenEndpoint(),
                                                         wellKnownResource.getIntrospectionEndpoint()),
                                                 oktaSigningKeyResolver
        );
    }


    @Override
    public OAuthTokenRevocator createOAuthTokenRevocator() {
        return new DefaultOAuthTokenRevocator(dataStore, wellKnownResource.getRevocationEndpoint());
    }



    @Override
    public OAuthClientCredentialsGrantRequestAuthenticator createClientCredentialsGrantAuthenticator() {
        throw new UnsupportedOperationException("createClientCredentialsGrantAuthenticator() method hasn't been implemented.");
    }

    @Override
    public OAuthStormpathSocialGrantRequestAuthenticator createStormpathSocialGrantAuthenticator() {
        throw new UnsupportedOperationException("createStormpathSocialGrantAuthenticator() method hasn't been implemented.");
    }

    @Override
    public OAuthStormpathFactorChallengeGrantRequestAuthenticator createStormpathFactorChallengeGrantAuthenticator() {
        throw new UnsupportedOperationException("createStormpathFactorChallengeGrantAuthenticator() method hasn't been implemented.");
    }

    public IdSiteAuthenticator createIdSiteAuthenticator() {
        throw new UnsupportedOperationException("createIdSiteAuthenticator() method hasn't been implemented.");
    }

}
