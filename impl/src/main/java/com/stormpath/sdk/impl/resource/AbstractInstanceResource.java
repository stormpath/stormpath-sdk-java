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
package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.impl.ds.*;
import com.stormpath.sdk.resource.*;

import java.util.*;

/**
 * @since 0.2
 */
public abstract class AbstractInstanceResource extends AbstractResource implements Saveable {

    protected AbstractInstanceResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected AbstractInstanceResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public void save() {
        getDataStore().save(this);
    }

    /**
     * Returns {@code true} if the specified data map represents a materialized instance resource data set, {@code
     * false} otherwise.
     *
     * @param props the data properties to test
     * @return {@code true} if the specified data map represents a materialized instance resource data set, {@code
     * false} otherwise.
     * @since 1.0.RC4.3
     */
    public static boolean isInstanceResource(Map<String, ?> props) {
        return isMaterialized(props) && !props.containsKey(AbstractCollectionResource.ITEMS_PROPERTY_NAME); //collections have 'items'
    }

}
