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

import java.util.Map;

/**
 * Names that indicate strategies of how a {@link Conversion} should convert an input object to an output object.
 *
 * @see Conversion#setStrategy(String)
 * @since 1.3.0
 */
public enum ConversionStrategyName {

    /**
     * A strategy that indicates only fields explicitly defined in the
     * Conversion's {@link Conversion#setFields(Map) fields} will be included in the output.  Any fields that are not
     * explicitly defined fields map <em>WILL NOT</em> be included in the converted output.
     */
    DEFINED,

    /**
     * A strategy that indicates that the conversion output should be just one of the source object's field values.
     * The named field to inspect is indicated by {@link Conversion#getField() conversion.getField()}.
     */
    SINGLE,

    /**
     * A strategy that indicates that all of the source object's scalar values should be in the output.  A scalar
     * value is any single value that is not a Collection, Map or compound/complex object.
     */
    SCALARS,

    /**
     * A strategy usable only if the source object is a Collection resource, this strategy ensures that the converted
     * output is the raw List of the collection's elements only, instead of an Object that contains a List of elements.
     * In other words, the converted output will not reflect any properties of the Collection resource itself - only its
     * elements represented as a single List.  If the source object is not a Collection resource/instance, this
     * strategy is ignored.
     */
    LIST,

    /**
     * A strategy that represents <em>ALL</em> fields of the source object should be in the output.  Be careful
     * when choosing this strategy as the output could be considerably larger than desired.
     */
    ALL;

    public static ConversionStrategyName fromName(String name) {
        for (ConversionStrategyName value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unrecognized conversion strategy name: '" + name + "'.");
    }
}
