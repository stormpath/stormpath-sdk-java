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
package com.stormpath.sdk.impl.sso;

import com.stormpath.sdk.account.AccountResult;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.account.DefaultAccountResult;
import com.stormpath.sdk.impl.authc.HttpServletRequestWrapper;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.jwt.JwtSignatureValidator;
import com.stormpath.sdk.impl.jwt.JwtWrapper;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.impl.jwt.JwtConstants.*;

/**
 * @since 1.0.RC
 */
public class DefaultSsoIdentityResolver {

    private static final String HTTP_SERVLET_REQUEST_FQCN = "javax.servlet.http.HttpServletRequest";

    private static final String HTTP_SERVLET_REQUEST_WRAPPER_FQCN = "com.stormpath.sdk.impl.authc.DefaultHttpServletRequestWrapper";

    private static final Class<? extends HttpServletRequestWrapper> HTTP_SERVLET_REQUEST_WRAPPER_CLASS;

    static {
        if (Classes.isAvailable(HTTP_SERVLET_REQUEST_FQCN)) {
            HTTP_SERVLET_REQUEST_WRAPPER_CLASS = Classes.forName(HTTP_SERVLET_REQUEST_WRAPPER_FQCN);
        } else {
            HTTP_SERVLET_REQUEST_WRAPPER_CLASS = null;
        }
    }

    private final InternalDataStore dataStore;

    public DefaultSsoIdentityResolver(InternalDataStore dataStore) {
        Assert.notNull(dataStore, "datastore cannot be null or empty.");
        this.dataStore = dataStore;
    }

    public AccountResult resolve(Application application, Object httpRequest) {
        Assert.notNull(dataStore, "dataStore cannot be null.");
        Assert.notNull(application, "application cannot be null.");
        Assert.notNull(httpRequest, "httpRequest cannot be null.");

        String jwtResponse;

        if (HttpRequest.class.isAssignableFrom(httpRequest.getClass())) {
            jwtResponse = ((HttpRequest) httpRequest).getParameter(JWR_RESPONSE_PARAM_NAME);
        } else {
            //This must never happen, if the object request is of HttpServletRequest type the HTTP_SERVLET_REQUEST_WRAPPER_CLASS
            //must be already loaded and therefore cannot be null.
            if (HTTP_SERVLET_REQUEST_WRAPPER_CLASS == null) {
                throw new RuntimeException("DefaultHttpServletRequestWrapper not loaded error occurred while handling httpRequest of type: " + httpRequest.getClass().getName());
            }

            Constructor<? extends HttpServletRequestWrapper> ctor = Classes.getConstructor(HTTP_SERVLET_REQUEST_WRAPPER_CLASS, Object.class);

            HttpServletRequestWrapper httpServletRequestWrapper = Classes.instantiate(ctor, httpRequest);

            jwtResponse = httpServletRequestWrapper.getParameter(JWR_RESPONSE_PARAM_NAME);
        }

        JwtWrapper jwtWrapper = new JwtWrapper(jwtResponse);

        Map jsonPayload = jwtWrapper.getJsonPayloadAsMap();

        String apiKeyId = getRequiredValue(jsonPayload, AUDIENCE_PARAM_NAME);

        getJwtSignatureValidator(apiKeyId).validate(jwtWrapper);

        Number expire = getRequiredValue(jsonPayload, EXPIRE_PARAM_NAME);
        String responseNonce = getRequiredValue(jsonPayload, RESPONSE_NONCE_PARAMETER);

        String issuer = getRequiredValue(jsonPayload, ISSUER_PARAM_NAME);

        String accountHref = getRequiredValue(jsonPayload, SUBJECT_PARAM_NAME);
        Boolean isNewAccount = getRequiredValue(jsonPayload, IS_NEW_SUBJECT_PARAM_NAME);
        String state = getOptionalValue(jsonPayload, STATE_PARAM_NAME);

        Map<String, Object> account = new HashMap<String, Object>();
        account.put(DefaultAccountResult.HREF_PROP_NAME, accountHref);

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(DefaultAccountResult.ACCOUNT.getName(), account);
        properties.put(DefaultAccountResult.NEW_ACCOUNT.getName(), isNewAccount);
        properties.put(DefaultAccountResult.STATE.getName(), state);

        return new DefaultAccountResult(dataStore, properties);
    }

    private JwtSignatureValidator getJwtSignatureValidator(String jwtApiKeyId) {

        ApiKey apiKey = dataStore.getApiKey();

        if (apiKey.getId().equals(jwtApiKeyId)) {
            return new JwtSignatureValidator(apiKey);
        }

        throw new IllegalArgumentException("The client used to sign the jwrResponse is different than the one used in this datasore.");
    }

    private <T> T getRequiredValue(Map jsonMap, String parameterName) {
        Object object = jsonMap.get(parameterName);

        if (object == null) {
            throw new IllegalArgumentException("Required jwtResponse parameter is missing: [" + parameterName + "]");
        }
        return (T) object;
    }

    private <T> T getOptionalValue(Map jsonMap, String parameterName) {

        Object object = jsonMap.get(parameterName);

        if (object == null) {
            return null;
        }

        return (T) object;
    }

}
