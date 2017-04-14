package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.AbstractErrorModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @since 1.0.RC7
 */
public class SpringSecurityLoginErrorModelFactory extends AbstractErrorModelFactory {

    private static final String INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    // Handling multiple login backend errors is a fix for
    // https://github.com/stormpath/stormpath-sdk-java/issues/915
    // and for conformance with the stormpath-framework-spec as enforced
    // by the stormpath-framework-tck
    private static final List<String> UNSUCCESSFUL_LOGIN_BACKEND_ERRORS = Arrays.asList(
        "Login attempt failed",
        "Invalid username or password",
        "Login and password required",
        "invalid_grant" // for Okta
        ,"User canceled the social login request."
    );

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
        if (e instanceof AuthenticationServiceException && e.getMessage() != null) {
            for (String validBackendMessage : UNSUCCESSFUL_LOGIN_BACKEND_ERRORS) {
                if (e.getMessage().contains(validBackendMessage)) {
                    return true;
                }
            }
        }
        return false;
    }
}
