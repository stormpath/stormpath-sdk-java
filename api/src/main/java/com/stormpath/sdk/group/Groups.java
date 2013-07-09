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
package com.stormpath.sdk.group;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

/**
 * @since 0.8
 */
public final class Groups {

    //prevent instantiation
    private Groups() {
    }

    public static GroupOptions options() {
        return (GroupOptions) Classes.newInstance("com.stormpath.sdk.impl.group.DefaultGroupOptions");
    }

    public static GroupCriteria criteria() {
        return (GroupCriteria) Classes.newInstance("com.stormpath.sdk.impl.group.DefaultGroupCriteria");
    }

    public static GroupCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    public static StringExpressionFactory name() {
        return newStringExpressionFactory("name");
    }

    public static StringExpressionFactory description() {
        return newStringExpressionFactory("description");
    }

    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    private static StringExpressionFactory newStringExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultStringExpressionFactory";
        return (StringExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }

}
