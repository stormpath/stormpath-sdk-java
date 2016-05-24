package com.stormpath.spring.mvc;

import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.0
 */
public class VerifyControllerConfigResolver extends AbstractSpringControllerConfigResolver {
    @Value("#{ @environment['stormpath.web.verifyEmail.enabled'] ?: true }")
    protected boolean verifyEnabled;

    @Value("#{ @environment['stormpath.web.verifyEmail.uri'] ?: '/verify' }")
    protected String verifyUri;

    @Value("#{ @environment['stormpath.web.verifyEmail.nextUri'] ?: '/login?status=verified' }")
    protected String verifyNextUri;

    @Value("#{ @environment['stormpath.web.verifyEmail.view'] ?: 'stormpath/verify' }")
    protected String verifyView;

    @Override
    public String getView() {
        return verifyView;
    }

    @Override
    public String getUri() {
        return verifyUri;
    }

    @Override
    public String getNextUri() {
        return verifyNextUri;
    }

    @Override
    public boolean isEnabled() {
        return verifyEnabled;
    }

    @Override
    public String getControllerKey() {
        return "verifyEmail";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
