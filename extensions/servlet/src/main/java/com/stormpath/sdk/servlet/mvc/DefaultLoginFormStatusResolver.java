/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @since 1.0.0
 */
public class DefaultLoginFormStatusResolver implements LoginFormStatusResolver {

    private MessageSource messageSource;
    private String verifyEmailUri;

    public DefaultLoginFormStatusResolver(MessageSource messageSource, String verifyEmailUri) {
        Assert.notNull(messageSource, "MessageSource cannot be null.");
        Assert.hasText(verifyEmailUri, "verifyEmailUri cannot be null or empty.");
        this.messageSource = messageSource;
        this.verifyEmailUri = verifyEmailUri;
    }

    @Override
    public String getStatusMessage(HttpServletRequest request, String status) {
        Set<String> validStatus = new LinkedHashSet<>();
        validStatus.add("unverified");
        validStatus.add("verified");
        validStatus.add("created");
        validStatus.add("forgot");
        validStatus.add("reset");

        if (validStatus.contains(status)) {
            return messageSource.getMessage("stormpath.web.login.form.status." + status, request.getLocale(), verifyEmailUri);
        } else {
            throw new IllegalArgumentException("Status '" + status + "' is not valid.");
        }
    }
}
