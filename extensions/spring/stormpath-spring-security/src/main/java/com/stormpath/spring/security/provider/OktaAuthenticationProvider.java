package com.stormpath.spring.security.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.provider.OktaProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.spring.security.token.JwtProviderAuthenticationToken;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;

/**
 *
 */
public class OktaAuthenticationProvider extends StormpathAuthenticationProvider {

    private final Application application;
    private final Client client;

    public OktaAuthenticationProvider(Application application, Client client) {
        super(application);
        this.application = application;
        this.client = client;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Authentication returnToken;

        try {
            if (authentication instanceof ProviderAuthenticationToken) {

                if (authentication.getCredentials() instanceof OktaProviderAccountResult) {
                    // This is a social login

                    OktaProviderAccountResult accountResult = ((OktaProviderAccountResult) authentication.getCredentials());

                    // if we have the account from the token the request was already validated
                    // TODO: test this
                    Account account = ((ProviderAuthenticationToken) authentication).getAccount();
                    User userDetails = new User(account.getHref(), "", getGrantedAuthorities(account));

                    returnToken = new JwtProviderAuthenticationToken(
                            userDetails,
                            accountResult.getTokenResponse().getAccessToken(),
                            accountResult.getTokenResponse().getRefreshToken(),
                            accountResult
                    );
                    returnToken.setAuthenticated(true);
                }
                else { // access_token cookie (user info NOT cached in session)

                    Account account = ((ProviderAuthenticationToken) authentication).getAccount();

                    returnToken = this.getAuthenticationTokenFactory()
                            .createAuthenticationToken(
                                    authentication.getPrincipal(),
                                    null,
                                    getGrantedAuthorities(account),
                                    account
                            );
                }
            }
            else {
                // this is a user/pass login
                AuthenticationRequest request = createAuthenticationRequest(authentication);

                try {
                    AccessTokenResult result = (AccessTokenResult) application.authenticateAccount(request);
                    Account account = result.getAccount();

                    User userDetails = new User(account.getHref(), "", getGrantedAuthorities(account));
                    // We are _not_ actually treating the access token as a JWT yet, but it is one,
                    // and we might handle it client side in the future.
                    returnToken = new JwtProviderAuthenticationToken(
                            userDetails,
                            result.getTokenResponse().getAccessToken(),
                            result.getTokenResponse().getRefreshToken(),
                            result);
                    returnToken.setAuthenticated(true);

                } finally {
                    //Clear the request data to prevent later memory access
                    request.clear();
                }
            }
        } catch (ResourceException e) {
            String msg = Strings.clean(e.getStormpathError().getMessage());
            if (msg == null) {
                msg = Strings.clean(e.getDeveloperMessage());
            }
            if (msg == null) {
                msg = "Invalid login or password.";
            }
            throw new AuthenticationServiceException(msg, e);
        }

        return returnToken;
    }
}
