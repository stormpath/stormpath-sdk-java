/*
 * Copyright 2013 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.Options;

import java.util.*;

/**
 * @since 0.8
 */
public class DefaultCriteria<T extends Criteria<T>, O extends Options> implements Criteria<T>, Expandable {

    protected final List<Criterion> criterionEntries;
    protected final List<Order> orderEntries;
    protected final O options;
    protected Integer limit;
    protected Integer offset;
    private Map<String, String> customAttributes;

    protected int currentOrderIndex = -1; //used for order clause building

    protected DefaultCriteria(O options) {
        Assert.notNull(options, "options argument cannot be null.");
        Assert.isInstanceOf(Expandable.class, options, "options argument is expected to implement the " + Expandable.class.getName() + " interface.");
        this.options = options;
        this.criterionEntries = new ArrayList<Criterion>();
        this.orderEntries = new ArrayList<Order>();
        this.customAttributes = new HashMap<>();
    }

    public T add(Criterion criterion) {
        Assert.notNull(criterion, "criterion cannot be null.");
        this.criterionEntries.add(criterion);
        return (T) this;
    }

    public T and(Criterion criterion) {
        return add(criterion);
    }

    public T add(Order order) {
        Assert.notNull(order, "order cannot be null.");
        this.orderEntries.add(order);
        this.currentOrderIndex = this.orderEntries.size() - 1;
        return (T) this;
    }

    public T orderBy(Property property) {
        Order order = Order.asc(property.getName()); //ascending by default - they can call descending() to reverse
        return add(order);
    }

    private int ensureOrderIndex() {
        int i = this.currentOrderIndex;
        Assert.state(i >= 0, "There is no current orderBy clause to declare as ascending or descending!");
        return i;
    }

    public List<Expansion> getExpansions() {
        //we can do this assertion because of the isInstance guarantee in the constructor:
        assert this.options instanceof Expandable;
        return ((Expandable)this.options).getExpansions();
    }

    public T ascending() {
        return orderDirection(true);
    }

    public T descending() {
        return orderDirection(false);
    }

    private T orderDirection(boolean ascending) {
        int i = ensureOrderIndex();
        Order order = this.orderEntries.get(i);
        if (order.isAscending() != ascending) { //only swap out the Order statement if it is different:
            String name = order.getPropertyName();
            Order newOrder = ascending ? Order.asc(name) : Order.desc(name);
            this.orderEntries.set(i, newOrder);
        }
        return (T) this;
    }

    public List<Criterion> getCriterionEntries() {
        return Collections.unmodifiableList(this.criterionEntries);
    }

    public List<Order> getOrderEntries() {
        return Collections.unmodifiableList(this.orderEntries);
    }

    protected O getOptions() {
        return this.options;
    }

    public T limitTo(int limit) {
        this.limit = Pagination.sanitizeLimit(limit);
        return (T) this;
    }

    public Integer getLimit() {
        return limit;
    }

    public T offsetBy(int offset) {
        this.offset = Pagination.sanitizeOffset(offset);
        return (T) this;
    }

    public Integer getOffset() {
        return offset;
    }

    public boolean isEmpty() {
        return options.isEmpty() && criterionEntries.isEmpty() && orderEntries.isEmpty() && (offset == null || offset == 0) && (limit == null || limit == 0);
    }

    public boolean hasCustomAttributes() {
        return !this.customAttributes.isEmpty();
    }

    public Map<String, String> getCustomAttributes() {
        return this.customAttributes;
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

        if (!orderEntries.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("order by ").append(Strings.collectionToDelimitedString(orderEntries, ", "));
        }

        if (offset != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("offset ").append(offset);
        }

        if (limit != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("limit ").append(limit);
        }

        if (!options.isEmpty() && options instanceof Expandable) {
            Expandable expandable = (Expandable)options;
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("expand ").append(Strings.collectionToDelimitedString(expandable.getExpansions(), ", "));
        }

        if(hasCustomAttributes()){
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append("custom attributes: ").append(getCustomAttributes().toString());
        }

        return sb.toString();
    }

}
