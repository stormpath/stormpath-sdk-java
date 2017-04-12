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
import com.stormpath.sdk.impl.resource.ListProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Collections;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @since 2.0.0
 */
public class OktaError extends AbstractResource implements Error, Serializable {

    static final long serialVersionUID = 42L;

    public static final IntegerProperty STATUS = new IntegerProperty("status");
    static final StringProperty ERROR_CODE = new StringProperty("errorCode");
    static final StringProperty ERROR_SUMMARY = new StringProperty("errorSummary");
    static final ListProperty ERROR_CAUSES = new ListProperty("errorCauses");
    static final StringProperty ERROR_ID = new StringProperty("errorId");
    static final StringProperty ERROR = new StringProperty("error");

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
        STATUS, ERROR_CODE, ERROR_SUMMARY, ERROR_CAUSES, ERROR_ID, ERROR
    );

    public OktaError(Map<String, Object> body) {
        super(null, body);
    }

    // Needed for this class to be serializable
    public OktaError() {
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

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getMessage() {
        return getString(ERROR);
    }

    @Override
    public String getDeveloperMessage() {
        return getString(ERROR);
    }

    @Override
    public String getMoreInfo() {
        List causes = getListProperty(ERROR_CAUSES.getName());
        if (!Collections.isEmpty(causes)) {
            return ((Map<String, String>) causes.get(0)).get("errorSummary");
        }
        return getDeveloperMessage();
    }

    @Override
    public String getRequestId() {
       return getString(ERROR_ID);
    }

}
