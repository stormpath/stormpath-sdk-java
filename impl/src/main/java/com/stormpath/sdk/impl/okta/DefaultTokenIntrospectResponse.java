package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.okta.TokenIntrospectResponse;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.TokenResponse;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class DefaultTokenIntrospectResponse extends AbstractInstanceResource implements TokenIntrospectResponse {

    private static final BooleanProperty ACTIVE = new BooleanProperty("active");
    private static final StringProperty SCOPE = new StringProperty("scope");
    private static final StringProperty USERNAME = new StringProperty("username");
    private static final StringProperty SUBJECT = new StringProperty("sub");
    private static final StringProperty AUDIENCE = new StringProperty("aud");
    private static final StringProperty ISSUER = new StringProperty("iss");
    private static final StringProperty JWT_ID = new StringProperty("jti");
    private static final StringProperty CLIENT_ID = new StringProperty("client_id");
    private static final StringProperty TOKEN_TYPE = new StringProperty("token_type");
    private static final StringProperty UID = new StringProperty("uid");

    private static final DateProperty EXPIRES_AT = new DateProperty("exp");
    private static final DateProperty ISSUED_AT = new DateProperty("iat");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(SCOPE, USERNAME, SUBJECT, AUDIENCE, ISSUER, JWT_ID, CLIENT_ID, TOKEN_TYPE, UID, EXPIRES_AT, ISSUED_AT);

    public DefaultTokenIntrospectResponse(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public boolean isActive() {
        return getBoolean(ACTIVE);
    }

    @Override
    public String getUsername() {
        return getString(USERNAME);
    }

    @Override
    public Date getExpiresAt() {
        return getDateProperty(EXPIRES_AT);
    }

    @Override
    public Date getIssuedAt() {
        return getDateProperty(ISSUED_AT);
    }

    @Override
    public String getSubject() {
        return getString(SUBJECT);
    }

    @Override
    public String getAudience() {
        return getString(AUDIENCE);
    }

    @Override
    public String getIssuer() {
        return getString(ISSUER);
    }

    @Override
    public String getJwtId() {
        return getString(JWT_ID);
    }

    @Override
    public String getTokenType() {
        return getString(TOKEN_TYPE);
    }

    @Override
    public String getClientId() {
        return getString(CLIENT_ID);
    }

    @Override
    public String getUid() {
        return getString(UID);
    }

    @Override
    public TokenResponse getTokenResponse() {
        return null;
    }

    @Override
    public Set<String> getScope() {
        return Strings.delimitedListToSet(getString(SCOPE), " ");
    }

    @Override
    public ApiKey getApiKey() {
        return null;
    }

    @Override
    public Account getAccount() {
        return getDataStore().getResource(OktaApiPaths.apiPath("users", getUid()), Account.class);
    }

    @Override
    public void accept(AuthenticationResultVisitor visitor) {
        visitor.visit(this);
    }
}
