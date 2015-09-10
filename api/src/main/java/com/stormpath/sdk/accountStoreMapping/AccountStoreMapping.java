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
package com.stormpath.sdk.accountStoreMapping;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.application.Application;

/**
 * An {@code AccountStoreMapping} represents the assignment of an {@link AccountStore AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or
 * {@link com.stormpath.sdk.directory.Directory Directory}) to an {@link Application Application} or {@link Organization Organization}.
 * <p/>
 * When an {@code AccountStoreMapping} is created, the accounts in the account store are granted access to (become users
 * of) the linked {@code Application} or {@code Organization}.
 *
 * @see com.stormpath.sdk.application.ApplicationAccountStoreMapping
 * @see com.stormpath.sdk.organization.OrganizationAccountStoreMapping
 *
 * @since 0.9
 */
public interface AccountStoreMapping extends Resource, Saveable, Deletable {}
