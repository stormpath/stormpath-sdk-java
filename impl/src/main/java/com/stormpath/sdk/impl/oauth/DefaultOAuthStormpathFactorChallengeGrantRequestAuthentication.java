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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthentication;

/**
 * @since 1.3.1
 */
public class DefaultOAuthStormpathFactorChallengeGrantRequestAuthentication implements OAuthStormpathFactorChallengeGrantRequestAuthentication {
    private final static String grant_type = "stormpath_factor_challenge";

    private String state;
    private String challenge;
    private String code;

    public DefaultOAuthStormpathFactorChallengeGrantRequestAuthentication(String state, String challenge, String code) {
        if (!Strings.hasText(state)) {
            Assert.hasText(challenge, "either state or challenge must be provided.");
        }
        Assert.hasText(code, "code cannot be null or empty.");

        this.state = state;
        this.challenge = challenge;
        this.code = code;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getChallenge() {
        return challenge;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getGrantType() {
        return grant_type;
    }
}
