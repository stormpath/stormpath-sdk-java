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

import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.StormpathFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Component
public class SpringStormpathFilter extends StormpathFilter implements ServletContextAware {

    private static final Logger log = LoggerFactory.getLogger(SpringStormpathFilter.class);

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private com.stormpath.sdk.servlet.mvc.Controller stormpathCoreController;

    public SpringStormpathFilter() {
        //TODO: required by StormpathWebMvcConfiguration#stormpathFilter
    }

    public SpringStormpathFilter(com.stormpath.sdk.servlet.mvc.Controller stormpathCoreController) {
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

//    /**
//     * Delegates to the wrapped {@link com.stormpath.sdk.servlet.mvc.Controller com.stormpath.sdk.servlet.mvc.Controller}
//     * instance and converts the returned Stormpath SDK-specific {@link ViewModel
//     * ViewModel} to a Spring {@link ModelAndView ModelAndView} instance.
//     *
//     * @param request  inbound http request
//     * @param response outbound http response
//     * @return the ModelAndView that should be rendered by Spring MVC.
//     */
//    @Override
//    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
//
//        ViewModel vm;
//        try {
//            vm = stormpathCoreController.handleRequest(request, response);
//        } catch (Exception e) {
//            throw new RuntimeException("Unable to invoke Stormpath core controller: " + e.getMessage(), e);
//        }
//
//        if (vm == null) {
//            return null;
//        }
//
//        String viewName = vm.getViewName();
//        Assert.hasText(viewName, "ViewModel must contain a viewName.");
//
//        if (log.isDebugEnabled()) {
//            String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
//            log.debug("Returning view name '{}' for lookup path [{}]", viewName, lookupPath);
//        }
//
//        Map<String, ?> model = vm.getModel();
//
//        boolean redirect = vm.isRedirect();
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addAllObjects(RequestContextUtils.getInputFlashMap(request));
//        modelAndView.addAllObjects(model);
//
//        if (redirect) {
//            //Fix to redirect to correct context path: https://github.com/stormpath/stormpath-sdk-java/issues/210
//            String targetUrl = new RedirectUrlBuilder(request).setUrl(viewName).setContextRelative(true).build();
//            modelAndView.setView(new RedirectView(targetUrl));
//        } else {
//            modelAndView.setViewName(viewName);
//        }
//
//        return modelAndView;
//    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        try {
            onInit();
        } catch (ServletException se) {
            throw new RuntimeException("Exception during SpringControllerFilter initialization", se.fillInStackTrace());
        }
    }

//    @Autowired
//    FilterChainResolver filterChainResolver;

    @Override
    protected void onInit() throws ServletException {
        //super.onInit();
//        com.stormpath.sdk.lang.Assert.notNull(controller, "Controller instance must be configured.");

//        InternalResourceViewResolver irvr = new InternalResourceViewResolver();
//        irvr.setPrefix(getPrefix());
//        irvr.setSuffix(getSuffix());
//        this.viewResolver = new DefaultViewResolver(irvr, new JacksonView(), producesMediaTypes());
    }
}
