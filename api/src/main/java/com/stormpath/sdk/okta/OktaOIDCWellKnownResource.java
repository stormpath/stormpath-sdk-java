package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

/**
 * Represents the response recieved from an OIDC .well-known endpoint
 */
public interface OktaOIDCWellKnownResource extends Resource {

    String getAuthorizationEndpoint();
    OktaOIDCWellKnownResource setAuthorizationEndpoint(String authorizationEndpoint);

    String getIntrospectionEndpoint();
    OktaOIDCWellKnownResource setIntrospectionEndpoint(String introspectionEndpoint);

    String getJwksUri();
    OktaOIDCWellKnownResource setJwksUri(String jwksUri);

    String getRevocationEndpoint();
    OktaOIDCWellKnownResource setRevocationEndpoint(String revocationEndpoint);

    String getTokenEndpoint();
    OktaOIDCWellKnownResource setTokenEndpoint(String tokenEndpoint);
}
