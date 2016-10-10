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
 * Schema represent a {@link Directory} account field configuration, where users can mark {@link Account} fields as required or not.
 *
 * @since 1.2.0
 */
public interface Schema extends Resource, Saveable {

    /**
     * Returns the {@link com.stormpath.sdk.resource.CollectionResource} of fields for the schema
     *
     * @return The list of fields
     */
    FieldList getFields();

}
