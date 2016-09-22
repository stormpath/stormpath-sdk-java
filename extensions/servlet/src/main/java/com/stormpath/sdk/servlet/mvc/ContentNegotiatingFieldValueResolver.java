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

import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 1.0.RC8
 */
public class ContentNegotiatingFieldValueResolver implements RequestFieldValueResolver {

    private static final Logger log = LoggerFactory.getLogger(ContentNegotiatingFieldValueResolver.class);

    private final RequestFieldValueResolver JSON_BODY_RESOLVER = new JacksonFieldValueResolver();
    private final RequestFieldValueResolver REQ_PARAM_RESOLVER = new RequestParameterFieldValueResolver();

    protected List<MediaType> produces;
    private ContentNegotiationResolver contentNegotiationResolver = ContentNegotiationResolver.INSTANCE;

    public void setProduces(List<MediaType> produces) {
        this.produces = produces;
    }

    @Override
    public String getValue(HttpServletRequest request, String fieldName) {
        if (isJsonPreferred(request)) {
            return JSON_BODY_RESOLVER.getValue(request, fieldName);
        }

        return REQ_PARAM_RESOLVER.getValue(request, fieldName);
    }

    @Override
    public Map<String, Object> getAllFields(HttpServletRequest request) {
        if (isJsonPreferred(request)) {
            return JSON_BODY_RESOLVER.getAllFields(request);
        }

        return REQ_PARAM_RESOLVER.getAllFields(request);
    }

    protected boolean isJsonPreferred(HttpServletRequest request) {
        try {
            return MediaType.APPLICATION_JSON.equals(contentNegotiationResolver.getContentType(request, null, produces));
        } catch (UnresolvedMediaTypeException e) {
            log.debug("isJsonPreferred: Couldn't resolve content type: {}", e.getMessage());
            return false;
        }
    }
}
