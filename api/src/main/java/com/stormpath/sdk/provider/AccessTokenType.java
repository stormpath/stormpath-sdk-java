package com.stormpath.sdk.provider;


import com.stormpath.sdk.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * A value specifying how to present the accessToken credential when interacting with the authorizationEndpoint and/or
 * the tokenEndpoint.
 * Possible values are bearer (token as part of authorization header), access_token and oauth_token (both of which are
 * query parameters).
 *
 * Different OAuth2 providers support different ways to present the accessToken. Please read the provider's "OAuth2"
 * implementation documentation in detail to identify and pick the type that they support. This allows us to support
 * any current OAuth2 provider.
 * Additionally, we can support new types as they emerge by updating this enum.
 *
 * @since 1.3.0
 */
public enum AccessTokenType {

    BEARER("bearer"),
    ACCESS_TOKEN_PARAMETER("access_token"),
    OAUTH_TOKEN_PARAMETER("oauth_token");

    private static final Map<String, AccessTokenType> TOKEN_TYPE_MAP;

    static {
        TOKEN_TYPE_MAP = new HashMap<>();

        for (AccessTokenType accessTokenType : AccessTokenType.values()) {
            TOKEN_TYPE_MAP.put(accessTokenType.nameKey, accessTokenType);
        }
    }

    private String nameKey;

    AccessTokenType(String nameKey) {
        this.nameKey = nameKey;
    }

    public static AccessTokenType fromNameKey(String nameKey) {

        Assert.notNull(nameKey, "accessTokenType is required.");

        AccessTokenType accessTokenType = TOKEN_TYPE_MAP.get(nameKey.toLowerCase());

        Assert.notNull(accessTokenType, "Invalid accessTokenType : " + nameKey);

        return accessTokenType;
    }

    public String getNameKey() {
        return this.nameKey;
    }

}
