package com.stormpath.spring.security.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.spring.security.token.JwtProviderAuthenticationToken;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 */
public class OktaAuthenticationProvider extends StormpathAuthenticationProvider {

    final private Application application;

    public OktaAuthenticationProvider(Application application) {
        super(application);
        this.application = application;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Account account;

        Authentication returnToken = null;

        try {
            if (authentication instanceof ProviderAuthenticationToken) {

                account = ((ProviderAuthenticationToken) authentication).getAccount();
                returnToken = authentication;

            } else {

                AuthenticationRequest request = createAuthenticationRequest(authentication);

                try {
                    AccessTokenResult result = (AccessTokenResult) application.authenticateAccount(request);
                    account = result.getAccount();

                    User userDetails = new User(account.getHref(), "", getGrantedAuthorities(account));
                    returnToken = new JwtProviderAuthenticationToken(userDetails, result.getTokenResponse().getAccessToken(), result.getTokenResponse().getRefreshToken(), result);

                } finally {
                    //Clear the request data to prevent later memory access
//                            request.clear();
                }

            }
        } catch (ResourceException e) {
            String msg = Strings.clean(e.getMessage());
            if (msg == null) {
                msg = Strings.clean(e.getDeveloperMessage());
            }
            if (msg == null) {
                msg = "Invalid login or password.";
            }
            throw new AuthenticationServiceException(msg, e);
        }

        if (returnToken == null) {
            UserDetails userDetails = new User(account.getHref(), "", getGrantedAuthorities(account));
            returnToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        return returnToken;
    }
}
