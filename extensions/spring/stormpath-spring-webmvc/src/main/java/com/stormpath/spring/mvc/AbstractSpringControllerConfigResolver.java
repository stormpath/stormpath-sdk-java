/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.mvc;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.FormFieldsFactory;
import com.stormpath.spring.config.AbstractStormpathWebMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 1.0.0
 */
abstract class AbstractSpringControllerConfigResolver implements ControllerConfigResolver, FormFieldsFactory {

    protected abstract String[] getDefaultFieldOrder();

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected Resolver<Locale> localeResolver;

    @Autowired
    protected CsrfTokenManager csrfTokenManager;

    @Autowired
    protected Publisher<RequestEvent> eventPublisher;

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
    public Publisher<RequestEvent> getRequestEventPublisher() {
        return eventPublisher;
    }

    private Map<String, Field> getDefaultFields() {
        Map<String, Field> fields = new LinkedHashMap<String, Field>();

        for (String fieldName : Arrays.asList(getDefaultFieldOrder())) {
            fields.put(fieldName,
                    new DefaultField.Builder()
                            .setName(fieldName)
                            .setRequired(true)
                            .setPlaceholder("stormpath.web." + getControllerKey() + ".form.fields." + fieldName + ".placeholder")
                            .setLabel("stormpath.web." + getControllerKey() + ".form.fields." + fieldName + ".label")
                            .setEnabled(!getDefaultDisableFields().contains(fieldName))
                            .setVisible(true)
                            .setType(getFieldType(fieldName))
                            .build());
        }

        return fields;
    }

    @Override
    public List<Field> getFormFields() {
        List<Field> fields = new ArrayList<Field>();

        for (String fieldName : getFormFieldNames()) {
            String trimmedFieldName = Strings.trimAllWhitespace(fieldName);

            //Could be null if no default field is defined for that name
            Field defaultField = getDefaultFields().get(trimmedFieldName);

            DefaultField field = DefaultField.builder()
                    .setName(trimmedFieldName)
                    .setType(env.getProperty(getFieldPropertyKey(trimmedFieldName, "type"), defaultField != null ? defaultField.getType() : null))
                    .setLabel(env.getProperty(getFieldPropertyKey(trimmedFieldName, "label"), defaultField != null ? defaultField.getLabel() : null))
                    .setPlaceholder(env.getProperty(getFieldPropertyKey(trimmedFieldName, "placeholder"), defaultField != null ? defaultField.getPlaceholder() : null))
                    .setRequired(env.getProperty(getFieldPropertyKey(trimmedFieldName, "required"), Boolean.class, defaultField != null ? defaultField.isRequired() : null))
                    .setEnabled(env.getProperty(getFieldPropertyKey(trimmedFieldName, "enabled"), Boolean.class, defaultField != null ? defaultField.isEnabled() : null))
                    .setVisible(env.getProperty(getFieldPropertyKey(trimmedFieldName, "visible"), Boolean.class, defaultField != null ? defaultField.isVisible() : null))
                    .build();
            fields.add(field);
        }


        return fields;
    }

    private List<String> getFormFieldNames() {
        List<String> fieldNames = new ArrayList<String>(
                Arrays.asList(env.getProperty("stormpath.web." + getControllerKey() + ".form.fields.fieldOrder", String[].class, getDefaultFieldOrder()))
        );

        Pattern pattern = Pattern.compile("^stormpath.web." + getControllerKey() + ".form.fields." + "(\\w+)");

        //Find any other fields that are not in the fieldOrder prop and add them to the end of the list as define in the spec
        for (String key : AbstractStormpathWebMvcConfiguration.getPropertiesStartingWith((ConfigurableEnvironment) env, "stormpath.web." + getControllerKey() + ".form.fields").keySet()) {
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

    private String getFieldPropertyKey(String fieldName, String property) {
        return "stormpath.web." + getControllerKey() + ".form.fields." + fieldName + "." + property;
    }

    private String getFieldType(String fieldName) {
        if (fieldName.toLowerCase().contains("password")) {
            return "password";
        } else if (fieldName.toLowerCase().contains("email")) {
            return "email";
        } else {
            return "text";
        }
    }

    protected List<String> getDefaultDisableFields() {
        return Collections.EMPTY_LIST;
    }
}
