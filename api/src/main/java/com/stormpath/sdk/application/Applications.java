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
package com.stormpath.sdk.application;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

/**
 * @since 0.8
 */
public final class Applications {

    @SuppressWarnings("unchecked")
    private static final Class<CreateApplicationRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.application.DefaultCreateApplicationRequestBuilder");

    public static ApplicationOptions options() {
        return (ApplicationOptions) Classes.newInstance("com.stormpath.sdk.impl.application.DefaultApplicationOptions");
    }

    public static ApplicationCriteria criteria() {
        return (ApplicationCriteria) Classes.newInstance("com.stormpath.sdk.impl.application.DefaultApplicationCriteria");
    }

    public static ApplicationCriteria where(Criterion criterion) {
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

    public static CreateApplicationRequestBuilder newCreateRequestFor(Application application) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Application.class);
        return (CreateApplicationRequestBuilder) Classes.instantiate(ctor, application);
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
