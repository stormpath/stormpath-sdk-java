package com.stormpath.sdk.lang;

/**
 * Represents a predicate (boolean-valued function) of two arguments.  This is mostly for internal development needs
 * to allow for certain JDK 8 functionality in a JDK 7 environment.
 *
 * @param <T>
 * @param <U>
 * @since 1.0.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/BiPredicate.html">JDK 8 BiPredicate</a>
 */
public interface BiPredicate<T, U> {

    boolean test(T t, U u);
}
