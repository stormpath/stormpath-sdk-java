package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0
 */
public abstract class AbstractErrorModelFactory implements ErrorModelFactory {
    protected MessageSource messageSource;

    protected abstract String getDefaultMessageKey();

    protected abstract Object[] getMessageParams();

    protected abstract boolean hasError(HttpServletRequest request, Exception e);

    public ErrorModel toError(HttpServletRequest request, Exception e) {
        if (!hasError(request, e)) {
            return null;
        }

        String errorMsg = getErrorMessage(request, getDefaultMessageKey());
        int status = 400;

        if (e.getCause() instanceof ResourceException) {
            Error stormpathError = ((ResourceException) e.getCause()).getStormpathError();

            status = stormpathError.getStatus();
            errorMsg = getErrorMessage(request, getErrorMessageKey(stormpathError.getCode()));
        }
        return new ErrorModel.Builder()
                .setStatus(status)
                .setMessage(errorMsg).build();
    }

    /**
     * Returns an i18n property key based on the actual Stormpath API error code
     *
     * @param code A Stormpath error code
     * @return A i18n property key
     */
    private String getErrorMessageKey(int code) {
        return "stormpath.web.errors." + code;
    }

    protected String getErrorMessage(HttpServletRequest request, String key) {
        String message = messageSource.getMessage(key, request.getLocale(), getMessageParams());

        //Key not found so use the default message key
        if (message.startsWith("!") && message.endsWith("!")) {
            return messageSource.getMessage(getDefaultMessageKey(), request.getLocale(), getMessageParams());
        }

        return message;
    }
}
