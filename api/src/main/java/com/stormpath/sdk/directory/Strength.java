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
package com.stormpath.sdk.directory;

import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * {@link Strength} is the resource used to configure the password strength policy. This policy defines requirements
 * like minimum uppercase chars or maximum password length (among others) and will enforced by Stormpath when setting
 * or resetting accounts' passwords.
 *
 * @since 1.0.0
 */
public interface Strength extends Resource, Saveable {

    /**
     * Return the minimum quantity of symbols (e.g. %, !, @) required by this policy.
     *
     * @return the minimum quantity of symbols (e.g. %, !, @) required by this policy.
     */
    int getMinSymbol();

    /**
     * Specifies the minimum quantity of symbols (e.g. %, !, @) required by this policy.
     *
     * @return this instance for method chaining.
     */
    Strength setMinSymbol(int minSymbol);

    /**
     * Return the minimum quantity of diacritic characters (e.g. ', `, ’) required by this policy.
     *
     * @return the minimum quantity of diacritic characters (e.g. ', `, ’) required by this policy.
     */
    int getMinDiacritic();

    /**
     * Specifies the minimum quantity of diacritic characters (e.g. ', `, ’) required by this policy.
     *
     * @return this instance for method chaining.
     */
    Strength setMinDiacritic(int minDiacritic);

    /**
     * Return the minimum quantity of uppercase characters (e.g. A, B, C) required by this policy.
     *
     * @return the minimum quantity of uppercase characters (e.g. A, B, C) required by this policy.
     */
    int getMinUpperCase();

    /**
     * Specifies the minimum quantity of uppercase characters (e.g. A, B, C) required by this policy.
     *
     * @return this instance for method chaining.
     */
    Strength setMinUpperCase(int minUpperCase);

    /**
     * Return the minimum quantity of total characters required in a password by this policy.
     *
     * @return the minimum quantity of total characters required in a password by this policy.
     */
    int getMinLength();

    /**
     * Specifies the minimum quantity of total characters required in a password by this policy.
     *
     * @return this instance for method chaining.
     */
    Strength setMinLength(int minLength);

    /**
     * Return the minimum quantity of lowercase characters (e.g. a, b, c) required by this policy.
     *
     * @return the minimum quantity of lowercase characters (e.g. a, b, c) required by this policy.
     */
    int getMinLowerCase();

    /**
     * Specifies the minimum quantity of lowercase characters (e.g. a, b, c) required by this policy.
     *
     * @return this instance for method chaining.
     */
    Strength setMinLowerCase(int minLowerCase);

    /**
     * Return the maximum quantity of total characters allowed in a password by this policy.
     *
     * @return the maximum quantity of total characters allowed in a password by this policy.
     */
    int getMaxLength();

    /**
     * Specifies the maximum quantity of total characters allowed in a password by this policy.
     *
     * @return this instance for method chaining.
     */
    Strength setMaxLength(int maxLength);

    /**
     * Return the minimum quantity of numeric characters (e.g. 1, 2, 3) required by this policy.
     *
     * @return the minimum quantity of numeric characters (e.g. 1, 2, 3) required by this policy.
     */
    int getMinNumeric();

    /**
     * Specifies the minimum quantity of numeric characters (e.g. 1, 2, 3) required by this policy.
     *
     * @return this instance for method chaining.
     */
    Strength setMinNumeric(int minNumeric);

}
