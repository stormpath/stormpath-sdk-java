package com.stormpath.sdk.query;

/**
 * @since 0.8
 */
public interface Criteria<T extends Criteria<T>> {

    T add(Criterion c);

    T and(Criterion c);

    T ascending();

    T descending();

    T offsetBy(int offset);

    T limitTo(int limit);

    boolean isEmpty();
}
