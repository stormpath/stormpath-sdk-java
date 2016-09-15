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
package com.stormpath.sdk.phone;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

// todo: mehrshad

public final class Phones {

    private static final Class<CreatePhoneRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.phone.DefaultCreatePhoneRequestBuilder");

    //prevent instantiation
    private Phones() {
    }

    /**
     * Returns a new {@link PhoneOptions} instance, used to customize how one or more {@link Phone}s are retrieved.
     *
     * @return a new {@link PhoneOptions} instance, used to customize how one or more {@link Phone}s are retrieved.
     */
    public static PhoneOptions<PhoneOptions> options() {
        return (PhoneOptions) Classes.newInstance("com.stormpath.sdk.impl.phone.DefaultPhoneOptions");
    }

    public static PhoneCriteria criteria() {
        return (PhoneCriteria) Classes.newInstance("com.stormpath.sdk.impl.phone.DefaultPhoneCriteria");
    }

    public static PhoneCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }


    public static StringExpressionFactory name() {
        return newStringExpressionFactory("name");
    }

    public static StringExpressionFactory number() {
        return newStringExpressionFactory("number");
    }

    public static StringExpressionFactory description() {
        return newStringExpressionFactory("description");
    }

    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    public static EqualsExpressionFactory verificationStatus() {
        return newEqualsExpressionFactory("verificationStatus");
    }

    /*public static CreatePhoneRequestBuilder newCreateRequestFor(Phone phone) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Phone.class);
        return (CreatePhoneRequestBuilder) Classes.instantiate(ctor, phone);
    }*/


    private static StringExpressionFactory newStringExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultStringExpressionFactory";
        return (StringExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    private static EqualsExpressionFactory newEqualsExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultEqualsExpressionFactory";
        return (EqualsExpressionFactory) Classes.newInstance(FQCN, propName);
    }

    public static DateExpressionFactory createdAt(){
        return newDateExpressionFactory("createdAt");
    }

    public static DateExpressionFactory modifiedAt(){
        return newDateExpressionFactory("modifiedAt");
    }

    private static DateExpressionFactory newDateExpressionFactory(String propName) {
        final String FQCN = "com.stormpath.sdk.impl.query.DefaultDateExpressionFactory";
        return (DateExpressionFactory) Classes.newInstance(FQCN, propName);
    }

}
