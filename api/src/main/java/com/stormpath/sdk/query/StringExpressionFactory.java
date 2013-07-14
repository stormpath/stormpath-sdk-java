package com.stormpath.sdk.query;

/**
 * An {@code StringExpressionFactory} creates String conditions (equals, startsWith, etc) for specific resource properties.
 *
 * @since 0.8
 */
public interface StringExpressionFactory {

    /**
     * Returns a new case-insensitive equals expression indicating the specified value should match the property value.
     *
     * @param value the value that should equal the property value (ignoring case).
     * @return a new case-insensitive equals expression reflecting the property name and the specified value.
     */
    Criterion eqIgnoreCase(String value);

    /**
     * Returns a new case-insensitive like expression indicating the specified value should match the beginning
     * of the property value.
     *
     * @param value the value that should be at the beginning of the property value.
     * @return a new case-insensitive like expression reflecting the property name and that the specified value
     *         should be at the beginning of the corresponding property value.
     */
    Criterion startsWithIgnoreCase(String value);

    /**
     * Returns a new case-insensitive like expression indicating that the specified value should match the end of the
     * property value.
     *
     * @param value the value that should be at the end of the property value.
     * @return a new case-insensitive like expression reflecting the property name and that the specified value
     *         should be at the end of the corresponding property value.
     */
    Criterion endsWithIgnoreCase(String value);

    /**
     * Returns a new case-insensitive like expression indicating the specified value should be contained anywhere in the
     * property value.
     *
     * @param value the value that should be contained anywhere in the property value.
     * @return a new case-insensitive like expression reflecting the property name and the specified value.
     */
    Criterion containsIgnoreCase(String value);
}
