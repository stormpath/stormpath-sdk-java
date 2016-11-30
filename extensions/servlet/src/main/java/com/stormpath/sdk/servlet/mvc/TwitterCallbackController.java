package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.3.0
 */
public class TwitterCallbackController extends AbstractSocialCallbackController {

    @Override
    protected ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {
        String accessToken = ServletUtils.getCleanParam(request, "accessToken");
        String accessTokenSecret = ServletUtils.getCleanParam(request, "accessTokenSecret");

        return Providers.TWITTER.account().setAccessToken(accessToken).setAccessTokenSecret(accessTokenSecret).build();
    }
}
