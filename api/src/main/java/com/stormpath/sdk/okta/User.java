package com.stormpath.sdk.okta;

import java.util.Date;
import java.util.Map;

/**
 * User object mapped into the Okta API.
 */
public interface User {

    String getId();
    User setId(String string);

    // "activated": null, ????

    UserStatus getStatus();
    User setStatus(UserStatus userStatus);

    Date getStatusChanged();
    User setStatusChanged(Date statusChangedDate);

    Date getCreated();
    User setCreated(Date createdDate);

    Date getLastLogin();
    User setLastLogin(Date lastLoginDate);

    Date getLastUpdated();
    User setLastUpdated(Date lastUpdatedDate);

    Date getPasswordChanged();
    User setPasswordChanged(Date passwordChangedDate);

    Profile getProfile();
    User setProfile(Profile profile);

    Map<String, Link> getLinks();
    User setLinks(Map<String, Link> links);

    Map<String, Object> getCredentials();
    User setCredentials(Map<String, Object> credentials);
}
