package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.impl.provider.DefaultOktaProviderAccountResult;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.oauth.OAuthErrorCode;
import com.stormpath.sdk.servlet.filter.oauth.OAuthException;
import com.stormpath.sdk.servlet.mvc.AbstractSocialCallbackController;
import com.stormpath.sdk.servlet.mvc.DefaultViewModel;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import com.stormpath.sdk.servlet.util.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 *
 */
public class OktaOIDCCallbackController extends AbstractSocialCallbackController {

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String errorDescription = ServletUtils.getCleanParam(request, "error_description");

        if (errorDescription != null) {
            return new DefaultViewModel( getNextUri()+ "?error="+errorDescription).setRedirect(true);
        } else {

            final ProviderAccountRequest providerRequest = getAccountProviderRequest(request);

            final DefaultOktaProviderAccountResult result = (DefaultOktaProviderAccountResult) getApplication(request).getAccount(providerRequest);

            // 751: Check if account is unverified and redirect to verifyUri if true
            final Account account = result.getAccount();
            if (account.getStatus().equals(AccountStatus.UNVERIFIED)) {
                Config config = (Config) request.getServletContext().getAttribute(Config.class.getName());
                String loginUri = config.getLoginConfig().getUri();
                return new DefaultViewModel(loginUri + "?status=unverified").setRedirect(true);
            }

            AuthenticationResult authcResult = new AccessTokenResult() {
                @Override
                public TokenResponse getTokenResponse() {
                    return result.getTokenResponse();
                }

                @Override
                public Set<String> getScope() {
                    return null; // TODO
                }

                @Override
                public ApiKey getApiKey() {
                    return null;
                }

                @Override
                public Account getAccount() {
                    return account;
                }

                @Override
                public void accept(AuthenticationResultVisitor visitor) {
                    visitor.visit(this);
                }

                @Override
                public String getHref() {
                    return null;
                }
            };
            authenticationResultSaver.set(request, response, authcResult);

            eventPublisher.publish(new DefaultSuccessfulAuthenticationRequestEvent(request, response, null, authcResult));
            String redirectUri = getRedirectUri(request);

            return new DefaultViewModel(redirectUri).setRedirect(true);
        }
    }

    private String getRedirectUri(HttpServletRequest request) {
        //Fixes #849 we send in the state the original path the user requested based on the next query param, so we can redirect back
        String redirectUri = nextUri;
        String next = ServletUtils.getCleanParam(request, "state");
        if (shouldGetRedirectUriFromState(next)) {
            redirectUri = next;
        }
        return redirectUri;
    }

    @Override
    public ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {

        String code = ServletUtils.getCleanParam(request, "code");

        // if no code, throw error
        if (code == null) {
            String error = ServletUtils.getCleanParam(request, "error");
            String errorDescription = ServletUtils.getCleanParam(request, "error_description");
            throw new OAuthException(new OAuthErrorCode(error), errorDescription);
        }

        return Providers.OKTA.account()
                .setCode(code)
                .setRedirectUri(request.getRequestURL().toString())
                .build();
    }
}
