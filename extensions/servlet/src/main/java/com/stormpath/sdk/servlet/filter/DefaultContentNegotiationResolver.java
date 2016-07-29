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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.UserAgents;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Default implementation for the
 * <a href="https://github.com/stormpath/stormpath-framework-spec/blob/master/requests.md#content-type-negotiation">
 * Content Negotiation Strategy</a> defined as part of our <a href="https://github.com/stormpath/stormpath-framework-spec">
 * Stormpath Framework Integration</a> specification.
 *
 * @since 1.0.0
 */
public class DefaultContentNegotiationResolver implements ContentNegotiationResolver {

    public MediaType getContentType(HttpServletRequest request, HttpServletResponse response, List<MediaType> producesMediaTypes) throws UnresolvedMediaTypeException {

        UserAgent ua = UserAgents.get(request);
        List<MediaType> preferredMediaTypes = ua.getAcceptedMediaTypes();

        if (preferredMediaTypes.size() == 0 || preferredMediaTypes.get(0).equals(MediaType.ALL)) {
            return producesMediaTypes.get(0);
        }

        if (ua.isJsonPreferred() && producesMediaTypes.contains(MediaType.APPLICATION_JSON)) {
            return MediaType.APPLICATION_JSON;
        }

        if (ua.isHtmlPreferred() && producesMediaTypes.contains(MediaType.TEXT_HTML)) {
            return MediaType.TEXT_HTML;
        }

        // 781: Add support for application/x-www-form-urlencoded in verify endpoint
        if (request.getHeader("accept").contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            return MediaType.APPLICATION_JSON;
        }

        throw new UnresolvedMediaTypeException(preferredMediaTypes, producesMediaTypes,
                "The ContentNegotiationResolver was not able to come up with a valid MediaType.");
    }

}
