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
package com.stormpath.sdk.convert;

import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.lang.Instants;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converts a {@link Resource} or {@link Map} to an output value that is structured according the specified
 * {@link #setConfig(Conversion) config}.
 *
 * @since 1.3.0
 */
public class ResourceConverter<T> implements Function<T, Object> {

    private static final Logger log = LoggerFactory.getLogger(ResourceConverter.class);

    public static final Conversion DEFAULT_CONFIG = Conversions
        //.withField("href", Conversions.disabled())
        .withField("customData", Conversions.withStrategy(ConversionStrategyName.SCALARS))
        .withField("groups", Conversions.withStrategy(ConversionStrategyName.DEFINED).setElements(Conversions.each(new Conversion())));

    private Conversion config;

    public ResourceConverter() {
        this.config = DEFAULT_CONFIG;
    }

    /**
     * Returns the {@code Conversion} that indicates how to convert the function argument into an output value.
     *
     * @return the {@code Conversion} that indicates how to convert the function argument into an output value.
     */
    public Conversion getConfig() {
        return config;
    }

    /**
     * Sets the {@code Conversion} that indicates how to convert the function argument into an output value.
     *
     * @param config the {@code Conversion} that indicates how to convert the function argument into an output value.
     */
    public void setConfig(Conversion config) {
        Assert.notNull(config, "Conversion argument cannot be null.");
        this.config = config;
    }

    /**
     * a {@link Resource} or {@link Map} to an output value that is structured according the specified
     * {@link #setConfig(Conversion) config}.
     *
     * @param t the resource or Map to convert
     * @return an output value that is constructed according to the specified {@link #setConfig(Conversion) config}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object apply(T t) {
        Assert.notNull(t, "Argument cannot be null");
        return convert(t, this.config, "");
    }

    public Object convert(Object o, Conversion config, String path) {

        Assert.notNull(o, "Object argument cannot be null");

        ConversionStrategyName strategy = config.getStrategy();
        Assert.notNull(strategy, "config strategy value cannot be null.");

        if (o instanceof AbstractResource) {
            AbstractResource ar = (AbstractResource) o;
            if (!ar.isMaterialized()) {
                ar.materialize();
            }
        }

        boolean isCollection = o instanceof Iterable;

        Map<String, Object> props = new LinkedHashMap<>();

        if (strategy == ConversionStrategyName.SINGLE) {
            String fieldName = config.getField();
            if (Strings.hasText(fieldName) && hasProperty(o, fieldName)) {
                return getProperty(o, fieldName);
            } else if (o instanceof AbstractResource) {
                return ((AbstractResource) o).getHref();
            } else {
                return null; //TODO: error or log message?
            }
        }

        Map<String, Conversion> fieldsConfig = config.getFields();

        Set<String> fieldNames = getPropertyNames(o);

        for (String fieldName : fieldNames) {

            String outputName = fieldName;
            Object value = null;
            boolean defined;
            boolean enabled;

            //if the current object is a collection, the elements in the collection are not represented by the
            //fields config - instead they are represented by the 'elements' config.  So if we're visiting an 'items'
            //field, we need to see if it is enabled by checking the 'elements' config, not the 'fields' config:
            if (isCollection && fieldName.equals("items")) {
                ElementsConversion elementsConfig = config.getElements();
                defined = elementsConfig != null;
                enabled = !defined || elementsConfig.isEnabled();
            } else {
                //a field - check the fieldsConfig:
                defined = fieldsConfig.containsKey(fieldName);
                enabled = isEnabled(fieldsConfig, fieldName);
            }

            if (strategy == ConversionStrategyName.DEFINED) {
                enabled = defined && enabled;
            }

            if (enabled) {
                value = getProperty(o, fieldName);
            }

            boolean compound = isCompound(value);

            if (strategy == ConversionStrategyName.SCALARS && compound && !defined) {
                enabled = false;
            }

            if (enabled) {

                Conversion fieldConfig = fieldsConfig.get(fieldName);
                if (fieldConfig != null) {
                    String name = fieldConfig.getName();
                    if (Strings.hasText(name)) {
                        outputName = name;
                    }
                }

                if (compound) {

                    if (isCollection) {
                        String elementsPath = joinPath(path, "elements");
                        ElementsConversion elementsConfig = config.getElements();
                        String eachPath = joinPath(elementsPath, "each");
                        Conversion elementConfig = elementsConfig.getEach();
                        List<Object> elements = new ArrayList<>();

                        int i = 0;
                        for (Object element : ((Iterable) o)) {
                            Object elementValue = convert(element, elementConfig, eachPath + "[" + i + "]");
                            if (elementValue != null) {
                                elements.add(elementValue);
                            }
                            i++;
                        }

                        value = elements;

                        if (strategy == ConversionStrategyName.LIST) {
                            return value;
                        }

                        String elementsName = elementsConfig.getName();
                        if (Strings.hasText(elementsName)) {
                            outputName = elementsName;
                        }

                    } else {
                        String fieldsPath = joinPath(path, "fields");
                        fieldConfig = fieldsConfig.get(fieldName);
                        String newPath = joinPath(fieldsPath, fieldName);
                        value = convert(value, fieldConfig, newPath);
                    }
                }

                props.put(outputName, value);
            }
        }

        if (props.isEmpty()) {
            //nothing configured, TODO: print warning?
            if (o instanceof AbstractResource) {
                props.put(AbstractResource.HREF_PROP_NAME, ((AbstractResource) o).getHref());
            }
        }

        return props;
    }

    @SuppressWarnings("unchecked")
    protected Set<String> getPropertyNames(Object o) {
        if (o instanceof AbstractResource) {
            return ((AbstractResource) o).getPropertyNames();
        } else if (o instanceof Map) {
            return ((Map) o).keySet();
        }
        throw new IllegalArgumentException("Argument must be an AbstractResource or Map.");
    }

    protected boolean hasProperty(Object o, String name) {
        if (o instanceof AbstractResource) {
            return ((AbstractResource) o).hasProperty(name);
        } else if (o instanceof Map) {
            return ((Map) o).containsKey(name);
        }
        throw new IllegalArgumentException("Argument must be an AbstractResource or Map.");
    }

    protected Object getProperty(Object o, String name) {

        Object value;

        if (o instanceof Map) {

            value = ((Map) o).get(name);

        } else if (o instanceof AbstractResource) {

            AbstractResource resource = (AbstractResource) o;

            if (o instanceof CollectionResource && name.equals("items")) {
                value = resource.getProperty(name);
            } else {
                try {
                    final Class<? extends AbstractResource> resourceClass = resource.getClass();
                    final String methodName = "get" + Strings.capitalize(name);
                    Method method = resourceClass.getMethod(methodName);
                    value = method.invoke(o);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    if (log.isDebugEnabled()) {
                        String msg = "Unable to access resource property '" + name + "': " + e.getMessage();
                        log.debug(msg, e);
                    }
                    value = resource.getProperty(name);
                }
            }

        } else {
            throw new IllegalArgumentException("Argument must be an AbstractResource or Map.");
        }

        if (value instanceof Date) {
            Date date = (Date) value;
            value = Instants.toUtcIso8601(date);
        }

        return value;
    }

    protected boolean isCompound(Object value) {
        return value instanceof Collection || value instanceof Map || value instanceof Resource;
    }

    protected boolean isEnabled(Map<String, Conversion> fieldConfig, String field) {
        if ("password".equals(field)) {
            return false;
        }
        Conversion config = fieldConfig.get(field);
        return config == null || config.isEnabled();
    }

    protected String joinPath(String parent, String child) {
        StringBuilder sb = new StringBuilder(parent);
        if (!"".equals(parent)) {
            sb.append('.');
        }
        sb.append(child);
        return sb.toString();
    }
}
