package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider;
import com.stormpath.sdk.saml.CreateSamlResponseRequest;

import java.util.Date;
import java.util.Map;

public class DefaultCreateSamlResponseRequest extends AbstractResource implements CreateSamlResponseRequest {
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<>("account", Account.class);
    static final ResourceReference<RegisteredSamlServiceProvider> SERVICE_PROVIDER = new ResourceReference<>("serviceProvider", RegisteredSamlServiceProvider.class);
    static final StringProperty REQUEST_ID = new StringProperty("requestId");
    static final DateProperty AUTHN_ISSUE_INSTANT = new DateProperty("authnIssueInstant");

    static final Map<String, Property> PROPERTY_DESCRIPTORS;

    static {
        PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(new Property[] { ACCOUNT, SERVICE_PROVIDER, REQUEST_ID, AUTHN_ISSUE_INSTANT });
    }

    public DefaultCreateSamlResponseRequest(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultCreateSamlResponseRequest(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public CreateSamlResponseRequest setAccount(Account account) {
        setResourceProperty(ACCOUNT, account);
        return this;
    }

    @Override
    public RegisteredSamlServiceProvider getServiceProvider() {
        return getResourceProperty(SERVICE_PROVIDER);
    }

    @Override
    public CreateSamlResponseRequest setServiceProvider(RegisteredSamlServiceProvider serviceProvider) {
        setResourceProperty(SERVICE_PROVIDER, serviceProvider);
        return this;
    }

    @Override
    public String getRequestId() {
        return getString(REQUEST_ID);
    }

    @Override
    public CreateSamlResponseRequest setRequestId(String requestId) {
        setProperty(REQUEST_ID, requestId);
        return this;
    }

    @Override
    public Date getAuthnIssueInstant() {
        return getDateProperty(AUTHN_ISSUE_INSTANT);
    }

    @Override
    public CreateSamlResponseRequest setAuthnIssueInstant(Date authnIssueInstant) {
        setProperty(AUTHN_ISSUE_INSTANT, authnIssueInstant);
        return this;
    }
}
