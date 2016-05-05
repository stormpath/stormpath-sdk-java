package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.AbstractSocialCallbackController;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0
 */
public class LinkedinCallbackController extends AbstractSocialCallbackController {

    public LinkedinCallbackController(String loginNextUri, Saver<AuthenticationResult> authenticationResultSaver) {
        super(loginNextUri, authenticationResultSaver);
    }

    @Override
    protected ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {
        String code = ServletUtils.getCleanParam(request, "code");
        return Providers.LINKEDIN.account().setCode(code).build();
    }
}
