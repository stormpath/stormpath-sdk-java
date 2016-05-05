package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0
 */
public class DefaultLoginFormStatusResolver implements LoginFormStatusResolver {

    private MessageSource messageSource;
    private String verifyEmailUri;

    public DefaultLoginFormStatusResolver(MessageSource messageSource, String verifyEmailUri) {
        this.messageSource = messageSource;
        this.verifyEmailUri = verifyEmailUri;
    }

    @Override
    public String getStatusMessage(HttpServletRequest request, String status) {
        return messageSource.getMessage("stormpath.web.login.form.status." + status, request.getLocale(), verifyEmailUri);
    }
}
