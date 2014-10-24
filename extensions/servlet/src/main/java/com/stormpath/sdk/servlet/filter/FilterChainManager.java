/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.util.List;
import java.util.Set;

/**
 * A {@code FilterChainManager} manages the creation and modification of {@link javax.servlet.Filter} chains from an
 * available pool of named {@link javax.servlet.Filter} instances.
 *
 * @since 1.0
 */
public interface FilterChainManager {

    /**
     * Returns {@code true} if one or more configured chains are available, {@code false} if none are configured.
     *
     * @return {@code true} if one or more configured chains are available, {@code false} if none are configured.
     */
    boolean hasChains();

    /**
     * Returns the names of all configured chains or an empty {@code Set} if no chains have been configured.
     *
     * @return the names of all configured chains or an empty {@code Set} if no chains have been configured.
     */
    Set<String> getChainNames();

    List<Filter> getChain(String name);

    /**
     * Proxies the specified {@code original} FilterChain with the named chain.  The returned
     * {@code FilterChain} instance will first execute the configured named chain and then lastly invoke the given
     * {@code original} chain.
     *
     * @param original  the original FilterChain to proxy
     * @param chainName the name of the internal configured filter chain that should 'sit in front' of the specified
     *                  original chain.
     * @return a {@code FilterChain} instance that will execute the named chain and then finally the
     *         specified {@code original} FilterChain instance.
     * @throws IllegalArgumentException if there is no configured chain with the given {@code chainName}.
     */
    FilterChain proxy(FilterChain original, String chainName);

    /**
     * Creates a filter chain for the given {@code chainName} with the specified {@code chainDefinition}
     * String.
     * <h3>Conventional Use</h3>
     * Because the {@code FilterChainManager} interface does not impose any restrictions on filter chain names,
     * (it expects only Strings), a convenient convention is to make the chain name an actual URL path expression
     * (such as an {@link com.stormpath.sdk.servlet.util.AntPathMatcher Ant path expression}).  For example:
     * <p/>
     * <code>createChain(<b><em>path_expression</em></b>, <em>path_specific_filter_chain_definition</em>);</code>
     * This convention can be used by a {@link FilterChainResolver} to inspect request URL paths
     * against the chain name (path) and, if a match is found, return the corresponding chain for runtime filtering.
     * <h3>Chain Definition Format</h3>
     * The {@code chainDefinition} method argument is expected to conform to the following format:
     * <pre>
     * filter1[optional_config1], filter2[optional_config2], ..., filterN[optional_configN]</pre>
     * where
     * <ol>
     * <li>{@code filterN} is the name of a {@link com.stormpath.sdk.servlet.filter.DefaultFilter DefaultFilter} and</li>
     * <li>{@code [optional_configN]} is an optional bracketed string that has meaning for that particular filter for
     * <em>this particular chain</em></li>
     * </ol>
     * If the filter does not need specific config for that chain name/URL path,
     * you may discard the brackets - that is, {@code filterN[]} just becomes {@code filterN}.
     * <p/>
     * And because this method does create a chain, remember that order matters!  The comma-delimited filter tokens in
     * the {@code chainDefinition} specify the chain's execution order.
     * <h3>Examples</h3>
     * <pre>/account/** = authc</pre>
     * This example says &quot;Create a filter named '{@code /account/**}' consisting of only the '{@code authc}'
     * filter&quot;.  Also because the {@code authc} filter does not need any path-specific
     * config, it doesn't have any config brackets {@code []}.
     * <p/>
     * <pre>/users/** = authc, roles[admin], perms[user:edit]</pre>
     * This example by contrast uses the 'roles' and 'perms' filters which <em>do</em> use bracket notation.  This
     * definition says:
     * <p/>
     * Construct a filter chain named '{@code /users/**}' which
     * <ol>
     * <li>ensures the user is first authenticated ({@code authc}) then</li>
     * <li>ensures that user has the {@code admin} role, and then finally</li>
     * <li>ensures that they have the {@code user:edit} permission.</li>
     * </ol>
     *
     * @param chainName       the name to associate with the chain, conventionally a URL path pattern.
     * @param chainDefinition the string-formatted chain definition used to construct an actual chain instance.
     * @see FilterChainResolver
     * @see com.stormpath.sdk.servlet.util.AntPathMatcher AntPathMatcher
     */
    void createChain(String chainName, String chainDefinition) throws ServletException;
}
