package com.stormpath.sdk.servlet.mail;

import com.stormpath.sdk.impl.mail.DefaultEmailServiceBuilder;
import com.stormpath.sdk.impl.mail.template.VelocityTemplateRenderer;
import com.stormpath.sdk.mail.EmailService;
import com.stormpath.sdk.mail.config.DefaultEmailServiceConfig;
import com.stormpath.sdk.mail.config.EmailServiceConfig;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;

import javax.servlet.ServletContext;

/**
 */
public class EmailServiceFactory extends ConfigSingletonFactory<EmailService> {
    @Override
    protected EmailService createInstance(ServletContext servletContext) throws Exception {

        Config config = ConfigResolver.INSTANCE.getConfig(servletContext);

        EmailServiceConfig emailConfig = new DefaultEmailServiceConfig()
                    .setTokenExpirationHours(new Integer(config.get("stormpath.email.tokenExpirationHours")))
                    .setValidationTemplateConfig(config.get("stormpath.email.verifyEmailTemplate"))
                    .setResetPasswordTemplateConfig(config.get("stormpath.email.forgotPasswordTemplate"))
                    .setHostname(config.get("stormpath.email.hostname"))
                    .setPort(new Integer(config.get("stormpath.email.port")))
                    .setSsl(Boolean.valueOf(config.get("stormpath.email.sslEnabled")))
                    .setSslCheckServerIdentity(Boolean.valueOf(config.get("stormpath.email.sslCheckServerIdentityEnabled")))
                    .setTls(Boolean.valueOf(config.get("stormpath.email.tlsEnabled")))
                    .setUsername(config.get("stormpath.email.username"))
                    .setPassword(config.get("stormpath.email.password"));

        return new DefaultEmailServiceBuilder()
                    .setConfig(emailConfig)
                    .setTemplateRenderer(new VelocityTemplateRenderer())
                    .build();
    }
}
