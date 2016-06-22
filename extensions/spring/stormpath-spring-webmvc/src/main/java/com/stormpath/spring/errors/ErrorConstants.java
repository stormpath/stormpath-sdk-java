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
package com.stormpath.spring.errors;

/**
 * Copied with much respect from JHipster: https://github.com/jhipster/generator-jhipster/tree/master/generators/server/templates/src/main/java/package/web/rest/errors
 *
 * @since 1.0.0
 */
public final class ErrorConstants {

    public static final String ERR_ACCESS_DENIED = "error.accessDenied";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
    public static final String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";
    public static final String ERR_PAGE_NOT_FOUND = "error.pageNotFound";

    private ErrorConstants() {
    }

}
