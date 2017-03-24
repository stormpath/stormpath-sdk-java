package com.stormpath.sdk.impl.okta;

/**
 * Static path properties in one place.
 */
public final class OktaApiPaths {

    public final static String API_V1 = "/api/v1/";

    public final static String USERS = "/api/v1/users/";

    public static String apiPath(String resource) {
        return API_V1 + resource;
    }

}
