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

import java.io.Serializable;
import java.util.Map;

/**
 * @since 0.1
 */
public class DefaultError extends AbstractResource implements Error, Serializable {

    static final long serialVersionUID = 42L;

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

    // Needed for this class to be serializable
    public DefaultError() {
        super(null, null);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public int getStatus() {
        return getInt(STATUS);
    }

    public DefaultError setStatus(int status) {
        setProperty(STATUS, status);
        return this;
    }

    @Override
    public int getCode() {
        return getInt(CODE);
    }

    public DefaultError setCode(int code) {
        setProperty(CODE, code);
        return this;
    }

    @Override
    public String getMessage() {
        return getString(MESSAGE);
    }

    public DefaultError setMessage(String message) {
        setProperty(MESSAGE, message);
        return this;
    }

    @Override
    public String getDeveloperMessage() {
        return getString(DEV_MESSAGE);
    }

    public DefaultError setDeveloperMessage(String message) {
        setProperty(DEV_MESSAGE, message);
        return this;
    }

    @Override
    public String getMoreInfo() {
        return getString(MORE_INFO);
    }

    public DefaultError setMoreInfo(String moreInfo) {
        setProperty(MORE_INFO, moreInfo);
        return this;
    }

    @Override
    public String getRequestId() {
       return getString(REQUEST_ID);
    }


    public DefaultError setRequestId(String requestId) {
        setProperty(REQUEST_ID, requestId);
        return this;
    }

}
