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
package com.stormpath.sdk.impl.resource;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.stormpath.sdk.impl.resource.AbstractCollectionResource.*;

/**
 * @since 1.0.RC
 */
public class CollectionProperties extends LinkedHashMap<String, Object> {

    private CollectionProperties(Builder builder) {

        put(AbstractResource.HREF_PROP_NAME, builder.href);
        put(OFFSET.getName(), builder.offset);
        put(LIMIT.getName(), builder.limit);
        put(ITEMS_PROPERTY_NAME, builder.itemsMapList);
    }

    public static class Builder {

        private String href;
        private final List<Map<String, ?>> itemsMapList = new LinkedList<Map<String, ?>>();
        private int offset;
        private int limit;


        public Builder setHref(String href) {
            this.href = href;
            return this;
        }

        public Builder setItemsMap(Map<String, ?> itemsMap) {
            itemsMapList.add(itemsMap);
            return this;
        }

        public Builder setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public CollectionProperties build() {
            return new CollectionProperties(this);
        }
    }

}
