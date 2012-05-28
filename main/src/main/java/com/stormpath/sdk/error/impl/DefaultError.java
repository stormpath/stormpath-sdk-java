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
package com.stormpath.sdk.error.impl;

import com.stormpath.sdk.error.Error;

/**
 * @since 0.1
 */
public class DefaultError implements Error {

    private final int status;
    private final int code;
    private final String message;
    private final String developerMessage;
    private final String moreInfo;

    public DefaultError(int status, int code, String message, String developerMessage, String moreInfo) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.developerMessage = developerMessage;
        this.moreInfo = moreInfo;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getDeveloperMessage() {
        return developerMessage;
    }

    @Override
    public String getMoreInfo() {
        return moreInfo;
    }
}
