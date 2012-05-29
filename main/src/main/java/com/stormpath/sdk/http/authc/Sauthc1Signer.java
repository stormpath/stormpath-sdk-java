/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.http.authc;

import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.http.Request;
import com.stormpath.sdk.http.impl.SignatureException;
import com.stormpath.sdk.util.RequestUtils;
import com.stormpath.sdk.util.StringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @since 0.1
 */
public class Sauthc1Signer implements Signer {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String HOST_HEADER = "Host";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String STORMAPTH_DATE_HEADER = "X-Stormpath-Date";
    private static final String SCOPE_TERMINATOR = "sauthc1_request";
    private static final String ALGORITHM = "HMAC-SHA-256";
    private static final String AUTHENTICATION_SCHEME = "SAuthc1";
    private static final String NL = "\n";

    private static final Logger log = LoggerFactory.getLogger(Sauthc1Signer.class);

    @Override
    public void sign(Request request, ApiKey apiKey) throws SignatureException {

        SimpleDateFormat dateStampFormat = new SimpleDateFormat("yyyyMMdd");
        dateStampFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));

        URI uri = request.getResourceUrl();

        String nonce = UUID.randomUUID().toString();

        // Stormpath1 requires that we sign the Host header so we
        // have to have it in the request by the time we sign.
        String hostHeader = uri.getHost();
        if (!RequestUtils.isDefaultPort(uri)) {
            hostHeader += ":" + uri.getPort();
        }
        request.getHeaders().set(HOST_HEADER, hostHeader);

        Date date = new Date();

        String dateTime = dateTimeFormat.format(date);
        String dateStamp = dateStampFormat.format(date);

        request.getHeaders().set(STORMAPTH_DATE_HEADER, dateTime);

        String method = request.getMethod().toString();
        String canonicalResourcePath = canonicalizeResourcePath(uri.getPath());
        String canonicalQueryString = canonicalizeQueryString(request);
        String canonicalHeadersString = canonicalizeHeadersString(request);
        String signedHeadersString = getSignedHeadersString(request);
        String requestPayloadHashHex = toHex(hash(getRequestPayload(request)));

        String canonicalRequest =
                method + NL +
                canonicalResourcePath + NL +
                canonicalQueryString + NL +
                canonicalHeadersString + NL +
                signedHeadersString + NL +
                requestPayloadHashHex;

        log.debug(AUTHENTICATION_SCHEME + " Canonical Request: " + canonicalRequest);

        String id = apiKey.getId() + "/" + dateStamp + "/" + nonce + "/" + SCOPE_TERMINATOR;
        String canonicalRequestHashHex = toHex(hash(canonicalRequest));

        String stringToSign =
                ALGORITHM + NL +
                dateTime + NL +
                id + NL +
                canonicalRequestHashHex;

        log.debug(AUTHENTICATION_SCHEME + " String to Sign: " + stringToSign);

        // SAuthc1 uses a series of derived keys, formed by hashing different pieces of data
        byte[] kSecret = toUtf8Bytes(AUTHENTICATION_SCHEME + apiKey.getSecret());
        byte[] kDate = sign(dateStamp, kSecret, MacAlgorithm.HmacSHA256);
        byte[] kNonce = sign(nonce, kDate, MacAlgorithm.HmacSHA256);
        byte[] kSigning = sign(SCOPE_TERMINATOR, kNonce, MacAlgorithm.HmacSHA256);

        byte[] signature = sign(toUtf8Bytes(stringToSign), kSigning, MacAlgorithm.HmacSHA256);
        String signatureHex = toHex(signature);

        String authorizationHeader =
                AUTHENTICATION_SCHEME + " " +
                "sauthc1Id=" + id + ", " +
                "sauthcSignedHeaders=" + signedHeadersString + ", " +
                "sauthc1Signature=" + signatureHex;

        request.getHeaders().set(AUTHORIZATION_HEADER, authorizationHeader);
    }

    public static byte[] toUtf8Bytes(String s) {
        if (s == null) {
            return null;
        }
        try {
            return s.getBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to UTF-8 encode!", e);
        }
    }

    /**
     * Converts byte data to a Hex-encoded string.
     *
     * @param data data to hex encode.
     * @return hex-encoded string.
     */
    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }

    /**
     * Hashes the string contents (assumed to be UTF-8) using the SHA-256
     * algorithm.
     *
     * @param text The string to hash.
     * @return The hashed bytes from the specified string.
     * @throws SignatureException If the hash cannot be computed.
     */
    protected byte[] hash(String text) throws SignatureException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(DEFAULT_ENCODING));
            return md.digest();
        } catch (Exception e) {
            throw new SignatureException("Unable to compute hash while signing request.", e);
        }
    }

    protected byte[] sign(String stringData, byte[] key, MacAlgorithm algorithm) throws SignatureException {
        try {
            byte[] data = stringData.getBytes(DEFAULT_ENCODING);
            return sign(data, key, algorithm);
        } catch (Exception e) {
            throw new SignatureException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }

    protected byte[] sign(byte[] data, byte[] key, MacAlgorithm algorithm) throws SignatureException {
        try {
            Mac mac = Mac.getInstance(algorithm.toString());
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new SignatureException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }

    protected String getRequestPayload(Request request) {
        return getRequestPayloadWithoutQueryParams(request);
    }

    protected String getRequestPayloadWithoutQueryParams(Request request) {
        try {
            InputStream content = request.getBody();
            if (content == null) return "";

            if (content instanceof StringInputStream) {
                return content.toString();
            }

            if (!content.markSupported()) {
                throw new SignatureException("Unable to read request payload to sign request.");
            }

            StringBuilder sb = new StringBuilder();
            content.mark(-1);
            int b;
            while ((b = content.read()) > -1) {
                sb.append((char) b);
            }
            content.reset();
            return sb.toString();
        } catch (Exception e) {
            throw new SignatureException("Unable to read request payload to sign request: " + e.getMessage(), e);
        }
    }

    protected String canonicalizeQueryString(Request request) {
        return request.getQueryString().toString(true);
    }

    private String canonicalizeResourcePath(String resourcePath) {
        if (resourcePath == null || resourcePath.length() == 0) {
            return "/";
        } else {
            return RequestUtils.encodeUrl(resourcePath, true, true);
        }
    }

    private String canonicalizeHeadersString(Request request) {
        List<String> sortedHeaders = new ArrayList<String>();
        sortedHeaders.addAll(request.getHeaders().keySet());
        Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

        StringBuilder buffer = new StringBuilder();
        for (String header : sortedHeaders) {
            buffer.append(header.toLowerCase()).append(":");
            List<String> values = request.getHeaders().get(header);
            boolean first = true;
            if (values != null) {
                for(String value : values) {
                    if (!first) {
                        buffer.append(",");
                    }
                    buffer.append(value);
                    first = false;
                }
            }
            buffer.append(NL);
        }

        return buffer.toString();
    }

    private String getSignedHeadersString(Request request) {
        List<String> sortedHeaders = new ArrayList<String>();
        sortedHeaders.addAll(request.getHeaders().keySet());
        Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);

        StringBuilder buffer = new StringBuilder();
        for (String header : sortedHeaders) {
            if (buffer.length() > 0) buffer.append(";");
            buffer.append(header.toLowerCase());
        }

        return buffer.toString();
    }
}
