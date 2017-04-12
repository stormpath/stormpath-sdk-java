package com.stormpath.sdk.provider;

/**
 */
public interface OktaProvider extends OAuthProvider {

    String getAuthorizeBaseUri();

    String getIdp();

}
