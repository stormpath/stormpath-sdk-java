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
package com.stormpath.sdk.directory;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @since 0.2
 */
public interface Directory extends Resource, Saveable {

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Status getStatus();

    void setStatus(Status status);

    /**
     * Creates a new account instance in the directory using the Directory's default registration workflow setting.
     * Whether a registration workflow is triggered or not for the account is based on the Directory's default setting.
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getAccounts() account collection}.
     *
     * @param account the account instance to create in the directory.
     * @see #createAccount(com.stormpath.sdk.account.Account, boolean)
     */
    void createAccount(Account account);

    /**
     * Creates a new account instance in the directory with an explicit registration workflow directive.
     * <p/>
     * If {@code registrationWorkflowEnabled} is {@code true}, the account registration workflow will be triggered
     * no matter what the Directory configuration is.
     * <p/>
     * If {@code registrationWorkflowEnabled} is {@code false}, the account registration workflow will <b>NOT</b>
     * be triggered, no matter what the Directory configuration is.
     * </p>
     * If you want to ensure the registration workflow behavior matches the Directory default, call the
     * {@link #createAccount(com.stormpath.sdk.account.Account)} method instead.
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getAccounts() account collection} using the specified {@code registrationWorkflowEnabled} argument.
     *
     * @param account                     account the account instance to create in the directory.
     * @param registrationWorkflowEnabled whether or not the account registration workflow will be triggered, no matter
     *                                    what the Directory configuration is.
     */
    void createAccount(Account account, boolean registrationWorkflowEnabled);

    AccountList getAccounts();

    GroupList getGroups();

    Tenant getTenant();

    /**
     * Creates a new group instance in the directory.
     * <p/>
     * <b>Note:</b> In the Stormpath REST API, new resources are created by interacting with a collection resource.
     * Therefore, this method is a convenience: it automatically issues a create with the directory's
     * {@link #getGroups() group collection}.
     *
     * @param group the group instance to create in the directory.
     * @since 0.6
     */
    void createGroup(Group group);
}
