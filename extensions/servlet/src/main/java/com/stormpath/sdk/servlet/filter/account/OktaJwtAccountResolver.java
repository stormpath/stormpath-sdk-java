package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.okta.OktaApiPaths;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class OktaJwtAccountResolver implements JwtAccountResolver {

    private final SigningKeyResolver signingKeyResolver;

    public OktaJwtAccountResolver(SigningKeyResolver signingKeyResolver) {
        this.signingKeyResolver = signingKeyResolver;
    }

    @Override
    public Account getAccountByJwt(HttpServletRequest request, HttpServletResponse response, String jwt) {

        Client client = getClient(request);

        Jws<Claims> jws = Jwts.parser().setSigningKeyResolver(signingKeyResolver).parseClaimsJws(jwt);
        Claims claims = jws.getBody();

        // TODO" not sure if this is needed
        if ("refresh".equals(jws.getHeader().get("stt"))) {
            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/674
            //This is a refresh token, let's not allow the account to be obtained from it
            return null;
        }

        String accountHref = OktaApiPaths.USERS + claims.get("uid");

        //will hit the cache:
        return client.getResource(accountHref, Account.class);
    }

    protected Client getClient(HttpServletRequest request) {
        return (Client)request.getAttribute(Client.class.getName());
    }
}
