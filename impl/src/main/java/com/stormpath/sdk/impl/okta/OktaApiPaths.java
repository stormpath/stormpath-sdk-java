package com.stormpath.sdk.impl.okta;

/**
 * Static path properties in one place.
 */
public final class OktaApiPaths {

    public static final String API_V1 = "/api/v1/";

    public static final String OAUTH2_V1 = "/oauth2/v1/";

    public static final String USERS = API_V1 + "users/";

    public static final String PASSWORD_RECOVERY = API_V1 + "authn/recovery/password";

    public static String oauthPath(String ... parts) {
        return buildPath(OAUTH2_V1, parts);
    }

    public static String apiPath(String ... parts) {

        return buildPath(API_V1, parts);
    }

    private static String buildPath(String base, String ... parts) {
        StringBuilder urlPart = new StringBuilder(base);
        for (String part : parts) {
            if ('/' != urlPart.charAt(urlPart.length()-1)) {
                urlPart.append("/");
            }
            urlPart.append(part);
        }
        return urlPart.toString();
    }

}
