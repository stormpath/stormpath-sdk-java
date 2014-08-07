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
package com.stormpath.sdk.idsite;

/**
 * Listener interface to get notifications about effective operations of the Id Site invocation:
 * registration, authentication or logout.
 * <p/>
 * For usage, see {@link com.stormpath.sdk.idsite.IdSiteCallbackHandler#setResultListener(IdSiteResultListener)}
 *
 * @since 1.0.0
 */
public interface IdSiteResultListener {

    /**
     * This method will be invoked if a successful registration operation takes place on Id Site.
     */
    public void onRegistered();

    /**
     * This method will be invoked if a successful authentication operation takes place on Id Site.
     */
    public void onAuthenticated();

    /**
     * This method will be invoked if a successful logout operation takes place on Id Site.
     */
    public void onLogout();



}
