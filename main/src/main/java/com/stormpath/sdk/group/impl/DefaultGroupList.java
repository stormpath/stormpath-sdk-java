package com.stormpath.sdk.group.impl;

import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.resource.impl.AbstractCollectionResource;

import java.util.Map;

/**
 * @author Jeff Wysong
 *         Date: 6/13/12
 *         Time: 11:39 AM
 */
public class DefaultGroupList extends AbstractCollectionResource<Group> implements GroupList {
    public DefaultGroupList(DataStore dataStore) {
        super(dataStore);
    }

    public DefaultGroupList(DataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    protected Class<Group> getItemType() {
        return Group.class;
    }
}
