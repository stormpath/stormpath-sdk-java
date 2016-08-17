package com.stormpath.sdk.lang;

/**
 * Represents a function that accepts one argument and produces a result.
 * <p>
 * <p>This is a functional interface whose functional method is {@link #apply(Object)}.</p>
 * <p>
 * <p>This is mostly for internal development needs to allow for certain JDK 8 functionality in a JDK 7 environment.</p>
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html">JDK 8 Function</a>
 * @since 1.0.0
 */
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);

}