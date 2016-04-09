/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.error.jwt.InvalidJwtException;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.idsite.AccountResult;
import com.stormpath.sdk.idsite.NonceStore;
import com.stormpath.sdk.impl.account.DefaultAccountResult;
import com.stormpath.sdk.impl.account.DefaultAuthenticationResult;
import com.stormpath.sdk.impl.account.DefaultLogoutResult;
import com.stormpath.sdk.impl.authc.HttpServletRequestWrapper;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.idsite.DefaultNonceStore;
import com.stormpath.sdk.impl.jwt.JwtSignatureValidator;
import com.stormpath.sdk.impl.jwt.JwtWrapper;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.saml.SamlCallbackHandler;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.sdk.saml.SamlRuntimeException;
import io.jsonwebtoken.Claims;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.stormpath.sdk.impl.idsite.IdSiteClaims.*;
import static com.stormpath.sdk.impl.jwt.JwtHeaderParameters.KEY_ID;

/**
 * @since 1.0.RC8
 */
public class DefaultSamlCallbackHandler implements SamlCallbackHandler {

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

    private List<SamlResultListener> resultListeners = new ArrayList<SamlResultListener>();

    public DefaultSamlCallbackHandler(InternalDataStore dataStore, Application application, Object httpRequest) {
        Assert.notNull(dataStore, "datastore cannot be null or empty.");
        Assert.notNull(application, "application cannot be null.");
        Assert.notNull(httpRequest, "httpRequest cannot be null.");

        this.dataStore = dataStore;
        this.application = application;
        this.jwtResponse = getJwtResponse(httpRequest);
        this.nonceStore = new DefaultNonceStore(dataStore.getCacheResolver());
    }

    @Override
    public SamlCallbackHandler setNonceStore(NonceStore nonceStore) {
        Assert.notNull(nonceStore);
        this.nonceStore = nonceStore;
        return this;
    }

    @Override
    public AccountResult getAccountResult() {

        JwtWrapper jwtWrapper = new JwtWrapper(jwtResponse);

        Map jsonPayload = jwtWrapper.getJsonPayloadAsMap();

        String apiKeyId;

        Map jsonHeader = jwtWrapper.getJsonHeaderAsMap();
        apiKeyId = getRequiredValue(jsonHeader, KEY_ID);

        getJwtSignatureValidator(apiKeyId).validate(jwtWrapper);

        Number expire = getRequiredValue(jsonPayload, Claims.EXPIRATION);

        verifyJwtIsNotExpired(expire.longValue());

        String issuer = getRequiredValue(jsonPayload, Claims.ISSUER);

        if (isError(jsonPayload)) {
            throw new SamlRuntimeException(constructError(jsonPayload, jsonHeader));
        }

        String responseNonce = getRequiredValue(jsonPayload, RESPONSE_ID);

        if (nonceStore.hasNonce(responseNonce)) {
            throw new InvalidJwtException(InvalidJwtException.ALREADY_USED_JWT_ERROR);
        }

        nonceStore.putNonce(responseNonce);

        //the 'sub' field can be null if calling /sso/logout when the subject is already logged out:
        String accountHref = getOptionalValue(jsonPayload, Claims.SUBJECT);
        boolean accountHrefPresent = Strings.hasText(accountHref);
        //but this is only legal during the logout scenario, so assert this:
        SamlResultStatus resultStatus = SamlResultStatus.valueOf((String) getRequiredValue(jsonPayload, STATUS));
        if (!accountHrefPresent && !SamlResultStatus.LOGOUT.equals(resultStatus)) {
            throw new InvalidJwtException(InvalidJwtException.JWT_RESPONSE_MISSING_PARAMETER_ERROR);
        }

        Boolean isNewAccount = getRequiredValue(jsonPayload, IS_NEW_SUBJECT);
        String state = getOptionalValue(jsonPayload, STATE);

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(DefaultAccountResult.NEW_ACCOUNT.getName(), isNewAccount);
        properties.put(DefaultAccountResult.STATE.getName(), state);

        if (accountHrefPresent) {
            Map<String, Object> account = new HashMap<String, Object>();
            account.put(DefaultAccountResult.HREF_PROP_NAME, accountHref);
            properties.put(DefaultAccountResult.ACCOUNT.getName(), account);
        }

        AccountResult accountResult = new DefaultAccountResult(dataStore, properties);

        if(this.resultListeners.size() > 0) {
            dispatchResponseStatus(resultStatus, properties);
        }

        return accountResult;
    }

    @Override
    public SamlCallbackHandler setResultListener(SamlResultListener idSiteResultListener) {
        if (idSiteResultListener != null) {
            this.resultListeners = new ArrayList<SamlResultListener>();
            return addResultListener(idSiteResultListener);
        }
        return this;
    }

    @Override
    public SamlCallbackHandler addResultListener(SamlResultListener idSiteResultListener) {
        if (idSiteResultListener != null) {
            this.resultListeners.add(idSiteResultListener);
        }
        return this;
    }

    private String getJwtResponse(Object httpRequestObject) {
        String jwtResponse;

        if (HttpRequest.class.isAssignableFrom(httpRequestObject.getClass())) {

            HttpRequest httpRequest = (HttpRequest) httpRequestObject;

            Assert.isTrue(httpRequest.getMethod() == HttpMethod.GET, "Only Http GET method is supported.");

            jwtResponse = httpRequest.getParameter(JWT_RESPONSE);

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

            jwtResponse = httpServletRequestWrapper.getParameter(JWT_RESPONSE);
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

    private void dispatchResponseStatus(SamlResultStatus status, Map<String, Object> properties) {
        switch (status) {
            case AUTHENTICATED:
                for (SamlResultListener resultListener : this.resultListeners) {
                    resultListener.onAuthenticated(new DefaultAuthenticationResult(dataStore, properties));
                }
                break;
            case LOGOUT:
                for (SamlResultListener resultListener : this.resultListeners) {
                    resultListener.onLogout(new DefaultLogoutResult(dataStore, properties));
                }
                break;
            default:
                throw new IllegalArgumentException("Encountered unknown IdSite result status: " + status);
        }
    }

    /* @since 1.0.RC5 */
    private Error constructError(Map jsonMap, Map jsonHeader) {
        Map<String, Object> errorMap = getRequiredValue(jsonMap, ERROR);

        if (jsonHeader.containsKey(HttpHeaders.STORMPATH_REQUEST_ID)) {
            errorMap.put(HttpHeaders.STORMPATH_REQUEST_ID, jsonHeader.get(HttpHeaders.STORMPATH_REQUEST_ID));
        }

        return new DefaultError(errorMap);
    }

    /* @since 1.0.RC5 */
    private boolean isError(Map jsonMap) {
        Assert.notNull(jsonMap, "jsonMap cannot be null.");
        Object error = getOptionalValue(jsonMap, ERROR);
        return error != null;
    }

}
