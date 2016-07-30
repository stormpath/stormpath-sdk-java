package com.stormpath.sdk.servlet.i18n;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.Resolver;

import java.util.Locale;

/**
 * @since 1.0.0
 */
public class DefaultMessageContext implements MessageContext {

    private final MessageSource messageSource;
    private final Resolver<Locale> localeResolver;

    public DefaultMessageContext(MessageSource messageSource, Resolver<Locale> localeResolver) {
        Assert.notNull(messageSource, "MessageSource cannot be null.");
        Assert.notNull(localeResolver, "Locale resolver cannot be null.");
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Override
    public Resolver<Locale> getLocaleResolver() {
        return localeResolver;
    }
}
