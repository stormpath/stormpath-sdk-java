package com.stormpath.sdk.impl.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountLink;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultAccountLink extends AbstractInstanceResource implements AccountLink {

    private static final String ENTITY_HREF = "/accountLinks";

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Account> LEFT_ACCOUNT = new ResourceReference<>("leftAccount", Account.class);
    static final ResourceReference<Account> RIGHT_ACCOUNT = new ResourceReference<>("rightAccount", Account.class);
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(LEFT_ACCOUNT, RIGHT_ACCOUNT,CREATED_AT);

    public DefaultAccountLink(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountLink(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Account getLeftAccount() {
        return getResourceProperty(LEFT_ACCOUNT);
    }

    @Override
    public Account getRightAccount() {
        return getResourceProperty(RIGHT_ACCOUNT);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    /**
     * THIS IS NOT PART OF THE STORMPATH PUBLIC API.  SDK end-users should not call it - it could be removed or
     * changed at any time.  It has the public modifier only as an implementation technique to be accessible to other
     * {@code Default*} implementations.
     *
     * @param leftAccount   the leftAccount of the AccountLink
     * @param rightAccount     the rightAccount of the AccountLink
     * @param dataStore the dataStore used to create the accountLink
     * @return the created AccountLink instance.
     */
    public static AccountLink create(Account leftAccount, Account rightAccount, InternalDataStore dataStore) {

        Assert.hasText(leftAccount.getHref(), "leftAccount does not yet have an 'href'.  You must first persist the account " +
                "before creating an AccountLink.");
        Assert.hasText(rightAccount.getHref(), "rightAccount does not yet have an 'href'.  You must first persist the account " +
                "before creating an AccountLink.");

        AccountLink accountLink = dataStore.instantiate(AccountLink.class);

        Assert.isInstanceOf(DefaultAccountLink.class, accountLink, "AccountLink instance is not an expected " +
                DefaultAccountLink.class.getName() + " instance.");

        DefaultAccountLink dal = (DefaultAccountLink)accountLink;

        dal.setResourceProperty(LEFT_ACCOUNT, leftAccount);
        dal.setResourceProperty(RIGHT_ACCOUNT, rightAccount);

        return dataStore.create(ENTITY_HREF, dal);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }
}

