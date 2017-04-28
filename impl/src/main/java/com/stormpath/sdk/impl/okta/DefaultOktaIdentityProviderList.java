package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.okta.OktaIdentityProviderList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.SetProperty;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.provider.OktaProvider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class DefaultOktaIdentityProviderList extends AbstractInstanceResource implements OktaIdentityProviderList {

    private static final SetProperty<OktaProvider> ITEMS = new SetProperty<>("items", OktaProvider.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ITEMS);

    public DefaultOktaIdentityProviderList(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaIdentityProviderList(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public Set<OktaProvider> getIdentityProviders() {
        Set<OktaProvider> providers = new HashSet<>();
        for (Object item : getSetProperty(ITEMS.getName())) {
            Map<String, Object> itemMap =  castToMap(item);
            String protocol = getProtocol(itemMap);
            String status = (String) itemMap.get("status");
            if ("ACTIVE".equals(status) && ("OAUTH2".equals(protocol) || "OIDC".equals(protocol))) {
                itemMap.put("href", "n/a");
                itemMap.put("scope", "profile email openid");
                itemMap.put("authorizeUri", getTemplateString(getSubMap(itemMap, "_links")));

                OktaProvider provider = getDataStore().instantiate(OktaProvider.class, itemMap);
                providers.add(provider);
            }
        }

        return providers;
    }

    private String getProtocol(Map<String, Object> item) {
        String type = null;
        Map<String, Object> protocol = getSubMap(item, "protocol");
        if (!Collections.isEmpty(protocol)) {
            type = (String) protocol.get("type");
        }
        return type;
    }

    private String getTemplateString(Object rawMap) {
        Map<String, Object> authorizeLink = getSubMap(castToMap(rawMap), "authorize");
        return (String) authorizeLink.get("href");
    }


    private Map<String, Object> getSubMap(Map<String, Object> map, String key) {
        return castToMap(map.get(key));
    }

    private Map<String, Object> castToMap(Object raw) {
        return (Map<String, Object>) raw;
    }

}
