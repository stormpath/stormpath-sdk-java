package com.stormpath.sdk.mail;

/**
 *
 */
public interface EmailService {

    void sendValidationEmail(EmailRequest request);

    void sendResetEmail(EmailRequest request);
}
