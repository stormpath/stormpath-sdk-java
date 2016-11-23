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

package com.stormpath.sdk.servlet.utils

import com.stormpath.sdk.servlet.filter.oauth.OAuthErrorCode
import com.stormpath.sdk.servlet.filter.oauth.OAuthException
import com.stormpath.sdk.servlet.util.DefaultGrantTypeValidator
import com.stormpath.sdk.servlet.util.GrantTypeValidator
import org.testng.Assert
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

/**
 * @since 1.2.0
 */
class DefaultGrantTypeValidatorTest {

    private DefaultGrantTypeValidator defaultGrantTypeStatusValidator;

    @BeforeTest
    public void init() {
        defaultGrantTypeStatusValidator = new DefaultGrantTypeValidator()
    }

    @Test()
    public void testPasswordGrantTypeEnabledPassesValidation() {
        defaultGrantTypeStatusValidator.setPasswordGrantTypeEnabled(true)
        defaultGrantTypeStatusValidator.validate(GrantTypeValidator.PASSWORD_GRANT_TYPE)
        //does not throw exception
    }

    @Test
    public void testPasswordGrantTypeDisabledThrowsOauthException() {
        try {
            defaultGrantTypeStatusValidator.setPasswordGrantTypeEnabled(false)
            defaultGrantTypeStatusValidator.validate(GrantTypeValidator.PASSWORD_GRANT_TYPE)
            Assert.fail("should have thrown OAuthException")
        }
        catch (Exception ex) {
            Assert.assertTrue(ex instanceof OAuthException)

            OAuthErrorCode errorCode = ((OAuthException) ex).getErrorCode()
            Assert.assertEquals(errorCode, OAuthErrorCode.UNSUPPORTED_GRANT_TYPE)
        }
    }

    @Test
    public void testClientCredentialsGrantTypeEnabledPassesValidation() {
        defaultGrantTypeStatusValidator.setClientCredentialsGrantTypeEnabled(true)
        defaultGrantTypeStatusValidator.validate(GrantTypeValidator.CLIENT_CREDENTIALS_GRANT_TYPE)
        //does not throw exception
    }

    @Test
    public void testClientCredentialsGrantTypeDisabledThrowsOauthException() {
        try {
            defaultGrantTypeStatusValidator.setClientCredentialsGrantTypeEnabled(false)
            defaultGrantTypeStatusValidator.validate(GrantTypeValidator.CLIENT_CREDENTIALS_GRANT_TYPE)
            Assert.fail("should have thrown OAuthException")
        }
        catch (Exception ex) {
            Assert.assertTrue(ex instanceof OAuthException)

            OAuthErrorCode errorCode = ((OAuthException) ex).getErrorCode()
            Assert.assertEquals(errorCode, OAuthErrorCode.UNSUPPORTED_GRANT_TYPE)
        }
    }

    @Test
    public void testOtherGrantTypesPassValidation() {
        defaultGrantTypeStatusValidator.validate("refresh_token")
        //does not throw exception
    }

}
