package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.mvc.FormController;

import java.util.List;

/**
 * @since 1.0.0
 */
public abstract class FormControllerFilterFactory<T extends FormController> extends ControllerFilterFactory<T> {

    protected final void configure(T c, Config config) throws Exception {
        c.setCsrfTokenManager(config.getCsrfTokenManager());
        c.setFieldValueResolver(config.getFieldValueResolver());
        apply(c, getResolver(config));
        doConfigure(c, config);
    }

    private void apply(FormController c, ControllerConfig cr) {
        c.setUri(cr.getUri());
        c.setNextUri(cr.getNextUri());
        c.setView(cr.getView());
        c.setControllerKey(cr.getControllerKey());
        List<Field> fields = cr.getFormFields();
        if (!Collections.isEmpty(fields)) { //might be empty if the fields are static / configured within the controller
            c.setFormFields(fields);
        }
    }

    protected abstract void doConfigure(T c, Config config) throws Exception;

    protected abstract ControllerConfig getResolver(Config config);
}
