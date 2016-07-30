package com.stormpath.sdk.servlet.i18n;

import com.stormpath.sdk.servlet.http.Resolver;

import java.util.Locale;

/**
 * @since 1.0.0
 */
public interface MessageContext {

    MessageSource getMessageSource();

    Resolver<Locale> getLocaleResolver();
}
