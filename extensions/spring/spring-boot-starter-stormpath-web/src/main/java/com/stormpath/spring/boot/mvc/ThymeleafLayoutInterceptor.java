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
package com.stormpath.spring.boot.mvc;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThymeleafLayoutInterceptor extends HandlerInterceptorAdapter {

    private static final String STORMPATH_TEMPLATE_DIR = "stormpath";
    private static final String STORMPATH_TEMPLATE_DIR_PREFIX = STORMPATH_TEMPLATE_DIR + "/";

    private static final String LAYOUT_TEMPLATE = STORMPATH_TEMPLATE_DIR_PREFIX + "page";
    private static final String VIEW_ATTRIBUTE_NAME = "view";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        if (modelAndView == null || !modelAndView.hasView()) {
            return;
        }

        String viewName = modelAndView.getViewName();
        if (isRedirectOrForward(viewName)) {
            return;
        }

        if (!viewName.startsWith(STORMPATH_TEMPLATE_DIR_PREFIX)) {
            if (viewName.startsWith("/")) {
                viewName = STORMPATH_TEMPLATE_DIR + viewName;
            } else {
                viewName = STORMPATH_TEMPLATE_DIR_PREFIX + viewName;
            }
        }

        modelAndView.setViewName(LAYOUT_TEMPLATE);
        modelAndView.addObject(VIEW_ATTRIBUTE_NAME, viewName);
    }

    private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }
}
