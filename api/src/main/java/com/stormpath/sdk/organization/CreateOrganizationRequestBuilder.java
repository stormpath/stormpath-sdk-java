/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.organization;

/**
 *
 * A Builder to construct {@link CreateOrganizationRequest}s.
 *
 * @see com.stormpath.sdk.organization.Organizations#newCreateRequestFor(Organization)
 * @since 1.0.RC5
 */public interface CreateOrganizationRequestBuilder {

    /**
     * Directive to also create a new Directory for the new Organization's needs.  The new Directory will automatically
     * be assigned as the Organization's default Groups and Accounts store.
     * <p/>
     * The directory will be automatically named based on heuristics to ensure a guaranteed unique name based on the
     * organization.  If you want to specify the Directory's name, use the {@link #createDirectoryNamed(String)} method.
     *
     * @return the builder instance for method chaining.
     * @see #createDirectoryNamed(String)
     */
    CreateOrganizationRequestBuilder createDirectory();

    /**
     * Directive to also create a new Directory for the new Organization's needs.  The new Directory will automatically
     * be assigned as the Organization's default Groups and Accounts store.
     * <p/>
     * If you don't care about the new Directory's name and want a reasonable default assigned automatically, don't
     * call this method - call {@link #createDirectory()} instead.
     *
     * @return the builder instance for method chaining.
     * @see #createDirectory()
     */
    CreateOrganizationRequestBuilder createDirectoryNamed(String name);

    /**
     * Creates a new {@code CreateOrganizationRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateOrganizationRequest} instance based on the current builder state.
     */
    CreateOrganizationRequest build();
}
