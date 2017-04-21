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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resolves Signing keys via Okta's /oauth2/v1/keys endpoint.
 * TODO: this could be made more generic by using the auto discovery endpoint.
 */
public class DefaultOktaSigningKeyResolver implements OktaSigningKeyResolver {

    private final DataStore dataStore;

    // cache the keys forever, they are not rotated often.
    private final Map<String, Key> keyMap = new LinkedHashMap<>();

    private String keysUrl = "/oauth2/v1/keys";

    public DefaultOktaSigningKeyResolver(DataStore dataStore, String authorizationServerId, String keysUrl) {
        this.dataStore = dataStore;

        if (Strings.hasText(authorizationServerId)) {
            // as that Resource is NOT cached.
            this.keysUrl = "/oauth2/" + authorizationServerId + "/v1/keys";
        }

        if (keysUrl != null) {
            this.keysUrl = keysUrl;
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
        String keyAlgorithm = header.getAlgorithm();

        if (!"RS256".equals(keyAlgorithm)) {
            throw new UnsupportedOperationException("Only 'RS256' key algorithm is supported.");
        }

        Key key = keyMap.get(keyId);
        if (key == null) {
            keyMap.putAll(getKeys());
            key = keyMap.get(keyId);
        }
        Assert.notNull(key, "Key with 'kid' of "+keyId+" could not be found via the '" + keysUrl + "' endpoint.");

        return key;
    }

    private Map<String, Key> getKeys() {

        Map<String, Key> keyMap = new LinkedHashMap<>();
        OIDCKeysList keyList = dataStore.getResource(keysUrl, OIDCKeysList.class);

        for (OIDCKey oidcKey : keyList.getKeys()) {
            try {

                BigInteger modulus = new BigInteger(1, Base64.decodeBase64(oidcKey.get("n")));
                BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(oidcKey.get("e")));
                Key key = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
                keyMap.put(oidcKey.getId(), key);

            } catch (NoSuchAlgorithmException e) {
                throw new UnsupportedOperationException("Failed to load key Algorithm", e);
            } catch (InvalidKeySpecException e) {
                throw new UnsupportedOperationException("Failed to load key", e);
            }
        }
        return keyMap;
    }

    @Override
    public String getHref() {
        return null;
    }
}
