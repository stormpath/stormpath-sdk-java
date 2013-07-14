/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.query;

/**
 * A Criteria instance represents one or more {@link Criterion} (conditions) that are used to customize query
 * results.
 *
 * @see com.stormpath.sdk.account.AccountCriteria AccountCriteria
 * @see com.stormpath.sdk.group.GroupCriteria GroupCriteria
 * @see com.stormpath.sdk.directory.DirectoryCriteria DirectoryCriteria
 * @see com.stormpath.sdk.application.ApplicationCriteria ApplicationCriteria
 * @since 0.8
 */
public interface Criteria<T extends Criteria<T>> {

    /**
     * Adds a {@link Criterion} to the total set of criteria (conditions), further restricting how the query executes.
     * <p/>
     * In practice, this is merely an alias for the {@link #and(Criterion)} method.
     *
     * @param c the criterion (condition) to add
     * @return the criteria instance for method chaining
     */
    T add(Criterion c);

    /**
     * Adds an 'and' condition (a conjunction) to the total set of criteria (conditions), further restricting how the
     * query executes.
     *
     * @param c the criterion (condition) to 'and' with any other existing criteria
     * @return the criteria instance for method chaining
     */
    T and(Criterion c);

    /**
     * Indicates the immediately preceding {@code orderBy} clause should indicate ascending order.  This is the default
     * for all {@code orderBy} clauses and is not required to be invoked.  For example, the following are functionally
     * equivalent:
     * <p/>
     * <pre>
     * ...orderByGivenName()
     *
     * ...orderByGivenName().ascending()
     * </pre>
     * <p/>
     * <b>Usage Note:</b> calling this method without first calling an {@code orderBy} method
     * immediately before this one will result in an {@link IllegalStateException}.  For example, this is valid:
     * <pre>
     * ...orderByGivenName().ascending()
     * </pre>
     * but <b>this is not valid</b>:
     * <pre>
     * ...and(name().containsIgnoreCase("foo").ascending()
     * </pre>
     * The second {@code ascending()} invocation did not immediately follow an {@code orderBy} clause.
     *
     * @return the criteria instance for method chaining
     * @throws IllegalStateException if this method is called without first calling an {@code orderBy} method
     *                               immediately before this one.
     */
    T ascending() throws IllegalStateException;

    /**
     * Indicates the immediately preceding {@code orderBy} clause should indicate descending order.  For example:
     * equivalent:
     * <p/>
     * <pre>
     * ...orderBySurname().descending()
     * </pre>
     * <p/>
     * <b>Usage Note:</b> calling this method without first calling an {@code orderBy} method
     * immediately before this one will result in an {@link IllegalStateException}.  For example, this is valid:
     * <pre>
     * ...orderBySurname().descending()
     * </pre>
     * but <b>this is not valid</b>:
     * <pre>
     * ...and(name().containsIgnoreCase("foo").descending()
     * </pre>
     * The second {@code descending()} invocation did not immediately follow an {@code orderBy} clause.
     *
     * @return the criteria instance for method chaining
     * @throws IllegalStateException if this method is called without first calling an {@code orderBy} method
     *                               immediately before this one.
     */
    T descending();

    /**
     * Sets the query's pagination offset: the index in the overall result set of matching resources that should be
     * considered the first item to include in the response 'page'.  All subsequent resources in the response page will
     * immediately follow this index.
     * <p/>
     * For example, a paged query with an offset of 50 will return a page of results where the page contains
     * resource at index 50, then the resource at index 51, then at 52, 53, etc.
     * <p/>
     * If unspecified, this number defaults to {@code 0}, indicating that the results should start at the first result
     * in the overall set (i.e the first 'page').  The maximum number of elements returned in a page is specified by
     * the {@link #limitTo(int)} method.
     *
     * @param offset the query's pagination offset: the index in the overall result set of matching resources that
     *               should be considered the first item to include in the response 'page'.
     * @return the criteria instance for method chaining
     * @see #limitTo(int)
     */
    T offsetBy(int offset);

    /**
     * Sets the query's page size limit: the maximum number of results to include in a single page.  If unspecified,
     * this number defaults to {@code 25}.  The minimum number allowed is {@code 1}, the maximum is {@code 100}.
     *
     * @param limit the query's page size limit: the maximum number of results to include in a single page.
     * @return the criteria instance for method chaining
     * @see #offsetBy(int)
     */
    T limitTo(int limit);

    /**
     * Returns {@code true} if this instance does not yet reflect any criteria conditions or orderBy statements,
     * {@code false} otherwise.
     *
     * @return {@code true} if this instance does not yet reflect any criteria conditions or orderBy statements,
     *         {@code false} otherwise.
     */
    boolean isEmpty();
}
