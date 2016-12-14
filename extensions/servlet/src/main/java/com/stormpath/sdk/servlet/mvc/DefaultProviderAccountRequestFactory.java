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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.stormpath.sdk.servlet.mvc.JacksonFieldValueResolver.MARSHALLED_OBJECT;

// Refactor of Provider requests for
// https://github.com/stormpath/stormpath-sdk-java/issues/915
// and to provide uniform responses across all integrations for
// conformance to stormpath-framework-spec as enforced by
// stormpath-framework-tck
/**
 * @since 1.0.3
 */
public class DefaultProviderAccountRequestFactory implements ProviderAccountRequestFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultProviderAccountRequestFactory.class);

    private final GithubAccessTokenResolver githubAccessTokenResolver = new GithubAccessTokenResolver();

    @Override
    @SuppressWarnings("unchecked")
    public ProviderAccountRequest getProviderAccountRequest(HttpServletRequest request) {
        Map<String, Object> map = (Map<String, Object>) request.getAttribute(MARSHALLED_OBJECT);

        if (map != null && map.get("providerData") != null) {
            Map<String, String> providerData = (Map<String, String>) map.get("providerData");

            String providerId = providerData.get("providerId");
            if (Strings.hasText(providerId)) {
                switch (providerId) {
                    case "facebook": {
                        String accessToken = providerData.get("accessToken");
                        return Providers.FACEBOOK
                                .account().setAccessToken(accessToken).build();
                    }
                    case "github": {
                        String accessToken = githubAccessTokenResolver.get(request, null);
                        return Providers.GITHUB
                                .account().setAccessToken(accessToken).build();
                    }
                    case "google": {
                        String code = providerData.get("code");
                        return Providers.GOOGLE
                                .account().setCode(code).build();
                    }
                    case "linkedin": {
                        String code = providerData.get("code");
                        return Providers.LINKEDIN
                                .account().setCode(code).build();
                    }
                    default: {
                        log.error("No provider configured for " + providerId);
                        return null;
                    }
                }
            }
        }

        log.debug("Provider data not found in request.");
        return null;
    }
}
