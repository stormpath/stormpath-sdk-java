package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.FormFieldsFactory;

import java.util.Locale;

/**
 * @since 1.0
 */
public interface ControllerConfigResolver extends FormFieldsFactory {
    String getView();
    String getUri();
    String getNextUri();
    boolean isEnable();
    MessageSource getMessageSource();
    Resolver<Locale> getLocaleResolver();
    CsrfTokenManager getCsrfTokenManager();
}
