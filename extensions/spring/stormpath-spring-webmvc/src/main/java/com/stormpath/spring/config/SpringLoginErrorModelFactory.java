package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0
 */
public class SpringLoginErrorModelFactory implements ErrorModelFactory {

    protected static final String INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    @Autowired
    protected MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public List<String> toErrors(HttpServletRequest request, Form form, Exception exception) {
        if (exception != null) {
            return Collections.singletonList(getInvalidLoginMessage(request));
        }

        return null;
    }

    protected String getInvalidLoginMessage(HttpServletRequest request) {
        return messageSource.getMessage(INVALID_LOGIN_MESSAGE, request.getLocale());
    }
}
