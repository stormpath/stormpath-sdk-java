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

import com.stormpath.sdk.servlet.mvc.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class SpringController extends UrlFilenameViewController {

    private static final Logger log = LoggerFactory.getLogger(SpringController.class);

    private com.stormpath.sdk.servlet.mvc.Controller stormpathCoreController;

    public SpringController(com.stormpath.sdk.servlet.mvc.Controller stormpathCoreController) {
        Assert.notNull(stormpathCoreController);
        this.stormpathCoreController = stormpathCoreController;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {

        ViewModel vm;
        try {
            vm = stormpathCoreController.handleRequest(request, response);
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke Stormpath core controller: " + e.getMessage(), e);
        }

        if (vm == null) {
            return null;
        }

        String viewName = vm.getViewName();
        if (!StringUtils.hasText(viewName)) {
            viewName = getViewNameForRequest(request);
        }

        if (log.isDebugEnabled()) {
            String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
            log.debug("Returning view name '" + viewName + "' for lookup path [" + lookupPath + "]");
        }

        Map<String,?> model = vm.getModel();

        boolean redirect = vm.isRedirect();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(RequestContextUtils.getInputFlashMap(request));
        modelAndView.addAllObjects(model);

        if (redirect) {
            modelAndView.setView(new RedirectView(viewName));
        } else {
            modelAndView.setViewName(viewName);
        }

        return modelAndView;
    }
}
