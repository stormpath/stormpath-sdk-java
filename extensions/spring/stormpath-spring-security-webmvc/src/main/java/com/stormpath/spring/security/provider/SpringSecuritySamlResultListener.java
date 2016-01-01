package com.stormpath.spring.security.provider;

import com.stormpath.sdk.idsite.AuthenticationResult;
import com.stormpath.sdk.idsite.LogoutResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.spring.security.token.SamlAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecuritySamlResultListener implements SamlResultListener {
    private static final Logger logger = LoggerFactory.getLogger(SpringSecuritySamlResultListener.class);

    protected StormpathAuthenticationProvider authenticationProvider;

    public SpringSecuritySamlResultListener(AuthenticationProvider stormpathAuthenticationProvider) {
        Assert.isTrue(
            stormpathAuthenticationProvider instanceof StormpathAuthenticationProvider,
            "AuthenticationProvider must be a StormpathAuthenticationProvider"
        );
        this.authenticationProvider = (StormpathAuthenticationProvider) stormpathAuthenticationProvider;
    }

    @Override
    public void onAuthenticated(AuthenticationResult result) {
        SecurityContextHolder.clearContext();
        Authentication authentication = new SamlAuthenticationToken(result.getAccount());
        authentication = authenticationProvider.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    public void onLogout(LogoutResult result) {
        SecurityContextHolder.clearContext();
    }
}
