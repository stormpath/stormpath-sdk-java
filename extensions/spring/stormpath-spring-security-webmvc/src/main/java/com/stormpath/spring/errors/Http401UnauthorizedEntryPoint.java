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
package com.stormpath.spring.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Returns a 401 error code (Unauthorized) to the client.
 *
 * @since 1.0.0
 */
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    private final Logger log = LoggerFactory.getLogger(Http401UnauthorizedEntryPoint.class);
    private final ObjectMapper om;
    private final ContentNegotiationResolver contentNegotiationResolver = ContentNegotiationResolver.INSTANCE;
    @Value("#{ @environment['stormpath.web.produces'] ?: 'application/json, text/html' }")
    protected String producesTypes;

    private boolean isJsonPreferred(HttpServletRequest request, HttpServletResponse response) {
        List<MediaType> produces = MediaType.parseMediaTypes(producesTypes);
        try {
            return MediaType.APPLICATION_JSON.equals(contentNegotiationResolver.getContentType(request, response, produces));
        } catch (UnresolvedMediaTypeException e) {
            log.error("Couldn't resolve content type", e);
            return false;
        }
    }

    public Http401UnauthorizedEntryPoint() {
        this.om = new ObjectMapper();
    }

    /**
     * Always returns a 401 error code to the client.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException, ServletException {

        log.debug("Pre-authenticated entry point called. Rejecting access");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (isJsonPreferred(request, response)) {
            om.writeValue(response.getOutputStream(),
                    new Error(ErrorConstants.ERR_ACCESS_DENIED, ex.getMessage()));
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
