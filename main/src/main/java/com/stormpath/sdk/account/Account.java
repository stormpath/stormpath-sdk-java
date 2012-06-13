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
package com.stormpath.sdk.account;

import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.InstanceResource;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @since 0.1
 */
public interface Account extends Resource {

    String getUsername();

    void setUsername(String username);

    String getEmail();

    void setEmail(String email);

    void setPassword(String password);

    String getGivenName();

    void setGivenName(String givenName);

    String getMiddleName();

    void setMiddleName(String middleName);

    String getSurname();

    void setSurname(String surname);

    Status getStatus();

    void setStatus(Status status);

    GroupList getGroups();
    Directory getDirectory();
    Tenant getTenant();
}
