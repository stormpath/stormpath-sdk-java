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
package com.stormpath.sdk.servlet.authc;

import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.servlet.event.RequestEvent;

/**
 * An event that indicates an authentication attempt has occurred while handling an HttpServletRequest.  This is an
 * intermediate interface - there is no concrete AuthenticationRequest implementation.  Instead event listeners are
 * usually interested in the {@link com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent
 * SuccessfulAuthenticationRequestEvent} and {@link com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent
 * FailedAuthenticationRequestEvent} sub-interfaces.
 *
 * @see com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent
 * @see com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent
 * @since 1.0.RC3
 */
public interface AuthenticationRequestEvent extends RequestEvent {

    /**
     * Returns the {@link AuthenticationRequest} used during the authentication attempt.
     *
     * @return the {@link AuthenticationRequest} used during the authentication attempt.
     */
    AuthenticationRequest getAuthenticationRequest();

}
