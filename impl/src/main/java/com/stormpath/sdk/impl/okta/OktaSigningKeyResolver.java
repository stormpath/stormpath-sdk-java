package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.resource.Resource;
import io.jsonwebtoken.SigningKeyResolver;

/**
 *
 */
public interface OktaSigningKeyResolver extends SigningKeyResolver, Resource {

    OktaSigningKeyResolver setKeysUrl(String keysUrl);

}
