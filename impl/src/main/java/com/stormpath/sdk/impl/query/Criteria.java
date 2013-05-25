package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.impl.util.Assert;
import com.stormpath.sdk.impl.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.stormpath.sdk.impl.query.Pagination.*;

/**
 * @since 0.8
 */
public class Criteria {

    private final List<Criterion> criterionEntries;
    private final List<Order> orderEntries;
    private final List<Expansion> expansionEntries;
    private Integer limit;
    private Integer offset;

    public Criteria() {
        this.criterionEntries = new ArrayList<Criterion>();
        this.orderEntries = new ArrayList<Order>();
        this.expansionEntries = new ArrayList<Expansion>();
    }

    public Criteria add(Criterion criterion) {
        Assert.notNull(criterion, "criterion cannot be null.");
        this.criterionEntries.add(criterion);
        return this;
    }

    public Criteria add(Order order) {
        Assert.notNull(order, "order cannot be null.");
        this.orderEntries.add(order);
        return this;
    }

    public Criteria orderBy(String propertyName) {
        return orderBy(propertyName, true);
    }

    public Criteria orderBy(String propertyName, boolean ascending) {
        Assert.isTrue(StringUtils.hasText(propertyName), "orderBy propertyName cannot be null or empty");
        Order order = new Order(propertyName, ascending);
        return add(order);
    }

    public Criteria expand(String propertyName) {
        Assert.isTrue(StringUtils.hasText(propertyName), "Expansion propertyName cannot be null or empty");
        this.expansionEntries.add(new Expansion(propertyName));
        return this;
    }

    public Criteria expand(String propertyName, int limit) {
        return expand(propertyName, limit, DEFAULT_OFFSET.getValue());
    }

    public Criteria expand(String propertyName, int limit, int offset) {
        Assert.isTrue(StringUtils.hasText(propertyName), "Collection expansion propertyName cannot be null or empty");
        int sLimit = sanitizeLimit(limit);
        int sOffset = sanitizeOffset(offset);
        this.expansionEntries.add(new CollectionExpansion(propertyName, sLimit, sOffset));
        return this;
    }

    public List<Criterion> getCriterionEntries() {
        return Collections.unmodifiableList(this.criterionEntries);
    }

    public List<Order> getOrderEntries() {
        return Collections.unmodifiableList(this.orderEntries);
    }

    public List<Expansion> getExpansionEntries() {
        return Collections.unmodifiableList(this.expansionEntries);
    }

    public Integer getLimit() {
        return limit;
    }

    private int sanitizeLimit(int limit) {
        limit = Math.min(limit, MAX_LIMIT.getValue());
        limit = Math.max(limit, MIN_LIMIT.getValue());
        return limit;
    }

    private int sanitizeOffset(int offset) {
        return Math.max(0, offset);
    }

    public Criteria setLimit(int limit) {
        this.limit = sanitizeLimit(limit);
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public Criteria setOffset(int offset) {
        this.offset = Math.max(DEFAULT_OFFSET.getValue(), offset);
        return this;
    }

    public boolean isEmpty() {
        return this.criterionEntries.isEmpty() && this.orderEntries.isEmpty() && this.expansionEntries.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        for (Criterion c : criterionEntries) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append(c);
        }

        if (!expansionEntries.isEmpty()) {
            if (!(criterionEntries.isEmpty())) {
                sb.append(" ");
            }
            sb.append("expand ").append(StringUtils.collectionToCommaDelimitedString(this.expansionEntries));
        }

        if (!orderEntries.isEmpty()) {
            if (!criterionEntries.isEmpty() || !expansionEntries.isEmpty()) {
                sb.append(" ");
            }
            sb.append("order by ").append(StringUtils.collectionToDelimitedString(this.orderEntries, ", "));
        }

        return sb.toString();
    }

}
