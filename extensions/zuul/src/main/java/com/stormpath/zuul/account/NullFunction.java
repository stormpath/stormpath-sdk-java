package com.stormpath.zuul.account;

import com.stormpath.sdk.lang.Function;

/**
 * A {@link Function} that always returns {@code null}.
 *
 * @since 1.1.0
 */
public class NullFunction<T, R> implements Function<T, R> {

    @Override
    public R apply(Object o) {
        return null;
    }
}
