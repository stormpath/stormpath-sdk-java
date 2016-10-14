package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.ProviderAccountRequest;

/**
 * @since 1.2.0
 */
public class DefaultProviderAccountRequestResolver implements ProviderAccountRequestResolver {
    @Override
    public ProviderAccountRequest getProviderAccountRequest(String providerId, String code, String redirectUri) {
        return null;
    }
}
