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

import com.stormpath.sdk.lang.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Conversion represents directives that indicate how to convert one object (or collection) into another.
 *
 * @since 1.3.0
 */
public class Conversion {

    private boolean enabled;

    private String name;

    private ConversionStrategyName strategy;

    private Map<String, Conversion> fields;

    private String field;

    private ElementsConversion elements;

    /**
     * Default constructor that enables the following settings:
     * <ul>
     * <li>{@link #isEnabled() enabled} = true</li>
     * <li>{@link #setStrategy(String) strategy} = {@link ConversionStrategyName#SCALARS SCALARS}</li>
     * </ul>
     */
    public Conversion() {
        this.enabled = true;
        this.strategy = ConversionStrategyName.SCALARS;
        this.fields = java.util.Collections.emptyMap();
    }

    /**
     * Returns {@code true} if this conversion is enabled and will result in a converted value, or {@code false} if
     * no conversion will occur and the input field or object will be skipped/removed entirely. Unless overridden,
     * the default value is {@code true}, meaning a discovered input field/object should be converted by default.
     *
     * @return {@code true} if this conversion is enabled and will result in a converted value, or {@code false} if
     * no conversion will occur and the input field or object will be skipped/removed entirely.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether or not the conversion is enabled and will result in a converted value.  If {@code false}, no
     * conversion will take place and the input object will not be represented at all.  Unless overridden, the default
     * value is {@code true}, meaning a discovered input field/object should be converted by default.
     *
     * @param enabled whether or not the conversion is enabled and will result in a converted value.
     * @return this object for method chaining.
     */
    public Conversion setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Returns the field/property name to represent the converted output object.  If unspecified, the name will be
     * the same name as discovered on the input object.
     *
     * @return the field/property name to represent the converted output object.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the field/property name to represent the converted output object.  If unspecified, the name will be
     * the same name as discovered on the input object.
     *
     * @param name the field/property name to represent the converted output object.
     * @return this object for method chaining.
     */
    public Conversion setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the conversion strategy to use when converting an input object to an output object.  Unless overridden,
     * the default strategy is {@link ConversionStrategyName#SCALARS SCALARS}.
     *
     * @return the conversion strategy to use whne converting the input object to an output object.
     */
    public ConversionStrategyName getStrategy() {
        return strategy;
    }

    /**
     * Sets the strategy to use when converting an input object to an output object.  Unless overridden, the
     * default strategy is {@link ConversionStrategyName#SCALARS SCALARS}.
     *
     * @param strategy the strategy to use when converting the input object.
     * @return this object for method chaining.
     * @see ConversionStrategyName
     */
    public Conversion setStrategy(ConversionStrategyName strategy) {
        Assert.notNull(strategy, "strategy argument cannot be null.");
        this.strategy = strategy;
        return this;
    }

    /**
     * Sets the strategy to use when converting the input object.  This is a convenience method that
     * {@link ConversionStrategyName#fromName(String) looks up} the {@link ConversionStrategyName} instance using the
     * method argument.
     * <p>Unless overridden, the default strategy is {@link ConversionStrategyName#SCALARS SCALARS}.</p>
     *
     * @param strategy the strategy to use when converting the input object.
     * @return this object for method chaining.
     * @see #setStrategy(ConversionStrategyName)
     * @see ConversionStrategyName
     */
    public Conversion setStrategy(String strategy) {
        return setStrategy(ConversionStrategyName.fromName(strategy));
    }


    /**
     * Returns the name of the field on the input object to use as the only output value.  This property is only used
     * with the {@link ConversionStrategyName#SINGLE SINGLE} strategy - it is ignored otherwise.
     *
     * @return the name of the field on the input object to use as the only output value.
     * @see ConversionStrategyName#SINGLE
     */
    public String getField() {
        return field;
    }

    /**
     * Sets the name of the field on the input object to use as the only output value.  This property is only used with
     * the {@link ConversionStrategyName#SINGLE SINGLE} strategy - it is ignored otherwise.
     *
     * @param field the name of the field on the input object to use as the only output value.
     * @return this object for method chaining.
     * @see ConversionStrategyName#SINGLE
     */
    public Conversion setField(String field) {
        this.field = field;
        return this;
    }

    /**
     * Returns the Conversion directives for any specifically named fields encountered on an input object.  The
     * Map's String key is the name of a field on the object to convert. The Map value for that key is the Conversion
     * that indicates how to convert the corresponding field's value.
     * <p>Because Conversion instances can contain fields (Maps of named Conversion instances), and so on, a
     * Conversion can reflect how to convert an entire object graph if desired.</p>
     *
     * @return the Conversion directives for any specifically named fields encountered on an input object.
     */
    public Map<String, Conversion> getFields() {
        return fields;
    }

    /**
     * Sets the Conversion directives for any specifically named fields encountered on an input object.  The
     * Map's String key is the name of a field on the object to convert. The Map value for that key is the Conversion
     * that indicates how to convert the corresponding field's value.
     * <p>Because Conversion instances can contain fields (Maps of named Conversion instances), and so on, a
     * Conversion can reflect how to convert an entire object graph if desired.</p>
     *
     * @param fields the conversions to apply when encountering named field values
     * @return this Conversion object for method chaining.
     */
    public Conversion setFields(Map<String, Conversion> fields) {
        if (fields == null) {
            fields = java.util.Collections.emptyMap();
        }
        this.fields = fields;
        return this;
    }

    private Map<String, Conversion> ensureFields() {
        Map<String, Conversion> fields = this.fields;
        if (fields == null || fields == java.util.Collections.EMPTY_MAP) {
            this.fields = fields = new LinkedHashMap<>();
        }
        return fields;
    }

    /**
     * A convenience builder method that will add the specified {@code conversion} argument to the
     * {@link #getFields() fields} map under the specified {@code fieldName} key.
     *
     * @param fieldName  the name of the field on the input object to be converted
     * @param conversion the conversion to apply when encountering the named field on the input object
     * @return this object for method chaining.
     */
    public Conversion withField(String fieldName, Conversion conversion) {
        Assert.hasText(fieldName, "fieldName argument cannot be null or empty.");
        Assert.notNull(conversion, "fieldConfig argument cannot be null.");
        ensureFields().put(fieldName, conversion);
        return this;
    }

    /**
     * Returns the {@link ElementsConversion} that indicates how to convert a collection's elements.  This property is
     * only used if the input object is a collection resource - it is ignored otherwise.
     *
     * @return the {@link ElementsConversion} that indicates how to convert a collection's elements.
     */
    public ElementsConversion getElements() {
        return elements;
    }

    /**
     * Sets the {@link ElementsConversion} that indicates how to convert a collection's elements.  This property is
     * only used if the input object is a collection resource - it is ignored otherwise.
     *
     * @param elements the {@code ElementsConversion} that indicates how to convert a collection's elements.
     * @return this object for method chaining.
     */
    public Conversion setElements(ElementsConversion elements) {
        this.elements = elements;
        return this;
    }
}
