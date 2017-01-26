/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.spring.cloud.zuul.config;

import com.stormpath.sdk.lang.Function;
import org.springframework.core.io.Resource;

import java.security.Key;

/**
 * @since 1.3.0
 */
public class DisabledResourceKeyResolver implements Function<Resource, Key> {

    @Override
    public Key apply(Resource resource) {
        throw new IllegalArgumentException("This implementation is disabled.");
    }
}
