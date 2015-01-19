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
package com.stormpath.sdk.servlet.http.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;

/**
 * @since 1.0.RC3
 */
public class BasicAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String NAME = "Basic";

    private UsernamePasswordRequestFactory usernamePasswordRequestFactory;

    public BasicAuthenticationScheme(UsernamePasswordRequestFactory factory) {
        Assert.notNull(factory, "UsernamePasswordRequestFactory cannot be null.");
        this.usernamePasswordRequestFactory = factory;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public UsernamePasswordRequestFactory getUsernamePasswordRequestFactory() {
        return usernamePasswordRequestFactory;
    }

    @Override
    public HttpAuthenticationResult authenticate(HttpAuthenticationAttempt attempt) {

        Assert.notNull(attempt, "attempt cannot be null.");
        Assert.notNull(attempt.getCredentials(), "credentials cannot be null.");
        Assert.isTrue(NAME.equalsIgnoreCase(attempt.getCredentials().getSchemeName()), "Unsupported scheme.");
        Assert.hasText(attempt.getCredentials().getSchemeValue(), "Cannot authenticate empty Basic value.");

        String schemeValue = attempt.getCredentials().getSchemeValue();
        byte[] bytes = DatatypeConverter.parseBase64Binary(schemeValue);
        String decoded = new String(bytes, UTF8);

        String submittedPrincipal = null;

        StringBuilder sb = new StringBuilder();

        int len = decoded.length();

        for (int i = 0; i < len; i++) {

            char c = decoded.charAt(i);

            if (submittedPrincipal == null && c == ':') {
                submittedPrincipal = sb.toString();
                sb = new StringBuilder(len - i + 1);
            } else {
                sb.append(c);
            }
        }

        String submittedCredentials = sb.length() > 0 ? sb.toString() : null;

        //heuristics to determine if the basic authentication is a username/password-based authentication
        //or an api key-based authentication:
        boolean isApiKey = isApiKeyAuthenticatedRequest(attempt, submittedPrincipal, submittedCredentials);

        if (isApiKey) {
            return authenticateApiKey(attempt, submittedPrincipal, submittedCredentials);
        } else {
            return authenticateUsernamePassword(attempt, submittedPrincipal, submittedCredentials);
        }
    }

    protected boolean isApiKeyAuthenticatedRequest(HttpAuthenticationAttempt attempt, String submittedPrincipal,
                                                   String submittedCredentials) {

        String grantType = attempt.getRequest().getParameter("grant_type");

        return
            //oauth api key authentication for an access token:
            (Strings.hasText(grantType) && grantType.equals("client_credentials")) ||

            //Stormpath-generated API Key IDs are 25 characters long:
            submittedPrincipal != null && submittedPrincipal.length() == 25 &&

            //Stormpath-generated API Key Secrets are 43 characters long
            submittedCredentials != null && submittedCredentials.length() == 43 &&

            //Stormpath-generated API Key IDs don't use the '@' character; likely to be an email address:
            submittedPrincipal.indexOf('@') < 0;
    }

    protected AuthenticationRequest createAuthenticationRequest(HttpAuthenticationAttempt attempt,
                                                                String usernameOrEmail, String password) {

        HttpServletRequest request = attempt.getRequest();
        HttpServletResponse response = attempt.getResponse();

        return getUsernamePasswordRequestFactory()
            .createUsernamePasswordRequest(request, response, usernameOrEmail, password);
    }

    protected HttpAuthenticationResult authenticateUsernamePassword(HttpAuthenticationAttempt attempt,
                                                                    String usernameOrEmail, String password) {

        HttpServletRequest request = attempt.getRequest();
        HttpServletResponse response = attempt.getResponse();
        AuthenticationResult result;
        try {
            AuthenticationRequest authcRequest = createAuthenticationRequest(attempt, usernameOrEmail, password);
            Application app = getApplication(attempt.getRequest());
            result = app.authenticateAccount(authcRequest);
        } catch (Exception e) {
            String msg = "Unable to authenticate usernameOrEmail and password-based request for usernameOrEmail [" +
                         usernameOrEmail + "]: " + e.getMessage();
            throw new HttpAuthenticationException(msg, e);
        }

        attempt.getRequest().setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME,
                                          HttpServletRequest.BASIC_AUTH);

        return new DefaultHttpAuthenticationResult(request, response, result);
    }

    protected HttpAuthenticationResult authenticateApiKey(HttpAuthenticationAttempt attempt, String submittedApiKeyId,
                                                          String submittedApiKeySecret)
        throws HttpAuthenticationException {

        HttpServletRequest request;
        HttpServletResponse response;

        ApiAuthenticationResult authcResult;

        try {
            request = attempt.getRequest();
            response = attempt.getResponse();

            final ApiKey apiKey = getEnabledApiKey(request, submittedApiKeyId);

            if (!submittedApiKeySecret.equals(apiKey.getSecret())) {
                throw new HttpAuthenticationException("Submitted API Key secret does not match stored API Key secret.");
            }

            final Account account = apiKey.getAccount();

            authcResult = new ApiAuthenticationResult() {
                @Override
                public ApiKey getApiKey() {
                    return apiKey;
                }

                @Override
                public Account getAccount() {
                    return account;
                }

                @Override
                public void accept(AuthenticationResultVisitor visitor) {
                    visitor.visit(this);

                }

                @Override
                public String getHref() {
                    return null;
                }
            };

            //retain the ApiKey in case downstream components need to reference it:
            request.setAttribute(ApiKey.class.getName(), apiKey);

        } catch (Exception e) {
            String msg = "Unable to authenticate request: " + e.getMessage();
            throw new HttpAuthenticationException(msg, e);
        }

        request
            .setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME, HttpServletRequest.BASIC_AUTH);

        return new DefaultHttpAuthenticationResult(request, response, authcResult);
    }


}
