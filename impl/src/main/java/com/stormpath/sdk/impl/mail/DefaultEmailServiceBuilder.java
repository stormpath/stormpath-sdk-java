package com.stormpath.sdk.impl.mail;

import com.stormpath.sdk.impl.io.DefaultResourceFactory;
import com.stormpath.sdk.impl.mail.template.VelocityTemplateRenderer;
import com.stormpath.sdk.mail.EmailService;
import com.stormpath.sdk.mail.EmailServiceBuilder;
import com.stormpath.sdk.mail.config.EmailServiceConfig;
import com.stormpath.sdk.mail.templates.TemplateRenderer;

/**
 *
 */
public class DefaultEmailServiceBuilder implements EmailServiceBuilder {

    private TemplateRenderer templateRenderer = new VelocityTemplateRenderer();
    private EmailServiceConfig config = null;

    @Override
    public EmailServiceBuilder setTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
        return this;
    }

    @Override
    public EmailServiceBuilder setConfig(EmailServiceConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public EmailService build() {
        return new CommonsEmailService(config, templateRenderer, new DefaultResourceFactory());
    }
}
