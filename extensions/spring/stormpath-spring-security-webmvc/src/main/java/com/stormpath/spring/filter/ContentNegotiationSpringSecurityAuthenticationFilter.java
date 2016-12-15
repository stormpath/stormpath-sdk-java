/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.spring.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.mvc.ProviderAccountRequestFactory;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.stormpath.sdk.servlet.mvc.JacksonFieldValueResolver.MARSHALLED_OBJECT;

/**
 * @since 1.0.0
 */
public class ContentNegotiationSpringSecurityAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(ContentNegotiationSpringSecurityAuthenticationFilter.class);

    private boolean postOnly = true;
    private List<MediaType> supportedMediaTypes;
    private ProviderAccountRequestFactory providerAccountRequestFactory;

    @Autowired
    protected Application application;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST") && !request.getRequestURI().contains("/callbacks/")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        if (supportedMediaTypes == null) {
            throw new AuthenticationServiceException("A list of supported media types must be set.");
        }

        MediaType mediaType;
        try {
            mediaType = ContentNegotiationResolver.INSTANCE.getContentType(request, response, supportedMediaTypes);
        } catch (UnresolvedMediaTypeException umt) {
            throw new AuthenticationServiceException("Unresolved media type: " + umt.getMessage(), umt);
        }

        log.debug("mediaType: {}", mediaType);
        log.debug("request.getContentType(): {}", request.getContentType());
        // the || is to handle an edge case where an Accept: */* is set but the content-type is application/x-www-form-urlencoded
        // a browser would not do this, but this guards against command line tomfoolery.
        if (!MediaType.APPLICATION_JSON.equals(mediaType) ||
                request.getHeader("accept").contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) ||
                request.getContentType().contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            return super.attemptAuthentication(request, response);
        }

        // Request body can only be read once and we don't yet know if it's a login/password attempt
        // or a provider attempt - such as Facebook
        Map<String, Object> loginProps = getLoginProps(request);

        // check to see if it's a login/password auth request
        if (loginProps.get("login") != null) {
            UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginProps.get("login"), loginProps.get("password"));
            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            return getAuthenticationManager().authenticate(authRequest);
        }

        // check to see if it's a Provider auth request
        // setup the request with the providerData
        request.setAttribute(MARSHALLED_OBJECT, loginProps);
        ProviderAccountRequest accountRequest = providerAccountRequestFactory.getProviderAccountRequest(request);

        try {
            ProviderAccountResult result = application.getAccount(accountRequest);
            Account account = result.getAccount();
            return getAuthenticationManager().authenticate(new ProviderAuthenticationToken(account));
        } catch (ResourceException | IllegalArgumentException e) {
            log.error("Unable to perform provider auth: {}", e.getMessage(), e);
            // auth has failed at this point, so cause a 400 to be returned
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(null, null));
        }
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    /**
     * @since 1.3.0
     */
    public void setProviderAccountRequestFactory(ProviderAccountRequestFactory providerAccountRequestFactory) {
        this.providerAccountRequestFactory = providerAccountRequestFactory;
    }

    private Map<String, Object> getLoginProps(HttpServletRequest request) {
        String body = getRequestBody(request);

        Map<String, Object> loginProps = null;
        try {
            loginProps = new ObjectMapper().readValue(body, HashMap.class);
        } catch(IOException ex) {
            log.error("Couldn't map request body: '{}': {}", body, ex.getMessage(), ex);
        }

        return loginProps;
    }

    private String getRequestBody(HttpServletRequest request) {
        BufferedReader bufferedReader = null;
        StringBuffer sb = new StringBuffer();
        try {
            bufferedReader =  request.getReader();
            char[] charBuffer = new char[128];
            int bytesRead;
            while ( (bytesRead = bufferedReader.read(charBuffer)) != -1 ) {
                sb.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            log.error("Problem reading request body: {}", ex.getMessage(), ex);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    log.error("Problem closing reader: {}", ex.getMessage(), ex);
                }
            }
        }
        return sb.toString();
    }
}
