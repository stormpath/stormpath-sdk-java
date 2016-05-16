package com.stormpath.spring.mvc;

import com.stormpath.sdk.servlet.form.Field;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * TODO this should be a single configuration and a single controller according to the spec but to I'm keeping as it is for now.
 *
 * @since 1.0.0
 */
public class SendVerificationEmailControllerConfigResolver extends AbstractSpringControllerConfigResolver {
    @Value("#{ @environment['stormpath.web.verify.enabled'] ?: true }")
    protected boolean verifyEnabled;

    @Value("#{ @environment['stormpath.web.verify.view'] ?: 'stormpath/verify' }")
    protected String verifyView;

    @Value("#{ @environment['stormpath.web.sendVerificationEmail.uri'] ?: '/sendVerificationEmail' }")
    protected String sendVerificationEmailUri;

    @Value("#{ @environment['stormpath.web.sendVerificationEmail.view'] ?: 'stormpath/sendVerificationEmail' }")
    protected String sendVerificationEmailView;

    @Override
    public String getView() {
        return sendVerificationEmailView;
    }

    @Override
    public String getUri() {
        return sendVerificationEmailUri;
    }

    @Override
    public String getNextUri() {
        return verifyView;
    }

    @Override
    public boolean isEnabled() {
        return verifyEnabled;
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
