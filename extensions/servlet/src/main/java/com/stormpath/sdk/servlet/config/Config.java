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
package com.stormpath.sdk.servlet.config;

import javax.servlet.ServletException;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public interface Config extends Map<String, String> {

    /**
     * Returns the context-relative URL of the login view.
     *
     * @return the context-relative URL of the login view.
     */
    String getLoginUrl();

    String getLoginNextUrl();

    String getLogoutUrl();

    String getForgotPasswordUrl();

    String getForgotPasswordNextUrl();

    String getChangePasswordUrl();

    String getChangePasswordNextUrl();

    String getLogoutNextUrl();

    /**
     * @since 1.0.RC6
     */
    boolean isLogoutInvalidateHttpSession();

    String getAccessTokenUrl();

    String getRegisterUrl();

    String getRegisterNextUrl();

    String getVerifyUrl();

    /**
     * @since 1.0.RC8.3
     */
    String getSendVerificationEmailUrl();

    /**
     * @since 1.0.RC8.3
     */
    boolean isVerifyEnabled();

    String getVerifyNextUrl();

    String getUnauthorizedUrl();

    boolean isMeEnabled();

    String getMeUrl();

    boolean getMeExpandGroups();

    CookieConfig getAccountCookieConfig();

    long getAccountJwtTtl();

    String getAccessTokenValidationStrategy();

    <T> T getInstance(String classPropertyName) throws ServletException;

    <T> Map<String,T> getInstances(String propertyNamePrefix, Class<T> expectedType) throws ServletException;

    /**
     * @since 1.0.0
     */
    String getProducesMediaTypes();
}
