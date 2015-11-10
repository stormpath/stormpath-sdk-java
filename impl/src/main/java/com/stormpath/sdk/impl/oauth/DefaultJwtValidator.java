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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.application.DefaultApplication;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import java.io.UnsupportedEncodingException;

/**
 * @since 1.0.RC6
 */
public class DefaultJwtValidator implements JwtValidator {

    private InternalDataStore dataStore;

    private Application application;

    public DefaultJwtValidator(Application app, DataStore dataStore){
        this.dataStore = (InternalDataStore) dataStore;
        this.application = app;
    }

    @Override
    public boolean validate(JwtValidationRequest jwtValidationRequest) {

        if (jwtValidationRequest.isWithLocalValidation()){
            byte[] bytes = null;
            String apiKeySecret = dataStore.getApiKey().getSecret();
            try {
                bytes = apiKeySecret.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e){
                return false;
            }
            try {
                // if JWT can be parsed correctly, then it's a valid JWT
                Claims claims = Jwts.parser()
                        .setSigningKey(bytes)
                        .parseClaimsJws(jwtValidationRequest.getJwt()).getBody();

                return true;
            } catch (SignatureException e){
                // JWT signature could not be validated and so it cannot be trusted
                return false;
            }
        } else {
            JwtAuthenticationRequest authRequest = new DefaultJwtAuthenticationRequest(jwtValidationRequest.getJwt());
            JwtAuthenticator jwtAuthenticator = new DefaultJwtAuthenticator(application, dataStore);
            JwtAuthenticationResult result = jwtAuthenticator.authenticate(authRequest);
            return result != null;
        }
    }
}
