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
package com.stormpath.sdk.schema;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * Schema represent the {@link Account} schema configuration used to determine which fields in the account resource are required.
 * <p>
 * The account schema belongs to a {@link Directory} and thus it applies to all the accounts on it.
 *
 * @since 1.2.0
 */
public interface Schema extends Resource, Saveable {

    /**
     * Returns a paginated list of all the {@link Field fields} defined by this schema.
     *
     * @return The list of fields
     * @since 1.2.0
     */
    FieldList getFields();

}
