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
package com.stormpath.sdk.application;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * An {@code AccountStoreMapping} represents the assignment of an
 * {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or
 * {@link com.stormpath.sdk.directory.Directory Directory}) to an {@link Application}.
 * <p/>
 * When an {@code AccountStoreMapping} is created, the accounts in the account store are granted access to (become users
 * of) the linked {@code Application}.  The {@link #getListIndex() order} in which {@code AccountStore}s are assigned
 * to an application determines <a href="http://docs.stormpath.com/rest/product-guide/#account-store-mappings">how
 * login attempts work in Stormpath</a>.
 * <h2>Default Account Store</h2>
 * Additionally, an {@code AccountStoreMapping} may be designated as the Application's
 * {@link #isDefaultAccountStore() defaultAccountStore}.  This causes any accounts created directly by the application
 * to be dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store accounts
 * directly.
 * <h2>Default Group Store</h2>
 * Similarly, an {@code AccountStoreMapping} may be designated as the Application's
 * {@link #isDefaultGroupStore() defaultGroupStore}.  This causes any groups created directly by the application
 * to be dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store groups
 * directly.
 * <b>Note:</b> A Group cannot store other Groups.  Therefore, the default group store must be a
 * {@code Directory}.
 *
 * @see Application#createAccountStoreMapping(AccountStoreMapping)
 * @since 0.9
 */
public interface AccountStoreMapping extends Resource, Saveable, Deletable {

    /**
     * Returns the Application represented by this {@code AccountStoreMapping} resource.
     *
     * @return the Application represented by this {@code AccountStoreMapping} resource.
     */
    Application getApplication();

    /**
     * Sets the Application represented by this {@code AccountStoreMapping} resource.
     *
     * @param application the Application represented by this {@code AccountStoreMapping} resource.
     * @return this instance for method chaining.
     */
    AccountStoreMapping setApplication(Application application);

    /**
     * Returns this mapping's {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or
     * {@link com.stormpath.sdk.directory.Directory Directory}), to be assigned to the application.
     *
     * @return this mapping's {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or
     *         {@link com.stormpath.sdk.directory.Directory Directory}) assigned to the application.
     */
    AccountStore getAccountStore();

    /**
     * Sets this mapping's {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group} or
     * {@link com.stormpath.sdk.directory.Directory Directory}), to be assigned to the application.
     *
     * @param accountStore the AccountStore to be assigned to the application.
     * @return this instance for method chaining.
     */
    AccountStoreMapping setAccountStore(AccountStore accountStore);

    /**
     * Returns the zero-based order in which the associated {@link #getAccountStore() accountStore} will be consulted
     * by the linked {@link #getApplication()} during an account authentication attempt.
     * <p/>
     * The lower the index, the higher precedence - the earlier it will be accessed - during an authentication attempt.
     * The higher the index, the lower the precedence - the later it will be accessed - during an authentication attempt.
     * <p/>
     * See the {@link #setListIndex(int) setListIndex} JavaDoc for more information.
     *
     * @return the zero-based order in which the associated {@link #getAccountStore() accountStore} will be consulted
     *         by the linked {@link #getApplication()} during an account authentication attempt.
     * @see #setListIndex(int)
     */
    int getListIndex();

    /**
     * Updates the zero-based order in which the associated {@link #getAccountStore() accountStore} will be consulted
     * by the linked {@link #getApplication()} during an account authentication attempt.
     * <p/>
     * <b>USAGE NOTE:  If you use this setter then you will invalidate the cache for all of the associated Application's
     * other AccountStoreMappings.</b>
     * <p/>
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
     * @return this instance for method chaining.
     */
    AccountStoreMapping setListIndex(int listIndex);

    /**
     * Returns {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     * {@link #getApplication() application}'s default account store, {@code false} otherwise.
     * <p/>
     * A {@code true} value indicates that any accounts created directly by the application will be
     * dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store accounts
     * directly.
     *
     * @return {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     *         {@link #getApplication() application}'s default account store, {@code false} otherwise.
     */
    boolean isDefaultAccountStore();

    /**
     * Sets whether or not the associated {@link #getAccountStore() accountStore} is designated as the
     * {@link #getApplication() application}'s default account store.
     * <p/>
     * A {@code true} value indicates that any accounts created directly by the application will be
     * dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store accounts
     * directly.
     * <p/>
     * <b>USAGE NOTE:  If you use this setter then you will invalidate the cache for all of the associated Application's
     * other AccountStoreMappings.</b>
     *
     * @param defaultAccountStore whether or not the associated {@link #getAccountStore() accountStore} is designated
     *                            as the {@link #getApplication() application}'s default account store.
     * @return this instance for method chaining.
     */
    AccountStoreMapping setDefaultAccountStore(boolean defaultAccountStore);

    /**
     * Returns {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     * {@link #getApplication() application}'s default <b>group</b> store, {@code false} otherwise.
     * <p/>
     * A {@code true} value indicates that any groups created directly by the application will be
     * dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store accounts
     * directly.
     *
     * @return {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     *         {@link #getApplication() application}'s default group store, {@code false} otherwise.
     */
    boolean isDefaultGroupStore();

    /**
     * Sets whether or not the associated {@link #getAccountStore() accountStore} is designated as the
     * {@link #getApplication() application}'s default <b>group</b> store.
     * <p/>
     * A {@code true} value indicates that any groups created directly by the application will be
     * dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store accounts
     * directly.
     * <p/>
     * <b>USAGE NOTE:  If you use this setter then you will invalidate the cache for all of the associated Application's
     * other AccountStoreMappings.</b>
     * <h3>Directory Only</h3>
     * Stormpath currently only supports Directories (not Groups) as a group store.  Attempting to set this value to
     * {@code true} if the associated {@link #getAccountStore() accountStore} is a Group and then calling
     * {@link #save() save} will result in a {@link com.stormpath.sdk.resource.ResourceException ResourceException}.
     *
     *
     * @param defaultGroupStore {@code true} if the associated {@link #getAccountStore() accountStore} is designated as
     *                          the {@link #getApplication() application}'s default group store, {@code false} otherwise.
     * @return this instance for method chaining.
     */
    AccountStoreMapping setDefaultGroupStore(boolean defaultGroupStore);
}
