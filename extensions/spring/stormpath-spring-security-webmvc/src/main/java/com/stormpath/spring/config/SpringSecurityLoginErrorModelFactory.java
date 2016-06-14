package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.AbstractErrorModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC7
 */
public class SpringSecurityLoginErrorModelFactory extends AbstractErrorModelFactory {

    private static final String INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    private static final String UNSUCCESSFUL_LOGIN_BACKEND_ERROR = "Login attempt failed";

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
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
        //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/648
        if (e instanceof AuthenticationServiceException) {
            if (e.getMessage() != null && e.getMessage().contains(UNSUCCESSFUL_LOGIN_BACKEND_ERROR)) {
                return true;
            }
        }
        return false;
    }
}
