package com.stormpath.sdk.provider;


import com.stormpath.sdk.lang.Assert;

import java.util.HashMap;
import java.util.Map;

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
