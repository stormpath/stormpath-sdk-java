/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.saml;

import java.util.Set;

/**
 * A rule that indicates how a SAML Attribute Statement should populate one or more Stormpath Account field values.  By
 * creating rules, you configure which SAML Attribute values should be copied to Stormpath Account field values.
 * <p>{@code AttributeStatementMappingRule}s are immutable.  If you want to change the mapping rules for Accounts in a
 * particular SAML Directory, you must remove and add new {@code AttributeStatementMappingRule} instances to the
 * Directory Provider's {@link AttributeStatementMappingRules} instance.
 * <h4>How does it work?</h4>
 * <p>Each {@code AttributeStatementMappingRule} has a {@code name} and a collection of Stormpath account
 * {@link #getAccountAttributes() attribute names}.</p>
 * <p>When a SAML Attribute Statement is encountered, every SAML Attribute with a matching name will have its value
 * copied to the corresponding specified Stormpath account fields.</p>
 * <h5>Example</h5>
 * <p>For example, assume that a mapping rule's {@code name} is {@code foo}, and the rule's
 * {@link #getAccountAttributes() accountAttributes} collection contains the Stormpath Account field names
 * of {@code givenName} and {@code surname}.</p>
 * <p>If an Attribute Statement named {@code foo} is encountered on login to have a value of {@code bar}, then
 * {@code bar} will automatically be copied to the specified Stormpath Account fields for that user.  This user would
 * then automatically have a {@code givenName} of {@code bar} and a {@code surname} of {@code bar}.</p>
 *
 * @since 1.0.RC8
 */
public interface AttributeStatementMappingRule {

    /**
     * Returns the SAML Attribute name, that when encountered, should have its value applied as Account field values.
     * When this name is encountered when processing a SAML Attribute Statement, its associated value will be set as the
     * value for all Stormpath Account field names specified in the
     * {@link #getAccountAttributes() accountAttributes} collection.
     *
     * @return SAML Attribute name, that when encountered, should have its value set on the
     * {@link #getAccountAttributes() specified} Account fields.
     */
    String getName();

    /**
     * Returns the format for the SAML Attribute identified by {@link #getName() getName()}.
     *
     * @return SAML format for the SAML Attribute identified by {@link #getName() getName()}.
     */
    String getNameFormat();

    /**
     * Returns the Stormpath account fields that should be updated when encountering {@link #getName() named}
     * SAML Attribute name.  If discovered, that SAML Attribute value will be set on all of the Stormpath account
     * fields named in this collection.
     *
     * @return the Stormpath account fields that should be updated when encountering {@link #getName() named}
     * SAML Attribute name.
     */
    Set<String> getAccountAttributes();
}
