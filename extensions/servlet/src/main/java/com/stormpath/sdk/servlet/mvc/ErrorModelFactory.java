package com.stormpath.sdk.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that defines how to convert a {@link java.lang.Exception} to a proper {@link com.stormpath.sdk.servlet.mvc.ErrorModel}
 *
 * @since 1.0.RC7
 */
public interface ErrorModelFactory {
    ErrorModel toError(HttpServletRequest request, Exception e);
}
