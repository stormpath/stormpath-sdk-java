package com.stormpath.sdk.query;

/**
 * An {@code EqualsExpressionFactory} creates {@code equals} {@link Criterion} for specific resource properties.
 *
 * @since 0.8
 */
public interface EqualsExpressionFactory {

    /**
     * Returns a new equals expression reflecting a resource property name and the specified value.
     *
     * @param value the value that should equal the property value.
     * @return a new equals expression reflecting the property name and the specified value.
     */
    Criterion eq(Object value);
}
