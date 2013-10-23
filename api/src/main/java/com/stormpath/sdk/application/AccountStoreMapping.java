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
package com.stormpath.sdk.application;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * @since 0.9
 */
public interface AccountStoreMapping extends Resource, Saveable, Deletable {

    Application getApplication();

    void setApplication(Application application);

    AccountStore getAccountStore();

    void setAccountStore(AccountStore accountStore);

    int getListIndex();

    /**
     * Updates the zero-based index for this AccountStoreMapping.
     *
     * WARNING:  If you use this setter then you will invalidate the cache for all AccountStoreMappings in the Application.
     *
     * <h3>Authentication Process and AccountStoreMapping Order</h3>
     * During an authentication attempt, an Application consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code AccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the application will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you control where the new {@code AccountStoreMapping} will reside in the Application's
     * overall list by setting its (zero-based) listIndex property before calling this
     * method.
     * <h4>{@code listIndex} values</h4>
     * <ul>
     * <li>negative: attempting to set a negative {@code listIndex} will cause an Error</li>
     * <li>zero: the account store mapping will be the first item in the list (and therefore consulted first
     * during the authentication process).</li>
     * <li>positive: the account store mapping will be inserted at that index.  Because list indices are zero-based,
     * the account store will be in the list at position {@code listIndex - 1}.</li>
     * </ul>
     * Any {@code listIndex} value equal to or greater than the current list size will automatically append the
     * {@code AccountStoreMapping} at the end of the list.
     * <h4>Example</h4>
     * Setting a new {@code AccountStoreMapping}'s {@code listIndex} to {@code 500} and then adding the mapping to
     * an application with an existing 3-item list will automatically save the {@code AccountStoreMapping} at the end
     * of the list and set its {@code listIndex} value to {@code 3} (items at index 0, 1, 2 were the original items,
     * the new fourth item will be at index 3).
     *
     * @param listIndex the zero based index
     */
    void setListIndex(int listIndex);

    boolean isDefaultAccountStore();

    void setDefaultAccountStore(boolean defaultAccountStore);

    void setDefaultGroupStore(boolean defaultGroupStore);

    boolean isDefaultGroupStore();
}
