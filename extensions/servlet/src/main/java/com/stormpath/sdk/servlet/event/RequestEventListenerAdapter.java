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

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.account.event.VerifiedAccountRequestEvent;
import com.stormpath.sdk.servlet.authc.FailedAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.LogoutRequestEvent;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default adapter implementation of the {@link RequestEventListener} interface, to be used to subclass and override
 * only the methods necessary.
 *
 * <p>All method implementations simply log to {@code DEBUG}.</p>
 *
 * @since 1.0.RC3
 */
public class RequestEventListenerAdapter implements RequestEventListener {

    private static final Logger log = LoggerFactory.getLogger(RequestEventListenerAdapter.class);

    @Override
    public void on(SuccessfulAuthenticationRequestEvent e) {
        log.debug("Received successful authentication request event: {}", e);
    }

    @Override
    public void on(FailedAuthenticationRequestEvent e) {
        log.debug("Received failed authentication request event: {}", e);
    }

    @Override
    public void on(RegisteredAccountRequestEvent e) {
        log.debug("Received registered account request event: {}", e);
    }

    @Override
    public void on(VerifiedAccountRequestEvent e) {
        log.debug("Received verified account request event: {}", e);
    }

    @Override
    public void on(LogoutRequestEvent e) {
        log.debug("Received logout request event: {}", e);
    }
}
