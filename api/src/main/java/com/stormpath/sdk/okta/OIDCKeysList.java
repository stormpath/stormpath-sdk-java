package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Resource;

import java.util.Set;

/**
 *
 */
public interface OIDCKeysList extends Resource {

    Set<OIDCKey> getKeys();
    OIDCKey getKeyById(String keyId);

}
