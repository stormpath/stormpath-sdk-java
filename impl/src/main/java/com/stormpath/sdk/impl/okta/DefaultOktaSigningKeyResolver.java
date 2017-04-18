package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.okta.OIDCKey;
import com.stormpath.sdk.okta.OIDCKeysList;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Resolves Signing keys via Okta's /oauth2/v1/keys endpoint.
 * TODO: this could be made more generic by using the auto discovery endpoint.
 */
public class DefaultOktaSigningKeyResolver implements OktaSigningKeyResolver {

    private final DataStore dataStore;

    private String keysUrl = "/oauth2/v1/keys";

    public DefaultOktaSigningKeyResolver(DataStore dataStore, String authorizationServerId) {
        this.dataStore = dataStore;

        if (Strings.hasText(authorizationServerId)) {
            // TODO: This should come from the discovery URL, no _easy_ way of getting it,
            // as that Resource is NOT cached.
            keysUrl = "/oauth2/" + authorizationServerId + "/v1/keys";
        }
    }

    @Override
    public OktaSigningKeyResolver setKeysUrl(String keysUrl) {
        this.keysUrl = keysUrl;
        return this;
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        return getKey(header);
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, String plaintext) {
        return getKey(header);
    }

    private Key getKey(JwsHeader header) {
        String keyId = header.getKeyId();
        String keyAlgoritm = header.getAlgorithm();

        if (!"RS256".equals(keyAlgoritm)) {
            throw new UnsupportedOperationException("Only 'RS256' key algorithm is supported.");
        }

        OIDCKeysList keyList = dataStore.getResource(keysUrl, OIDCKeysList.class);
        OIDCKey key = keyList.getKeyById(keyId);
        Assert.notNull(key, "Key with 'kid' of "+keyId+" could not be found via the '" + keysUrl + "' endpoint.");

        try {

            BigInteger modulus = new BigInteger(1, Base64.decodeBase64(key.get("n")));
            BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(key.get("e")));
            return KeyFactory.getInstance("RSA").generatePublic(
                    new RSAPublicKeySpec(modulus, publicExponent));

        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Failed to load key Algorithm", e);
        } catch (InvalidKeySpecException e) {
            throw new UnsupportedOperationException("Failed to load key", e);
        }
    }

    @Override
    public String getHref() {
        return null;
    }
}
