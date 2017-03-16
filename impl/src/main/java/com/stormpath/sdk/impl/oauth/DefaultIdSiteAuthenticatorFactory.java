/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.application.DefaultApplication;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.oauth.IdSiteAuthenticator;
import com.stormpath.sdk.oauth.IdSiteAuthenticatorFactory;
import com.stormpath.sdk.resource.Resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @since 1.0.RC8.2
 */
public class DefaultIdSiteAuthenticatorFactory implements IdSiteAuthenticatorFactory {

    @Override
    public IdSiteAuthenticator forApplication(Application application) {

        if (application instanceof DefaultApplication) {
            return ((DefaultApplication) application).createIdSiteAuthenticator();
        }

        // FIXME: ugly ugly ugly
        try {
            Method dataStoreMethod = AbstractResource.class.getDeclaredMethod("getDataStore");
            dataStoreMethod.setAccessible(true);
            InternalDataStore internalDataStore = (InternalDataStore) dataStoreMethod.invoke(application);

            return new DefaultIdSiteAuthenticator(application, internalDataStore);

        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("Could not get access to Application's 'dataStore'", e);
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException("Could not get access to Application's 'dataStore'", e);
        } catch (InvocationTargetException e) {
            throw new UnsupportedOperationException("Could not get access to Application's 'dataStore'", e);
        }
    }
}