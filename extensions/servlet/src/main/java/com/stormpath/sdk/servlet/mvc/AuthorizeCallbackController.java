package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
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
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

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
        String code = request.getParameter("code");
        String providerId = (String) stateClaims.get("provider");
        ProviderAccountRequest providerAccountRequest = providerAccountRequestResolver.getProviderAccountRequest(providerId,
                code, getRedirectURI(request));
        ProviderAccountResult providerAccountResult = getApplication(request).getAccount(providerAccountRequest);
        Account account = providerAccountResult.getAccount();
        AuthenticationResult authcResult = new TransientAuthenticationResult(account);
        authenticationResultSaver.set(request, response, authcResult);

        eventPublisher.publish(new DefaultSuccessfulAuthenticationRequestEvent(request, response, null, authcResult));

        return new DefaultViewModel(getApplicationCallbackUri(account, stateClaims, request)).setRedirect(true);
    }

    private String getApplicationCallbackUri(Account account, Claims stateClaims, HttpServletRequest request)
            throws URISyntaxException, UnsupportedEncodingException {
        URIBuilder uriBuilder = new URIBuilder((String) stateClaims.get("redirect_uri"));
        uriBuilder.addParameter("jwtResponse", getJwtResponse(account, stateClaims, request));
        if (stateClaims.containsKey("state")) {
            uriBuilder.addParameter("state", stateClaims.get("state").toString());
        }
        return uriBuilder.build().toString();
    }

    private String getJwtResponse(Account account, Claims stateClaims, HttpServletRequest request) throws UnsupportedEncodingException {
        ApiKey apiKey = getApiKey(request);
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + 60000);
        Claims claims = Jwts.claims().setSubject(account.getHref())
                .setId(UUID.randomUUID().toString())
                .setIssuer(getApplication(request).getHref())
                .setAudience(apiKey.getId())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration);
        claims.put("status", "AUTHENTICATED");
        claims.put("isNewSub", false);
        if (stateClaims.getId() != null) {
            claims.put("irt", stateClaims.getId());
        }
        if (stateClaims.containsKey("state")) {
            claims.put("state", stateClaims.get("state"));
        }
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, apiKey.getSecret().getBytes("UTF-8"))
                .setHeaderParam("kid", apiKey.getId())
                .setHeaderParam("stt", "assertion")
                .setClaims(claims)
                .compact();
    }

    private String getRedirectURI(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    private Claims getState(HttpServletRequest request) {
        ApiKey apiKey = getApiKey(request);
        String signingKey = apiKey.getSecret();
        String stateJwt = request.getParameter("state");
        Jws<Claims> jws = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(stateJwt);
        return jws.getBody();
    }

    private ApiKey getApiKey(HttpServletRequest request) {
        Client client = (Client) request.getAttribute(Client.class.getName());
        Assert.notNull(client, "client must be available in request attributes");
        return client.getApiKey();
    }


}
