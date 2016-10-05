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
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenge;
import com.stormpath.sdk.challenge.sms.SmsChallenge;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.resource.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * This class wraps {@link DefaultResourceFactory} . Its sole purpose is to intercept the
 * {@link ResourceFactory#instantiate(Class, Object...)} method to inspect it for the type
 * {@link Factor} to instantiate the correct implementation based on the "type" field in the
 * resource's payload.
 * The background for such interception is to be able to instantiate the right implementation
 * class in cases there are more then one implementation for a given {@link Resource}
 *
 * @since 1.1.0
 */
public class SubtypeDispatchingResourceFactory implements ResourceFactory {

    private DefaultResourceFactory defaultResourceFactory;
    private static final Map<String, Class> specifiedFactorAttributeToResolvedTypeMap = new HashMap<>(2);
    private static final Map<String, Class> specifiedChallengeAttributeToResolvedTypeMap = new HashMap<>(2);
    private static final String TYPE = "type";
    private static final String MESSAGE = "message";
    private static final String NO_MESSAGE = "noMessage";
    private static final String HREF = "href";

    // There is no "DefaultFactor" or "DefaultChallenge" and at this point supported
    // Factor(s) are "DefaultSmsFactor" and "DefaultGoogleAuthenticatorFactor" and
    // supported Challenge(s) are "DefaultSmsChallenge" and "DefaultGoogleAuthenticatorChallenge"
    // Add more types to the map as new Factors get introduced.
    static{
        specifiedFactorAttributeToResolvedTypeMap.put("SMS",SmsFactor.class);
        specifiedFactorAttributeToResolvedTypeMap.put("GOOGLE-AUTHENTICATOR",GoogleAuthenticatorFactor.class);
        specifiedChallengeAttributeToResolvedTypeMap.put(MESSAGE, SmsChallenge.class);
        specifiedChallengeAttributeToResolvedTypeMap.put(NO_MESSAGE, GoogleAuthenticatorChallenge.class);
    }

    public SubtypeDispatchingResourceFactory(InternalDataStore dataStore){
        this.defaultResourceFactory = new DefaultResourceFactory(dataStore);
    }

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz, Object... constructorArgs) {
        if(clazz.equals(Factor.class) && constructorArgs.length > 0){
            if(((Map)constructorArgs[0]).get(TYPE) == null && ((Map)constructorArgs[0]).get(HREF) != null){
                throw new IllegalStateException("Unable to determine concrete Factor type since Factor is unmaterialized!");
            }
            return (T) defaultResourceFactory.instantiate(specifiedFactorAttributeToResolvedTypeMap.get(((Map)constructorArgs[0]).get(TYPE).toString().toUpperCase()), constructorArgs);
        }
        else if(clazz.equals(Challenge.class) && constructorArgs.length > 0){
            if(((Map)constructorArgs[0]).get(MESSAGE) != null){
                return (T) defaultResourceFactory.instantiate(specifiedChallengeAttributeToResolvedTypeMap.get(MESSAGE), constructorArgs);
            }else{
                return (T) defaultResourceFactory.instantiate(specifiedChallengeAttributeToResolvedTypeMap.get(NO_MESSAGE), constructorArgs);
            }
        }
        else{
            return defaultResourceFactory.instantiate(clazz,constructorArgs);
        }
    }

    // This method is needed to address the following scenario:
    // The implementation class of the interface based on the type of ITEMS within "DefaultFactorList" (Factor) is
    // required in order to retrieve the "PROPERTY_DESCRIPTORS" and gain access to a materialized resource (Phone)
    // Following code would trigger such scenario:
    // "account.getFactors(Factors.SMS.criteria().withPhone().orderByStatus().ascending())"
    // and this code would hit "WriteCacheFilter#getPropertyDescriptor(Class<T> clazz, String propertyName)" with "clazz" as
    // the interface with no direct default implementation (Factor)
    // The way this problem is addressed is by inspecting the materialized property name (Phone) in order to find the
    // concrete class of the nesting resource "DefaultSmsFactor"
    public static <T extends Resource> Class<T> getImplementationClass(Class<T> clazz, String propertyName) {
        if(clazz.equals(Factor.class)){
            if("phone".equals(propertyName)) {
                clazz = (Class<T>) SmsFactor.class;
            }
            else{
                clazz = (Class<T>) GoogleAuthenticatorFactor.class;
            }
        }
        if(clazz.equals(Challenge.class)){
            if("message".equals(propertyName)) {
                clazz = (Class<T>) SmsChallenge.class;
            }
            else{
                clazz = (Class<T>) GoogleAuthenticatorChallenge.class;
            }
        }
        if (clazz.isInterface()) {
            return DefaultResourceFactory.convertToImplClass(clazz);
        }
        return clazz;
    }
}
