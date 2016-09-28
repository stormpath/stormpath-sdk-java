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
package com.stormpath.sdk.factor;


import com.stormpath.sdk.factor.sms.SmsFactors;


/**
 * Static utility/helper methods for working with {@link Factor} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * Factor-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. For example:
 * <pre>
 * <b>Factors.SMS.where(Factors.SMS.status()</b>.eq(FactorStatus.ENABLED)<b>)</b>
 *     .and(<b>Factors.verificationsSatus()</b>.eq(FactorVerificationStatus.VERIFIED))
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.factor.Factors.SMS.*;
 *
 * ...
 *
 * <b>where(status()</b>.eq(FactorStatus.ENABLED)<b>)</b>
 *     .and(<b>verificationStatus()</b>.eq(FactorVerificationStatus.VERIFIED))
 *     .orderByStatus().descending()
 *     .withAccounts(10, 10)
 *     .offsetBy(50)
 *     .limitTo(25));
 * </pre>
 *
 * @since 1.1.0
 */
public final class Factors {

    public static final SmsFactors SMS = SmsFactors.getInstance();

    //prevent instantiation
    private Factors() {
    }
}
