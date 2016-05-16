package com.stormpath.sdk.servlet.mvc;

/**
 * @since 1.0
 */
public class MismatchedPasswordException extends ValidationException {
    public MismatchedPasswordException() {
        super();
    }

    public MismatchedPasswordException(String s) {
        super(s);
    }

    public MismatchedPasswordException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MismatchedPasswordException(Throwable throwable) {
        super(throwable);
    }

    protected MismatchedPasswordException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
