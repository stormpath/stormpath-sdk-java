package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.query.StringExpressionFactory;

/**
 * @since 0.8
 */
public class DefaultStringExpressionFactory implements StringExpressionFactory {

    private final String propertyName;

    public DefaultStringExpressionFactory(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Returns a new case-insensitive equals expression reflecting the specified value.
     *
     * @param value the value that should equal the property value (ignoring case).
     * @return a new case-insensitive equals expression reflecting the current property name and the specified value.
     */
    public SimpleExpression eqIgnoreCase(String value) {
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    /**
     * Returns a new case-insensitive like expression reflecting that the specified value
     * should be at the beginning of the matching property value.
     *
     * @param value the value that should be at the beginning of the property value.
     * @return a new case-insensitive like expression reflecting the current property name and that the specified value
     *         should be at the beginning of the corresponding property value.
     */
    public LikeExpression startsWithIgnoreCase(String value) {
        return new LikeExpression(propertyName, value, MatchLocation.BEGIN);
    }

    /**
     * a new case-insensitive like expression reflecting that the specified value
     * should be at the end of the corresponding property value.
     *
     * @param value the value that should be at the end of the property value.
     * @return a new case-insensitive like expression reflecting the current property name and that the specified value
     *         should be at the end of the corresponding property value.
     */
    public LikeExpression endsWithIgnoreCase(String value) {
        return new LikeExpression(propertyName, value, MatchLocation.END);
    }

    /**
     * Returns a new case-insensitive like expression reflecting the property and the specified value.
     * The 'i' prefix to the method name indicates case-insensitivity.
     *
     * @param value the value that should be contained anywhere in the property value.
     * @return a new case-insensitive like expression reflecting the current property name and the specified value.
     */
    public LikeExpression containsIgnoreCase(String value) {
        return new LikeExpression(propertyName, value);
    }
}
