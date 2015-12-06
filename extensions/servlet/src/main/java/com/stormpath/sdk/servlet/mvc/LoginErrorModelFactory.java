package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.form.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public List<String> toErrors(HttpServletRequest request, Form form, Exception e) {
        if (e != null) {
            log.debug("Unable to login user.", e);
            List<String> errors = new ArrayList<String>(1);
            errors.add("Invalid username or password.");
            return errors;
        }
        return null;
    }
}
