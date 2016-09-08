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
package com.stormpath.sdk.impl.schema

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.accountStoreMapping.AccountStoreMapping
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupMembership
import com.stormpath.sdk.group.Groups
import com.stormpath.sdk.impl.api.ApiKeyParameter
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.impl.security.ApiKeySecretEncryptionService
import com.stormpath.sdk.schema.Field
import com.stormpath.sdk.schema.FieldList
import org.testng.annotations.Test

import java.text.DateFormat

import static com.stormpath.sdk.api.ApiKeys.criteria
import static com.stormpath.sdk.api.ApiKeys.options
import static org.testng.Assert.*

/**
 * @since 1.0.4
 */
class AccountSchemaIT extends ClientIT {

    def encryptionServiceBuilder = new ApiKeySecretEncryptionService.Builder()

    @Test
    void test() {

        def app = createTempApp()

        //create a user group:
        def dir = client.instantiate(Directory)
        dir.name = uniquify('JSDK.AccountSchemaIT.test')
        dir = client.createDirectory(dir)
        deleteOnTeardown(dir)
        AccountStoreMapping accountStoreMapping = app.addAccountStore(dir)
        accountStoreMapping.setDefaultAccountStore(true)

        def accountSchema = dir.getAccountSchema()
        FieldList fields = accountSchema.getFields()
        for (Field field : fields) {
            System.out.println(field);
        }

    }



}
