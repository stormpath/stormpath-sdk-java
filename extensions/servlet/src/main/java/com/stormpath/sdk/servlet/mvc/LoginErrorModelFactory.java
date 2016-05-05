package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.http.HttpServletRequest;

/**
 * Generates the error model on a failed login attempt.
 *
 * @since 1.0.RC7
 */
public class LoginErrorModelFactory extends AbstractErrorModelFactory {
    private static final String INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    public LoginErrorModelFactory(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected String getDefaultMessageKey() {
        return INVALID_LOGIN_MESSAGE;
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
