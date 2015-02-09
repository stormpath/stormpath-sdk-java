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
package com.stormpath.sdk.mail;

import java.util.Map;

/**
 * The ResetEmailTemplate is a {@link EmailTemplate} resource which also provides the ability to configure the url where
 * the user will be redirected once he clicks the link received in the Reset Password Email.
 *
 * @since 1.0.0
 */
public interface ResetEmailTemplate extends EmailTemplate<ResetEmailTemplate> {

    /**
     * An {@link java.util.Collections#unmodifiableMap(java.util.Map) UnmodifiableMap} where Stormpath-reserved
     * properties (like <code>linkBaseUrl</code>) are stored.
     *
     * @return the map where Stormpath-reserved properties (like linkBaseUrl) are stored.
     * @see #setLinkBaseUrl(String)
     */
    Map<String, String> getDefaultModel();

    /**
     * Specifies the clickable url that the user will receive inside the reset password email. This url should point to the form
     * where the user can insert his new password.
     *
     * @param linkBaseUrl clickable url where the user will be prompted for the new password.
     * @return this instance for method chaining.
     */
    ResetEmailTemplate setLinkBaseUrl(String linkBaseUrl);

    /**
     * Return the clickable url that the user will receive inside the reset password email.
     *
     * @return clickable url where the user will be prompted for the new password.
     */
    String getLinkBaseUrl();

}
