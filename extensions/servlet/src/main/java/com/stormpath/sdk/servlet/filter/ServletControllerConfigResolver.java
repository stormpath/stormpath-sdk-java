package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.impl.ConfigReader;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (val == null) {
            return true;
        }
        return new Boolean(val);
    }

    @Override
    public String getControllerKey() {
        return controllerKey;
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
            log.error("Couldn't instantiate the default LocaleResolver instance", e);
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
    public Publisher<RequestEvent> getRequestEventPublisher() {
        try {
            return config.getInstance("stormpath.web.request.event.publisher");
        } catch (ServletException e) {
            log.error("Couldn't instantiate the default RequestEventPublisher instance", e);
            return null;
        }
    }

    @Override
    public List<Field> getFormFields() {
        List<Field> fields = new ArrayList<Field>();

        for (String fieldName : getFormFieldNames()) {
            DefaultField field = new DefaultField.Builder()
                    .setName(fieldName)
                    .setType(config.get("stormpath.web." + controllerKey + ".form.fields." + fieldName + ".type"))
                    .setLabel(config.get("stormpath.web." + controllerKey + ".form.fields." + fieldName + ".label"))
                    .setPlaceholder(config.get("stormpath.web." + controllerKey + ".form.fields." + fieldName + ".placeholder"))
                    .setRequired(Boolean.parseBoolean(config.get("stormpath.web." + controllerKey + ".form.fields." + fieldName + ".required")))
                    .setEnable(Boolean.parseBoolean(config.get("stormpath.web." + controllerKey + ".form.fields." + fieldName + ".enabled")))
                    .setVisible(Boolean.parseBoolean(config.get("stormpath.web." + controllerKey + ".form.fields." + fieldName + ".visible")))
                    .build();

            fields.add(field);
        }

        return fields;
    }

    @SuppressWarnings("unchecked")
    private List<String> getFormFieldNames() {
        List<String> fieldsOrder = configReader.getList("stormpath.web." + controllerKey + ".form.fields.fieldOrder");
        List<String> fieldNames = new ArrayList<String>();

        if (fieldsOrder != null) {
            fieldNames.addAll(fieldsOrder);
        }

        Pattern pattern = Pattern.compile("^stormpath.web." + controllerKey + ".form.fields." + "(\\w+)");

        //Find any other fields that are not in the fieldOrder prop and add them to the end of the list as define in the spec
        for (String key : config.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                if (!"fieldOrder".equals(fieldName) && !fieldNames.contains(fieldName)) {
                    fieldNames.add(fieldName);
                }
            }
        }

        return fieldNames;
    }
}
