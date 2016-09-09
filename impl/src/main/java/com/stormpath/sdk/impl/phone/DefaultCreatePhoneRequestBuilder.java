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
package com.stormpath.sdk.impl.phone;

import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.CreateGroupRequestBuilder;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupOptions;
import com.stormpath.sdk.impl.group.DefaultCreateGroupRequest;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.phone.CreatePhoneRequest;
import com.stormpath.sdk.phone.CreatePhoneRequestBuilder;
import com.stormpath.sdk.phone.Phone;

// todo: mehrshad

/**
 * @since 0.9
 */
public class DefaultCreatePhoneRequestBuilder implements CreatePhoneRequestBuilder {

    private final Phone phone;

    public DefaultCreatePhoneRequestBuilder(Phone phone) {
        Assert.notNull(phone, "Phone can't be null.");
        this.phone = phone;
    }


    @Override
    public CreatePhoneRequest build() {
        return new DefaultCreatePhoneRequest(phone);
    }
}
