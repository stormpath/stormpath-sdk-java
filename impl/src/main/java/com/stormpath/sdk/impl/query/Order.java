package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.impl.util.Assert;
import com.stormpath.sdk.impl.util.StringUtils;

import java.io.Serializable;

/**
 * @since 1.3
 */
public class Order implements Serializable {

    private final String propertyName;
    private final boolean ascending;

    public Order(String propertyName) {
        this(propertyName, true); //ascending by default.
    }

    public Order(String propertyName, boolean ascending) {
        Assert.isTrue(StringUtils.hasText(propertyName), "propertyName cannot be null or empty.");
        this.propertyName = propertyName;
        this.ascending = ascending;
    }

    /**
     * Ascending order.
     *
     * @param propertyName the name of the property to use to sort results in ascending order.
     * @return a new ascending Order instance.
     */
    public static Order asc(String propertyName) {
        return new Order(propertyName, true);
    }

    /**
     * Descending order.
     *
     * @param propertyName the name of the property to use to sort results in descending order.
     * @return a new descending Order instance
     */
    public static Order desc(String propertyName) {
        return new Order(propertyName, false);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isAscending() {
        return ascending;
    }

    @Override
    public String toString() {
        return this.propertyName + ' ' + (this.isAscending() ? "asc" : "desc");
    }
}
