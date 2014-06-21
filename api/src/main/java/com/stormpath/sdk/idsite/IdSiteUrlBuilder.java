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
 * Helps build a URL you can use to redirect your application users to a hosted login/registration/forgot-password site
 * - what Stormpath calls an 'Identity Site' (or 'ID Site' for short) - for performing common user identity
 * functionality.  When the user is done (logging in, registering, etc), they will be redirected back to a
 * {@link #setCallbackUri(String) callbackUri} of your choice.
 *
 * <h5>Retaining Application State</h5>
 *
 * <p>If you need to retain application state, such as the location in your UI the user is attempting to visit, and you
 * want that state presented back to you when the user returns from the ID Site, you can use the {@link
 * #setState(String) setState(aString)} method.  This state will be retained while the user is on the ID Site, and
 * the exact same state will be available when the user returns to your application's {@code callbackUri}.</p>
 *
 * <p>The {@code state} property, while sent over TLS/SSL and cryptographically signed, is guaranteed that it will not
 * be tampered with or manipulated in any way.  However, it itself is not additionally encrypted and might be
 * accessible
 * as a URL query parameter for anyone that can see the URL.  As such, it is recommended that you do not store
 * secure state in this value unless you encrypt it first.</p>
 *
 * @see #setCallbackUri(String)
 * @see #setPath(String)
 * @see #setState(String)
 * @see com.stormpath.sdk.application.Application#newIdSiteUrlBuilder()
 * @see com.stormpath.sdk.application.Application#newIdSiteCallbackHandler(Object)
 *
 * @since 1.0.RC2
 */
public interface IdSiteUrlBuilder {

    /**
     * Sets the location where the user will be sent when returning from the ID Site.  This property is mandatory and
     * must be set.  See the {@link com.stormpath.sdk.application.Application#newIdSiteCallbackHandler(Object)
     * application.newIdSiteCallbackHandler} documentation for information on how to process requests to your
     * {@code callbackUri}.
     *
     * <p>For security reasons, <b>this location must be registered in your ID Site configuration in the Stormpath
     * Admin Console</b>.</p>
     *
     * @param callbackUri the final destination where to get the browser redirected.
     * @return this instance for method chaining.
     */
    IdSiteUrlBuilder setCallbackUri(String callbackUri);

    /**
     * Sets application-specific state that should be retained and made available to your
     * {@link #setCallbackUri(String) callbackUri} when the user returns from the ID Site.
     *
     * @param state application-specific state that should be retained and made available to your
     *              {@link #setCallbackUri(String) callbackUri} when the user returns from the ID Site.
     * @return this instance for method chaining.
     */
    IdSiteUrlBuilder setState(String state);

    /**
     * Sets the initial path in the ID Site where the user should be sent. If unspecified, this defaults to
     * {@code /}, implying that the ID Site's landing/home page is the desired location.
     *
     * <h5>Example</h5>
     *
     * <p>Most Stormpath customers allow their ID Site's default landing page {@code /} to reflect a traditional
     * 'Login or Signup' page for convenience.  However, if you know that an end-user is attempting to register, and
     * your ID Site's user registration form is located at {@code /register}, you might want to call
     * {@code setPath(&quot;/&quot;)} to send the user directly to that view instead.</p>
     *
     * @param path the initial path in the ID Site where the user should be sent.
     * @return this instance for method chaining.
     */
    IdSiteUrlBuilder setPath(String path);

    /**
     * Builds and returns the the fully qualified URL representing the initial location in the ID Site where the
     * end-user should be redirected.
     *
     * @return the the fully qualified URL representing the initial location in the ID Site where the
     *         end-user should be redirected.
     */
    String build();
}
