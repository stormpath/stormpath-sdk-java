package com.stormpath.sdk.impl.okta;

/**
 * Static path properties in one place.
 */
public final class OktaApiPaths {

    public static final String API_V1 = "/api/v1/";

    public static final String USERS = API_V1 + "users/";

    public static final String PASSWORD_RECOVERY = API_V1 + "authn/recovery/password";

    public static String apiPath(String resource) {
        return API_V1 + resource;
    }

}
