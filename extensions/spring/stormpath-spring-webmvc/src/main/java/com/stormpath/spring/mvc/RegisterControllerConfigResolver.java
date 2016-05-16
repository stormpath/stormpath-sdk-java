package com.stormpath.spring.mvc;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

/**
 * @since 1.0.0
 */
public class RegisterControllerConfigResolver extends AbstractSpringControllerConfigResolver {

    private static final String[] DEFAULT_FIELD_NAMES = new String[]{"username", "givenName", "middleName", "surname", "email", "password", "confirmPassword"};

    @Value("#{ @environment['stormpath.web.register.enabled'] ?: true }")
    protected boolean registerEnabled;

    @Value("#{ @environment['stormpath.web.register.uri'] ?: '/register' }")
    protected String registerUri;

    @Value("#{ @environment['stormpath.web.register.nextUri'] ?: '/' }")
    protected String registerNextUri;

    @Value("#{ @environment['stormpath.web.register.view'] ?: 'stormpath/register' }")
    protected String registerView;

    @Override
    public String getView() {
        return registerView;
    }

    @Override
    public String getUri() {
        return registerUri;
    }

    @Override
    public String getNextUri() {
        return registerNextUri;
    }

    @Override
    public boolean isEnabled() {
        return registerEnabled;
    }

    @Override
    public String getControllerKey() {
        return "register";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return DEFAULT_FIELD_NAMES;
    }

    @Override
    protected List<String> getDefaultDisableFields() {
        return Arrays.asList("username", "middleName", "confirmPassword");
    }
}
