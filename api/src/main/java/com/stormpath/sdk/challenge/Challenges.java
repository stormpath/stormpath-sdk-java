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
package com.stormpath.sdk.challenge;

import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenges;
import com.stormpath.sdk.challenge.sms.SmsChallenges;

/**
 * Static utility/helper methods for working with {@link Challenge} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Group-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Challenges.where(Challenges.messageId()</b>.containsIgnoreCase("2345")<b>)</b>
 *     .and(<b>Challenges.status()</b>.eq(ChallengeStatus.DENIED))
 *     .orderByCode().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.challenge.Challenges.*;
 *
 * ...
 *
 * <b>where(code()</b>.containsIgnoreCase("3569")<b>)</b>
 *     .and(<b>status()</b>.eq(ChallengeStatus.WAITING_FOR_PROVIDER))
 *     .orderByName().descending()
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class Challenges {

    public static final SmsChallenges SMS = SmsChallenges.getInstance();
    public static final GoogleAuthenticatorChallenges GOOGLE_AUTHENTICATOR = GoogleAuthenticatorChallenges.getInstance();

    //prevent instantiation
    private Challenges() {
    }
}
