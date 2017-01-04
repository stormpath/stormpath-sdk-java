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

import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.filter.account.InsecureCookieException;
import com.stormpath.sdk.servlet.i18n.MessageSource;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.0
 */
public abstract class AbstractErrorModelFactory implements ErrorModelFactory {

    protected MessageSource messageSource;

    protected abstract String getDefaultMessageKey();

    protected abstract Object[] getMessageParams();

    protected abstract boolean hasError(HttpServletRequest request, Exception e);

    public ErrorModel toError(HttpServletRequest request, Exception e) {
        if (!hasError(request, e)) {
            return null;
        }

        String errorMsg = getErrorMessage(request, getDefaultMessageKey(), "");
        int status = 400;

        if (e.getCause() instanceof ResourceException) {
            return translateResourceException(request, (ResourceException) e.getCause());
        } else if (e instanceof ResourceException) {
            return translateResourceException(request, (ResourceException) e);
        } else if (e instanceof ValidationException) {
            errorMsg = e.getMessage();
        } else if (e instanceof InsecureCookieException) {
            errorMsg = getErrorMessage(request, e.getLocalizedMessage(), e.getMessage());
        }
        return ErrorModel.builder()
                .setStatus(status)
                .setMessage(errorMsg).build();
    }

    private ErrorModel translateResourceException(HttpServletRequest request, ResourceException e) {
        return new ErrorModel.Builder()
                .setStatus(e.getStormpathError().getStatus())
                .setMessage(getErrorMessage(request, "stormpath.web.errors." + e.getStormpathError().getCode(), e.getStormpathError().getMessage()))
                .build();
    }

    protected String getErrorMessage(HttpServletRequest request, String key, String defaultMessage) {
        if (key.isEmpty()) {
            return defaultMessage;
        }
        return messageSource.getMessage(key, defaultMessage, request.getLocale(), getMessageParams());
    }
}
