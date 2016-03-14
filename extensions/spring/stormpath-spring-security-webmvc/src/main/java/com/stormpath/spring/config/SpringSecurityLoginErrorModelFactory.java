package com.stormpath.spring.config;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.RC7
 */
public class SpringSecurityLoginErrorModelFactory implements ErrorModelFactory {

    private static final String  INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    @Autowired
    MessageSource messageSource;

    @Override
    public List<String> toErrors(HttpServletRequest request, Form form, Exception exception) {

        String query = Strings.clean(request.getQueryString());
        if (query != null && query.contains("error")) {
            return Collections.singletonList(getInvalidLoginMessage(request));
        }

        return null;
    }

    private String getInvalidLoginMessage(HttpServletRequest request) {
        return messageSource.getMessage(INVALID_LOGIN_MESSAGE, request.getLocale());
    }
}
