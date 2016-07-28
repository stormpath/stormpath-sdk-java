package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.MeFilter;
import com.stormpath.sdk.servlet.mvc.MeController;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public class MeFilterFactory extends FilterFactory<MeFilter> {

    @Override
    protected MeFilter createInstance(ServletContext servletContext, Config config) throws Exception {

        MeController c = new MeController(config.getMeExpandedProperties());
        c.setUri(config.getMeUrl());
        c.setProduces(config.getProducedMediaTypes());
        c.init();

        MeFilter filter = new MeFilter();
        filter.setProducedMediaTypes(config.getProducedMediaTypes());
        filter.setController(c);

        return filter;
    }
}
