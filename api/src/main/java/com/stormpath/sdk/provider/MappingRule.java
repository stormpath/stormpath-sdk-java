package com.stormpath.sdk.provider;

import java.util.Set;

/**
 * @since 1.3.0
 */
public interface MappingRule {

    /**
     * Returns the attribute name for a Social Provider or SAML Provider, that when encountered, should have its value
     * applied as Account field values.
     * When this name is encountered when processing UserInfo from a Social Provider or SAML Attribute Statement,
     * its associated value will be set as the value for all Stormpath Account field names specified in the
     * {@link #getAccountAttributes() accountAttributes} collection.
     *
     * @return attribute name, that when encountered, should have its value set on the
     * {@link #getAccountAttributes() specified} Account fields.
     */
    String getName();

    /**
     * Returns the Stormpath account fields that should be updated when encountering {@link #getName() named}
     * attribute name.  If discovered, that attribute value will be set on all of the Stormpath account
     * fields named in this collection.
     *
     * @return the Stormpath account fields that should be updated when encountering {@link #getName() named}
     * attribute name.
     */
    Set<String> getAccountAttributes();
}
