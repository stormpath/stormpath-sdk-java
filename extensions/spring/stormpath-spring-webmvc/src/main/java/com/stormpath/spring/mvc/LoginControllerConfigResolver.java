package com.stormpath.spring.mvc;

import com.stormpath.sdk.servlet.mvc.FormFieldsFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.0
 */
public class LoginControllerConfigResolver extends AbstractSpringControllerConfigResolver implements FormFieldsFactory {

    private static final String[] DEFAULT_FIELD_NAMES = new String[]{"login", "password"};

    @Value("#{ @environment['stormpath.web.login.enabled'] ?: true }")
    private boolean loginEnabled;

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    private String loginUri;

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    private String loginNextUri;

    @Value("#{ @environment['stormpath.web.login.view'] ?: 'stormpath/login' }")
    private String loginView;

    @Override
    public String getView() {
        return loginView;
    }

    @Override
    public String getUri() {
        return loginUri;
    }

    @Override
    public String getNextUri() {
        return loginNextUri;
    }

    @Override
    public boolean isEnabled() {
        return loginEnabled;
    }

    @Override
    public String getControllerKey() {
        return "login";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return DEFAULT_FIELD_NAMES;
    }
}
