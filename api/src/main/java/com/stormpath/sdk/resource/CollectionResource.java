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
package com.stormpath.sdk.resource;

/**
 * A {@code CollectionResource} is a first-class {@link Resource} that has its own properties (such as
 * {@link #getOffset() offset} and {@link #getLimit() limit}, but also contains other {@link Resource} instances.
 *
 * @since 0.1
 */
public interface CollectionResource<T extends Resource> extends Resource, Iterable<T> {

    /**
     * Returns the index of the resource in the entire collection that is the first resource in the current page.
     * <p/>
     * All subsequent resources in the response page will immediately follow this index.
     * <p/>
     * For example, a paged query with an offset of 50 will return a page of results where the page contains the
     * resource at index 50, then the resource at index 51, then at 52, 53, etc.
     * <p/>
     * If unspecified when retrieving the collection, this number defaults to {@code 0}, indicating that the results
     * should start at the first result in the overall set (i.e the first 'page').  The maximum number of elements
     * returned in a page is specified by the {@link #getLimit() limit}.
     *
     * @return the index of the resource in the entire collection that is the first resource in the current page.
     */
    public int getOffset();

    /**
     * Returns the maximum number of results included in a single page.  If unspecified when retrieving the collection,
     * this number defaults to {@code 25}.  The minimum number allowed is {@code 1}, the maximum is {@code 100}.
     *
     * @return the maximum number of results included in a single page. Min: 1, Max: 100.
     */
    public int getLimit();

    /**
     * Returns the total count (quantity) of all items across the entire collection. Pagination does not impact this value.
     * <p>The <code>size</code> property does not necessary express that all these items have been transferred from the backend.
     * This value indicates the total quantity of items that the list contains but, in order to avoid retrieving a large
     * amount of data at once from the backend, the list is actually paginated with the size specified by <code>limit</code>.
     * This means, that for example, if a list has a <code>size</code> of 100 and the <code>limit</code> is 25, the backend
     * will be contacted four times and 25 items will be retrieved each time.</p>
     *
     * @return the total quantity of items that this CollectionResource contains.
     * @since 1.0.RC4
     */
    public int getSize();

}
