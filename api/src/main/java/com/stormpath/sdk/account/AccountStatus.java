/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.account;

/**
 * An {@code AccountStatus} represents the various states an account may be in.
 *
 * @since 0.8
 */
public enum AccountStatus {

    /**
     * An enabled account may login to applications.
     */
    ENABLED,

    /**
     * A disabled account may not login to applications.
     */
    DISABLED,

    /**
     * An unverified account is a disabled account that does not have a verified email address.
     */
    UNVERIFIED
}
