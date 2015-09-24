/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.error.jwt;

/**
 * A sub-class of {@link RuntimeException} representing an attempt to use a <a href="http://self-issued.info/docs/draft-ietf-oauth-json-web-token.html">
 * Json Web Token</a> with an invalid signature.
 *
 * @since 1.0.RC
 */
public class InvalidJwtException extends RuntimeException {

    public static final String JWT_REQUIRED_ERROR = "JWT parameter is required..";

    public static final String JWT_INVALID_VALUE_ERROR = "The jwt value format is not correct.";

    public static final String INVALID_JWT_BODY_ENCODING_ERROR = "JWT json body cannot be decoded.";

    public static final String INVALID_JWT_HEADER_ENCODING_ERROR = "JWT json header cannot be decoded.";

    public static final String INVALID_JWT_SIGNATURE_ERROR = "JWT Signature is invalid.";

    public static final String EXPIRED_JWT_ERROR = "JWT has already expired.";

    public static final String ALREADY_USED_JWT_ERROR = "JWT has already been used.";

    public static final String JWT_RESPONSE_MISSING_PARAMETER_ERROR = "Required jwtResponse parameter is missing.";

    public static final String JWT_RESPONSE_INVALID_APIKEY_ID_ERROR = "The client used to sign the jwrResponse is different than the one used in this datasore.";

    public InvalidJwtException(String jwtError) {
        super(jwtError);
    }
}
