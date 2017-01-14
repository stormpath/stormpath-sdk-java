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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.resource.Resource;

/**
 * This class is used to wrap the information required to request an OAuth token in exchange for verifying
 * a challenge to an authentication factor.
 *
 * @since 1.3.1
 */
public interface OAuthStormpathFactorChallengeGrantAuthenticationAttempt extends Resource {
    /**
     * Method used to set the Authentication Grant Type that will be used for the token exchange request.
     * @param grantType the Authentication Grant Type that will be used for the token exchange request.
     */
    void setGrantType(String grantType);

    /**
     * Method used to set the href of the challenge to be verified.
     * @param challenge the href of the challenge to be verified.
     */
    void setChallenge(String challenge);

    /**
     * Method used to set the code for the multifactor challenge.
     * @param code the code for the multifactor challenge.
     */
    void setCode(String code);
}
