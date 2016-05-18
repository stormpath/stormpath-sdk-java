package com.stormpath.sdk.servlet.mvc;

/**
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {
    public ValidationException() {
        super();
    }

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ValidationException(Throwable throwable) {
        super(throwable);
    }
}
