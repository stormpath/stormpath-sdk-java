/*
* Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.application.Application;

/**
 * Factory used to construct {@link JwtValidator JwtValidator} instances required for performing JWT validation requests.
 *
 * @since 1.0.RC6
 */
public interface JwtValidatorFactory {

    /**
     * Specifies the new {@link JwtValidator JwtValidator} instance will be used to validate a JWT for a specific application.
     * @param application the application against which the JWT will be validated
     * @return a {@link JwtValidator JwtValidator} instances
     */
    JwtValidator forApplication(Application application);

}
