package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.RC7
 */
public class SpringSecurityLoginErrorModelFactory implements ErrorModelFactory {

    private static final String INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    private static final String UNSUCCESSFUL_LOGIN_STORMPATH_ERROR = "Login attempt failed because there is no Account in " +
            "the Application's associated Account Stores with the specified username or email";

    @Autowired
    MessageSource messageSource;

    @Override
    public List<String> toErrors(HttpServletRequest request, Form form, Exception exception) {

        //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/648
        if (exception instanceof AuthenticationServiceException) {
            if (exception.getMessage() != null && exception.getMessage().contains(UNSUCCESSFUL_LOGIN_STORMPATH_ERROR)) {
                return Collections.singletonList(getInvalidLoginMessage(request));
            }
        }

        return null;
    }

    private String getInvalidLoginMessage(HttpServletRequest request) {
        return messageSource.getMessage(INVALID_LOGIN_MESSAGE, request.getLocale());
    }
}
