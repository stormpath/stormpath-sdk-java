package com.stormpath.sdk.factor.sms;

import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;
import com.stormpath.sdk.query.StringExpressionFactory;

import java.lang.reflect.Constructor;

// todo: mehrshad

public final class SmsFactors {

    private static final Class<CreateSmsFactorRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.factor.sms.DefaultCreateSmsFactorRequestBuilder");

    //prevent instantiation
    private SmsFactors() {
    }

    public static SmsFactorOptions<SmsFactorOptions> options() {
        return (SmsFactorOptions) Classes.newInstance("com.stormpath.sdk.impl.factor.sms.DefaultSmsFactorOptions");
    }

    public static EqualsExpressionFactory status() {
        return newEqualsExpressionFactory("status");
    }

    public static EqualsExpressionFactory verificationStatus() {
        return newEqualsExpressionFactory("verificationStatus");
    }

    public static CreateSmsFactorRequestBuilder newCreateRequestFor(SmsFactor smsFactor) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, SmsFactor.class);
        return (CreateSmsFactorRequestBuilder) Classes.instantiate(ctor, smsFactor);
    }


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
