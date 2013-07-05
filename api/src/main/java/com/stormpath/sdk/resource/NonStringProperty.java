package com.stormpath.sdk.resource;

import com.stormpath.sdk.query.Operator;
import com.stormpath.sdk.query.SimpleExpression;

/**
 * @since 0.9
 */
public abstract class NonStringProperty<T> extends Property<T> {

    protected NonStringProperty(String name, Class<T> type, boolean required) {
        super(name, type, required);
    }

    /**
     * Returns a new equals expression reflecting the property name and the specified value.
     *
     * @param value the value that should equal the property value.
     * @return a new equals expression reflecting the property name and the specified value.
     */
    public SimpleExpression eq(Object value) {
        return new SimpleExpression(this.name, value, Operator.EQUALS);
    }
}
