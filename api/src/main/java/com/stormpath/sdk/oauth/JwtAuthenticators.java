/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.lang.Classes;

import java.lang.reflect.Constructor;

/**
 * @since 1.0.RC5.1
 */
public class JwtAuthenticators {

    @SuppressWarnings("unchecked")
    private static final Class<JwtAuthenticator> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.oauth.DefaultJwtAuthenticator");

    public static JwtAuthenticator forApplication(Application application, DataStore dataStore) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Application.class, DataStore.class);
        return (JwtAuthenticator) Classes.instantiate(ctor, application, dataStore);
    }
}
