package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.query.Options;
import com.stormpath.sdk.resource.ReferenceProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 0.9
 */
public class DefaultOptions<T extends Options> implements Options, Expandable {

    protected final List<Expansion> expansions;

    protected DefaultOptions() {
        this.expansions = new ArrayList<Expansion>();
    }

    protected T expand(ReferenceProperty property) {
        Assert.notNull(property, "property argument cannot be null.");
        this.expansions.add(new Expansion(property.getName()));
        return (T) this;
    }

    protected T expand(ReferenceProperty property, int limit) {
        return expand(property, limit, Pagination.DEFAULT_OFFSET.getValue());
    }

    protected T expand(ReferenceProperty property, int limit, int offset) {
        Assert.notNull(property, "property argument cannot be null.");
        Assert.state(property.isCollection(), "Only Collection properties can be expanded with a limit and/or offset.");
        int sLimit = Pagination.sanitizeLimit(limit);
        int sOffset = Pagination.sanitizeOffset(offset);
        this.expansions.add(new CollectionExpansion(property.getName(), sLimit, sOffset));
        return (T) this;
    }

    public List<Expansion> getExpansions() {
        return Collections.unmodifiableList(this.expansions);
    }

    public boolean isEmpty() {
        return this.expansions.isEmpty();
    }
}
