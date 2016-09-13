package com.stormpath.sdk.account;

/**
 * The {@code MatchingProperty} represents the property based on which
 * an account in the default Account Store can be automatically linked to
 * the current account (used during login), when AccountLinking is enabled
 * for the Application/Organization.
 * e.g. When the MatchingProperty is email, if an {@link Account} exists in the default
 * Account Store with the same email, as the current account(used during login)
 * then they will be automatically linked.
 *
 * This takes effect only if the AccountLinkingStatus is {@link AccountLinkingStatus#ENABLED ENABLED}
 * and when the current account (used during login) is not already linked to an {@link Account) in the
 * default Account Store of the Application/Organization.
 * When, AccountLinkingStatus is {@link AccountLinkingStatus#DISABLED DISABLED}, then the value
 * of MatchingProperty has no effect.
 *
 * @since 1.1.0
 * @see AccountLinkingPolicy
 */
public enum MatchingProperty {

    EMAIL("email");

    private final String value;

    private MatchingProperty(String value) {
        this.value = value;
    }

    public static MatchingProperty fromName(String name) {
        for (MatchingProperty matchingProperty : values()) {
            if (matchingProperty.name().equalsIgnoreCase(name)) {
                return matchingProperty;
            }
        }
        throw new IllegalArgumentException("No MatchingProperty named '" + name + "'");
    }

    @SuppressWarnings("UnusedDeclaration")
    public static MatchingProperty fromValue(String value) {
        for (MatchingProperty matchingProperty : values()) {

            if (matchingProperty.getValue().equalsIgnoreCase(value)) {
                return matchingProperty;
            }
        }
        throw new IllegalArgumentException("No MatchingProperty has value [" + value + "]");
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
