package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.application.okta.AuthNResult;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Objects;

import java.util.Map;

/**
 *
 */
public class DefaultAuthNResult extends AbstractInstanceResource implements AuthNResult {

    private final static StringProperty SESSION_TOKEN = new StringProperty("sessionToken");
    private final static MapProperty EMBEDDED = new MapProperty("_embedded");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(SESSION_TOKEN, EMBEDDED);

    public DefaultAuthNResult(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAuthNResult(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getSessionToken() {
        return getString(SESSION_TOKEN);
    }

    public String getUserId() {
        Map<String, Object> embedded = getMap(EMBEDDED);
        Map<String, Object> user = (Map<String, Object>) embedded.get("user");
        if (!Collections.isEmpty(user)) {
            return (String) user.get("id");
        }
        return null;
    }
}
