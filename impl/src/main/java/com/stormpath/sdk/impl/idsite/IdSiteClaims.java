/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.idsite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stormpath.sdk.impl.provider.ProviderClaims;

/**
 * IdSiteClaims exposes the Claims parameters used for IdSite.
 *
 * @since 1.0.RC5
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdSiteClaims extends ProviderClaims {

    public static final String SHOW_ORGANIZATION_FIELD = "sof";
    public static final String USE_SUBDOMAIN = "usd";

    public static final String ACCESS_TOKEN = "accessToken";
    public static final String JWT_REQUEST = "jwtRequest";
    public static final String JWT_RESPONSE = "jwtResponse";

    //request/response
    public static final String RESPONSE_ID = "irt";

    //Id Token Parameters
    public static final String IS_NEW_SUBJECT = "isNewSub";

    public static final String STATUS = "status";

    public static final String ERROR = "err";

    public Boolean getUseSubdomain() {
        return getBoolean(USE_SUBDOMAIN);
    }

    public IdSiteClaims setUseSubdomain(boolean useSubdomain) {
        setValue(USE_SUBDOMAIN, useSubdomain);
        return this;
    }

    public Boolean getShowOrganizationField() {
        return getBoolean(SHOW_ORGANIZATION_FIELD);
    }

    public IdSiteClaims setShowOrganizationField(boolean showOrganizationField) {
        setValue(SHOW_ORGANIZATION_FIELD, showOrganizationField);
        return this;
    }

    protected Boolean getBoolean(String name) {
        Object v = get(name);
        if (v == null) {
            return null;
        } else if (v instanceof Boolean) {
            return (Boolean) v;
        } else if (v instanceof String) {
            String booleanStr = (String) v;
            if (booleanStr.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            if (booleanStr.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }
        }
        throw new IllegalStateException("Cannot convert '" + name + "' value [" + v + "] to Boolean instance.");
    }

}
