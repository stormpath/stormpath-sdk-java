package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.OktaProvider;
import com.stormpath.sdk.provider.social.UserInfoMappingRules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 */
public class DefaultOktaProvider extends AbstractOAuthProvider<OktaProvider> implements OktaProvider {

    private static final StringProperty ID = new StringProperty("id");
    private static final StringProperty TYPE = new StringProperty("type");
    private static final StringProperty NAME = new StringProperty("name");
    private static final StringProperty SCOPE_OKTA = new StringProperty("scope");
    private static final DateProperty CREATED_AT_OKTA = new DateProperty("created");
    private static final DateProperty LAST_UPDATED_AT_OKTA  = new DateProperty("lastUpdated");

    private static final MapProperty LINKS = new MapProperty("_links");
    private static final StringProperty AUTHORIZE_URI = new StringProperty("authorizeUri");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ID);

    public DefaultOktaProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultOktaProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getAuthorizeBaseUri() {
        return getString(AUTHORIZE_URI);
    }

    @Override
    public String getClientSecret() {
        return null;
    }

    @Override
    public UserInfoMappingRules getUserInfoMappingRules() {
        return null;
    }

    @Override
    public String getProviderType() {
        return getString(TYPE);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT_OKTA);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(LAST_UPDATED_AT_OKTA);
    }

    @Override
    protected String getConcreteProviderId() {
        return getProviderType();
    }

    @Override
    public String getHref() {
        return "n/a";
    }

    @Override
    public String getProviderId() {
        if (Strings.hasLength(getProviderType())) {
            return getProviderType().toLowerCase();
        }
        return "default";
    }

    @Override
    public List<String> getScope() {
        return new ArrayList<>(Strings.delimitedListToSet(getString(SCOPE_OKTA), " "));
    }

    @Override
    public String getIdp() {
        return getString(ID);
    }

    @Override
    public String getClientId() {
        return "n/a";
    }
}
