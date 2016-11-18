/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.saml;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

//todo: saml javadoc
public class SamlServiceProviderRegistrations {
    private static final Class<CreateSamlServiceProviderRegistrationRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.saml.DefaultCreateSamlServiceProviderRegistrationRequestBuilder");

    //prevent instantiation
    private SamlServiceProviderRegistrations() {
    }


    public static SamlServiceProviderRegistrationOptions<SamlServiceProviderRegistrationOptions> options() {
        return (SamlServiceProviderRegistrationOptions) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultSamlServiceProviderRegistrationOptions");
    }


    public static SamlServiceProviderRegistrationCriteria criteria() {
        return (SamlServiceProviderRegistrationCriteria) Classes.newInstance("com.stormpath.sdk.impl.saml.DefaultSamlServiceProviderRegistrationCriteria");
    }


    public static SamlServiceProviderRegistrationCriteria where(Criterion criterion) {
        return criteria().add(criterion);
    }

    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    public static EqualsExpressionFactory defaultRelayState() {
        return newEqualsExpressionFactory("defaultRelayState");
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


    public static CreateSamlServiceProviderRegistrationRequestBuilder newCreateRequestFor(SamlServiceProviderRegistration samlServiceProviderRegistration) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, SamlServiceProviderRegistration.class);
        return (CreateSamlServiceProviderRegistrationRequestBuilder) Classes.instantiate(ctor, samlServiceProviderRegistration);
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
