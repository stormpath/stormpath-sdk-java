package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.authc.DefaultOktaAuthNAuthenticator;
import com.stormpath.sdk.impl.ds.InternalDataStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class OktaJwtAccountResolver implements JwtAccountResolver {

//    private final JwtSigningKeyResolver signingKeyResolver;
//
//    public OktaJwtAccountResolver(JwtSigningKeyResolver signingKeyResolver) {
//        this.signingKeyResolver = signingKeyResolver;
//    }

    @Override
    public Account getAccountByJwt(final HttpServletRequest request, final HttpServletResponse response, String jwt) {

        Client client = getClient(request);

        OktaAuthNAuthenticator oktaAuthNAuthenticator = new DefaultOktaAuthNAuthenticator((InternalDataStore)client.getDataStore());
        return oktaAuthNAuthenticator.getAccountByToken(jwt);

//        Jws<Claims> jws = Jwts.parser().setSigningKeyResolver(new SigningKeyResolver() {
//            @Override
//            public Key resolveSigningKey(JwsHeader header, Claims claims) {
//                return signingKeyResolver.getSigningKey(request, response, header, claims);
//            }
//
//            @Override
//            public Key resolveSigningKey(JwsHeader header, String plaintext) {
//                throw new UnsupportedOperationException("resolveSigningKey(JwsHeader header, String plaintext), has not been implemented");
//            }
//        }).parseClaimsJws(jwt);
//        Claims claims = jws.getBody();
//
//        // TODO" not sure if this is needed
//        if ("refresh".equals(jws.getHeader().get("stt"))) {
//            //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/674
//            //This is a refresh token, let's not allow the account to be obtained from it
//            return null;
//        }
//
//        String accountHref = OktaApiPaths.USERS + claims.get("uid");
//
//        //will hit the cache:
//        return client.getResource(accountHref, Account.class);
    }

    protected Client getClient(HttpServletRequest request) {
        return (Client)request.getAttribute(Client.class.getName());
    }
}
