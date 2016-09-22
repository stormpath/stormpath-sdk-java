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
package com.stormpath.sdk.impl.directory

import com.stormpath.sdk.directory.PasswordStrength
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.resource.IntegerProperty
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.RC4
 */
class DefaultPasswordStrengthTest {

    @Test
    void testGetPropertyDescriptors() {

        PasswordStrength strength = new DefaultPasswordStrength(createStrictMock(InternalDataStore))

        def propertyDescriptors = strength.getPropertyDescriptors()

        assertEquals(propertyDescriptors.size(), 8)

        assertTrue(propertyDescriptors.get("minSymbol") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("minDiacritic") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("minUpperCase") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("minLength") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("minLowerCase") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("maxLength") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("minNumeric") instanceof IntegerProperty)
        assertTrue(propertyDescriptors.get("preventReuse") instanceof IntegerProperty)
    }


    @Test
    void testMethods() {

        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e",
                minSymbol: 0,
                minDiacritic: 0,
                minUpperCase: 1,
                minLength: 8,
                minLowerCase: 1,
                maxLength: 100,
                minNumeric: 1,
                preventReuse: 0
        ]

        PasswordStrength strength = new DefaultPasswordStrength(internalDataStore, properties)
        assertEquals(strength.getMinSymbol(), 0)
        assertEquals(strength.getMinDiacritic(), 0)

        strength = strength.setMinSymbol(1)
                .setMinDiacritic(2)
                .setMinUpperCase(3)
                .setMinLength(4)
                .setMinLowerCase(5)
                .setMaxLength(6)
                .setMinNumeric(7)
                .setPreventReuse(8)

        assertEquals(strength.getMinSymbol(), 1)
        assertEquals(strength.getMinDiacritic(), 2)
        assertEquals(strength.getMinUpperCase(), 3)
        assertEquals(strength.getMinLength(), 4)
        assertEquals(strength.getMinLowerCase(), 5)
        assertEquals(strength.getMaxLength(), 6)
        assertEquals(strength.getMinNumeric(), 7)
        assertEquals(strength.getPreventReuse(), 8)
    }


    @Test
    void testLimits() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href: "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e",
                          minSymbol: 0,
                          minDiacritic: 0,
                          minUpperCase: 1,
                          minLength: 8,
                          minLowerCase: 1,
                          maxLength: 100,
                          minNumeric: 1,
                          preventReuse: 0
        ]

        PasswordStrength strength = new DefaultPasswordStrength(internalDataStore, properties)

        try {
            strength.setMinSymbol(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minSymbol cannot be a negative number.")
        }

        try {
            strength.setMinDiacritic(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minDiacritic cannot be a negative number.")
        }

        try {
            strength.setMinUpperCase(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minUpperCase cannot be a negative number.")
        }

        try {
            strength.setMinLength(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minLength cannot be less than 1 or larger than 1024.")
        }

        try {
            strength.setMinLength(0) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minLength cannot be less than 1 or larger than 1024.")
        }

        strength.setMinLength(1024) //OK

        try {
            strength.setMinLength(1025) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minLength cannot be less than 1 or larger than 1024.")
        }

        try {
            strength.setMinLowerCase(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minLowerCase cannot be a negative number.")
        }

        try {
            strength.setMaxLength(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "maxLength cannot be less than 1 or larger than 1024.")
        }

        try {
            strength.setMaxLength(0) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "maxLength cannot be less than 1 or larger than 1024.")
        }

        strength.setMaxLength(1024) //OK

        try {
            strength.setMaxLength(1025) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "maxLength cannot be less than 1 or larger than 1024.")
        }

        try {
            strength.setMinNumeric(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minNumeric cannot be a negative number.")
        }

        try {
            strength.setPreventReuse(-1) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "preventReuse cannot be a negative number.")
        }

        try {
            strength.setPreventReuse(26) //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "preventReuse cannot be larger than 25.")
        }
    }

    @Test
    void testRealisticConditions() {
        def internalDataStore = createStrictMock(InternalDataStore)

        def properties = [href      : "https://api.stormpath.com/v1/passwordPolicies/35YM3OwioW9PVtfLOh6q1e",
                          minSymbol : 0,
                          minDiacritic: 0,
                          minUpperCase: 1,
                          minLength : 8,
                          minLowerCase: 1,
                          maxLength : 100,
                          minNumeric: 1,
                          preventReuse: 5
        ]

        PasswordStrength strength = new DefaultPasswordStrength(internalDataStore, properties)

        try {
            strength.setMinSymbol(101)
            strength.save() //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "maxLength is not large enough to hold all the minimum conditions specified.")
        }

        try {
            strength.setMaxLength(7)
            strength.save() //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minLength cannot be greater than maxLength.")
        }

        try {
            strength.setMinSymbol(1).setMinDiacritic(1).setMinUpperCase(1)
                    .setMinLength(1).setMaxLength(4).setMinNumeric(1)
            strength.save() //must throw
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "maxLength is not large enough to hold all the minimum conditions specified.")
        }
    }


}
