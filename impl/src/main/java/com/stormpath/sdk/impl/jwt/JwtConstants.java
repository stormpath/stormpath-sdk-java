package com.stormpath.sdk.impl.jwt;

/**
 * JwtConstants
 *
 * @since 1.0.RC
 */
public abstract class JwtConstants {

    public static final String JWT_TOKENS_SEPARATOR = ".";

    public static final String REDIRECT_URI_PARAM_NAME = "cb_uri";
    public static final String NONCE_PARAM_NAME = "jti";
    public static final String ISSUED_AT_PARAM_NAME = "iat";
    public static final String PATH_PARAM_NAME = "path";

    public static final String JWT_PARAM_NAME = "jwt";
    public static final String JWR_REQUEST_PARAM_NAME = "jwtRequest";
    public static final String JWR_RESPONSE_PARAM_NAME = "jwtResponse";

    //request/response

    public static final String STATE_PARAM_NAME = "state";
    public static final String RESPONSE_NONCE_PARAMETER = "irt";

    //Id Token Parameters
    public static final String ISSUER_PARAM_NAME = "iss";
    public static final String SUBJECT_PARAM_NAME = "sub";
    public static final String IS_NEW_SUBJECT_PARAM_NAME = "isNewSub";
    public static final String AUDIENCE_PARAM_NAME = "aud";

    public static final String EXPIRE_PARAM_NAME = "exp";

    public static final String STATUS_PARAM_NAME = "status";

    private JwtConstants() {
    }
}
