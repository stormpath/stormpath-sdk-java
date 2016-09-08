package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.AccountLink;
import com.stormpath.sdk.account.AccountLinkList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractCollectionResource;
import com.stormpath.sdk.impl.resource.ArrayProperty;
import com.stormpath.sdk.impl.resource.Property;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultAccountLinkList extends AbstractCollectionResource<AccountLink> implements AccountLinkList {

    private static final ArrayProperty<AccountLink> ITEMS = new ArrayProperty<>("items", AccountLink.class);

    private static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(OFFSET, LIMIT, ITEMS);

    public DefaultAccountLinkList(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountLinkList(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    public DefaultAccountLinkList(InternalDataStore dataStore, Map<String, Object> properties, Map<String,Object> queryParams) {
        super(dataStore, properties, queryParams);
    }
    @Override
    protected Class<AccountLink> getItemType() {
        return AccountLink.class;
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }
}
