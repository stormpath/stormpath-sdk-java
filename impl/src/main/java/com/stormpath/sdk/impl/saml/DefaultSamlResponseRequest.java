package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider;
import com.stormpath.sdk.saml.SamlResponseRequest;

import java.util.Map;

public class DefaultSamlResponseRequest extends AbstractResource implements SamlResponseRequest {
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<>("account", Account.class);
    static final ResourceReference<RegisteredSamlServiceProvider> SERVICE_PROVIDER = new ResourceReference<>("serviceProvider", RegisteredSamlServiceProvider.class);
    static final StringProperty REQUEST_ID = new StringProperty("requestId");

    static final Map<String, Property> PROPERTY_DESCRIPTORS;

    static {
        PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(new Property[] { ACCOUNT, SERVICE_PROVIDER, REQUEST_ID });
    }

    public DefaultSamlResponseRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSamlResponseRequest(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public SamlResponseRequest setAccount(Account account) {
        setResourceProperty(ACCOUNT, account);
        return this;
    }

    @Override
    public RegisteredSamlServiceProvider getServiceProvider() {
        return getResourceProperty(SERVICE_PROVIDER);
    }

    @Override
    public SamlResponseRequest setServiceProvider(RegisteredSamlServiceProvider serviceProvider) {
        setResourceProperty(SERVICE_PROVIDER, serviceProvider);
        return this;
    }

    @Override
    public String getRequestId() {
        return getString(REQUEST_ID);
    }

    @Override
    public SamlResponseRequest setRequestId(String requestId) {
        setProperty(REQUEST_ID, requestId);
        return this;
    }

}
