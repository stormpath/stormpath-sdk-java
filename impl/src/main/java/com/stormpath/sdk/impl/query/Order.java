/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.lang.Assert;

import java.io.Serializable;

/**
 * @since 0.8
 */
public class Order implements Serializable {

    private final String propertyName;
    private final boolean ascending;

    public Order(String propertyName, boolean ascending) {
        Assert.hasText(propertyName, "propertyName cannot be null or empty.");
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    /**
     * Ascending order.
     *
     * @param propertyName the name of the property to use to sort results in ascending order.
     * @return a new ascending Order instance.
     */
    public static Order asc(String propertyName) {
        return new Order(propertyName, true);
    }

    /**
     * Descending order.
     *
     * @param propertyName the name of the property to use to sort results in descending order.
     * @return a new descending Order instance
     */
    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isAscending() {
        return ascending;
    }

    @Override
    public String toString() {
        return this.propertyName + ' ' + (this.isAscending() ? "asc" : "desc");
    }
}
