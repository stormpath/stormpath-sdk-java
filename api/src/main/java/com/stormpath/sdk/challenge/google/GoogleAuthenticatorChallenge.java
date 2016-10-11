/*
* Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.challenge.google;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;

/**
 * This domain object represents a challenge of a {@link GoogleAuthenticatorFactor} for a Multi Factor Authentication.
 * <p/>
 * In a Multi Factor Authentication scenario authenticating a user is challenged by additional {@link Factor}s like an {@link GoogleAuthenticatorFactor}.
 *
 * For Example: Using an {@link GoogleAuthenticatorFactor} as an additional {@link Factor} for authentication the user would receive a multi-digit code on their google authenticator app
 * The user would verify the authentication challenge by entering the code back to the system.
 *
 * @since 1.1.0
 */
public interface GoogleAuthenticatorChallenge extends Challenge<GoogleAuthenticatorFactor,GoogleAuthenticatorChallengeStatus>{

}
