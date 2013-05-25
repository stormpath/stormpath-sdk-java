package com.stormpath.sdk.impl.query;

/**
 * A collection-specific {@link Expansion} that adds pagination capabilities.
 *
 * @since 0.8
 */
public class CollectionExpansion extends Expansion {

    private final int limit;
    private final int offset;

    public CollectionExpansion(String name, int limit, int offset){
        super(name);
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}
