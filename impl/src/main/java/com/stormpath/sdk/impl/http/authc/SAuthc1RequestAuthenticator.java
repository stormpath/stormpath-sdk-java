/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.http.authc;

import com.stormpath.sdk.impl.authc.RequestAuthenticator;
import com.stormpath.sdk.impl.authc.credentials.ClientCredentials;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.support.RequestAuthenticationException;
import com.stormpath.sdk.impl.util.RequestUtils;
import com.stormpath.sdk.impl.util.StringInputStream;
import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.SimpleTimeZone;
import java.util.UUID;

/**
 * @since 0.1
 */
public class SAuthc1RequestAuthenticator implements RequestAuthenticator {

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String HOST_HEADER = "Host";
    public static final String STORMPATH_DATE_HEADER = "X-Stormpath-Date";
    public static final String ID_TERMINATOR = "sauthc1_request";
    public static final String ALGORITHM = "HMAC-SHA-256";
    public static final String AUTHENTICATION_SCHEME = "SAuthc1";
    public static final String SAUTHC1_ID = "sauthc1Id";
    public static final String SAUTHC1_SIGNED_HEADERS = "sauthc1SignedHeaders";
    public static final String SAUTHC1_SIGNATURE = "sauthc1Signature";

    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    public static final String TIME_ZONE = "UTC";

    private static final String NL = "\n";

    private static final Logger log = LoggerFactory.getLogger(SAuthc1RequestAuthenticator.class);

    private final ClientCredentials clientCredentials;

    public SAuthc1RequestAuthenticator(ClientCredentials clientCredentials) {
        Assert.notNull(clientCredentials, "clientCredentials must not be null.");
        this.clientCredentials = clientCredentials;
    }

    @Override
    public void authenticate(Request request) throws RequestAuthenticationException {
        Date date = new Date();
        String nonce = UUID.randomUUID().toString();
        authenticate(request, date, nonce);
    }

    public void authenticate(final Request request, final Date date, final String nonce) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));

        SimpleDateFormat timestampFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        timestampFormat.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));

        URI uri = request.getResourceUrl();

        // SAuthc1 requires that we sign the Host header so we
        // have to have it in the request by the time we sign.
        String hostHeader = uri.getHost();
        if (!RequestUtils.isDefaultPort(uri)) {
            hostHeader += ":" + uri.getPort();
        }
        request.getHeaders().set(HOST_HEADER, hostHeader);

        String timestamp = timestampFormat.format(date);
        String dateStamp = dateFormat.format(date);

        request.getHeaders().set(STORMPATH_DATE_HEADER, timestamp);

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

        log.debug("{} Canonical Request: {}", AUTHENTICATION_SCHEME, canonicalRequest);

        String id = clientCredentials.getId() + "/" + dateStamp + "/" + nonce + "/" + ID_TERMINATOR;

        String canonicalRequestHashHex = toHex(hash(canonicalRequest));

        String stringToSign =
                ALGORITHM + NL +
                        timestamp + NL +
                        id + NL +
                        canonicalRequestHashHex;

        log.debug("{} String to Sign: {}", AUTHENTICATION_SCHEME, stringToSign);

        // SAuthc1 uses a series of derived keys, formed by hashing different pieces of data
        byte[] kSecret = toUtf8Bytes(AUTHENTICATION_SCHEME + clientCredentials.getSecret());
        byte[] kDate = sign(dateStamp, kSecret, MacAlgorithm.HmacSHA256);
        byte[] kNonce = sign(nonce, kDate, MacAlgorithm.HmacSHA256);
        byte[] kSigning = sign(ID_TERMINATOR, kNonce, MacAlgorithm.HmacSHA256);

        byte[] signature = sign(toUtf8Bytes(stringToSign), kSigning, MacAlgorithm.HmacSHA256);
        String signatureHex = toHex(signature);

        String authorizationHeader =
                AUTHENTICATION_SCHEME + " " +
                        createNameValuePair(SAUTHC1_ID, id) + ", " +
                        createNameValuePair(SAUTHC1_SIGNED_HEADERS, signedHeadersString) + ", " +
                        createNameValuePair(SAUTHC1_SIGNATURE, signatureHex);

        log.debug("{}: {}", AUTHORIZATION_HEADER, authorizationHeader);

        request.getHeaders().set(AUTHORIZATION_HEADER, authorizationHeader);
    }

    private static String createNameValuePair(String name, String value) {
        return name + "=" + value;
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
     * @throws RequestAuthenticationException If the hash cannot be computed.
     */
    protected byte[] hash(String text) throws RequestAuthenticationException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(DEFAULT_ENCODING));
            return md.digest();
        } catch (Exception e) {
            throw new RequestAuthenticationException("Unable to compute hash while signing request.", e);
        }
    }

    protected byte[] sign(String stringData, byte[] key, MacAlgorithm algorithm) throws RequestAuthenticationException {
        try {
            byte[] data = stringData.getBytes(DEFAULT_ENCODING);
            return sign(data, key, algorithm);
        } catch (Exception e) {
            throw new RequestAuthenticationException("Unable to calculate a request signature: " + e.getMessage(), e);
        }
    }

    protected byte[] sign(byte[] data, byte[] key, MacAlgorithm algorithm) throws RequestAuthenticationException {
        try {
            Mac mac = Mac.getInstance(algorithm.toString());
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RequestAuthenticationException("Unable to calculate a request signature: " + e.getMessage(), e);
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
                throw new RequestAuthenticationException("Unable to read request payload to authenticate request (mark not supported).");
            }

            content.mark(-1);

            //convert InputStream into a String in one shot:
            String string;
            try {
                string = new Scanner(content, "UTF-8").useDelimiter("\\A").next();
            } catch (NoSuchElementException nsee) {
                string = "";
            }
            //BAM!  That just happened.

            content.reset();

            return string;

        } catch (Exception e) {
            throw new RequestAuthenticationException("Unable to read request payload to authenticate request: " + e.getMessage(), e);
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
                for (String value : values) {
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
