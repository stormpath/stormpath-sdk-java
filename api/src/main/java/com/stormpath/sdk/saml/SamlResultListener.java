/*
 * Copyright 2015 Stormpath, Inc.
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

import com.stormpath.sdk.idsite.AuthenticationResult;
import com.stormpath.sdk.idsite.LogoutResult;

/**
 * Listener interface to get notifications about effective operations of the SAML IdP invocation:
 * authentication or logout.
 * <p/>
 * For usage, see {@link com.stormpath.sdk.saml.SamlCallbackHandler#setResultListener(SamlResultListener)}
 *
 * @since 1.0.RC8
 */
public interface SamlResultListener {

    /**
     * This method will be invoked if a successful authentication operation takes place on SAML IdP.
     *
     * @param result the {@link AuthenticationResult} containing data specific to this event.
     */
    public void onAuthenticated(AuthenticationResult result);

    /**
     * This method will be invoked if a successful logout operation takes place on SAML IdP.
     *
     * @param result the {@link LogoutResult} containing data specific to this event.
     */
    public void onLogout(LogoutResult result);
}
