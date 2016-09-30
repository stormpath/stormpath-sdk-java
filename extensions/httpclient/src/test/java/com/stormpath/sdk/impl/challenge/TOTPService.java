/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class TOTPService {

    private static final Logger logger = LoggerFactory.getLogger(TOTPService.class);

    private static final String HMAC_HASH_FUNCTION = "HmacSHA1";
    private static final long TIME_STEP_SIZE_IN_MILLIS = TimeUnit.SECONDS.toMillis(30);
    private static final int CODE_DIGITS = 6;
    private static final int KEY_MODULUS = (int) Math.pow(10, CODE_DIGITS);

    public static String getTotpPassword(byte[] signingKey, long currentTime) {
        String code = String.valueOf(calculateCode(signingKey, getTimeWindowFromTime(currentTime)));

        while (code.length() < 6) {
            code = "0" + code;
        }

        return code;
    }

    public static boolean authorize(byte[] signingKey, int verificationCode, long time) {
        // Checking if the verification code is between the legal bounds.
        if (verificationCode <= 0 || verificationCode >= KEY_MODULUS) {
            return false;
        }

        // Checking the validation code using the current UNIX time.
        return checkCode(signingKey, verificationCode, time, 0);
    }

    private static long getTimeWindowFromTime(long time) {
        return time / TIME_STEP_SIZE_IN_MILLIS;
    }

    private static boolean checkCode(byte[] secret, long code, long timestamp, int window) {
        // convert unix time into a 30 second "window" as specified by the
        // TOTP specification. Using Google's default interval of 30 seconds.
        final long timeWindow = getTimeWindowFromTime(timestamp);

        // Calculating the verification code of the given key in each of the
        // time intervals and returning true if the provided code is equal to
        // one of them.
        for (int i = -((window - 1) / 2); i <= window / 2; ++i) {
            // Calculating the verification code for the current time interval.
            long hash = calculateCode(secret, timeWindow + i);

            // Checking if the provided code is equal to the calculated one.
            if (hash == code) {
                // The verification code is valid.
                return true;
            }
        }

        // The verification code is invalid.
        return false;
    }

    private static int calculateCode(byte[] key, long tm) {
        // Allocating an array of bytes to represent the specified instant
        // of time.
        byte[] data = new byte[8];
        long value = tm;

        // Converting the instant of time from the long representation to a
        // big-endian array of bytes (RFC4226, 5.2. Description).
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        // Building the secret key specification for the HmacSHA1 algorithm.
        SecretKeySpec signKey = new SecretKeySpec(key, HMAC_HASH_FUNCTION);

        try {
            // Getting an HmacSHA1 algorithm implementation from the JCE.
            Mac mac = Mac.getInstance(HMAC_HASH_FUNCTION);

            // Initializing the MAC algorithm.
            mac.init(signKey);

            // Processing the instant of time and getting the encrypted data.
            byte[] hash = mac.doFinal(data);

            // Building the validation code performing dynamic truncation
            // (RFC4226, 5.3. Generating an HOTP value)
            int offset = hash[hash.length - 1] & 0xF;

            // We are using a long because Java hasn't got an unsigned integer type
            // and we need 32 unsigned bits).
            long truncatedHash = 0;

            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;

                // Java bytes are signed but we need an unsigned integer:
                // cleaning off all but the LSB.
                truncatedHash |= (hash[offset + i] & 0xFF);
            }

            // Clean bits higher than the 32nd (inclusive) and calculate the
            // module with the maximum validation code value.
            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= KEY_MODULUS;

            // Returning the validation code to the caller.
            return (int) truncatedHash;
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            logger.error("There was an error generating the TOTP", ex);
            throw new IllegalStateException(ex);
        }
    }
}