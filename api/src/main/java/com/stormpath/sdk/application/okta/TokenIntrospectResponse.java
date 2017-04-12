package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.resource.Resource;

import java.util.Date;

/**
 *
 */
public interface TokenIntrospectResponse extends Resource, AccessTokenResult {

    boolean isActive();
//    String getScope();
    String getUsername();
    Date getExpiresAt();
    Date getIssuedAt();
    String getSubject();
    String getAudience();
    String getIssuer();
    String getJwtId();
    String getTokenType();
    String getClientId();
    String getUid();
}
