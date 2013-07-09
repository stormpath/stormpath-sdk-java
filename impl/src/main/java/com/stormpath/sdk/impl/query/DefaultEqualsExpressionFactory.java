package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.query.EqualsExpressionFactory;

/**
 * @since 0.8
 */
public class DefaultEqualsExpressionFactory implements EqualsExpressionFactory {

    private final String propertyName;

    public DefaultEqualsExpressionFactory(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Returns a new equals expression reflecting the property name and the specified value.
     *
     * @param value the value that should equal the property value.
     * @return a new equals expression reflecting the property name and the specified value.
     */
    public SimpleExpression eq(Object value) {
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }
}
