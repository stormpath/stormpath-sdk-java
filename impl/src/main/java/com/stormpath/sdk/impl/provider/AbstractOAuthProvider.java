package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.ListProperty;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.provider.FacebookProvider;
import com.stormpath.sdk.provider.OAuthProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.social.UserInfoMappingRules;

import java.util.List;
import java.util.Map;

/**
 * AbstractOAuthProvider is an abstract representation for OAuth Provider-specific resources like {@link DefaultGoogleProvider} or
 * {@link DefaultFacebookProvider}.
 *
 * @param <T> the specific interface (such as {@link FacebookProvider}) that the concrete implementation of this class implements
 * @since 1.2
 */
public abstract class AbstractOAuthProvider<T extends OAuthProvider> extends AbstractProvider implements OAuthProvider {
    // SIMPLE PROPERTIES
    static final StringProperty CLIENT_ID = new StringProperty("clientId");
    static final StringProperty CLIENT_SECRET = new StringProperty("clientSecret");
    static final ListProperty SCOPE = new ListProperty("scope");

    static final ResourceReference<UserInfoMappingRules> USER_INFO_MAPPING_RULES =
            new ResourceReference<>("userInfoMappingRules", UserInfoMappingRules.class);


    public AbstractOAuthProvider(InternalDataStore dataStore) {
        super(dataStore);
    }

    public AbstractOAuthProvider(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getClientId() {
        return getString(CLIENT_ID);
    }

    public T setClientId(String clientId) {
        setProperty(CLIENT_ID, clientId);
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public String getClientSecret() {
        return getString(CLIENT_SECRET);
    }

    public T setClientSecret(String clientSecret) {
        setProperty(CLIENT_SECRET, clientSecret);
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public List<String> getScope() {
        //noinspection unchecked
        return getListProperty("scope");
    }

    @Override
    public UserInfoMappingRules getUserInfoMappingRules() {
        Object value = getProperty(USER_INFO_MAPPING_RULES.getName());

        if (UserInfoMappingRules.class.isInstance(value) || value == null) {
            return (UserInfoMappingRules) value;
        }
        if (value instanceof Map && !((Map) value).isEmpty()) {
            String href = (String) ((Map) value).get(HREF_PROP_NAME);

            if (href == null) {
                throw new IllegalStateException("userInfoMappingRules resource does not contain its required href property.");
            }

            UserInfoMappingRules rules = getDataStore().getResource(href, UserInfoMappingRules.class);

            return rules;
        }

        String msg = "'" + USER_INFO_MAPPING_RULES.getName() + "' property value type does not match the specified type. Specified type: " +
                USER_INFO_MAPPING_RULES.getType() + ".  Existing type: " + value.getClass().getName() + ".  Value: " + value;
        throw new IllegalStateException(msg);
    }

    protected Provider setUserInfoMappingRules(UserInfoMappingRules userInfoMappingRules) {
        setProperty(USER_INFO_MAPPING_RULES, userInfoMappingRules);
        return this;
    }

}
