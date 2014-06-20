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
 * Resolves an {@code ssoRequest} to obtain the {@link AccountResult}. This request is usually submitted
 * to the application's SSO response endpoint (e.g. {@code /sso/response}).
 * <p>
 * This interface reflects the <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> so
 * that the request to execute the {@code accountResult} process may be customized.
 * </p>
 * <p/>
 * <h3>Usage Example</h3>
 * <pre>
 * //assume a GET request to, say, https://mycompany.com/sso/response?jwtResponse=...;
 *
 * public void resolveAccountIdentity(HttpServletRequest request, HttpServletResponse response) {
 *
 *     Application application = client.getResource(myApplicationRestUrl, Application.class);
 *
 *    //if you want to control how the nonce are stored in your application.
 *    <b>NonceStore nonceStore = new MyNonceStore();</b> //create the 'MyNonceStore' class yourself
 *
 *     AccountResult result = application.handleIdSiteReply(request)
 *        <b>{@link #withNonceStore(NonceStore) .withNonceStore(nonceStore)}
 *        .execute()</b>;
 *
 *    //Put the account in the session.
 * }
 * </pre>
 *
 * @see com.stormpath.sdk.application.Application#handleIdSiteReply(Object)
 * @see #withNonceStore(NonceStore)
 * @see #execute()
 * @since 1.0.RC2
 */
public interface IdSiteAccountResolver {

    /**
     * Overrides the default implementation of the {@link NonceStore} to be used when resolving the {@code accountResult}
     * from this request.
     *
     * @param nonceStore - The {@link NonceStore} implementation to use during the process to execute this request.
     * @throws IllegalArgumentException when the {@code nonceStore} argument is {@code null}.
     */
    void withNonceStore(NonceStore nonceStore);

    /**
     * Executes this resolve identity request.
     *
     * @return - the resolved identity in the form of an {@link AccountResult}
     */
    AccountResult execute();
}
