package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.i18n.DefaultMessageSource;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Generates the error model on a failed login attempt.
 *
 * @since 1.0.RC7
 */
public class LoginErrorModelFactory implements ErrorModelFactory {

    private static final Logger log = LoggerFactory.getLogger(LoginErrorModelFactory.class);
    private static final String  INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    public List<String> toErrors(HttpServletRequest request, Form form, Exception e) {
        if (e != null) {
            log.debug("Unable to login user.", e);
            List<String> errors = new ArrayList<String>(1);
            errors.add(getInvalidLoginMessage(request.getLocale()));
            return errors;
        }
        return null;
    }

    private String getInvalidLoginMessage(Locale locale) {
        MessageSource messageSource = new DefaultMessageSource();
        return messageSource.getMessage(INVALID_LOGIN_MESSAGE, locale);
    }
}
