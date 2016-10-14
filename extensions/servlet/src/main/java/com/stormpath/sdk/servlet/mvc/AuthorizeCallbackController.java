package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.servlet.authc.impl.DefaultSuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.provider.ProviderAccountRequestResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.2.0
 */
public class AuthorizeCallbackController extends AbstractController {

    private Saver<AuthenticationResult> authenticationResultSaver;
    private ProviderAccountRequestResolver providerAccountRequestResolver;

    public void setAuthenticationResultSaver(Saver<AuthenticationResult> authenticationResultSaver) {
        this.authenticationResultSaver = authenticationResultSaver;
    }


    @Override
    public void init() throws Exception {
        Assert.notNull(applicationResolver, "applicationResolver cannot be null.");
        Assert.notNull(authenticationResultSaver, "authenticationResultSaver cannot be null.");
        Assert.notNull(eventPublisher, "eventPublisher cannot be null.");
        Assert.notNull(providerAccountRequestResolver, "providerAccountRequestResolver cannot be null.");
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    public void setProviderAccountRequestResolver(ProviderAccountRequestResolver providerAccountRequestResolver) {
        this.providerAccountRequestResolver = providerAccountRequestResolver;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Claims stateClaims = getState(request);
        String code = (String) stateClaims.get("code");
        String providerId = (String) stateClaims.get("provider");
        ProviderAccountRequest providerAccountRequest = providerAccountRequestResolver.getProviderAccountRequest(providerId,
                code, request.getRequestURI());
        ProviderAccountResult providerAccountResult = getApplication(request).getAccount(providerAccountRequest);
        Account account = providerAccountResult.getAccount();
        AuthenticationResult authcResult = new TransientAuthenticationResult(account);
        authenticationResultSaver.set(request, response, authcResult);

        eventPublisher.publish(new DefaultSuccessfulAuthenticationRequestEvent(request, response, null, authcResult));

        return new DefaultViewModel((String) stateClaims.get("redirect_uri")).setRedirect(true);
    }

    private Claims getState(HttpServletRequest request) {
        Client client = (Client) request.getAttribute(Client.class.getName());
        Assert.notNull(client, "client must be available in request attributes");
        String signingKey = client.getApiKey().getSecret();
        String stateJwt = request.getParameter("state");
        Jws<Claims> jws = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(stateJwt);
        return jws.getBody();
    }


}
