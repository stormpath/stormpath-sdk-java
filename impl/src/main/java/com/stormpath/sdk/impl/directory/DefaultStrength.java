/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.Strength;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;

import java.util.Map;

import static com.stormpath.sdk.lang.Assert.*;

/**
 * @since 1.0.0
 */
public class DefaultStrength extends AbstractInstanceResource implements Strength {

    // SIMPLE PROPERTIES
    static final IntegerProperty MIN_SYMBOL = new IntegerProperty("minSymbol");
    static final IntegerProperty MIN_DIACRITIC = new IntegerProperty("minDiacritic");
    static final IntegerProperty MIN_UPPERCASE = new IntegerProperty("minUpperCase");
    static final IntegerProperty MIN_LENGTH = new IntegerProperty("minLength");
    static final IntegerProperty MIN_LOWERCASE = new IntegerProperty("minLowerCase");
    static final IntegerProperty MAX_LENGTH = new IntegerProperty("maxLength");
    static final IntegerProperty MIN_NUMERIC = new IntegerProperty("minNumeric");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            MIN_SYMBOL, MIN_DIACRITIC, MIN_UPPERCASE, MIN_LENGTH, MIN_LOWERCASE, MAX_LENGTH, MIN_NUMERIC);

    public DefaultStrength(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultStrength(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public int getMinSymbol() {
        return getInt(MIN_SYMBOL);
    }

    @Override
    public Strength setMinSymbol(int minSymbol) {
        isTrue(minSymbol >= 0, "minSymbol cannot be a negative number.");
        setProperty(MIN_SYMBOL, minSymbol);
        return this;
    }

    @Override
    public int getMinDiacritic() {
        return getInt(MIN_DIACRITIC);
    }

    @Override
    public Strength setMinDiacritic(int minDiacritic) {
        isTrue(minDiacritic >= 0, "minDiacritic cannot be a negative number.");
        setProperty(MIN_DIACRITIC, minDiacritic);
        return this;
    }

    @Override
    public int getMinUpperCase() {
        return getInt(MIN_UPPERCASE);
    }

    @Override
    public Strength setMinUpperCase(int minUpperCase) {
        isTrue(minUpperCase >= 0, "minUpperCase cannot be a negative number.");
        setProperty(MIN_UPPERCASE, minUpperCase);
        return this;
    }

    @Override
    public int getMinLength() {
        return getInt(MIN_LENGTH);
    }

    @Override
    public Strength setMinLength(int minLength) {
        isTrue(minLength > 0 && minLength < 1024, "minLength cannot be less than 1 or larger than 1023.");
        setProperty(MIN_LENGTH, minLength);
        return this;
    }

    @Override
    public int getMinLowerCase() {
        return getInt(MIN_LOWERCASE);
    }

    @Override
    public Strength setMinLowerCase(int minLowerCase) {
        isTrue(minLowerCase >= 0, "minLowerCase cannot be a negative number.");
        setProperty(MIN_LOWERCASE, minLowerCase);
        return this;
    }

    @Override
    public int getMaxLength() {
        return getInt(MAX_LENGTH);
    }

    @Override
    public Strength setMaxLength(int maxLength) {
        isTrue(maxLength > 0 && maxLength < 1024, "maxLength cannot be less than 1 or larger than 1023.");
        setProperty(MAX_LENGTH, maxLength);
        return this;
    }

    @Override
    public int getMinNumeric() {
        return getInt(MIN_NUMERIC);
    }

    @Override
    public Strength setMinNumeric(int minNumeric) {
        isTrue(minNumeric >= 0, "minNumeric cannot be a negative number.");
        setProperty(MIN_NUMERIC, minNumeric);
        return this;
    }

    @Override
    public void save() {
        isTrue(getMaxLength() >= getMinLength(), "minLength cannot be greater than maxLength.");
        isTrue(getMinSymbol() + getMinDiacritic() + getMinUpperCase() + getMinLowerCase() + getMinNumeric() <= getMaxLength(), "maxLength is not large enough to hold all the minimum conditions specified.");
        super.save();
    }
}
