package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0
 */
public class RegisterErrorModelFactory extends AbstractErrorModelFactory {

    public RegisterErrorModelFactory(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected String getDefaultMessageKey() {
        return "stormpath.web.register.form.errors.default";
    }

    @Override
    protected Object[] getMessageParams() {
        return new Object[0];
    }

    @Override
    protected boolean hasError(HttpServletRequest request, Exception e) {
        return e != null;
    }
}
