/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.resource.CollectionResource;
import com.stormpath.sdk.resource.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @since 0.2
 */
public abstract class AbstractCollectionResource<T extends Resource> extends AbstractResource implements CollectionResource<T> {

    public static final IntegerProperty OFFSET = new IntegerProperty("offset");
    public static final IntegerProperty LIMIT = new IntegerProperty("limit");
    public static final IntegerProperty SIZE = new IntegerProperty("size");
    public static final String ITEMS_PROPERTY_NAME = "items";

    private final Map<String, Object> queryParams;

    private AtomicBoolean iterated = new AtomicBoolean();

    protected AbstractCollectionResource(InternalDataStore dataStore) {
        super(dataStore);
        this.queryParams = Collections.emptyMap();
    }

    protected AbstractCollectionResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        this.queryParams = Collections.emptyMap();
    }

    protected AbstractCollectionResource(InternalDataStore dataStore, Map<String, Object> properties, Map<String, Object> queryParams) {
        super(dataStore, properties);
        if (queryParams != null) {
            this.queryParams = queryParams;
        } else {
            this.queryParams = Collections.emptyMap();
        }
    }

    /**
     * Returns {@code true} if the specified data map represents a materialized collection resource data set, {@code
     * false} otherwise.
     *
     * @param props the data properties to test
     * @return {@code true} if the specified data map represents a materialized collection resource data set, {@code
     * false} otherwise.
     * @since 1.0.RC4.3
     */
    public static boolean isCollectionResource(Map<String,?> props) {
        return isMaterialized(props) && (props.get("items") instanceof Iterable);
    }

    @Override
    public int getOffset() {
        return getInt(OFFSET);
    }

    @Override
    public int getLimit() {
        return getInt(LIMIT);
    }

    @Override
    public int getSize() {
        return getInt(SIZE);
    }

    protected abstract Class<T> getItemType();

    @SuppressWarnings("unchecked")
    public Page<T> getCurrentPage() {

        Collection<T> items = Collections.emptyList();

        Object value = getProperty(ITEMS_PROPERTY_NAME);

        if (value != null) {
            Collection c = null;
            if (value instanceof Map[]) {
                Map[] vals = (Map[]) value;
                if (vals.length > 0) {
                    c = Arrays.asList((Map[]) vals);
                }
            } else if (value instanceof Collection) {
                Collection vals = (Collection) value;
                if (vals.size() > 0) {
                    c = vals;
                }
            }
            if (c != null && !c.isEmpty()) {
                //do a look ahead to see if resource conversion has already taken place:
                if (!getItemType().isInstance(c.iterator().next())) {
                    //need to convert the list of links to a list of unmaterialized Resources
                    items = toResourceList(c, getItemType());
                    //replace the existing list of links with the newly constructed list of Resources.  Don't dirty
                    //the instance - we're just swapping out a property that already exists for the materialized version.
                    setProperty(ITEMS_PROPERTY_NAME, items, false);
                } else {
                    //the collection has already been converted to Resources - use it directly:
                    items = c;
                }
            }
        }

        return new DefaultPage<T>(getOffset(), getLimit(), getSize(), items);
    }


    @Override
    public Iterator<T> iterator() {
        //added the new iterated atomic boolean to PaginatedIterator constructor to prevent issue when new collection
        //resources requested the first page twice.
        return new PaginatedIterator<T>(this, iterated.getAndSet(true));
    }

    private Collection<T> toResourceList(Collection vals, Class<T> itemType) {

        List<T> list = new ArrayList<T>(vals.size());

        for (Object o : vals) {
            Map<String, Object> properties = (Map<String, Object>) o;
            T resource = toResource(itemType, properties);
            list.add(resource);
        }

        return list;
    }

    protected T toResource(Class<T> resourceClass, Map<String, Object> properties) {
        return getDataStore().instantiate(resourceClass, properties);
    }

    private class PaginatedIterator<T extends Resource> implements Iterator<T> {

        private AbstractCollectionResource<T> resource;

        private Page<T> currentPage;
        private Iterator<T> currentPageIterator;
        private int currentItemIndex;

        private PaginatedIterator(AbstractCollectionResource<T> resource, boolean iterated) {

            if (iterated) {
                //We get a new resource in order to have different iterator instances: issue 62 (https://github.com/stormpath/stormpath-sdk-java/issues/62)
                this.resource = getDataStore().getResource(resource.getHref(), resource.getClass(), resource.queryParams);
                this.currentPage = this.resource.getCurrentPage();
            } else {
                this.resource = resource;
                this.currentPage = resource.getCurrentPage();
            }

            this.currentPageIterator = this.currentPage.getItems().iterator();
            this.currentItemIndex = 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean hasNext() {

            boolean hasNext = currentPageIterator.hasNext();

            int pageLimit = currentPage.getLimit();

            boolean exhaustedLimit = (currentItemIndex == pageLimit);

            if (!hasNext && exhaustedLimit) {

                //If we have already exhausted the whole collection size there is no need to contact the backend again: https://github.com/stormpath/stormpath-sdk-java/issues/161
                boolean exhaustedSize = ((currentPage.getOffset() + 1) * pageLimit == getSize());
                if (!exhaustedSize) {

                    //if we're done with the current page, and we've exhausted the page limit (i.e. we've read a
                    //full page), we will have to execute another request to check to see if another page exists.
                    //We can't 'trust' the current page iterator to know if more results exist on the server since it
                    //only represents a single page.

                    //query for the next page (move the offset up):
                    int offset = currentPage.getOffset() + pageLimit;

                    Map<String, Object> queryParams = new LinkedHashMap<String, Object>(resource.queryParams);
                    queryParams.put(OFFSET.getName(), offset);
                    queryParams.put(LIMIT.getName(), pageLimit);

                    AbstractCollectionResource nextResource =
                            getDataStore().getResource(resource.getHref(), resource.getClass(), queryParams);
                    Page<T> nextPage = nextResource.getCurrentPage();
                    Iterator<T> nextIterator = nextPage.getItems().iterator();

                    if (nextIterator.hasNext()) {
                        hasNext = true;
                        //update to reflect the new page:
                        this.resource = nextResource;
                        this.currentPage = nextPage;
                        this.currentPageIterator = nextIterator;
                        this.currentItemIndex = 0;
                    }
                }
            }

            return hasNext;
        }

        @Override
        public T next() {
            T item = currentPageIterator.next();
            currentItemIndex++;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported.");
        }
    }

    private static class DefaultPage<T> implements Page<T> {

        private final int offset;
        private final int limit;
        private final int size;
        private final Collection<T> items;

        DefaultPage(int offset, int limit, int size, Collection<T> items) {
            this.offset = offset;
            this.limit = limit;
            this.size = size;
            this.items = Collections.unmodifiableCollection(items);
        }

        @Override
        public int getOffset() {
            return this.offset;
        }

        @Override
        public int getLimit() {
            return this.limit;
        }

        @Override
        public int getSize() {
            return this.size;
        }

        @Override
        public Collection<T> getItems() {
            return this.items;
        }
    }
}
