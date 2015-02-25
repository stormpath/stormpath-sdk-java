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
 * {@link PasswordStrength} is the resource used to configure the password strength policy. This policy defines requirements
 * like minimum uppercase chars or maximum password length (among others) and will enforced by Stormpath when setting
 * or resetting accounts' passwords.
 *
 * @since 1.0.RC4
 */
public interface PasswordStrength extends Resource, Saveable {

    /**
     * Return the minimum quantity of symbols required by this policy.
     * </p>
     * This is the complete list of symbols supported: space, !, ", #, $, %, &, ', (, ), *, +, comma, -, ., /, :, ;, <, =, >, @,
     * [, \, ], ^, _, {, |, }, ~, ¡, §, ©, «, ¬, ® , ±, µ, ¶, ·, », ½, ¿, ×, ÷.
     *
     * @return the minimum quantity of symbols required by this policy.
     */
    int getMinSymbol();

    /**
     * Specifies the minimum quantity of symbols required by this policy.
     * </p>
     * This is the complete list of symbols supported: space, !, ", #, $, %, &, ', (, ), *, +, comma, -, ., /, :, ;, <, =, >, @,
     * [, \, ], ^, _, {, |, }, ~, ¡, §, ©, «, ¬, ® , ±, µ, ¶, ·, », ½, ¿, ×, ÷.
     *
     * @return this instance for method chaining.
     */
    PasswordStrength setMinSymbol(int minSymbol);

    /**
     * Return the minimum quantity of diacritic characters required by this policy.
     * </p>
     * This is the complete list of diacritic characters supported: À, Á, Â, Ã, Ä, Å, Æ, Ç, È, É, Ê, Ë, Ì, Í, Î, Ï, Ð, Ñ,
     * Ò, Ó, Ô, Õ, Ö, Ø, Ù, Ú, Û, Ü, Ý, Þ, ß, à, á, â, ã, ä, å, æ, ç, è, é, ê, ë, ì, í, î, ï, ð, ñ, ò, ó, ô, õ, ö, ø,
     * ù, ú, û, ü, ý, þ, ÿ.
     *
     * @return the minimum quantity of diacritic characters required by this policy.
     */
    int getMinDiacritic();

    /**
     * Specifies the minimum quantity of diacritic characters required by this policy.
     * </p>
     * This is the complete list of diacritic characters supported: À, Á, Â, Ã, Ä, Å, Æ, Ç, È, É, Ê, Ë, Ì, Í, Î, Ï, Ð, Ñ,
     * Ò, Ó, Ô, Õ, Ö, Ø, Ù, Ú, Û, Ü, Ý, Þ, ß, à, á, â, ã, ä, å, æ, ç, è, é, ê, ë, ì, í, î, ï, ð, ñ, ò, ó, ô, õ, ö, ø,
     * ù, ú, û, ü, ý, þ, ÿ.
     *
     * @return this instance for method chaining.
     */
    PasswordStrength setMinDiacritic(int minDiacritic);

    /**
     * Returns the minimum quantity of uppercase characters (i.e. A, B, C, ... Z) required by this policy.
     *
     * @return the minimum quantity of uppercase characters required by this policy.
     */
    int getMinUpperCase();

    /**
     * Specifies the minimum quantity of uppercase characters (i.e. A, B, C, ... Z) required by this policy.
     *
     * @return this instance for method chaining.
     */
    PasswordStrength setMinUpperCase(int minUpperCase);

    /**
     * Returns the minimum quantity of total characters required in a password by this policy.
     *
     * @return the minimum quantity of total characters required in a password by this policy.
     */
    int getMinLength();

    /**
     * Specifies the minimum quantity of total characters required in a password by this policy.
     *
     * @return this instance for method chaining.
     */
    PasswordStrength setMinLength(int minLength);

    /**
     * Returns the minimum quantity of lowercase characters (i.e. a, b, c, ... z) required by this policy.
     *
     * @return the minimum quantity of lowercase characters required by this policy.
     */
    int getMinLowerCase();

    /**
     * Specifies the minimum quantity of lowercase characters (i.e. a, b, c, ... z) required by this policy.
     *
     * @return this instance for method chaining.
     */
    PasswordStrength setMinLowerCase(int minLowerCase);

    /**
     * Returns the maximum quantity of total characters allowed in a password by this policy.
     *
     * @return the maximum quantity of total characters allowed in a password by this policy.
     */
    int getMaxLength();

    /**
     * Specifies the maximum quantity of total characters allowed in a password by this policy.
     * <p/>
     * The maximum allowed password length is 1024 characters.
     *
     * @return this instance for method chaining.
     */
    PasswordStrength setMaxLength(int maxLength);

    /**
     * Returns the minimum quantity of numeric characters (i.e. 0, 1, 2, 3, 5, 6, 7, 8, 9) required by this policy.
     *
     * @return the minimum quantity of numeric characters required by this policy.
     */
    int getMinNumeric();

    /**
     * Specifies the minimum quantity of numeric characters (i.e. 0, 1, 2, 3, 5, 6, 7, 8, 9) required by this policy.
     *
     * @return this instance for method chaining.
     */
    PasswordStrength setMinNumeric(int minNumeric);

}
