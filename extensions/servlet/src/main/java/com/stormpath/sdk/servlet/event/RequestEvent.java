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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An event triggered while handling an {@link javax.servlet.http.HttpServletRequest HttpServletRequest}.
 *
 * @since 1.0.RC3
 */
public interface RequestEvent {

    /**
     * The request being processed.
     *
     * @return the request being processed.
     */
    HttpServletRequest getRequest();

    /**
     * The response corresponding to the {@link #getRequest() request}.
     *
     * @return response corresponding to the {@link #getRequest() request}.
     */
    HttpServletResponse getResponse();

    /**
     * Dispatch method that allows a listener to process this event in a type-safe manner.
     *
     * @param listener the listener that will process this event.
     */
    void accept(RequestEventListener listener);

}
