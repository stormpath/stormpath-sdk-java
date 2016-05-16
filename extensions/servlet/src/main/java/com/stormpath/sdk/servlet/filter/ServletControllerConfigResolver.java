package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.impl.ConfigReader;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @since 1.0.0
 */
public class ServletControllerConfigResolver implements ControllerConfigResolver {
    private static final Logger log = LoggerFactory.getLogger(ServletControllerConfigResolver.class);

    private ConfigReader configReader;
    private Config config;
    private String controllerKey;

    public ServletControllerConfigResolver(Config config, ConfigReader configReader, String controllerKey) {
        this.configReader = configReader;
        this.controllerKey = controllerKey;
        this.config = config;

        Assert.notNull(config, "config cannot be null.");
        Assert.notNull(configReader, "configReader cannot be null.");
        Assert.notNull(controllerKey, "controllerKey cannot be null.");
    }

    @Override
    public String getView() {
        return configReader.getString("stormpath.web." + controllerKey + ".view");
    }

    @Override
    public String getUri() {
        return configReader.getString("stormpath.web." + controllerKey + ".uri");
    }

    @Override
    public String getNextUri() {
        return configReader.getString("stormpath.web." + controllerKey + ".nextUri");
    }

    @Override
    public boolean isEnabled() {
        String val = config.get("stormpath.web." + controllerKey + ".enabled");
        if(val == null) {
            return true;
        }
        return new Boolean(val);
    }

    @Override
    public MessageSource getMessageSource() {
        try {
            return config.getInstance("stormpath.web.message.source");
        } catch (ServletException e) {
            log.error("Couldn't instantiate the default MessageSource instance", e);
            return null;
        }
    }

    @Override
    public Resolver<Locale> getLocaleResolver() {
        try {
            return config.getInstance("stormpath.web.locale.resolver");
        } catch (ServletException e) {
            log.error("Couldn't instantiate the default CsrfTokenManager instance", e);
            return null;
        }
    }

    @Override
    public CsrfTokenManager getCsrfTokenManager() {
        try {
            return config.getInstance("stormpath.web.csrf.token.manager");
        } catch (ServletException e) {
            log.error("Couldn't instantiate the default CsrfTokenManager instance", e);
            return null;
        }
    }

    @Override
    public List<Field> getFormFields() {
        List<String> fieldNames = Arrays.asList(config.get("stormpath.web." + controllerKey + ".form.fields.fieldOrder").split(","));
        List<Field> fields = new ArrayList<Field>();

        for (String fieldName : fieldNames) {
            String trimmedFieldName = Strings.trimAllWhitespace(fieldName);
            DefaultField field = DefaultField.builder()
                    .setName(trimmedFieldName)
                    .setType(config.get("stormpath.web." + controllerKey + ".form.fields." + trimmedFieldName + ".type"))
                    .setLabel(config.get("stormpath.web." + controllerKey + ".form.fields." + trimmedFieldName + ".label"))
                    .setPlaceholder(config.get("stormpath.web." + controllerKey + ".form.fields." + trimmedFieldName + ".placeholder"))
                    .setRequired(Boolean.parseBoolean(config.get("stormpath.web." + controllerKey + ".form.fields." + trimmedFieldName + ".required")))
                    .build();
            fields.add(field);
        }

        return fields;
    }
}
