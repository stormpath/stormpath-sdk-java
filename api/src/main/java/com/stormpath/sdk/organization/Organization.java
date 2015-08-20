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
package com.stormpath.sdk.organization;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Extendable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.Auditable;

/**
 * An Organization is a top-level container of Directories and Groups. Directories and Groups are guaranteed to
 * be unique within an {@link Organization}, but not across multiple Organizations. A {@code Organization}'s name is guaranteed
 * to be unique across all of a {@link com.stormpath.sdk.tenant.Tenant}'s organizations.
 * <p/>
 * You can think of an Organization as a 'virtual' AccountStore that 'wraps' other AccountStores.  Like other
 * AccountStores, an Organization can be mapped to an Application so that users in the Organization can login to that application.
 *
 * @since 1.0.RC4.6
 */
public interface Organization extends Resource, Saveable, Deletable, AccountStore, Extendable, Auditable {


}
