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

import com.stormpath.sdk.authc.AuthenticationResult;

/**
 * Event that indicates an authentication attempt executed while handling an HttpServletRequest was successful.
 *
 * @since 1.0.RC3
 */
public interface SuccessfulAuthenticationRequestEvent extends AuthenticationRequestEvent {

    /**
     * The result of the successful authentication attempt.  If you need to perform logic based the <em>type</em> of
     * authentication that was executed, you can use an {@link com.stormpath.sdk.authc.AuthenticationResultVisitor
     * AuthenticationResultVisitor}, For example:
     *
     * <pre>
     * authenticationResult.accept(new {@link com.stormpath.sdk.authc.AuthenticationResultVisitorAdapter AuthenticationResultVisitorAdapter}() {
     *
     *     //override the methods of interest
     * };
     * </pre>
     *
     * @return result of the successful authentication attempt.
     * @see com.stormpath.sdk.authc.AuthenticationResultVisitorAdapter AuthenticationResultVisitorAdapter for a more complete code example.
     */
    AuthenticationResult getAuthenticationResult();

}
