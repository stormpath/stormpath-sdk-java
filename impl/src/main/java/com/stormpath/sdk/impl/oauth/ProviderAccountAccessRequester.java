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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.*;

@Deprecated
public class ProviderAccountAccessRequester {

    private InternalDataStore dataStore;

    public ProviderAccountAccessRequester(InternalDataStore dataStore) {
        this.dataStore = dataStore;
    }

    //public ProviderAccountResult requestAccess(String parentHref, ProviderAccountAccessAsMario providerAccountAccessAsMario) {
    //public ProviderAccountResult requestAccess(String parentHref, ProviderAccountAccess providerAccountAccess) {
    public ProviderAccountResult requestAccess(String parentHref, ProviderAccountRequest request) {
        Assert.notNull(parentHref, "parentHref argument must be specified");
        Assert.notNull(request, "request argument cannot be null");
        Assert.notNull(request.getProviderData(), "request's providerData must be specified");
        //Assert.hasText(providerAccountAccessAsMario.getProviderId(), "providerAccountAccess's providerId must be specified");
        //Assert.notNull(request.getProviderData().containsKey("providerId"), "request's providerId must be specified");

        //String providerId = (String) request.getProviderData().get("providerId");
//
//        DefaultProviderAccountAccess providerAccountAccess = null;
//        ProviderData providerData = null;

        //Class<? extends ProviderData> providerDataClass = IdentityProviderType.fromNameKey(providerId).getProviderDataClass();

        //ProviderAccountAccess ProviderAccountAccess = IdentityProviderType.fromNameKey(providerId).getProviderAccountAccess()

        //return this.dataStore.getResource(getResourceProperty(PROVIDER).getHref(), Provider.class, "providerId", IdentityProviderType.IDENTITY_PROVIDER_CLASS_MAP);
//
//        this.dataStore.instantiate()
//
//        //this.dataStore.instantiate(GoogleData.class, request.getProviderData());
//
//        providerDataClass.getClass();
//        providerAccountAccess = new DefaultProviderAccountAccess<>(this.dataStore);
//
//        ProviderAccountAccess = this.dataStore.instantiate(ProviderAccountAccessAsMario.class);
//
        ProviderAccountAccess providerAccountAccess = new DefaultProviderAccountAccess(this.dataStore);
//
//        providerAccountAccess = new DefaultProviderAccountAccess<GoogleData>(this.dataStore);
//


//        BeanInfo info = null;
//        try {
//            info = Introspector.getBeanInfo(providerDataClass);
//            PropertyDescriptor[] pds = info.getPropertyDescriptors();
//        } catch (IntrospectionException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        //ProviderData providerData = this.dataStore.instantiate(providerDataClass, request.getProviderData());
        //request.getProviderData();

//
//        if(providerId.equals(GoogleProviderRequest.PROVIDER_ID)) {
//            providerAccountAccess = new DefaultProviderAccountAccess<GoogleData>(this.dataStore);
//            providerData = this.dataStore.instantiate(GoogleData.class, request.getProviderData());
//        } else if(providerId.equals(FacebookProviderRequest.PROVIDER_ID)) {
//            providerAccountAccess = new DefaultProviderAccountAccess<FacebookData>(this.dataStore);
//            providerData = this.dataStore.instantiate(FacebookData.class, request.getProviderData());
//        } else {
//            throw new IllegalStateException("providerId is not recognized: " + providerId);
//        }


        //providerData = this.dataStore.instantiate(FacebookData.class, request.getProviderData());

//        ProviderAccountAccess providerAccountAccess = new DefaultProviderAccountAccess(this.dataStore);
//        try {
//            Method method;
//            for(String key : request.getProviderData().keySet()) {
//                method = providerData.getClass().getMethod("set" + Strings.capitalize(key), String.class);
//                method.invoke(providerData, request.getProviderData().get(key));
//            }
//        } catch (SecurityException e) {
//            // ...
//        } catch (NoSuchMethodException e) {
//            // ...
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }


     providerAccountAccess.setProviderData(request.getProviderData());
    String href = parentHref + "/accounts";

    return this.dataStore.create(href, providerAccountAccess, ProviderAccountResult.class);

    //return this.dataStore.create(href, providerAccountAccessAsMario, ProviderAccountResult.class);
}

}
