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

/**
 * @since 0.8
 */
public enum Pagination {

    DEFAULT_LIMIT(25),
    DEFAULT_OFFSET(0),
    MAX_LIMIT(100),
    MIN_LIMIT(1);

    private int value;

    private Pagination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static int sanitizeLimit(int limit) {
        limit = Math.min(limit, MAX_LIMIT.getValue());
        limit = Math.max(limit, MIN_LIMIT.getValue());
        return limit;
    }

    static int sanitizeOffset(int offset) {
        offset = Math.min(offset, Integer.MAX_VALUE);
        offset = Math.max(offset, DEFAULT_OFFSET.getValue());
        return offset;
    }
}
