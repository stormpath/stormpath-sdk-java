package com.stormpath.sdk.mail.config;

/**
 */
public class DefaultEmailServiceConfig implements EmailServiceConfig {

    private int tokenExpirationHours;
    private String validationTemplateConfig;
    private String resetPasswordTemplateConfig;

    private String hostname;
    private int port;
    private boolean ssl;
    private boolean sslCheckServerIdentity;
    private boolean tls;
    private String username;
    private String password;

    @Override
    public String getValidationTemplateConfig() {
        return validationTemplateConfig;
    }

    public DefaultEmailServiceConfig setValidationTemplateConfig(String validationTemplateConfig) {
        this.validationTemplateConfig = validationTemplateConfig;
        return this;
    }

    @Override
    public String getResetPasswordTemplateConfig() {
        return resetPasswordTemplateConfig;
    }

    public DefaultEmailServiceConfig setResetPasswordTemplateConfig(String resetPasswordTemplateConfig) {
        this.resetPasswordTemplateConfig = resetPasswordTemplateConfig;
        return this;
    }

    @Override
    public int getTokenExpirationHours() {
        return tokenExpirationHours;
    }

    public DefaultEmailServiceConfig setTokenExpirationHours(int tokenExpirationHours) {
        this.tokenExpirationHours = tokenExpirationHours;
        return this;
    }

    @Override
    public String getHostName() {
        return hostname;
    }

    public DefaultEmailServiceConfig setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    @Override
    public int getPort() {
        return port;
    }

    public DefaultEmailServiceConfig setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public boolean isSSL() {
        return ssl;
    }

    public DefaultEmailServiceConfig setSsl(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    @Override
    public boolean isSSLCheckServerIdentity() {
        return sslCheckServerIdentity;
    }

    public DefaultEmailServiceConfig setSslCheckServerIdentity(boolean sslCheckServerIdentity) {
        this.sslCheckServerIdentity = sslCheckServerIdentity;
        return this;
    }

    @Override
    public boolean isTLS() {
        return tls;
    }

    public DefaultEmailServiceConfig setTls(boolean tls) {
        this.tls = tls;
        return this;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public DefaultEmailServiceConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public DefaultEmailServiceConfig setPassword(String password) {
        this.password = password;
        return this;
    }
}
