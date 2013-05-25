package com.stormpath.sdk.impl.query;

/**
 * @since 0.8
 */
public enum Pagination {

    DEFAULT_LIMIT(25),
    DEFAULT_OFFSET(0),
    MAX_LIMIT(100),
    MIN_LIMIT(1);

    private int value;

    private Pagination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
