/*
 * Copyright 2013 Stormpath, Inc.
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

import com.stormpath.sdk.application.Application;

/**
 * <h2>Deprecated</h2>
 * This class has been deprecated as of 0.8 and it will be removed before 1.0 final.  Instead of using this class,
 * use the {@link Clients#builder()} method and after built, call
 * <pre>
 *     client.getResource(appUrl, Application.class);
 * </pre>
 * to acquire an application instance resource.
 * <p/>
 * <p/>
 * A ClientApplication is a simple wrapper around a {@link Client} and {@link Application} instance, returned from
 * the {@code ClientApplicationBuilder}.{@link com.stormpath.sdk.client.ClientApplicationBuilder#build()}
 * method.
 *
 * @since 0.5
 * @deprecated in 0.8 and will be removed before 1.0 final.  Use the Client.Builder and then call <code>client.getResource(appUrl, Application.class);</code>
 */
@Deprecated
public class ClientApplication {

    private final Client client;
    private final Application application;

    public ClientApplication(Client client, Application application) {
        this.client = client;
        this.application = application;
    }

    public Client getClient() {
        return client;
    }

    public Application getApplication() {
        return application;
    }
}
