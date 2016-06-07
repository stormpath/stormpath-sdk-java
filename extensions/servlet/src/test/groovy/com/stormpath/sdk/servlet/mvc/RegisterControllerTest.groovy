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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.Assert;
import org.testng.annotations.Test
import static org.easymock.EasyMock.createStrictMock;

/**
 * @since 1.0.0
 */
public class RegisterControllerTest {

    @Test
    void testAccountProperties() {
        //We need to be sure that every non-standard account property is properly considered to be custom data
        //Since there is now way to identify them automatically they have been hardcoded in RegisterController#ACCOUNT_PROPERTIES
        //This test checks that this list is accurate.
        //If the simple properties ever change in the account and this test fails then be sure to update RegisterController#ACCOUNT_PROPERTIES
        //in order to allow this test to pass.

        final List<String> NON_SIMPLE_PROPERTIES = Collections.unmodifiableList(Arrays.asList("fullName", "status", "customData", "emailVerificationToken", "directory", "tenant", "providerData", "groups", "groupMemberships", "apiKeys", "applications", "accessTokens", "refreshTokens"));

        def defaultAccount = new DefaultAccount(createStrictMock(InternalDataStore));
        def actualSimpleProperties = defaultAccount.PROPERTY_DESCRIPTORS
        for (String property : NON_SIMPLE_PROPERTIES) {
            actualSimpleProperties.remove(property)
        }

        actualSimpleProperties = actualSimpleProperties.keySet().asList()

        Assert.assertTrue(actualSimpleProperties.containsAll(RegisterController.ACCOUNT_PROPERTIES) && RegisterController.ACCOUNT_PROPERTIES.containsAll(actualSimpleProperties))
    }
}
