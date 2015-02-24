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
     * An {@link Map} where JSON data can be stored. This allows both Stormpath and developers to define variables that can later be
     * replaced when the template is being processed. For example, we currently store the <code>linkBaseUrl</code> key to hold the
     * clickable url that the user will receive inside the reset password email.
     *
     * @return the map where custom JSON data (like <code>linkBaseUrl</code>) can be stored.
     * @see #setLinkBaseUrl(String)
     */
    Map<String, String> getDefaultModel();

    /**
     * Convenience method to specify the clickable url that the user will receive inside the reset password email. This url should point to the form
     * where the user can insert his new password.
     * <p/>
     * This is just a convenience method and doing this:
     * <pre>
     *      resetEmailTemplate.setLinkBaseUrl("http://mycompany.com/resetEmail.html");
     * </pre>
     * is equivalent to doing:
     * <pre>
     *     resetEmailTemplate.getDefaultModel().put("linkBaseUrl", "http://mycompany.com/resetEmail.html");
     * </pre>
     *
     * @param linkBaseUrl clickable url where the user will be prompted for the new password.
     * @return this instance for method chaining.
     */
    ResetEmailTemplate setLinkBaseUrl(String linkBaseUrl);

    /**
     * Return the clickable url that the user will receive inside the reset password email.
     * This is just a convenience method and doing this:
     * <pre>
     *      String linkBaseUrl = resetEmailTemplate.getLinkBaseUrl();
     * </pre>
     * is equivalent to doing:
     * <pre>
     *      String linkBaseUrl = resetEmailTemplate.getDefaultModel().get("linkBaseUrl");
     * </pre>
     *
     * @return clickable url where the user will be prompted for the new password.
     */
    String getLinkBaseUrl();

}
