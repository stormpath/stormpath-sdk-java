package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.account.AccountResolver;
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

        c.setEventPublisher(config.getRequestEventPublisher());
        c.setAccountResolver(AccountResolver.INSTANCE);
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
