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
package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.lang.Assert;

/**
 * A collection-specific {@link Expansion} that adds pagination capabilities.
 *
 * @since 0.8
 */
public class CollectionExpansion extends Expansion {

    private final int limit;
    private final int offset;

    public CollectionExpansion(String name, int limit, int offset) {
        super(name);
        Assert.isTrue(limit > 0 || offset > 0, "Either a limit or offset (or both) must be specified.");
        this.limit = Pagination.sanitizeLimit(limit);
        this.offset = Pagination.sanitizeOffset(offset);
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(getName());
        if (limit > 0 || offset > 0) {
            sb.append("(");
            if (offset > 0) {
                sb.append("offset:").append(offset);
            }
            if (limit > 0) {
                if (offset > 0) {
                    sb.append(",");
                }
                sb.append("limit:").append(limit);
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
