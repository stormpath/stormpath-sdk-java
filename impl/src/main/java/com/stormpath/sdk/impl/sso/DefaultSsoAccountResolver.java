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
import com.stormpath.sdk.error.jwt.InvalidJwtException;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.impl.account.DefaultAccountResult;
import com.stormpath.sdk.impl.authc.HttpServletRequestWrapper;
import com.stormpath.sdk.impl.ds.DefaultDataStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.jwt.JwtSignatureValidator;
import com.stormpath.sdk.impl.jwt.JwtWrapper;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.sso.NonceStore;
import com.stormpath.sdk.sso.SsoAccountResolver;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.impl.jwt.JwtConstants.*;

/**
 * @since 1.0.RC
 */
public class DefaultSsoAccountResolver implements SsoAccountResolver {

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

    private final Application application;

    private final String jwtResponse;

    private NonceStore nonceStore;

    public DefaultSsoAccountResolver(InternalDataStore dataStore, Application application, Object httpRequest) {
        Assert.notNull(dataStore, "datastore cannot be null or empty.");
        Assert.notNull(application, "application cannot be null.");
        Assert.notNull(httpRequest, "httpRequest cannot be null.");

        this.dataStore = dataStore;
        this.application = application;
        this.jwtResponse = getJwtResponse(httpRequest);
        this.nonceStore = new DefaultNonceStore((DefaultDataStore) dataStore);
    }

    @Override
    public void withNonceStore(NonceStore nonceStore) {
        Assert.notNull(nonceStore);
        this.nonceStore = nonceStore;
    }

    @Override
    public AccountResult execute() {

        JwtWrapper jwtWrapper = new JwtWrapper(jwtResponse);

        Map jsonPayload = jwtWrapper.getJsonPayloadAsMap();

        String apiKeyId = getRequiredValue(jsonPayload, AUDIENCE_PARAM_NAME);

        getJwtSignatureValidator(apiKeyId).validate(jwtWrapper);

        Number expire = getRequiredValue(jsonPayload, EXPIRE_PARAM_NAME);

        verifyJwtIsNotExpired(expire.longValue());

        String responseNonce = getRequiredValue(jsonPayload, RESPONSE_NONCE_PARAMETER);

        if (nonceStore.hasNonce(responseNonce)) {
            throw new InvalidJwtException(InvalidJwtException.ALREADY_USED_JWT_ERROR);
        }

        nonceStore.putNonce(responseNonce);

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

    private String getJwtResponse(Object httpRequestObject) {
        String jwtResponse;

        if (HttpRequest.class.isAssignableFrom(httpRequestObject.getClass())) {

            HttpRequest httpRequest = (HttpRequest) httpRequestObject;

            Assert.isTrue(httpRequest.getMethod() == HttpMethod.GET, "Only Http GET method is supported.");

            jwtResponse = httpRequest.getParameter(JWR_RESPONSE_PARAM_NAME);

        } else {
            //This must never happen, if the object request is of HttpServletRequest type the HTTP_SERVLET_REQUEST_WRAPPER_CLASS
            //must be already loaded and therefore cannot be null.
            if (HTTP_SERVLET_REQUEST_WRAPPER_CLASS == null) {
                throw new RuntimeException("DefaultHttpServletRequestWrapper not loaded error occurred while handling httpRequest of type: " + httpRequestObject.getClass().getName());
            }

            Constructor<? extends HttpServletRequestWrapper> ctor = Classes.getConstructor(HTTP_SERVLET_REQUEST_WRAPPER_CLASS, Object.class);

            HttpServletRequestWrapper httpServletRequestWrapper = Classes.instantiate(ctor, httpRequestObject);
            HttpMethod method = HttpMethod.fromName(httpServletRequestWrapper.getMethod());
            Assert.isTrue(HttpMethod.GET == method, "Only Http GET method is supported.");

            jwtResponse = httpServletRequestWrapper.getParameter(JWR_RESPONSE_PARAM_NAME);
        }

        if (!Strings.hasText(jwtResponse)) {
            throw new InvalidJwtException(InvalidJwtException.JWT_REQUIRED_ERROR);
        }
        return jwtResponse;
    }

    private void verifyJwtIsNotExpired(long expireInSeconds) {

        long now = System.currentTimeMillis() / 1000;

        if (now > expireInSeconds) {
            throw new InvalidJwtException(InvalidJwtException.EXPIRED_JWT_ERROR);
        }
    }

    private JwtSignatureValidator getJwtSignatureValidator(String jwtApiKeyId) {

        ApiKey apiKey = dataStore.getApiKey();

        if (apiKey.getId().equals(jwtApiKeyId)) {
            return new JwtSignatureValidator(apiKey);
        }

        throw new InvalidJwtException(InvalidJwtException.JWT_RESPONSE_INVALID_APIKEY_ID_ERROR);
    }

    private <T> T getRequiredValue(Map jsonMap, String parameterName) {
        Object object = jsonMap.get(parameterName);

        if (object == null) {
            throw new InvalidJwtException(InvalidJwtException.JWT_RESPONSE_MISSING_PARAMETER_ERROR);
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

