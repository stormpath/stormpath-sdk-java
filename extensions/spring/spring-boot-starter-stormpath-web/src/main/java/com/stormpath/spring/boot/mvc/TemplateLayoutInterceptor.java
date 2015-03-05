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

import com.stormpath.sdk.lang.Strings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC4
 */
public class TemplateLayoutInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

    public static final String HEAD_VIEW_NAME_KEY = "headViewName";
    public static final String HEAD_FRAGMENT_SELECTOR_KEY = "headFragmentSelector";

    private String headViewName;
    private String headFragmentSelector;

    public String getHeadViewName() {
        return headViewName;
    }

    public void setHeadViewName(String headViewName) {
        this.headViewName = headViewName;
    }

    public String getHeadFragmentSelector() {
        return headFragmentSelector;
    }

    public void setHeadFragmentSelector(String headFragmentSelector) {
        this.headFragmentSelector = headFragmentSelector;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(headViewName, "headViewName must be specified.");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        if (modelAndView == null || !modelAndView.isReference()) {
            return;
        }

        String viewName = modelAndView.getViewName();
        if (isRedirectOrForward(viewName)) {
            return;
        }

        if (!modelAndView.getModel().containsKey(HEAD_VIEW_NAME_KEY)) {
            modelAndView.addObject(HEAD_VIEW_NAME_KEY, headViewName);
        }

        if (Strings.hasText(headFragmentSelector) && !modelAndView.getModel().containsKey(HEAD_FRAGMENT_SELECTOR_KEY)) {
            modelAndView.addObject(HEAD_FRAGMENT_SELECTOR_KEY, headFragmentSelector);
        }
    }

    private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }
}
