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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IdSiteController extends AbstractController {

    private ServerUriResolver serverUriResolver;

    private String callbackUri;

    private String idSiteUri;

    public void setServerUriResolver(ServerUriResolver serverUriResolver) {
        this.serverUriResolver = serverUriResolver;
    }

    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
    }

    public void setIdSiteUri(String idSiteUri) {
        this.idSiteUri = idSiteUri;
    }

    public void init() {
        Assert.notNull(serverUriResolver, "Application must be configured.");
        Assert.notNull(callbackUri, "callbackUri must be configured.");
    }

    protected Application getApplication(HttpServletRequest request) {
        return ApplicationResolver.INSTANCE.getApplication(request);
    }

    protected String buildCallbackUri(HttpServletRequest request) {

        String uri = this.serverUriResolver.getServerUri(request);

        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.equals("/")) {
            contextPath = "";
        }
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }

        uri += contextPath + this.callbackUri;

        return uri;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String idSiteUrl = createIdSiteUrl(request);

        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");

        return new DefaultViewModel(idSiteUrl).setRedirect(true);
    }

    protected String createIdSiteUrl(HttpServletRequest request) {
        IdSiteUrlBuilder builder = createIdSiteUrlBuilder(request);
        return builder.build();
    }

    protected IdSiteUrlBuilder createIdSiteUrlBuilder(HttpServletRequest request) {

        Application application = getApplication(request);

        String callbackUri = buildCallbackUri(request);

        IdSiteUrlBuilder builder = application.newIdSiteUrlBuilder().setCallbackUri(callbackUri);

        if (this.idSiteUri != null) {
            builder.setPath(this.idSiteUri);
        }

        return builder;
    }
}
