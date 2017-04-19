package com.stormpath.sdk.mail;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.mail.config.EmailServiceConfig;
import com.stormpath.sdk.mail.templates.TemplateRenderer;

/**
 *
 */
public interface EmailServiceBuilder {

    EmailServiceBuilder INSTANCE = Classes.newInstance("com.stormpath.sdk.impl.mail.DefaultEmailServiceBuilder");

    EmailServiceBuilder setTemplateRenderer(TemplateRenderer templateRenderer);

    EmailServiceBuilder setConfig(EmailServiceConfig config);

    EmailService build();

}
