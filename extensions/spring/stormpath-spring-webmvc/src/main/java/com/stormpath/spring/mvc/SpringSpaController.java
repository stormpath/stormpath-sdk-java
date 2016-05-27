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
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.UserAgents;
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
                               //Controller spaViewController,
                               String jsonView,
                               List<MediaType> producesMediaTypes) {
        Assert.notNull(delegate, "Delegate controller cannot be null.");
        //Assert.notNull(spaViewController, "SPA view controller cannot be null.");
        Assert.notEmpty(producesMediaTypes, "produced media types cannot be null or empty.");
        Assert.hasText(jsonView, "jsonView cannot be null or empty.");
        this.delegate = delegate;
        //this.spaViewController = spaViewController;
        this.jsonView = jsonView;
        this.producesMediaTypes = producesMediaTypes;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //Controller controller = delegate;

        //String method = request.getMethod();

        ModelAndView mav = null;
        UserAgent ua = UserAgents.get(request);
        List<MediaType> preferredMediaTypes = ua.getAcceptedMediaTypes();

        if (preferredMediaTypes.size() == 0 || preferredMediaTypes.get(0).equals(MediaType.ALL)) {
            MediaType mediaType = producesMediaTypes.get(0);

            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                mav = delegate.handleRequest(request, response);
                mav.setViewName(jsonView);
            }
            if (mediaType.equals(MediaType.TEXT_HTML)) {
                mav = delegate.handleRequest(request, response);
            }
        }

        if (ua.isJsonPreferred() && producesMediaTypes.contains(MediaType.APPLICATION_JSON)) {
            mav = delegate.handleRequest(request, response);
            mav.setViewName(jsonView);
        }

        if (ua.isHtmlPreferred() && producesMediaTypes.contains(MediaType.TEXT_HTML)) {
            mav = delegate.handleRequest(request, response);
        }

        if (mav == null) {
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
