/*
 * Copyright 2012 Stormpath, Inc.
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

import java.util.*;

/**
 * @since 0.2
 */
public abstract class AbstractCollectionResource<T extends Resource> extends AbstractResource implements CollectionResource<T> {

    private static final String OFFSET = "offset";
    private static final String LIMIT = "limit";
    private static final String ITEMS = "items";

    protected AbstractCollectionResource(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected AbstractCollectionResource(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    protected int getOffset() {
        return getIntProperty(OFFSET);

    }

    protected int getLimit() {
        return getIntProperty(LIMIT);
    }

    protected abstract Class<T> getItemType();

    @SuppressWarnings("unchecked")
    public Page<T> getCurrentPage() {

        Collection<T> items = Collections.emptyList();

        Object value = getProperty(ITEMS);

        if (value != null) {
            Collection c = null;
            if (value instanceof Map[]) {
                Map[] vals = (Map[]) value;
                if (vals.length > 0) {
                    c = Arrays.asList((Map[])vals);
                }
            } else if (value instanceof Collection) {
                Collection vals = (Collection)value;
                if (vals.size() > 0) {
                    c = vals;
                }
            }
            if (c != null) {
                items = toResourceList(c, getItemType());
            }
        }

        return new DefaultPage<T>(getOffset(), getLimit(), items);
    }


    @Override
    public Iterator<T> iterator() {
        //temporary, only until pagination is fully supported:
        return getCurrentPage().getItems().iterator();
    }

    private Collection<T> toResourceList(Collection vals, Class<T> itemType) {

        List<T> list = new ArrayList<T>(vals.size());

        for (Object o : vals) {
            Map<String,Object> properties = (Map<String,Object>)o;
            T resource = toResource(itemType, properties);
            list.add(resource);
        }

        return list;
    }

    protected T toResource(Class<T> resourceClass, Map<String, Object> properties) {
        return getDataStore().instantiate(resourceClass, properties);
    }

    private static class DefaultPage<T> implements Page<T> {

        private final int offset;
        private final int limit;
        private final Collection<T> items;

        DefaultPage(int offset, int limit, Collection<T> items) {
            this.offset = offset;
            this.limit = limit;
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
        public Collection<T> getItems() {
            return this.items;
        }
    }
}
