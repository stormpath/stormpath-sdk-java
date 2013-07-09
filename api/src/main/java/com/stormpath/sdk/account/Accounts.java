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
package com.stormpath.sdk.account;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

/**
 * @since 0.8
 */
public final class Accounts {

    public static AccountOptions options() {
        return (AccountOptions) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultAccountOptions");
    }

    public static AccountCriteria criteria() {
        return (AccountCriteria) Classes.newInstance("com.stormpath.sdk.impl.account.DefaultAccountCriteria");
    }

    public static AccountCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    public static StringExpressionFactory email() {
        return newStringExpressionFactory("email");
    }

    public static StringExpressionFactory username() {
        return newStringExpressionFactory("username");
    }

    public static StringExpressionFactory givenName() {
        return newStringExpressionFactory("givenName");
    }

    public static StringExpressionFactory middleName() {
        return newStringExpressionFactory("middleName");
    }

    public static StringExpressionFactory surname() {
        return newStringExpressionFactory("surname");
    }

    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    private static StringExpressionFactory newStringExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultStringExpressionFactory";
        return (StringExpressionFactory)Classes.newInstance(FQCN, propName);
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory)Classes.newInstance(FQCN, propName);
    }
}
