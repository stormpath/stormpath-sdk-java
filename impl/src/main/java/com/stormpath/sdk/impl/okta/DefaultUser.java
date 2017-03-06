package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.ParentAwareObjectProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.okta.Link;
import com.stormpath.sdk.okta.Profile;
import com.stormpath.sdk.okta.User;
import com.stormpath.sdk.okta.UserStatus;

import java.util.Date;
import java.util.Map;

/**
 * Default implementation of {@link com.stormpath.sdk.okta.User}.
 */
public class DefaultUser extends AbstractInstanceResource implements User  {

    // SIMPLE PROPERTIES
    private static final StringProperty ID = new StringProperty("id");
    private static final EnumProperty<UserStatus> STATUS = new EnumProperty<>("status", UserStatus.class);
    private static final DateProperty STATUS_CHANGED = new DateProperty("statusChanged");
    private static final DateProperty CREATED = new DateProperty("created");
    private static final DateProperty LAST_LOGIN = new DateProperty("lastLogin");
    private static final DateProperty LAST_UPDATED = new DateProperty("lastUpdated");
    private static final DateProperty PASSWORD_CHANGED = new DateProperty("passwordChanged");

    private static final ParentAwareObjectProperty<DefaultProfile, AbstractPropertyRetriever> PROFILE = new ParentAwareObjectProperty<>("profile", DefaultProfile.class, AbstractPropertyRetriever.class);
    private static final MapProperty LINKS = new MapProperty("_links");
    private static final MapProperty CREDENTIALS = new MapProperty("credentials");


    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap();

    public DefaultUser(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultUser(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getId() {
        return getString(ID);
    }

    @Override
    public User setId(String id) {
        setProperty(ID, id);
        return this;
    }

    @Override
    public UserStatus getStatus() {
        return getEnumProperty(STATUS);
    }

    @Override
    public User setStatus(UserStatus userStatus) {
        setProperty(STATUS, userStatus);
        return this;
    }

    @Override
    public Date getStatusChanged() {
        return getDateProperty(STATUS_CHANGED);
    }

    @Override
    public User setStatusChanged(Date statusChangedDate) {
        setProperty(STATUS_CHANGED, statusChangedDate);
        return this;
    }

    @Override
    public Date getCreated() {
        return getDateProperty(CREATED);
    }

    @Override
    public User setCreated(Date createdDate) {
        setProperty(CREATED, createdDate);
        return this;
    }

    @Override
    public Date getLastLogin() {
        return getDateProperty(LAST_LOGIN);
    }

    @Override
    public User setLastLogin(Date lastLoginDate) {
        setProperty(LAST_LOGIN, lastLoginDate);
        return this;
    }

    @Override
    public Date getLastUpdated() {
        return getDateProperty(LAST_UPDATED);
    }

    @Override
    public User setLastUpdated(Date lastUpdatedDate) {
        setProperty(LAST_UPDATED, lastUpdatedDate);
        return this;
    }

    @Override
    public Date getPasswordChanged() {
        return getDateProperty(PASSWORD_CHANGED);
    }

    @Override
    public User setPasswordChanged(Date passwordChangedDate) {
        setProperty(PASSWORD_CHANGED, passwordChangedDate);
        return this;
    }

    @Override
    public Profile getProfile() {
        return getParentAwareObjectProperty(PROFILE);
    }

    @Override
    public User setProfile(Profile profile) {
        setProperty(PROFILE, profile);
        return this;
    }

    @Override
    public Map<String, Link> getLinks() {
        return getMap(LINKS);
    }

    @Override
    public User setLinks(Map<String, Link> links) {
        setProperty(LINKS, links);
        return this;
    }

    @Override
    public Map<String, Object> getCredentials() {
        return getMap(CREDENTIALS);
    }

    @Override
    public User setCredentials(Map<String, Object> credentials) {
        setProperty(CREDENTIALS, credentials);
        return this;
    }


}
