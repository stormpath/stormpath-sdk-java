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
package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupMembership;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.4
 */
public class DefaultGroupMembership extends AbstractInstanceResource implements GroupMembership {

    private final static String ACCOUNT = "account";
    private final static String GROUP = "group";


    public DefaultGroupMembership(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGroupMembership(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT, Account.class);
    }

    @Override
    public Group getGroup() {
        return getResourceProperty(GROUP, Group.class);
    }

    public static GroupMembership create(Account account, Group group, InternalDataStore dataStore) {

        //TODO: enable auto discovery
        String href = "/groupMemberships";

        Map<String, Object> props = new LinkedHashMap<String, Object>(2);

        Map<String, String> accountProps = new LinkedHashMap<String, String>(1);
        accountProps.put(HREF_PROP_NAME, account.getHref());

        Map<String, String> groupProps = new LinkedHashMap<String, String>(1);
        groupProps.put(HREF_PROP_NAME, group.getHref());

        props.put(ACCOUNT, accountProps);
        props.put(GROUP, groupProps);

        GroupMembership groupMembership = dataStore.instantiate(GroupMembership.class, props);

        return dataStore.create(href, groupMembership);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }
}
