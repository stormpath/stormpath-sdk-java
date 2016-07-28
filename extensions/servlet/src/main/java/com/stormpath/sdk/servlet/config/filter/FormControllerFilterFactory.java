package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.mvc.FormController;

/**
 * @since 1.0.0
 */
public abstract class FormControllerFilterFactory<T extends FormController> extends ControllerFilterFactory<T> {

    protected final void configure(T c, Config config) throws Exception {
        apply(c, getResolver(config));
        doConfigure(c, config);
    }

    private void apply(FormController c, ControllerConfig cr) {
        c.setControllerKey(cr.getControllerKey());
        c.setCsrfTokenManager(cr.getCsrfTokenManager());
        c.setFormFields(cr.getFormFields());
        c.setLocaleResolver(cr.getLocaleResolver());
        c.setMessageSource(cr.getMessageSource());
        c.setNextUri(cr.getNextUri());
        c.setUri(cr.getUri());
        c.setView(cr.getView());
    }

    protected abstract void doConfigure(T c, Config config) throws Exception;

    protected abstract ControllerConfig getResolver(Config config);
}
