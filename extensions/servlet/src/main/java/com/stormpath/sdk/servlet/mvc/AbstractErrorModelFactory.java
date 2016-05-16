package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.0
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
            return translateResourceException(request, (ResourceException) e.getCause());
        } else if (e instanceof ResourceException) {
            return translateResourceException(request, (ResourceException) e);
        } else if (e instanceof ValidationException) {
            errorMsg = e.getMessage();
        }
        return ErrorModel.builder()
                .setStatus(status)
                .setMessage(errorMsg).build();
    }

    private ErrorModel translateResourceException(HttpServletRequest request, ResourceException e) {
        return new ErrorModel.Builder()
                .setStatus(e.getStormpathError().getStatus())
                .setMessage(getErrorMessage(request, "stormpath.web.errors." + e.getStormpathError().getCode()))
                .build();
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
