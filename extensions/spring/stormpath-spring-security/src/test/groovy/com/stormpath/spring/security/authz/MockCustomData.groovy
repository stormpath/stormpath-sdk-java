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
package com.stormpath.spring.security.authz

import com.stormpath.sdk.directory.CustomData

class MockCustomData implements CustomData {

    private final Date CREATED_AT;
    private transient Date modifiedAt;
    private final Map<String,Object> FIELDS;
    String href;

    public MockCustomData() {
        CREATED_AT = new Date()
        FIELDS = [:]
    }

    @Override
    Date getCreatedAt() {
        return CREATED_AT
    }

    @Override
    Date getModifiedAt() {
        return modifiedAt
    }

    @Override
    void delete() {
        touch()
    }

    @Override
    int size() {
        return FIELDS.size();
    }

    @Override
    boolean isEmpty() {
        return FIELDS.isEmpty()
    }

    @Override
    boolean containsKey(Object o) {
        return FIELDS.containsKey(o)
    }

    @Override
    boolean containsValue(Object o) {
        return FIELDS.containsValue(o)
    }

    @Override
    Object get(Object o) {
        return FIELDS.get(o)
    }

    @Override
    Object put(String k, Object v) {
        return FIELDS.put(k,v)
    }

    @Override
    Object remove(Object o) {
        return FIELDS.remove(o)
    }

    @Override
    void putAll(Map<? extends String, ?> map) {
        FIELDS.putAll(map)
    }

    @Override
    void clear() {
        FIELDS.clear()
    }

    @Override
    Set<String> keySet() {
        return FIELDS.keySet()
    }

    @Override
    Collection<Object> values() {
        return FIELDS.values()
    }

    @Override
    Set<Map.Entry<String, Object>> entrySet() {
        return FIELDS.entrySet()
    }

    @Override
    String getHref() {
        return href;
    }

    @Override
    void save() {
        touch()
    }

    private void touch() {
        modifiedAt = new Date()
    }
}
