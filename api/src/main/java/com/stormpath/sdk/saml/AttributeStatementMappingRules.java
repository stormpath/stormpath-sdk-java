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

import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

import java.util.Set;

/**
 * An ordered Set of all {@link AttributeStatementMappingRule}s that indicate how SAML Attribute Statements should
 * populate one or more Stormpath Account field values after a successful SAML login.
 *
 * <p>{@link AttributeStatementMappingRule} instances are immutable.  You therefore control which rules are applied
 * by manipulating this {@code AttributeStatementMappingRules} instance.</p>
 *
 * <p>Rules are evaluated in iteration order. Later rules can override values set by previous rules.</p>
 *
 * @since 1.0.RC8
 */
public interface AttributeStatementMappingRules extends Resource, Set<AttributeStatementMappingRule>, Saveable, Deletable{

    /**
     * Removes the {@link AttributeStatementMappingRule}(s) identified by {@code ruleNames}.
     *
     * @param ruleNames the name of the {@link AttributeStatementMappingRule}(s) to remove.
     *
     * @return  the {@code Set<AttributeStatementMappingRule>} after the "remove" operation is performed.
     */
    Set<AttributeStatementMappingRule> removeByName(String... ruleNames);

    /**
     * Specifies the Set of all {@link AttributeStatementMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @param attributeStatementMappingRules the set of {@link AttributeStatementMappingRule AttributeStatementMappingRule}s to build a SAML provider.
     *
     * @return this instance for method chaining.
     */
    void setItems(Set<AttributeStatementMappingRule> attributeStatementMappingRules);

    /**
     * Returns the Set of all {@link AttributeStatementMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     *
     * @return the Set of all {@link AttributeStatementMappingRule}s that indicate how SAML Attribute Statements should
     * populate one or more Stormpath Account field values after a successful SAML login.
     */
    Set<AttributeStatementMappingRule> getItems();
}
