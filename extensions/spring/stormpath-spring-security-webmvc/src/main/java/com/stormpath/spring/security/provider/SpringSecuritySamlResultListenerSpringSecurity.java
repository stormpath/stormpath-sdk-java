package com.stormpath.spring.security.provider;

import com.stormpath.sdk.idsite.AuthenticationResult;
import com.stormpath.sdk.idsite.LogoutResult;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.spring.security.token.SamlAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecuritySamlResultListenerSpringSecurity extends AbstractSpringSecurityProviderResultListener implements SamlResultListener {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecuritySamlResultListenerSpringSecurity.class);

    public SpringSecuritySamlResultListenerSpringSecurity(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public void onAuthenticated(AuthenticationResult result) {
        super.doAuthenticate(new SamlAuthenticationToken(result.getAccount()));
    }

    @Override
    public void onLogout(LogoutResult result) {
        SecurityContextHolder.clearContext();
    }

}
