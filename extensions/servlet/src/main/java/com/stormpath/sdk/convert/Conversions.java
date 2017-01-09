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

/**
 * A utility method class that enhances working with and constructing {@link Conversion} rules/chains.
 *
 * @since 1.3.0
 */
public final class Conversions {

    //prevent public instantiation:
    private Conversions() {
    }

    /**
     * Returns a new {@code Conversion} that is disabled.
     *
     * @return a new {@code Conversion} that is disabled.
     */
    public static Conversion disabled() {
        return new Conversion().setEnabled(false);
    }

    /**
     * Returns a new {@code Conversion} instance that contains a field that should be converted according to the
     * specified {@code conversion} argument.  That is, the specified argument applies to the object's named field,
     * not the object itself.
     *
     * @param name the name of a field on an object to convert
     * @param c    the conversion rules to apply when encountering the named field's value
     * @return a new {@code Conversion} instance that contains a field that should be converted according to the
     * specified {@code conversion} argument.
     */
    public static Conversion withField(String name, Conversion c) {
        return new Conversion().withField(name, c);
    }

    /**
     * Returns a new {@code Conversion} instance that uses the specified strategy.
     *
     * @param name the name of the strategy to use when converting an input object.
     * @return a new {@code Conversion} instance that uses the specified strategy.
     */
    public static Conversion withStrategy(ConversionStrategyName name) {
        return new Conversion().setStrategy(name);
    }

    /**
     * Returns a new {@code ElementsConversion} instance where {@link ElementsConversion#each each} element will
     * be converted according to the specified {@code conversion} argument.
     *
     * @param conversion a new {@code ElementsConversion} instance where {@link ElementsConversion#each each} element
     *                   will be converted according to the specified {@code conversion} argument.
     * @return a new {@code ElementsConversion} instance where {@link ElementsConversion#each each} element will
     * be converted according to the specified {@code conversion} argument.
     */
    public static ElementsConversion each(Conversion conversion) {
        return new ElementsConversion().setEach(conversion);
    }

}
