package com.stormpath.sdk.mail.config;

/**
 */
public interface EmailServiceConfig {

    int getTokenExpirationHours();

    String getValidationTemplateConfig();

    String getResetPasswordTemplateConfig();

    String getHostName();

    int getPort();

    boolean isSSL();

    boolean isSSLCheckServerIdentity();

    boolean isTLS();

    String getUsername();

    String getPassword();

}
