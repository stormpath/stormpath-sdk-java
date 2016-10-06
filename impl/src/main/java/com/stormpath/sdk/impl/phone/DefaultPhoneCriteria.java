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


import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.phone.PhoneCriteria;
import com.stormpath.sdk.phone.PhoneOptions;

/**
 * @since 1.1.0
 */
public class DefaultPhoneCriteria extends DefaultCriteria<PhoneCriteria, PhoneOptions> implements PhoneCriteria {

    public DefaultPhoneCriteria() {
        super(new DefaultPhoneOptions());
    }

    @Override
    public PhoneCriteria orderByName() {
        return orderBy(DefaultPhone.NAME);
    }

    @Override
    public PhoneCriteria orderByDescription() {
        return orderBy(DefaultPhone.DESCRIPTION);
    }

    @Override
    public PhoneCriteria orderByStatus() {
        return orderBy(DefaultPhone.STATUS);
    }

    @Override
    public PhoneCriteria orderByVerificationStatus() {
        return orderBy(DefaultPhone.VERIFICATION_STATUS);
    }

    @Override
    public PhoneCriteria withAccount() {
        getOptions().withAccount();
        return this;
    }

}
