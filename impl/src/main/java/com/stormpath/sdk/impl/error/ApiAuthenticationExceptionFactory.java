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
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.resource.ResourceException;

import java.lang.reflect.Constructor;

/**
 * ApiAuthenticationExceptionFactory
 *
 * @since 1.0.RC
 */
public class ApiAuthenticationExceptionFactory {

    public static final int AUTH_EXCEPTION_STATUS = 401;

    public static final int AUTH_EXCEPTION_CODE = 401;

    public static final String MORE_INFO = "http://docs.stormpath.com/java/quickstart";

    public static ResourceException newApiAuthenticationException(Class<? extends ResourceException> clazz) {

        Error error = DefaultErrorBuilder.status(AUTH_EXCEPTION_STATUS).code(AUTH_EXCEPTION_CODE).moreInfo(MORE_INFO)
                .developerMessage("You suck at this.").message("Authentication Required").build();

        return Classes.newInstance(clazz, error);
    }

    public static ResourceException newApiAuthenticationException(Class<? extends ResourceException> clazz, String message) {

        Error error = DefaultErrorBuilder.status(AUTH_EXCEPTION_STATUS).code(AUTH_EXCEPTION_CODE).moreInfo(MORE_INFO)
                .developerMessage(message).message("Authentication Required").build();

        Constructor<? extends ResourceException> constructor = Classes.getConstructor(clazz, Error.class);

        return Classes.instantiate(constructor, error);
    }

    public static ResourceException newOauthException(Class<? extends ResourceException> clazz, String oauthError) {

        String oauthClientError = "error: " + oauthError;

        Error error = DefaultErrorBuilder.status(AUTH_EXCEPTION_STATUS).code(AUTH_EXCEPTION_CODE).moreInfo(MORE_INFO)
                .developerMessage("Oauth authentication failed").message(oauthClientError).build();

        Constructor<? extends ResourceException> constructor = Classes.getConstructor(clazz, Error.class);

        return Classes.instantiate(constructor, error);
    }

}
