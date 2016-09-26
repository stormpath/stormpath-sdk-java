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
package com.stormpath.sdk.oauth;

/**
 * This {@link OAuthRequestAuthenticator} is in charge of executing a <a href="https://tools.ietf.org/html/rfc6749#section-1.3.4">client credentials</a>
 * authentication attempt.
 * <p>When this authenticator finally executes it will cause a <code>grant_type=client_credentials</p> request to be triggered to Stormpath.
 *
 * @since 1.1.0
 */
public interface OAuthClientCredentialsGrantRequestAuthenticator extends OAuthRequestAuthenticator<OAuthGrantRequestAuthenticationResult> {
}
