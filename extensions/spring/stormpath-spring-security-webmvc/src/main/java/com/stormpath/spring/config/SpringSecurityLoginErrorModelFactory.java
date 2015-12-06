package com.stormpath.spring.config;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.RC7
 */
public class SpringSecurityLoginErrorModelFactory implements ErrorModelFactory {

    @Override
    public List<String> toErrors(HttpServletRequest request, Form form, Exception exception) {

        String query = Strings.clean(request.getQueryString());
        if (query != null && query.contains("error")) {
            return Collections.singletonList("Invalid login or password.");
        }

        return null;
    }
}
