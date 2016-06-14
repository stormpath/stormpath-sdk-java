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
package com.stormpath.spring.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class SpringSpaController extends AbstractController {

    private final Controller delegate;

    private final String jsonView;

    private final List<MediaType> producesMediaTypes;

    public SpringSpaController(Controller delegate,
                               String jsonView,
                               List<MediaType> producesMediaTypes) {
        Assert.notNull(delegate, "Delegate controller cannot be null.");
        Assert.notEmpty(producesMediaTypes, "produced media types cannot be null or empty.");
        Assert.hasText(jsonView, "jsonView cannot be null or empty.");
        this.delegate = delegate;
        this.jsonView = jsonView;
        this.producesMediaTypes = producesMediaTypes;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = null;

        try {

            MediaType mediaType = ContentNegotiationResolver.INSTANCE.getContentType(request, response, producesMediaTypes);
            mav = delegate.handleRequest(request, response);
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                mav.setViewName(jsonView);
            }

        } catch (UnresolvedMediaTypeException e) {
            //No MediaType could be resolved for this request based on the produces setting. Let's return 404.
            mav = new ModelAndView(new View() {
                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                }
            });
        }

        return mav;
    }

}
