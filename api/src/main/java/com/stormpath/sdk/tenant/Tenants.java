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
package com.stormpath.sdk.tenant;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.resource.ReferenceProperty;
import com.stormpath.sdk.resource.StringProperty;

/**
 * @since 0.8
 */
public final class Tenants {

    public static final StringProperty NAME = new StringProperty("name", true);
    public static final StringProperty KEY = new StringProperty("key", true);
    public static final ReferenceProperty<Application> APPLICATIONS = new ReferenceProperty<Application>("applications", Application.class, true, true);
    public static final ReferenceProperty<Directory> DIRECTORIES = new ReferenceProperty<Directory>("directories", Directory.class, true, true);

    //prevent instantiation
    private Tenants() {
    }

    public static TenantOptions options() {
        return (TenantOptions) Classes.newInstance("com.stormpath.sdk.impl.tenant.DefaultTenantOptions");
    }
}
