package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

/**
 */
public interface OktaUserToApplicationMapping extends Resource {

    String getId();
    OktaUserToApplicationMapping setId(String id);

    String getScope();
    OktaUserToApplicationMapping setScope(String scope);

    String getUsername();
    OktaUserToApplicationMapping setUsername(String username);

}
