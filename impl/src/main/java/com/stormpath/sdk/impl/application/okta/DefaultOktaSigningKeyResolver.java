package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.application.okta.OIDCKey;
import com.stormpath.sdk.application.okta.OIDCKeysList;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.util.Base64;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;

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

    public DefaultOktaSigningKeyResolver(DataStore dataStore) {
        this.dataStore = dataStore;
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

        OIDCKeysList keyList = dataStore.getResource("/oauth2/v1/keys", OIDCKeysList.class);
        OIDCKey key = keyList.getKeyById(keyId);

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
