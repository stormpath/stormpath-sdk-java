package com.stormpath.spring.config;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.AbstractErrorModelFactory;
import com.stormpath.sdk.servlet.mvc.ErrorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC7
 */
public class SpringSecurityLoginErrorModelFactory extends AbstractErrorModelFactory {

    private static final String INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    @Autowired
    private org.springframework.context.MessageSource messageSource;

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
        String query = Strings.clean(request.getQueryString());
        return query != null && query.contains("error");
    }

    @Override
    protected String getErrorMessage(HttpServletRequest request, String key) {
        String defaultMessage = messageSource.getMessage(getDefaultMessageKey(), new Object[]{}, request.getLocale());

        return messageSource.getMessage(key, getMessageParams(), defaultMessage, request.getLocale());
    }
}
