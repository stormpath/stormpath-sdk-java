package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.ProviderAccountRequest;

/**
 * @since 1.2.0
 */
public interface ProviderAccountRequestResolver {
    ProviderAccountRequest getProviderAccountRequest(String providerId, String code, String redirectUri);
}
