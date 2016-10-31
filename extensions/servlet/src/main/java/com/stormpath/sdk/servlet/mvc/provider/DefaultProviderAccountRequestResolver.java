package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderRequestFactory;
import com.stormpath.sdk.provider.Providers;

/**
 * @since 1.2.0
 */
public class DefaultProviderAccountRequestResolver implements ProviderAccountRequestResolver {
    @Override
    public ProviderAccountRequest getProviderAccountRequest(String providerId, String code, String redirectUri) {
        return getRequestFactory(providerId).account().setCode(code).setRedirectUri(redirectUri).build();
    }

    @Override
    public ProviderAccountRequest getProviderAccountRequestWithOrganizationHref(String providerId, String code, String organizationHref, String redirectUri) {
        return getRequestFactory(providerId).account().setCode(code).setRedirectUri(redirectUri).build();
    }

    private ProviderRequestFactory getRequestFactory(String providerId) {
        Assert.hasText(providerId);
        switch (providerId) {
            case "facebook":
                return Providers.FACEBOOK;
            case "github":
                return Providers.GITHUB;
            case "google":
                return Providers.GOOGLE;
            case "linkedin":
                return Providers.LINKEDIN;
            default:
                throw new IllegalArgumentException("Unrecognized providerId: " + providerId);
        }
    }
}
