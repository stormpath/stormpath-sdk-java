package com.stormpath.spring.mvc;

import com.stormpath.sdk.lang.Assert;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * 1.1.0
 */
public class SpringMessageSource implements com.stormpath.sdk.servlet.i18n.MessageSource {

    private final MessageSource delegate;

    public SpringMessageSource(MessageSource delegate) {
        Assert.notNull("delegate Spring MessageSource cannot be null.");
        this.delegate = delegate;
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return getMessage(key, locale, new Object[0]);
    }

    @Override
    public String getMessage(String key, String defaultMessage, Locale locale, Object... args) {
        try {
            return delegate.getMessage(key, args, locale);
        } catch (NoSuchMessageException e) {
            //Same behavior as com.stormpath.sdk.servlet.i18n.DefaultMessageSource
            return defaultMessage;
        }
    }

    @Override
    public String getMessage(String key, String defaultMessage, Locale locale) {
        try {
            return delegate.getMessage(key, new Object[0], locale);
        } catch (NoSuchMessageException e) {
            //Same behavior as com.stormpath.sdk.servlet.i18n.DefaultMessageSource
            return defaultMessage;
        }
    }

    @Override
    public String getMessage(String key, Locale locale, Object... args) {
        try {
            return delegate.getMessage(key, args, locale);
        } catch (NoSuchMessageException e) {
            //Same behavior as com.stormpath.sdk.servlet.i18n.DefaultMessageSource
            return '!' + key + '!';
        }
    }
}
