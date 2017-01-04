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
package com.stormpath.sdk.servlet.filter.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.mvc.Controller;
import com.stormpath.sdk.servlet.mvc.DefaultViewResolver;
import com.stormpath.sdk.servlet.mvc.InternalResourceViewResolver;
import com.stormpath.sdk.servlet.mvc.JacksonView;
import com.stormpath.sdk.servlet.mvc.View;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import com.stormpath.sdk.servlet.mvc.ViewResolver;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * A Servlet Filter that acts as an MVC {@link com.stormpath.sdk.servlet.mvc.Controller Controller} by delegating to an
 * internal Controller instance.  Because it acts as a controller and not a filter, requests will never proceed past
 * this one in the filter chain - it always handles the response directly.  This implies that when configured as part of
 * a filter chain, it must always be the last filter in the chain.
 *
 * @since 1.0.RC4
 */
public class ControllerFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(ControllerFilter.class);

    private Controller controller;

    private String prefix = "/WEB-INF/jsp/stormpath/";
    private String suffix = ".jsp";
    private List<MediaType> producedMediaTypes;
    private ViewResolver viewResolver;

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public List<MediaType> getProducedMediaTypes() {
        return producedMediaTypes;
    }

    public void setProducedMediaTypes(List<MediaType> producedMediaTypes) {
        this.producedMediaTypes = producedMediaTypes;
    }

    public void setViewResolver(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    @Override
    protected void onInit() throws Exception {
        Assert.notNull(controller, "Controller instance must be configured.");
        Assert.notEmpty(producedMediaTypes, "producedMediaTypes property cannot be null or empty.");

        if (this.viewResolver == null) {
            InternalResourceViewResolver irvr = new InternalResourceViewResolver();
            irvr.setPrefix(getPrefix());
            irvr.setSuffix(getSuffix());
            this.viewResolver = new DefaultViewResolver(irvr, new JacksonView(), producedMediaTypes);
        }
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {

        ViewModel vm;
        try {
            vm = controller.handleRequest(request, response);
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke Stormpath controller: " + e.getMessage(), e);
        }

        if (vm == null) { //controller rendered the response directly, so just return
            return;
        }

        View view = this.viewResolver.getView(vm, request);

        if (view == null) {
            chain.doFilter(request, response);
            return;
        }

        String viewName = vm.getViewName();
        Assert.hasText(viewName, "ViewModel must contain a viewName.");

        boolean redirect = vm.isRedirect();

        if (redirect) {
            redirect(request, response, vm);
        } else {
            render(request, response, vm);
        }
    }

    protected void redirect(HttpServletRequest request, HttpServletResponse response, ViewModel vm) throws IOException {
        String redirectUri = vm.getViewName();
        log.debug("Redirecting to '{}' for request URI [{}]", redirectUri, request.getRequestURI());
        ServletUtils.issueRedirect(request, response, redirectUri, null, true, true);
    }

    protected void render(HttpServletRequest request, HttpServletResponse response, ViewModel vm) throws Exception {

        log.debug("Rendering view '{}' for request URI [{}]", vm.getViewName(), request.getRequestURI());
        View view = this.viewResolver.getView(vm, request);
        view.render(request, response, vm);
    }
}
