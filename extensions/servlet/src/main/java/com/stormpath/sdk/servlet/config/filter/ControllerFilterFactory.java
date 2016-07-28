package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.AbstractController;

import javax.servlet.ServletContext;

/**
 * @since 1.0.0
 */
public abstract class ControllerFilterFactory<T extends AbstractController> extends FilterFactory<ControllerFilter> {

    @Override
    protected final ControllerFilter createInstance(ServletContext servletContext, Config config) throws Exception {

        T c = newController();
        c.setAccountResolver(config.getAccountResolver());
        c.setContentNegotiationResolver(config.getContentNegotiationResolver());
        c.setEventPublisher(config.getRequestEventPublisher());
        c.setLocaleResolver(config.getLocaleResolver());
        c.setMessageSource(config.getMessageSource());
        c.setProduces(config.getProducedMediaTypes());

        configure(c, config);

        c.init();

        ControllerFilter filter = new ControllerFilter();
        filter.setProducedMediaTypes(config.getProducedMediaTypes());
        filter.setController(c);

        return filter;
    }

    protected abstract T newController();

    protected abstract void configure(T c, Config config) throws Exception;
}
