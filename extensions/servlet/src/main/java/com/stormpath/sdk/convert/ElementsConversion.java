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

/**
 * An ElementsConversion reflects directives that indicate how to convert elements in a Collection resource into
 * converted output.
 *
 * @since 1.3.0
 */
public class ElementsConversion {

    public static final String DEFAULT_NAME = "items";

    private String name;

    private boolean enabled;

    private Conversion each;

    /**
     * Default constructor that, by default, will represent a collection as an object with a nested {@code items}
     * list.
     */
    public ElementsConversion() {
        this.name = DEFAULT_NAME;
        this.enabled = true;
        this.each = new Conversion();
    }

    /**
     * Returns the assigned name of the elements list within a wrapping object.  This property is ignored if the
     * parent collection's {@link Conversion} strategy is set to {@link ConversionStrategyName#LIST LIST}.  In the
     * {@code LIST} case, the collection elements will be returned directly and not wrapped in an object.
     *
     * @return the assigned name of the elements list within a wrapping object.
     * @see ConversionStrategyName#LIST
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the assigned name of the elements list within a wrapping object. This property is ignored if the
     * parent collection's {@link Conversion} strategy is set to {@link ConversionStrategyName#LIST LIST}.  In the
     * {@code LIST} case, the collection elements will be returned directly and not wrapped in an object.
     *
     * @param name the assigned name of the elements list within a wrapping object.
     * @return this object for method chaining.
     * @see ConversionStrategyName#LIST
     */
    public ElementsConversion setName(String name) {
        Assert.hasText("name argument cannot be null or empty.");
        this.name = name;
        return this;
    }

    /**
     * Returns {@code true} if this conversion is enabled and the elements should be converted, {@code false} if
     * the elements should not be converted and skipped/removed from the resulting output entirely.
     *
     * @return {@code true} if this conversion is enabled and the elements should be converted, {@code false} if
     * the elements should not be converted and skipped/removed from the resulting output entirely.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether or not this conversion is enabled. A {@code true} value indicates that this conversion is enabled
     * and the elements should be converted, a {@code false} value indicates that the elements should not be converted
     * and skipped/removed from the resulting output entirely.
     *
     * @param enabled whether or not this conversion is enabled.
     * @return this object for method chaining.
     */
    public ElementsConversion setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Returns the Conversion that directs how to convert each element within the collection.
     *
     * @return the Conversion that directs how to convert each element within the collection.
     */
    public Conversion getEach() {
        return each;
    }

    /**
     * Sets the Conversion that directs how to convert each element within the collection.
     *
     * @param each the Conversion that directs how to convert each element within the collection.
     * @return this object for method chaining.
     */
    public ElementsConversion setEach(Conversion each) {
        this.each = each;
        return this;
    }
}
