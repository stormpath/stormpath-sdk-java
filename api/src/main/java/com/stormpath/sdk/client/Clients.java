/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.client;

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper class for working with {@link Client} resources. For example:
 * <pre>
 * <b>Clients.builder()</b>
 *     .setApiKeyFileLocation(path)
 *     .setProxy(new Proxy("192.168.2.120", 9001))
 *     .build();
 * </pre>
 *
 * @since 0.9.4
 */
public final class Clients {

    /**
     * Returns a new {@link ClientBuilder} instance, used to construct {@link Client} instances.
     *
     * @return a a new {@link ClientBuilder} instance, used to construct {@link Client} instances.
     */
    public static ClientBuilder builder() {
        return (ClientBuilder) Classes.newInstance("com.stormpath.sdk.impl.client.DefaultClientBuilder");
    }

}
