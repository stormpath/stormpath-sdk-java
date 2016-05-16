package com.stormpath.spring.mvc;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.FormFieldsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.*;

/**
 * @since 1.0.0
 */
abstract class AbstractSpringControllerConfigResolver implements ControllerConfigResolver, FormFieldsFactory {

    protected abstract Map<String, Field> getDefaultFields();

    protected abstract String getFormKey();

    protected abstract String getDefaultFieldOrder();

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected Resolver<Locale> localeResolver;

    @Autowired
    protected CsrfTokenManager csrfTokenManager;

    @Autowired
    protected Environment env;

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Override
    public Resolver<Locale> getLocaleResolver() {
        return localeResolver;
    }

    @Override
    public CsrfTokenManager getCsrfTokenManager() {
        return csrfTokenManager;
    }

    @Override
    public List<Field> getFormFields() {
        List<String> fieldNames = Arrays.asList(env.getProperty("stormpath.web." + getFormKey() + ".form.fields.fieldOrder", getDefaultFieldOrder()).split(","));
        List<Field> fields = new ArrayList<Field>();

        for (String fieldName : fieldNames) {
            String trimmedFieldName = Strings.trimAllWhitespace(fieldName);

            //Could be null if no default field is defined for that name
            Field defaultField = getDefaultFields().get(trimmedFieldName);

            DefaultField field = DefaultField.builder()
                    .setName(trimmedFieldName)
                    .setType(env.getProperty(getFieldPropertyKey(trimmedFieldName, "type"), defaultField != null ? defaultField.getType() : null))
                    .setLabel(env.getProperty(getFieldPropertyKey(trimmedFieldName, "label"), defaultField != null ? defaultField.getLabel() : null))
                    .setPlaceholder(env.getProperty(getFieldPropertyKey(trimmedFieldName, "placeholder"), defaultField != null ? defaultField.getPlaceholder() : null))
                    .setRequired(env.getProperty(getFieldPropertyKey(trimmedFieldName, "required"), Boolean.class, defaultField != null ? defaultField.isRequired() : null))
                    .setEnable(env.getProperty(getFieldPropertyKey(trimmedFieldName, "enable"), Boolean.class, defaultField != null ? defaultField.isEnabled() : null))
                    .setVisible(env.getProperty(getFieldPropertyKey(trimmedFieldName, "visible"), Boolean.class, defaultField != null ? defaultField.isVisible() : null))
                    .build();
            fields.add(field);
        }

        return fields;
    }

    String getFieldPropertyKey(String fieldName, String property) {
        return "stormpath.web." + getFormKey() + ".form.fields." + fieldName + "." + property;
    }
}
