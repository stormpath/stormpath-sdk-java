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

import com.stormpath.sdk.directory.PasswordStrength;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;

import java.util.Map;


/**
 * @since 1.0.RC4
 */
public class DefaultPasswordStrength extends AbstractInstanceResource implements PasswordStrength {

    // SIMPLE PROPERTIES
    static final IntegerProperty MIN_SYMBOL = new IntegerProperty("minSymbol");
    static final IntegerProperty MIN_DIACRITIC = new IntegerProperty("minDiacritic");
    static final IntegerProperty MIN_UPPERCASE = new IntegerProperty("minUpperCase");
    static final IntegerProperty MIN_LENGTH = new IntegerProperty("minLength");
    static final IntegerProperty MIN_LOWERCASE = new IntegerProperty("minLowerCase");
    static final IntegerProperty MAX_LENGTH = new IntegerProperty("maxLength");
    static final IntegerProperty MIN_NUMERIC = new IntegerProperty("minNumeric");
    static final IntegerProperty PREVENT_REUSE = new IntegerProperty("preventReuse");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            MIN_SYMBOL, MIN_DIACRITIC, MIN_UPPERCASE, MIN_LENGTH, MIN_LOWERCASE, MAX_LENGTH, MIN_NUMERIC, PREVENT_REUSE);

    public DefaultPasswordStrength(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultPasswordStrength(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public PasswordStrength setMinSymbol(int minSymbol) {
        Assert.isTrue(minSymbol >= 0, "minSymbol cannot be a negative number.");
        setProperty(MIN_SYMBOL, minSymbol);
        return this;
    }

    @Override
    public int getMinDiacritic() {
        return getInt(MIN_DIACRITIC);
    }

    @Override
    public PasswordStrength setMinDiacritic(int minDiacritic) {
        Assert.isTrue(minDiacritic >= 0, "minDiacritic cannot be a negative number.");
        setProperty(MIN_DIACRITIC, minDiacritic);
        return this;
    }

    @Override
    public int getMinUpperCase() {
        return getInt(MIN_UPPERCASE);
    }

    @Override
    public PasswordStrength setMinUpperCase(int minUpperCase) {
        Assert.isTrue(minUpperCase >= 0, "minUpperCase cannot be a negative number.");
        setProperty(MIN_UPPERCASE, minUpperCase);
        return this;
    }

    @Override
    public int getMinLength() {
        return getInt(MIN_LENGTH);
    }

    @Override
    public PasswordStrength setMinLength(int minLength) {
        Assert.isTrue(minLength > 0 && minLength <= 1024, "minLength cannot be less than 1 or larger than 1024.");
        setProperty(MIN_LENGTH, minLength);
        return this;
    }

    @Override
    public int getMinLowerCase() {
        return getInt(MIN_LOWERCASE);
    }

    @Override
    public PasswordStrength setMinLowerCase(int minLowerCase) {
        Assert.isTrue(minLowerCase >= 0, "minLowerCase cannot be a negative number.");
        setProperty(MIN_LOWERCASE, minLowerCase);
        return this;
    }

    @Override
    public int getMaxLength() {
        return getInt(MAX_LENGTH);
    }

    @Override
    public PasswordStrength setMaxLength(int maxLength) {
        Assert.isTrue(maxLength > 0 && maxLength <= 1024, "maxLength cannot be less than 1 or larger than 1024.");
        setProperty(MAX_LENGTH, maxLength);
        return this;
    }

    @Override
    public int getMinNumeric() {
        return getInt(MIN_NUMERIC);
    }

    @Override
    public PasswordStrength setMinNumeric(int minNumeric) {
        Assert.isTrue(minNumeric >= 0, "minNumeric cannot be a negative number.");
        setProperty(MIN_NUMERIC, minNumeric);
        return this;
    }


    /**
     * @since 1.1.0
     */
    @Override
    public int getPreventReuse() {
        return getInt(PREVENT_REUSE);
    }


    /**
     * @since 1.1.0
     */
    @Override
    public PasswordStrength setPreventReuse(int preventReuse) {
        Assert.isTrue(preventReuse >= 0, "preventReuse cannot be a negative number.");
        Assert.isTrue(preventReuse <= 25, "preventReuse cannot be larger than 25.");
        setProperty(PREVENT_REUSE, preventReuse);
        return this;
    }

    @Override
    public void save() {
        Assert.isTrue(getMaxLength() >= getMinLength(), "minLength cannot be greater than maxLength.");
        Assert.isTrue(getMinSymbol() + getMinDiacritic() + getMinUpperCase() + getMinLowerCase() + getMinNumeric() <= getMaxLength(), "maxLength is not large enough to hold all the minimum conditions specified.");
        super.save();
    }
}
