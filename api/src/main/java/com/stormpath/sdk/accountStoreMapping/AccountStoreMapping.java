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
 * An {@code AccountStoreMapping} represents the assignment of an {@link AccountStore AccountStore} (either a {@link com.stormpath.sdk.group.Group Group}, {@link Organization Organization} or
 * {@link com.stormpath.sdk.directory.Directory Directory}) to an {@link Application Application} or an {@link com.stormpath.sdk.organization.Organization}.
 * <p/>
 * When an {@code AccountStoreMapping} is created, the accounts in the account store are granted access to (become users
 * of) the linked {@code Application} or {@code Organization}.
 *
 * @see com.stormpath.sdk.application.ApplicationAccountStoreMapping
 * @see com.stormpath.sdk.organization.OrganizationAccountStoreMapping
 *
 * @since 0.9
 */
public interface AccountStoreMapping<T extends AccountStoreHolder> extends Resource, Saveable, Deletable {

    /**
     * Returns this mapping's {@link com.stormpath.sdk.directory.AccountStore} (either a {@link com.stormpath.sdk.group.Group Group}, {@link Organization Organization} or
     * {@link com.stormpath.sdk.directory.Directory Directory}), to be assigned to the application or organization.
     *
     * @return this mapping's {@link com.stormpath.sdk.directory.AccountStore} (either a {@link com.stormpath.sdk.group.Group Group}, {@link Organization Organization} or
     *         {@link com.stormpath.sdk.directory.Directory Directory}) assigned to the application or organization.
     */
    AccountStore getAccountStore();

    /**
     * Sets this mapping's {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group}, {@link Organization Organization} or
     * {@link com.stormpath.sdk.directory.Directory Directory}), to be assigned to the application or organization.
     *
     * @param accountStore the AccountStore to be assigned to the application or organization.
     * @return this instance for method chaining.
     */
    AccountStoreMapping<T> setAccountStore(AccountStore accountStore);

//    AccountStoreMapping<T> setHolder(T accountStoreHolder);
////
//    T getHolder(T accountStoreHolder);


    /**
     * Returns the zero-based order in which the associated {@link #getAccountStore() accountStore} will be consulted
     * by the linked Application or Organization during an account authentication attempt.
     * <p/>
     * The lower the index, the higher precedence - the earlier it will be accessed - during an authentication attempt.
     * The higher the index, the lower the precedence - the later it will be accessed - during an authentication attempt.
     * <p/>
     * See the {@link #setListIndex(int) setListIndex} JavaDoc for more information.
     *
     * @return the zero-based order in which the associated {@link #getAccountStore() accountStore} will be consulted
     *         by the linked Organization or Application during an account authentication attempt.
     * @see #setListIndex(int)
     */
    int getListIndex();

    /**
     * Updates the zero-based order in which the associated {@link #getAccountStore() accountStore} will be consulted
     * by the linked Organization or Application during an account authentication attempt.
     * <p/>
     * <b>USAGE NOTE:  If you use this setter then you will invalidate the cache for all of the associated Application's or Organization's
     * other AccountStoreMappings.</b>
     * <p/>
     * <h3>Authentication Process and AccountStoreMapping Order</h3>
     * During an authentication attempt, an Application or Organization consults its mapped account stores in <em>iteration order</em>,
     * trying to find the first matching account to use for authentication.  The lower the {@code AccountStoreMapping}
     * index (closer to zero), the earlier that store is consulted during authentication.  If no matching account is
     * found in an account store, the application/organization will move on to the next {@code AccountStore} (next highest index)
     * in the list.  This continues either a matching account is found, or until all account stores are exhausted.
     * When a matching account is found, the process is short-circuited and the discovered account will be used
     * immediately for authentication.
     * <p/>
     * When calling this method, you control where the new {@code AccountStoreMapping} will reside in the Application's or Organization's
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
     * {@code ApplicationAccountStoreMapping} at the end of the list.
     * <h4>Example</h4>
     * Setting a new {@code AccountStoreMapping}'s {@code listIndex} to {@code 500} and then adding the mapping to
     * an application/organization with an existing 3-item list will automatically save the {@code AccountStoreMapping} at the end
     * of the list and set its {@code listIndex} value to {@code 3} (items at index 0, 1, 2 were the original items,
     * the new fourth item will be at index 3).
     *
     * @param listIndex the zero based index
     * @return this instance for method chaining.
     */
    AccountStoreMapping<T> setListIndex(int listIndex);

    /**
     * Returns {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     * Organization's or Application's default account store, {@code false} otherwise.
     * <p/>
     * A {@code true} value indicates that any accounts created directly by the application will be
     * dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store accounts
     * directly.
     *
     * @return {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     *         Organization's or Application's default account store, {@code false} otherwise.
     */
    boolean isDefaultAccountStore();

    /**
     * Sets whether or not the associated {@link #getAccountStore() accountStore} is designated as the
     * Organization's or Application's default account store.
     * <p/>
     * A {@code true} value indicates that any accounts created directly by the application/organization will be
     * dispatched to and saved in the associated {@code AccountStore}, since Applications and Organizations cannot store accounts
     * directly.
     * <p/>
     * <b>USAGE NOTE:  If you use this setter then you will invalidate the cache for all of the associated Application's or Organization's
     * other AccountStoreMappings.</b>
     *
     * @param defaultAccountStore whether or not the associated {@link #getAccountStore() accountStore} is designated
     *                            as the Organization's or Application's default account store.
     * @return this instance for method chaining.
     */
    AccountStoreMapping<T> setDefaultAccountStore(boolean defaultAccountStore);

    /**
     * Returns {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     * Organization's or Application's default <b>group</b> store, {@code false} otherwise.
     * <p/>
     * A {@code true} value indicates that any groups created directly by the application will be
     * dispatched to and saved in the associated {@code AccountStore}, since an Application cannot store accounts
     * directly.
     *
     * @return {@code true} if the associated {@link #getAccountStore() accountStore} is designated as the
     *         Organization's or Application's default group store, {@code false} otherwise.
     */
    boolean isDefaultGroupStore();

    /**
     * Sets whether or not the associated {@link #getAccountStore() accountStore} is designated as the
     * Organization's or Application's default <b>group</b> store.
     * <p/>
     * A {@code true} value indicates that any groups created directly by the application/organization will be
     * dispatched to and saved in the associated {@code AccountStore}, since Applications and Organizations cannot store accounts
     * directly.
     * <p/>
     * <b>USAGE NOTE:  If you use this setter then you will invalidate the cache for all of the associated Application's or Organization's
     * other AccountStoreMappings.</b>
     * <h3>Directory Only</h3>
     * Stormpath currently only supports Directories (not Groups) as a group store.  Attempting to set this value to
     * {@code true} if the associated {@link #getAccountStore() accountStore} is a Group and then calling
     * {@link #save() save} will result in a {@link com.stormpath.sdk.resource.ResourceException ResourceException}.
     *
     *
     * @param defaultGroupStore {@code true} if the associated {@link #getAccountStore() accountStore} is designated as
     *                          the Organization's or Application's default group store, {@code false} otherwise.
     * @return this instance for method chaining.
     */
    AccountStoreMapping<T> setDefaultGroupStore(boolean defaultGroupStore);
}
