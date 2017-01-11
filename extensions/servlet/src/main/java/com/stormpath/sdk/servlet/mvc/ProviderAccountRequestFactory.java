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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.provider.ProviderAccountRequest;

import javax.servlet.http.HttpServletRequest;

// Refactor of Provider requests for
// https://github.com/stormpath/stormpath-sdk-java/issues/915
// and to provide uniform responses across all integrations for
// conformance to stormpath-framework-spec as enforced by
// stormpath-framework-tck
/**
 * @since 1.3.0
 */
public interface ProviderAccountRequestFactory {

    ProviderAccountRequest getProviderAccountRequest(HttpServletRequest request);
}
