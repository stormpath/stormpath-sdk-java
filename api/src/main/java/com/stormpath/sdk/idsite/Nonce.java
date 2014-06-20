/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.idsite;

import com.stormpath.sdk.resource.Resource;

/**
 * A <a href="http://en.wikipedia.org/wiki/Cryptographic_nonce">cryptographic nonce</a> representation
 * for values that must cannot be used more than once.
 *
 * @since 1.0.RC2
 */
public interface Nonce extends Resource {

    /**
     * Returns the current {@code value} of this {@link Nonce nonce} instance.
     *
     * @return - The {@code value} of this {@link Nonce nonce} instance.
     */
    String getValue();

}
