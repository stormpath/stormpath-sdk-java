package com.stormpath.spring.mvc;

import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.0
 */
public class ForgotPasswordControllerConfigResolver extends AbstractSpringControllerConfigResolver {
    @Value("#{ @environment['stormpath.web.forgotPassword.enabled'] ?: true }")
    protected boolean forgotEnabled;

    @Value("#{ @environment['stormpath.web.forgotPassword.uri'] ?: '/forgot' }")
    protected String forgotUri;

    @Value("#{ @environment['stormpath.web.forgotPassword.nextUri'] ?: '/login?status=forgot' }")
    protected String forgotNextUri;

    @Value("#{ @environment['stormpath.web.forgotPassword.view'] ?: 'stormpath/forgot' }")
    protected String forgotView;

    @Override
    public String getView() {
        return forgotView;
    }

    @Override
    public String getUri() {
        return forgotUri;
    }

    @Override
    public String getNextUri() {
        return forgotNextUri;
    }

    @Override
    public boolean isEnabled() {
        return forgotEnabled;
    }

    @Override
    public String getControllerKey() {
        return "forgotPassword";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
