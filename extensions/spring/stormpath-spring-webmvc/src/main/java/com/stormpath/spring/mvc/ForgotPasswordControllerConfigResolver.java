package com.stormpath.spring.mvc;

import com.stormpath.sdk.servlet.form.Field;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * @since 1.0
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
    public boolean isEnable() {
        return forgotEnabled;
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
