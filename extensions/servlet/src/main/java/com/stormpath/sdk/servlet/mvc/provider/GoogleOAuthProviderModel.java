package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.GoogleProvider;
import com.stormpath.sdk.provider.GoogleProviderAccessType;
import com.stormpath.sdk.provider.GoogleProviderDisplay;

/**
 * @since 1.2.0
 */
public class GoogleOAuthProviderModel extends DefaultOAuthProviderModel {
    private final GoogleProvider provider;

    public GoogleOAuthProviderModel(GoogleProvider provider) {
        super(provider);
        this.provider = provider;
    }

    public String getHd() {
        return provider.getHd();
    }

    public String getAccessType() {
        GoogleProviderAccessType accessType = provider.getAccessType();
        return accessType == null ? null : accessType.name().toLowerCase();
    }

    public String getDisplay() {
        GoogleProviderDisplay display = provider.getDisplay();
        return display == null ? null : display.name().toLowerCase();
    }
}
