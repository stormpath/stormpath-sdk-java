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

import com.stormpath.sdk.factor.Factor;
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
    private static final Map<String, Class> specifiedTypeToResolvedTypeMap = new HashMap<>(1);
    private static final String TYPE = "type";

    // There is no "DefaultFactor" and at this point the only supported cocrete Factor is SmsFactor
    // Add more types to the map as new Factors get introduced
    static{
        specifiedTypeToResolvedTypeMap.put("SMS",SmsFactor.class);
    }

    public SubtypeDispatchingResourceFactory(InternalDataStore dataStore){
        this.defaultResourceFactory = new DefaultResourceFactory(dataStore);
    }

    @Override
    public <T extends Resource> T instantiate(Class<T> clazz, Object... constructorArgs) {
        if(clazz.equals(Factor.class) && constructorArgs.length > 0){
            return (T) defaultResourceFactory.instantiate(specifiedTypeToResolvedTypeMap.get(((Map)constructorArgs[0]).get(TYPE)), constructorArgs);
        }
        else{
            return defaultResourceFactory.instantiate(clazz,constructorArgs);
        }
    }
}
