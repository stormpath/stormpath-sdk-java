package com.stormpath.spring.mvc;

import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.mvc.FormFieldsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class LoginControllerConfigResolver extends AbstractSpringControllerConfigResolver implements FormFieldsFactory {
    @Value("#{ @environment['stormpath.web.login.enabled'] ?: true }")
    private boolean loginEnabled;

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    private String loginUri;

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    private String loginNextUri;

    @Value("#{ @environment['stormpath.web.login.view'] ?: 'stormpath/login' }")
    private String loginView;

    @Autowired
    Environment env;

    private static Map<String, Field> DEFAULT_FIELDS = new LinkedHashMap<String, Field>();

    static {
        DEFAULT_FIELDS.put(
                "login",
                DefaultField.builder()
                        .setName("login")
                        .setLabel("stormpath.web.login.form.fields.login.label")
                        .setPlaceholder("stormpath.web.login.form.fields.login.placeholder")
                        .setRequired(true)
                        .setType("text")
                        .setEnabled(true)
                        .setVisible(true)
                        .build()
        );
        DEFAULT_FIELDS.put(
                "password",
                DefaultField.builder()
                        .setName("password")
                        .setLabel("stormpath.web.login.form.fields.password.label")
                        .setPlaceholder("stormpath.web.login.form.fields.password.placeholder")
                        .setRequired(true)
                        .setType("password")
                        .setEnabled(true)
                        .setVisible(true)
                        .build()
        );
    }

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
    protected Map<String, Field> getDefaultFields() {
        return DEFAULT_FIELDS;
    }

    @Override
    protected String getFormKey() {
        return "login";
    }

    @Override
    protected String getDefaultFieldOrder() {
        return "login,password";
    }
}
