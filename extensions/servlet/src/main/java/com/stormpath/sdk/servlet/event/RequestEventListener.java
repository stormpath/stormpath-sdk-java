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
package com.stormpath.sdk.servlet.event;

import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;

/**
 * A {@code RequestEventListener} implementation can react to interest events triggered while handling an {@code
 * HttpServletRequest}.
 *
 * @since 1.0.RC3
 */
public interface RequestEventListener {

    /**
     * Called when an authentication attempt executed while handling an HttpServletRequest was successful.
     *
     * @param e event
     */
    void on(SuccessfulAuthenticationRequestEvent e);

    /**
     * Called when an authentication attempt executed while handling an HttpServletRequest has failed.
     *
     * @param e event
     */
    void on(FailedAuthenticationRequestEvent e);

    /**
     * Called when processing an HttpServletRequest that results in a newly registered {@link
     * com.stormpath.sdk.account.Account Account}.
     *
     * @param e event
     */
    void on(RegisteredAccountRequestEvent e);

    /**
     * Called when processing an HttpServletRequest that indicates an account's email address has been verified.
     *
     * @param e event
     */
    void on(VerifiedAccountRequestEvent e);

    /**
     * Called when a user account explicitly (manually) logs out of a web application.
     *
     * @param e event
     */
    void on(LogoutRequestEvent e);
}
