/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.resource;

import com.stormpath.sdk.error.Error;

/**
 * @since 0.2
 */
public class ResourceException extends RuntimeException implements Error {

    private final Error error;

    public ResourceException(Error error) {
        super(error != null ? error.getMessage() : "");
        this.error = error;
    }

    @Override
    public int getStatus() {
        return error != null ? error.getStatus() : -1;
    }

    @Override
    public int getCode() {
        return error != null ? error.getCode() : -1;
    }

    @Override
    public String getDeveloperMessage() {
        return error != null ? error.getDeveloperMessage() : null;
    }

    @Override
    public String getMoreInfo() {
        return error != null ? error.getMoreInfo() : null;
    }
}
