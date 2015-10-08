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
     * Ensures the ID Site is customized for the {@code Organization} with the specified {@code organizationNameKey}.
     * This is useful for multi-tenant or white label scenarios where you know the user belongs to a specific
     * {@code Organization}.
     *
     * @param organizationNameKey unique identifier of the {@code Organization} to use when customizing ID Site.
     * @return this instance for method chaining.
     * @since 1.0.RC5
     */
    IdSiteUrlBuilder setOrganizationNameKey(String organizationNameKey);

    /**
     * Ensures that the user will visit ID Site using a subdomain equal to the
     * {@link #setOrganizationNameKey(String) organizationNameKey} instead of the standard base domain.
     * <p>
     * <h5>Example</h5>
     * <p>
     * <p>Assume your ID Site is located at the domain {@code id.myapp.com}.  If you specify an
     * {@link #setOrganizationNameKey(String) organizationNameKey} of {@code greatcustomer} and set
     * {@link #setUseSubdomain(boolean) useSubdomain} to {@code true}, the user will be sent to
     * {@code https://greatcustomer.id.myapp.com} instead, providing for a more customized white-labeled URL experience.
     *
     * @param useSubdomain {@code true} to ensure that the user will visit ID Site using a subdomain equal to the
     *                     {@link #setOrganizationNameKey(String) organizationNameKey}, {@code false} to ensure that
     *                     the standard ID Site domain.
     * @return this instance for method chaining.
     * @since 1.0.RC5
     */
    IdSiteUrlBuilder setUseSubdomain(boolean useSubdomain);

    /**
     * Ensures that the ID Site will show the {@link #setOrganizationNameKey(String) organizationNameKey} field to
     * the end-user in the ID Site user interface.
     * <p>
     * <p>Setting this to {@code true} allows the user to see the field and potentially change the value.  This might be
     * useful if users can have accounts in different organizations - it would allow the user to specify which
     * organization they want to login to as desired.</p>
     *
     * @param showOrganizationField {@code true} the ID Site will show the
     *                              {@link #setOrganizationNameKey(String) organizationNameKey} field to end-user in
     *                              the ID Site user interface, {@code false} otherwise.z
     * @since 1.0.RC5
     */
    IdSiteUrlBuilder setShowOrganizationField(boolean showOrganizationField);

    /**
     * Sets the {@code sp_token} property to be used during the initial sso call. The {@code sp_token} must correspond to
     * an actual password reset token and when successful the {@code IdSite} will be redirected to the IdSite.
     * <p>
     * <h5>Example</h5>
     * <p>
     * <p>Assume your ID Site is located at the domain {@code id.myapp.com}.  If you specify an
     * {@link #setSpToken(String) spToken} of {@code aSpToken} and set
     * {@link #setPath(String) path} to {@code #reset}, the user will be sent to
     * {@code https://id.myapp.com/#reset?jwt=....} to complete the reset password process in your {@code IdSite}.
     *
     * @param spToken a unique token used to reset a password.
     * @return this instance for method chaining.
     * @since 1.0.RC5
     */
    IdSiteUrlBuilder setSpToken(String spToken);

    /**
     * Convenience method to set any key value. This is important to decouple the server releases from the library
     * releases, when a new property is supported in the initial {@code IdSite} call, and there is no {@code setter} to
     * add such property during the initial request, this method can be used to add it.
     *
     * <p>
     * <h5>Example</h5>
     * <p>
     * <p>Assume the initial call for IdSite now supports a new property called {@code exp} (expire), since at the moment
     * there is no such method to set the new property, you can use this new convenience method to the key-value pair to
     * the request.
     * <p>
     * {@link #addProperty(String, Object) addProperty("exp", new Date())}, cal
     * <p>
     * Notes:
     * <p>
     * 1) Properties like "iat", "iss", and "iat" will be overriden when the {@code IdSiteUrl} is being built, to avoid
     * unknown conflicts when the {@code url} is being consumed by Stormpath.
     * 2) Prefer the setter methods over this one (when possible), since there is type safe guarantee via the setters.
     *
     * @param name  of the new property.
     * @param value of the new property.
     * @return this instance for method chaining.
     * @since 1.0.RC5
     */
    IdSiteUrlBuilder addProperty(String name, Object value);

    /**
     *
     * A user that has an open session and wants to logout from it will invoke this method when constructing the {@code callbackUri}.
     * <p/>
     * To execute this operation the application will create a signed request to the internal logout endpoint in Stormpath
     * (i.e. {@code /sso/logout}). If successfully executed, the user will be redirected to the {@code callbackUri} indicating
     * that the account has been properly logged out.
     * <p/>
     * When users logs out, their session with the ID Site is no longer valid for every application pertaining to this domain.
     * They will be required to log in again when trying to access any of those applications.
     * <p/>
     * Note that once this method is invoked, there is no way to modify it (i.e. undo) to make it point to the regular
     * SSO endpoint. To achieve that, a new {@link IdSiteUrlBuilder} will need to be constructed.
     *
     * @return this instance for method chaining.
     * @since 1.0.RC3
     */
    IdSiteUrlBuilder forLogout();

    /**
     * Builds and returns the the fully qualified URL representing the initial location in the ID Site where the
     * end-user should be redirected.
     *
     * @return the the fully qualified URL representing the initial location in the ID Site where the
     *         end-user should be redirected.
     */
    String build();
}
