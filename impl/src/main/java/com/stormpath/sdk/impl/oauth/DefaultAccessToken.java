/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.error.jwt.InvalidJwtException;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @since 1.0.RC7
 */
public class DefaultAccessToken extends AbstractBaseOAuthToken implements AccessToken {

    private static final Logger log = LoggerFactory.getLogger(DefaultAccessToken.class);

    public DefaultAccessToken(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        ensureAccessToken();
    }

    /**
     * This method will validate that the received jwt corresponds to an `access_token` (as opposed to a
     * `refresh_token`). If that is not the case then this operation will throw a JwtException. It is called
     * from the constructor, so an AccessToken cannot be instantiated without verifying the jwt is a proper
     * `access_token`
     *
     * @since 1.0.RC8.3
     */
    private void ensureAccessToken() {
        if(isMaterialized()) {
            try {
                JwsHeader header = Jwts.parser()
                        .setSigningKey(getDataStore().getApiKey().getSecret().getBytes("UTF-8"))
                        .parseClaimsJws(getString(JWT)).getHeader();

                String tokenType = header != null ? (String) header.get("stt") : null;

                if (tokenType == null) {
                    String message = "Missing 'stt' property in header. This jwt is not a valid access_token.";
                    log.debug(message);
                    throw new InvalidJwtException(message);
                }

                Assert.isTrue(tokenType.equals("access"));
            } catch (UnsupportedJwtException uje) {
                String message = InvalidJwtException.JWT_INVALID_VALUE_ERROR;
                log.debug(message);
                throw new InvalidJwtException(message, uje);
            } catch (MalformedJwtException mje) {
                String message = "The JWT was not correctly constructed and therefore was rejected (it is not a valid JWS).";
                log.debug(message);
                throw new InvalidJwtException(message, mje);
            } catch (SignatureException se) {
                String message = InvalidJwtException.INVALID_JWT_SIGNATURE_ERROR;
                log.debug(message);
                throw new InvalidJwtException(message, se);
            } catch (ExpiredJwtException eje) {
                String message = InvalidJwtException.EXPIRED_JWT_ERROR;
                log.debug(message);
                throw new InvalidJwtException(message, eje);
            } catch (IllegalArgumentException iae) {
                String message = "The JWT is null, empty or only contains whitespaces.";
                log.debug(message);
                throw new InvalidJwtException(message, iae);
            } catch (UnsupportedEncodingException uee) {
                String message = "The character encoding for the API secret is not supported.";
                log.debug(message);
                throw new InvalidJwtException(message, uee);
            } catch (Exception e) {
                //Todo 2.0.0: this JwtException must be replaced by InvalidJwtException. See https://github.com/stormpath/stormpath-sdk-java/issues/1018
                throw new JwtException("JWT failed validation; it cannot be trusted.", e);
            }
         } else {
            String href = getStringProperty(HREF_PROP_NAME);
            if (href != null) {
                Assert.isTrue(href.contains("/accessTokens/"), "href does not belong to an access token.");
            }
        }
    }
}
