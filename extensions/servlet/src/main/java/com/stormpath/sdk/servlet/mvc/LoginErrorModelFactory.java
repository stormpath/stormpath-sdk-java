package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.i18n.DefaultMessageSource;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates the error model on a failed login attempt.
 *
 * @since 1.0.RC7
 */
public class LoginErrorModelFactory implements ErrorModelFactory {

    private static final Logger log = LoggerFactory.getLogger(LoginErrorModelFactory.class);
    public static final String MESSAGE_SOURCE = "stormpath.web.message.source";
    private static final String  INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    public List<String> toErrors(HttpServletRequest request, Form form, Exception e) {
        if (e != null) {
            log.debug("Unable to login user.", e);
            List<String> errors = new ArrayList<String>(1);
            errors.add(getInvalidLoginMessage(request));
            return errors;
        }
        return null;
    }

    private String getInvalidLoginMessage(HttpServletRequest request) {
        Config config = getConfig(request);
        MessageSource messageSource = null;
        try {
            messageSource = config.getInstance(MESSAGE_SOURCE);
        }
        catch (ServletException se){
            messageSource = new DefaultMessageSource();
        }
        return messageSource.getMessage(INVALID_LOGIN_MESSAGE, request.getLocale());
    }

    private Config getConfig(HttpServletRequest request){
        return ConfigResolver.INSTANCE.getConfig(request.getServletContext());
    }
}
