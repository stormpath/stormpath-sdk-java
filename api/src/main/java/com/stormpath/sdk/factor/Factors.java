package com.stormpath.sdk.factor;


// todo: mehrshad

import com.stormpath.sdk.factor.sms.CreateSmsFactorRequestBuilder;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.factor.sms.SmsFactorCriteria;
import com.stormpath.sdk.factor.sms.SmsFactorOptions;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import com.stormpath.sdk.query.EqualsExpressionFactory;

import java.lang.reflect.Constructor;

public final class Factors {

    public static final SmsFactors SMS = new SmsFactors();

    //prevent instantiation
    private Factors() {
    }


    public static final class SmsFactors {

        private static final Class<CreateSmsFactorRequestBuilder> BUILDER_CLASS =
                Classes.forName("com.stormpath.sdk.impl.factor.sms.DefaultCreateSmsFactorRequestBuilder");

        //prevent instantiation outside of outer class
        private SmsFactors() {
        }

        public static SmsFactorOptions<SmsFactorOptions> options() {
            return (SmsFactorOptions) Classes.newInstance("com.stormpath.sdk.impl.factor.sms.DefaultSmsFactorOptions");
        }

        public static SmsFactorCriteria criteria() {
            return (SmsFactorCriteria) Classes.newInstance("com.stormpath.sdk.impl.factor.sms.DefaultSmsFactorCriteria");
        }

        public static SmsFactorCriteria where(Criterion criterion) {
            return (SmsFactorCriteria) criteria().add(criterion);
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

}
