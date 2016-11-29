package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.BaseOAuthToken;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.oauth.OAuthRevocationRequest;
import com.stormpath.sdk.oauth.OAuthTokenRevocators;
import com.stormpath.sdk.oauth.TokenTypeHint;
import com.stormpath.sdk.tenant.Tenant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolverAdapter;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.0.RC8.3
 */
public abstract class AbstractBaseOAuthToken extends AbstractInstanceResource implements BaseOAuthToken {

    // SIMPLE PROPERTIES
    static final String ACCOUNT_PROP_NAME = "account";
    static final String APPLICATION_PROP_NAME = "application";
    static final String JWT_PROP_NAME = "jwt";
    static final String TENANT_PROP_NAME = "tenant";

    static final StringProperty JWT = new StringProperty(JWT_PROP_NAME);
    static final DateProperty CREATED_AT = new DateProperty("created_at");
    static final MapProperty EXPANDED_JWT = new MapProperty("expandedJwt");

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<Account>(ACCOUNT_PROP_NAME, Account.class);
    static final ResourceReference<Application> APPLICATION = new ResourceReference<Application>(APPLICATION_PROP_NAME, Application.class);
    static final ResourceReference<Tenant> TENANT = new ResourceReference<Tenant>(TENANT_PROP_NAME, Tenant.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(JWT, ACCOUNT, APPLICATION, TENANT, CREATED_AT);

    public AbstractBaseOAuthToken(InternalDataStore dataStore) {
        super(dataStore);
    }

    public AbstractBaseOAuthToken(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }


    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getJwt() {
        return getString(JWT);
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public Map<String, Object> getExpandedJwt() {
        return getMap(EXPANDED_JWT);
    }

    @Override
    public void revoke() {
        OAuthRevocationRequest revocationRequest = OAuthRequests.OAUTH_TOKEN_REVOCATION_REQUEST.builder()
                .setToken(getJwt()).setTokenTypeHint(getTokenTypeHint()).build();

        OAuthTokenRevocators.OAUTH_TOKEN_REVOCATOR.forApplication(getApplication()).revoke(revocationRequest);
    }

    protected static Jws<Claims> parseJws(String token, final InternalDataStore dataStore) {

        final ApiKey clientApiKey = dataStore.getApiKey();

        return Jwts.parser().setSigningKeyResolver(new SigningKeyResolverAdapter() {
            @Override
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                String keyId = header.getKeyId();

                if (Strings.hasText(keyId) && !clientApiKey.getId().equals(keyId)) {
                    String url = dataStore.getBaseUrl() + "/apiKeys/" + keyId;
                    ApiKey apiKey = dataStore.getResource(url, ApiKey.class);
                    return Strings.getBytesUtf8(apiKey.getSecret());
                }
                return Strings.getBytesUtf8(clientApiKey.getSecret());
            }
        }).parseClaimsJws(token);
    }

    protected abstract TokenTypeHint getTokenTypeHint();
}
