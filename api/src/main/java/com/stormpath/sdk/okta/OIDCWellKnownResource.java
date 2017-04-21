package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

/**
 * Represents the response received from an OIDC .well-known endpoint
 */
public interface OIDCWellKnownResource extends Resource {

    String getAuthorizationEndpoint();
    OIDCWellKnownResource setAuthorizationEndpoint(String authorizationEndpoint);

    String getIntrospectionEndpoint();
    OIDCWellKnownResource setIntrospectionEndpoint(String introspectionEndpoint);

    String getJwksUri();
    OIDCWellKnownResource setJwksUri(String jwksUri);

    String getRevocationEndpoint();
    OIDCWellKnownResource setRevocationEndpoint(String revocationEndpoint);

    String getTokenEndpoint();
    OIDCWellKnownResource setTokenEndpoint(String tokenEndpoint);
    
}
