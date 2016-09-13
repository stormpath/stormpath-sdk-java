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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class DefaultWrappedServletRequestFactory implements WrappedServletRequestFactory {

    private UsernamePasswordRequestFactory usernamePasswordRequestFactory;
    private Saver<AuthenticationResult> authenticationResultSaver;
    private Publisher<RequestEvent> eventPublisher;
    private String userPrincipalStrategyName;
    private String remoteUserStrategyName;
    private Resolver<String> organizationNameKeyResolver;

    public DefaultWrappedServletRequestFactory(UsernamePasswordRequestFactory factory,
                                               Saver<AuthenticationResult> authenticationResultSaver,
                                               Publisher<RequestEvent> eventPublisher,
                                               Resolver<String> organizationNameKeyResolver,
                                               String userPrincipalStrategyName, String remoteUserStrategyName) {
        this.usernamePasswordRequestFactory = factory;
        this.authenticationResultSaver = authenticationResultSaver;
        this.eventPublisher = eventPublisher;
        this.userPrincipalStrategyName = userPrincipalStrategyName;
        this.remoteUserStrategyName = remoteUserStrategyName;
        this.organizationNameKeyResolver = organizationNameKeyResolver;
    }

    @Override
    public HttpServletRequest wrapHttpServletRequest(HttpServletRequest request, HttpServletResponse response) {
        return new StormpathHttpServletRequest(request, response,
                usernamePasswordRequestFactory,
                eventPublisher,
                authenticationResultSaver,
                organizationNameKeyResolver,
                userPrincipalStrategyName, remoteUserStrategyName);
    }
}
