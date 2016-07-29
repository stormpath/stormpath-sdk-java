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
package com.stormpath.spring.config;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.DefaultFailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.sdk.servlet.mvc.FormController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import static com.stormpath.sdk.servlet.mvc.Controller.NEXT_QUERY_PARAM;

/**
 * A simple {@link AuthenticationFailureHandler} implementation that delegates to an actual/delegate handler for
 * handling logic, but will then send a Stormpath {@link FailedAuthenticationRequestEvent} after delegate invocation.
 * <p>This enables Stormpath web events to be sent in a Spring Security environment when
 * encountering a Spring Security {@link AuthenticationException AuthenticationException}.</p>
 *
 * @since 1.0.RC9
 */
public class StormpathAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Value("#{ @environment['stormpath.web.me.uri'] ?: '/me' }")
    private String meUri;

    private static final Logger log = LoggerFactory.getLogger(StormpathAuthenticationFailureHandler.class);

    private final Publisher<RequestEvent> publisher;

    private final String defaultFailureUrl;

    private final ErrorModelFactory errorModelFactory;

    private final List<MediaType> supportedMediaTypes;

    private ContentNegotiationResolver contentNegotiationResolver;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public StormpathAuthenticationFailureHandler(
            String defaultFailureUrl, Publisher<RequestEvent> publisher,
            ErrorModelFactory errorModelFactory, String produces
    ) {
        Assert.hasText(defaultFailureUrl, "defaultFailureUrl argument cannot be null.");
        Assert.notNull(publisher, "RequestEvent Publisher argument cannot be null.");
        Assert.notNull(errorModelFactory, "Error Model Factory argument cannot be null.");
        this.defaultFailureUrl = defaultFailureUrl;
        this.publisher = publisher;
        this.errorModelFactory = errorModelFactory;
        this.supportedMediaTypes = MediaType.parseMediaTypes(produces);
        this.contentNegotiationResolver = ContentNegotiationResolver.INSTANCE;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // Content Negotiation per https://github.com/stormpath/stormpath-sdk-java/issues/682
        try {
            MediaType mediaType = contentNegotiationResolver.getContentType(request, response, supportedMediaTypes);

            if (MediaType.APPLICATION_JSON.equals(mediaType)) {
                request.getRequestDispatcher(meUri).forward(request, response);
            } else {
                //We are saving the error message in the session (rather than in the request itself) since a redirect is taking place
                //along the line and that causes the saved attributes to be lost.
                //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/648
                request.getSession().setAttribute(FormController.SPRING_SECURITY_AUTHENTICATION_FAILED_KEY, errorModelFactory.toError(request, exception));

                String redirectUrl = defaultFailureUrl;

                //Don't loose the next param if present
                String next = request.getParameter(NEXT_QUERY_PARAM);

                if (Strings.hasText(next)) {
                    if (redirectUrl.contains("?")) {
                        redirectUrl += "&" + NEXT_QUERY_PARAM + "=" + URLEncoder.encode(next, "UTF-8");
                    } else {
                        redirectUrl += "?" + NEXT_QUERY_PARAM + "=" + URLEncoder.encode(next, "UTF-8");
                    }
                }

                redirectStrategy.sendRedirect(request, response, redirectUrl);
            }
        } catch (UnresolvedMediaTypeException ex) {
            log.error("Couldn't resolve media type: {}", ex.getMessage(), ex);
        } finally {
            FailedAuthenticationRequestEvent event = createFailureEvent(request, response, exception);
            publisher.publish(event);
        }
    }

    protected FailedAuthenticationRequestEvent createFailureEvent(HttpServletRequest request,
                                                                  HttpServletResponse response,
                                                                  AuthenticationException exception) {

        return new SpringSecurityFailedAuthenticationRequestEvent(request, response, exception);
    }

    protected static class SpringSecurityFailedAuthenticationRequestEvent
            extends DefaultFailedAuthenticationRequestEvent {

        public SpringSecurityFailedAuthenticationRequestEvent(HttpServletRequest request,
                                                              HttpServletResponse response,
                                                              Exception exception) {
            super(request, response, null, exception);
        }

        @Override
        public AuthenticationRequest getAuthenticationRequest() {
            String msg = "The current Stormpath Spring Security integration does not provide a means to access " +
                    "the raw AuthenticationRequest used by the StormpathAuthenticationProvider.";
            throw new UnsupportedOperationException(msg);
        }
    }

    //For testing purposes
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    //For testing purposes
    public void setContentNegotiationResolver(ContentNegotiationResolver contentNegotiationResolver) {
        this.contentNegotiationResolver = contentNegotiationResolver;
    }

    //For testing purposes
    public void setMeUri(String meUri) {
        this.meUri = meUri;
    }
}
