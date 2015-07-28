/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.provider.*;

/**
 * Provider's account creation status (whether it is new or not) is represented as a convenience property
 * ('isNewAccount') in the result based on request/result status.
 *
 * @since 1.0.beta
 */
public class ProviderAccountResultFilter implements Filter {

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        ResourceDataResult result = chain.filter(request);

        if (ProviderAccountResult.class.isAssignableFrom(result.getResourceClass())) {
            result.getData().put("isNewAccount", result.getAction() == ResourceAction.CREATE);
        }

        return result;
    }
}
