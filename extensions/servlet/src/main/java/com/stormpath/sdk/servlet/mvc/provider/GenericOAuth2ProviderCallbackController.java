package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.mvc.AbstractSocialCallbackController;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.3.0
 */
public class GenericOAuth2ProviderCallbackController extends AbstractSocialCallbackController {

    @Override
    public ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {
        String providerId = ServletUtils.getCleanParam(request, "providerId");
        String code = ServletUtils.getCleanParam(request, "code");

        if (StringUtils.hasText(code)) {
            return Providers.OAUTH2.account().setProviderId(providerId).setCode(code).build();
        } else {
            String accessToken = ServletUtils.getCleanParam(request, "accessToken");
            return Providers.OAUTH2.account().setProviderId(providerId).setAccessToken(accessToken).build();
        }
    }
}
