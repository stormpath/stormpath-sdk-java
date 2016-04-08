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
package com.stormpath.sdk.impl.error;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.RC
 */
public class DefaultErrorBuilder {

    private final Map<String, Object> errorProperties;

    public DefaultErrorBuilder(Integer status) {
        Assert.notNull(status, "status cannot be null.");
        errorProperties = new HashMap<String, Object>();
        errorProperties.put(DefaultError.STATUS.getName(), status);
    }

    public static DefaultErrorBuilder status(Integer status) {
        return new DefaultErrorBuilder(status);
    }

    public DefaultErrorBuilder code(Integer code) {
        this.errorProperties.put(DefaultError.CODE.getName(), code);
        return this;
    }

    public DefaultErrorBuilder message(String message) {
        this.errorProperties.put(DefaultError.MESSAGE.getName(), message);
        return this;
    }

    public DefaultErrorBuilder developerMessage(String developerMessage) {
        this.errorProperties.put(DefaultError.DEV_MESSAGE.getName(), developerMessage);
        return this;
    }

    public DefaultErrorBuilder moreInfo(String moreInfo) {
        this.errorProperties.put(DefaultError.MORE_INFO.getName(), moreInfo);
        return this;
    }

    public DefaultErrorBuilder requestId(String requestId) {
        this.errorProperties.put(DefaultError.REQUEST_ID.getName(), requestId);
        return this;
    }

    public Error build() {
        for (Object value : errorProperties.values()) {
            Assert.notNull(value);
        }
        return new DefaultError(errorProperties);
    }
}
