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
package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.lang.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DefaultConfig implements Config {

    private final Map<String, String> props;

    public DefaultConfig(Map<String, String> props) {
        Assert.notNull(props, "Properties argument cannot be null.");
        this.props = Collections.unmodifiableMap(props);
    }

    @Override
    public String getLoginUrl() {
        return props.get(LOGIN_URL);
    }

    @Override
    public String getLoginNextUrl() {
        return props.get(LOGIN_NEXT_URL);
    }

    @Override
    public String getLogoutUrl() {
        return props.get(LOGOUT_URL);
    }

    @Override
    public String getLogoutNextUrl() {
        return props.get(LOGOUT_NEXT_URL);
    }

    @Override
    public String getRegisterUrl() {
        return props.get(REGISTER_URL);
    }

    @Override
    public String getRegisterNextUrl() {
        return props.get(REGISTER_NEXT_URL);
    }

    @Override
    public String getVerifyUrl() {
        return props.get(VERIFY_URL);
    }

    @Override
    public String getVerifyNextUrl() {
        return props.get(VERIFY_NEXT_URL);
    }

    @Override
    public int size() {
        return props.size();
    }

    @Override
    public boolean isEmpty() {
        return props.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return props.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return props.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return props.get(o);
    }

    @Override
    public String put(String s, String s2) {
        return props.put(s, s2);
    }

    @Override
    public String remove(Object o) {
        return props.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        props.putAll(map);
    }

    @Override
    public void clear() {
        props.clear();
    }

    @Override
    public Set<String> keySet() {
        return props.keySet();
    }

    @Override
    public Collection<String> values() {
        return props.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return props.entrySet();
    }
}
