package com.stormpath.spring.mvc;

import com.stormpath.sdk.servlet.form.Field;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * @since 1.0
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
    public boolean isEnable() {
        return logoutEnabled;
    }

    @Override
    protected Map<String, Field> getDefaultFields() {
        return null;
    }

    @Override
    protected String getFormKey() {
        return null;
    }

    @Override
    protected String getDefaultFieldOrder() {
        return null;
    }
}
