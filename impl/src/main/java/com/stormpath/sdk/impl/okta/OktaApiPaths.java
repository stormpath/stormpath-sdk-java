package com.stormpath.sdk.impl.okta;

/**
 * Static path properties in one place.
 */
public final class OktaApiPaths {

    public static final String API_V1 = "/api/v1/";

    public static final String USERS = API_V1 + "users/";

    public static final String PASSWORD_RECOVERY = API_V1 + "authn/recovery/password";

    public static String apiPath(String ... parts) {
        StringBuilder urlPart = new StringBuilder(API_V1);
        for (String part : parts) {
            if ('/' != urlPart.charAt(urlPart.length()-1)) {
                urlPart.append("/");
            }
            urlPart.append(part);
        }
        return urlPart.toString();
    }

}
