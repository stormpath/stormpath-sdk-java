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
package com.stormpath.spring.mvc;

import com.stormpath.sdk.servlet.mvc.ViewModel;
import com.stormpath.sdk.servlet.util.RedirectUrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This is an adapter or bridge component: it is a Spring {@link org.springframework.web.servlet.mvc.Controller
 * Controller} implementation that simply wraps (delegates) to a Stormpath {@link com.stormpath.sdk.servlet.mvc.Controller
 * com.stormpath.sdk.servlet.mvc.Controller} implementation.
 *
 * <p>It allows existing Stormpath mvc-framework-agnostic controller implementations to be used as Spring controllers in
 * Spring environments.</p>
 *
 * @since 1.0.RC4
 * @deprecated
 */
@Deprecated
public class SpringController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(SpringController.class);

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private com.stormpath.sdk.servlet.mvc.Controller stormpathCoreController;

    public SpringController(com.stormpath.sdk.servlet.mvc.Controller stormpathCoreController) {
        Assert.notNull(stormpathCoreController);
        this.stormpathCoreController = stormpathCoreController;
    }

    /**
     * Set the UrlPathHelper to use for the resolution of lookup paths. <p>Use this to override the default
     * UrlPathHelper with a custom subclass, or to share common UrlPathHelper settings across multiple
     * MethodNameResolvers and HandlerMappings.
     *
     * @see org.springframework.web.servlet.handler.AbstractUrlHandlerMapping#setUrlPathHelper
     */
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    /**
     * Return the UrlPathHelper to use for the resolution of lookup paths.
     */
    protected UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    /**
     * Delegates to the wrapped {@link com.stormpath.sdk.servlet.mvc.Controller com.stormpath.sdk.servlet.mvc.Controller}
     * instance and converts the returned Stormpath SDK-specific {@link com.stormpath.sdk.servlet.mvc.ViewModel
     * ViewModel} to a Spring {@link org.springframework.web.servlet.ModelAndView ModelAndView} instance.
     *
     * @param request  inbound http request
     * @param response outbound http response
     * @return the ModelAndView that should be rendered by Spring MVC.
     */
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
        Assert.hasText(viewName, "ViewModel must contain a viewName.");

        if (log.isDebugEnabled()) {
            String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
            log.debug("Returning view name '{}' for lookup path [{}]", viewName, lookupPath);
        }

        Map<String, ?> model = vm.getModel();

        boolean redirect = vm.isRedirect();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(RequestContextUtils.getInputFlashMap(request));
        modelAndView.addAllObjects(model);

        if (redirect) {
            //Fix to redirect to correct context path: https://github.com/stormpath/stormpath-sdk-java/issues/210
            String targetUrl = new RedirectUrlBuilder(request).setUrl(viewName).setContextRelative(true).build();
            modelAndView.setView(new RedirectView(targetUrl));
        } else {
            modelAndView.setViewName(viewName);
        }

        return modelAndView;
    }
}
