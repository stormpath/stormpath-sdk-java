package com.stormpath.spring.mvc;

import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.0
 */
public class LogoutControllerConfigResolver extends AbstractSpringControllerConfigResolver {
    @Value("#{ @environment['stormpath.web.logout.enabled'] ?: true }")
    protected boolean logoutEnabled;

    @Value("#{ @environment['stormpath.web.logout.uri'] ?: '/logout' }")
    protected String logoutUri;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/login?status=logout' }")
    protected String logoutNextUri;

    @Override
    public String getView() {
        return null;
    }

    @Override
    public String getUri() {
        return logoutUri;
    }

    @Override
    public String getNextUri() {
        return logoutNextUri;
    }

    @Override
    public boolean isEnabled() {
        return logoutEnabled;
    }

    @Override
    public String getControllerKey() {
        return "logout";
    }

    @Override
    protected String[] getDefaultFieldOrder() {
        return new String[0];
    }
}
