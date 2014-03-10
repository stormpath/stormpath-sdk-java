package com.stormpath.sdk.impl.http.authc;

import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.impl.http.authc.RequestAuthenticator;

/**
 * Factory interface to create {@link RequestAuthenticator}s out of {@link AuthenticationScheme}s.
 *
 * @since 0.9.3
 */
public interface RequestAuthenticatorFactory {

    RequestAuthenticator create(AuthenticationScheme scheme);

}
