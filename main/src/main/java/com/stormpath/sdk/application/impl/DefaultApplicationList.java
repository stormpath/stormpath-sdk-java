/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.application.impl;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.resource.impl.AbstractCollectionResource;

import java.util.Map;

/**
 * @since 0.2
 */
public class DefaultApplicationList extends AbstractCollectionResource<Application> implements ApplicationList {

    public DefaultApplicationList(DataStore dataStore) {
        super(dataStore);
    }

    public DefaultApplicationList(DataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    protected Class<Application> getItemType() {
        return Application.class;
    }
}
