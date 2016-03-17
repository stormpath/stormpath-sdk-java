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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.AccountResolver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public abstract class AbstractController implements Controller {

    private static final HttpServlet DEFAULT_HANDLER = new HttpServlet() {
    };

    protected String nextUri;

    protected Map<String, Object> newModel() {
        return new HashMap<String, Object>();
    }

    /**
     * Method returns true if the controller doesn't allow requests if there is a user already authenticated,
     * this is true for most controllers.
     *
     * @return True if controller doesn't allow request when user is authenticated, false otherwise
     */
    public abstract boolean isNotAllowIfAuthenticated();

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        Assert.hasText(nextUri, "nextUri cannot be null or empty.");
        this.nextUri = nextUri;
    }

    @Override
    public ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String method = request.getMethod();

        boolean hasAccount = AccountResolver.INSTANCE.hasAccount(request);

        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            if (isNotAllowIfAuthenticated() && hasAccount) {
                return new DefaultViewModel(getNextUri()).setRedirect(true);
            }
            return doGet(request, response);
        } else if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            if (isNotAllowIfAuthenticated() && hasAccount) {
                response.sendError(403);
                return null;
            }
            return doPost(request, response);
        } else {
            return service(request, response);
        }
    }

    protected ViewModel service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DEFAULT_HANDLER.service(request, response);
        return null;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return service(request, response);
    }

    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return service(request, response);
    }

}
