package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.FormFieldsFactory;

import java.util.Locale;

/**
 * @since 1.0.0
 */
public interface ControllerConfigResolver extends FormFieldsFactory {
    String getView();
    String getUri();
    String getNextUri();
    boolean isEnabled();

    String getControllerKey();
    MessageSource getMessageSource();
    Resolver<Locale> getLocaleResolver();
    CsrfTokenManager getCsrfTokenManager();

    Publisher<RequestEvent> getRequestEventPublisher();
}
