/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.servlet.util;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.servlet.api.ServletApiRequestAuthenticator;
import com.stormpath.sdk.servlet.api.impl.DefaultServletApiRequestAuthenticator;
import com.stormpath.sdk.servlet.oauth.ServletOauthRequestAuthenticator;
import com.stormpath.sdk.servlet.oauth.impl.DefaultServletOauthRequestAuthenticator;

/**
 * Static utility/helper methods for working with API Request Authenticators for Servlet environments.
 *
 * @since 1.0.RC4.3-SNAPSHOT
 */
public final class Servlets {

    public static ServletApiRequestAuthenticator servletApiRequestAuthenticator(Application application) {
        Assert.notNull(application, "application argument cannot be null.");
        return new DefaultServletApiRequestAuthenticator(application);
    }

    public static ServletOauthRequestAuthenticator servletOauthRequestAuthenticator(Application application){
        Assert.notNull(application, "application argument cannot be null.");
        return new DefaultServletOauthRequestAuthenticator(application);
    }

}
