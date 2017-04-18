package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.okta.OktaApplicationConfigResource;

import java.util.Arrays;
import java.util.Map;

/**
 */
public class DefaultOktaApplicationConfigResource extends AbstractInstanceResource implements OktaApplicationConfigResource {

    private static final MapProperty SETTINGS = new MapProperty("settings");
    private static final MapProperty NOTIFICATIONS = new MapProperty("notifications");
    private static final MapProperty VPN = new MapProperty("vpn");
    private static final StringProperty MESSAGE = new StringProperty("message");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(SETTINGS);

    public DefaultOktaApplicationConfigResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaApplicationConfigResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAuthorizationServerId() {

        // This is a bit of a hack, but for the migration we are _reusing_ 'settings.notifications.vpn.message'
        // to link the authorization server.

        // if anything in the path is null, just return null
        // not the most elegant way of doing this, but it should be pretty easy to debug if
        // there are changes in the future.
        Map<String, Object> settings = getMap(SETTINGS);
        Map<String, Object> notifications = nullSafeGetMap(settings, NOTIFICATIONS);
        Map<String, Object> vpn = nullSafeGetMap(notifications, VPN);
        return nullSafeGetString(vpn, MESSAGE.getName());
    }

    private String nullSafeGetString(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }
        return (String) map.get(key);
    }

    private Map<String, Object> nullSafeGetMap(Map<String, Object> map, MapProperty key) {

        if (map == null) {
            return null;
        }

        return (Map<String, Object>) map.get(key.getName());
    }



}
