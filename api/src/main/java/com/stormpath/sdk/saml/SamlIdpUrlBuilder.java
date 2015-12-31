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

/**
 * A <a href="http://en.wikipedia.org/wiki/Builder_pattern">Builder design pattern</a> used to construct a URL you can
 * use to redirect application users to a SAML authentication site (Identity Provider or IdP) for performing common user
 * identity functionality.
 * When users are done (authenticating, logging out, etc), they will be redirected back to a {@code callbackUri} of
 * your choice.
 *
 * Example of building a basic Saml IdP Url:
 * <pre>
 *     String redirectUrl = <b>application.newSamlIdpUrlBuilder()
 *      .{@link SamlIdpUrlBuilder#setCallbackUri(String)} setCallbackUri}(callbackUri)
 *      .build()</b>;
 * </pre>
 *
 * @see com.stormpath.sdk.application.Application#newSamlIdpUrlBuilder()
 *
 * @since 1.0.RC8
 */
public interface SamlIdpUrlBuilder {

    /**
     * Sets the location where the user will be sent when returning from the SAML Identity Provider.  This property is mandatory and
     * must be set.  See the {@link com.stormpath.sdk.application.Application#newSamlCallbackHandler(Object)
     * application.newSamlCallbackHandler} documentation for information on how to process requests to your
     * {@code callbackUri}.
     *
     * @param callbackUri the final destination where to get the browser redirected.
     * @return this instance for method chaining.
     */
    SamlIdpUrlBuilder setCallbackUri(String callbackUri);

    /**
     * Sets application-specific state that should be retained and made available to your
     * {@link #setCallbackUri(String) callbackUri} when the user returns from the SAML Identity Provider.
     *
     * @param state application-specific state that should be retained and made available to your
     *              {@link #setCallbackUri(String) callbackUri} when the user returns from the SAML Identity Provider.
     * @return this instance for method chaining.
     */
    SamlIdpUrlBuilder setState(String state);

    /**
     * Sets the initial path in the SAML Identity Provider where the user should be sent. If unspecified, this defaults to
     * {@code /}, implying that the SAML Identity Provider's landing/home page is the desired location.
     *
     * @param path the initial path in the SAML Identity Provider where the user should be sent.
     * @return this instance for method chaining.
     */
    SamlIdpUrlBuilder setPath(String path);

    /**
     * Ensures the SAML Identity Provider is customized for the {@code Organization} with the specified {@code organizationNameKey}.
     * This is useful for multi-tenant or white label scenarios where you know the user belongs to a specific
     * {@code Organization}.
     *
     * @param organizationNameKey unique identifier of the {@code Organization} to use when customizing SAML Identity Provider.
     * @return this instance for method chaining.
     */
    SamlIdpUrlBuilder setOrganizationNameKey(String organizationNameKey);

    /**
     * Sets the {@code sp_token} property used by the SAML Identity Provider to complete an account password reset workflow. The
     * {@code sp_token} must correspond to an actual password reset token issued to one of the accounts of this
     * {@code application}. If {@code sp_token} property is present and valid, the user will be redirected to
     * the SAML Identity Provider URL (included the configured path, see {@link #setPath(String)}, with the {@code sp_token} embedded in
     * the signed JWT.
     *
     * <p>
     * <h5>Example</h5>
     * <p>
     * <p>Assume your SAML Identity Provider is located at the domain {@code id.myapp.com}.  If you specify a
     * {@link #setSpToken(String) spToken} of {@code aSpToken} and set
     * {@link #setPath(String) path} to {@code #reset}, the user will be sent to
     * {@code https://id.myapp.com/#reset?jwt=signedJwt}, with the provided {@code aSpToken} value embedded in the
     * {@code signedJwt} to complete the reset password process in your SAML Identity Provider.
     *
     * @param spToken a unique token used to reset a password.
     * @return this instance for method chaining.
     */
    SamlIdpUrlBuilder setSpToken(String spToken);

    /**
     * Convenience method to set any key value. This is important to decouple the server releases from the library
     * releases, when a new property is supported in the initial SAML Identity Provider call, and there is no {@code setter} to
     * add such property during the initial request, this method can be used to add it.
     *
     * <p>
     * <h5>Example</h5>
     * <p>
     * <p>Assume the initial call for SAML Identity Provider now supports a new property called {@code exp} (expire), since at the moment
     * there is no such method to set the new property, you can use this new convenience method to the key-value pair to
     * the request.
     * <p>
     * {@link #addProperty(String, Object) addProperty("exp", new Date())}, cal
     * <p>
     * Notes:
     * <p>
     * 1) Properties like "iat", "iss", and "iat" will be overriden when the SAML request URL is being built, to avoid
     * conflicts when the {@code url} is being consumed by Stormpath.
     * 2) Prefer the setter methods over this one (when possible), since there is type safe guarantee via the setters.
     *
     * @param name  of the new property.
     * @param value of the new property.
     * @return this instance for method chaining.     
     */
    SamlIdpUrlBuilder addProperty(String name, Object value);

    /**
     * Builds and returns the fully qualified URL representing the initial location in the SAML Identity Provider where the
     * end-user should be redirected.
     *
     * @return the fully qualified URL representing the initial location in the SAML Identity Provider where the
     *         end-user should be redirected.
     */
    String build();
}
