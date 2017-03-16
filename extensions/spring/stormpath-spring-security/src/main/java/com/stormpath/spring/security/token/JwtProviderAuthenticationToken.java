package com.stormpath.spring.security.token;

import com.stormpath.sdk.authc.AuthenticationResult;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 */
public class JwtProviderAuthenticationToken extends AbstractAuthenticationToken {

    private final String accessToken;
    private final String refreshToken;
    private final UserDetails userDetails;
    private final AuthenticationResult authenticationResult;

    public JwtProviderAuthenticationToken(User userDetails, String accessToken, String refreshToken, AuthenticationResult authenticationResult) {

        super(userDetails.getAuthorities());
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userDetails = userDetails;
        this.authenticationResult = authenticationResult;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }
}
