package com.stormpath.sdk.application.okta;

import com.stormpath.sdk.resource.Resource;

import java.security.Key;
import java.util.Set;

/**
 *
 */
public interface OIDCKeysList extends Resource {

    Set<OIDCKey> getKeys();
    OIDCKey getKeyById(String keyId);

}
