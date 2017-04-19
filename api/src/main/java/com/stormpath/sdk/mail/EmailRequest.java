package com.stormpath.sdk.mail;

/**
 */
public interface EmailRequest {

    String getToAddress();
    EmailRequest setToAddress(String email);

    String getToDisplayName();
    EmailRequest setToDisplayName(String displayName);

    String getToken();
    EmailRequest setToken(String token);

}
