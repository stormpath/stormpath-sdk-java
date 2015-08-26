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
package com.stormpath.sdk.impl.jwt;

/**
 * IdSiteClaims exposes the Claims parameters used for IdSite.
 *
 * @since 1.0.RC
 */
public abstract class IdSiteClaims {

    public static final String REDIRECT_URI = "cb_uri";
    public static final String PATH = "path";
    public static final String ORGANIZATION_NAME_KEY = "onk";
    public static final String SHOW_ORGANIZATION_FIELD = "sof";
    public static final String USE_SUBDOMAIN = "usd";

    public static final String JWT_REQUEST = "jwtRequest";
    public static final String JWT_RESPONSE = "jwtResponse";

    //request/response
    public static final String STATE = "state";
    public static final String RESPONSE_ID = "irt";

    //Id Token Parameters
    public static final String IS_NEW_SUBJECT = "isNewSub";

    public static final String STATUS = "status";

    private IdSiteClaims() {
    }
}
