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
package com.stormpath.sdk.impl.error;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.IntegerProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.Map;

/**
 * @since 0.1
 */
public class DefaultError extends AbstractResource implements Error {

    static final IntegerProperty STATUS = new IntegerProperty("status");
    static final IntegerProperty CODE = new IntegerProperty("code");
    static final StringProperty MESSAGE = new StringProperty("message");
    static final StringProperty DEV_MESSAGE = new StringProperty("developerMessage");
    static final StringProperty MORE_INFO = new StringProperty("moreInfo");
    public static final StringProperty REQUEST_ID = new StringProperty("requestId");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            STATUS, CODE, MESSAGE, DEV_MESSAGE, MORE_INFO
    );

    public DefaultError(Map<String, Object> body) {
        super(null, body);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public int getStatus() {
        return getInt(STATUS);
    }

    @Override
    public int getCode() {
        return getInt(CODE);
    }

    @Override
    public String getMessage() {
        return getString(MESSAGE);
    }

    @Override
    public String getDeveloperMessage() {
        return getString(DEV_MESSAGE);
    }

    @Override
    public String getMoreInfo() {
        return getString(MORE_INFO);
    }

    @Override
    public String getRequestId() {
       return getString(REQUEST_ID);
    }

}
