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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.UriCleaner;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StormpathFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(StormpathFilter.class);

    public static final String ROUTE_CONFIG_NAME_PREFIX = "stormpath.web.routes.";
    public static final String FILTER_CONFIG_NAME_PREFIX = "stormpath.servlet.filter.";

    private FilterChainResolver filterChainResolver;

    private List<Filter> immediateExecutionFilters;

    public StormpathFilter() {
    }

    protected String cleanUri(String uri) {
        return UriCleaner.INSTANCE.clean(uri);
    }

    @Override
    protected void onInit() throws ServletException {
        try {
            doInit();
        } catch (ServletException e) {
            log.error("Unable to initialize StormpathFilter.", e);
            throw e;
        } catch (Exception e) {
            String msg = "Unable to initialize StormpathFilter: " + e.getMessage();
            log.error(msg, e);
            throw new ServletException(msg);
        }
    }

    protected void doInit() throws ServletException {

        PathMatchingFilterChainResolver resolver = new PathMatchingFilterChainResolver(getServletContext());
        this.filterChainResolver = resolver;

        //now register any configured chains:
        Config config = getConfig();

        //ensure the always-on AccountResolverFilter is available:
        Filter accountFilter = Filters.builder().setServletContext(getServletContext())
            .setName(Strings.uncapitalize(AccountResolverFilter.class.getSimpleName()))
            .setFilterClass(AccountResolverFilter.class)
            .build();
        this.immediateExecutionFilters = Arrays.asList(accountFilter);

        //Too much copy-and-paste. YUCK.
        //TODO: refactor this method to be more generic

        //Ensure handlers are registered:
        String loginUrl = config.getLoginUrl();
        String loginUrlPattern = cleanUri(loginUrl);
        boolean loginChainSpecified = false;

        String logoutUrl = config.getLogoutUrl();
        String logoutUrlPattern = cleanUri(logoutUrl);
        boolean logoutChainSpecified = false;

        String registerUrl = config.getRegisterUrl();
        String registerUrlPattern = cleanUri(registerUrl);
        boolean registerChainSpecified = false;

        String verifyUrl = config.getVerifyUrl();
        String verifyUrlPattern = cleanUri(verifyUrl);
        boolean verifyChainSpecified = false;

        String accessTokenUrl = config.getAccessTokenUrl();
        String accessTokenUrlPattern = cleanUri(accessTokenUrl);
        boolean accessTokenChainSpecified = false;

        String unauthorizedUrl = config.getUnauthorizedUrl();
        String unauthorizedUrlPattern = cleanUri(unauthorizedUrl);
        boolean unauthorizedChainSpecified = false;

        //uriPattern-to-chainDefinition:
        Map<String, String> patternChains = new LinkedHashMap<String, String>();

        for (String key : config.keySet()) {

            if (key.startsWith(ROUTE_CONFIG_NAME_PREFIX)) {

                String uriPattern = key.substring(ROUTE_CONFIG_NAME_PREFIX.length());
                String chainDefinition = config.get(key);

                if (uriPattern.startsWith(loginUrlPattern)) {
                    loginChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.login.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }

                } else if (uriPattern.startsWith(logoutUrlPattern)) {
                    logoutChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.logout.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }

                } else if (uriPattern.startsWith(registerUrlPattern)) {
                    registerChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.register.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(verifyUrlPattern)) {
                    verifyChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.verify.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }

                } else if (uriPattern.startsWith(accessTokenUrlPattern)) {
                    accessTokenChainSpecified = true;

                    String filterName = DefaultFilter.accessToken.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }

                } else if (uriPattern.startsWith(unauthorizedUrlPattern)) {
                    unauthorizedChainSpecified = true;

                    //did they specify the unauthorized filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.unauthorized.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                }

                patternChains.put(uriPattern, chainDefinition);
            }
        }

        //register configured request handlers if not yet specified:
        FilterChainManager fcManager = resolver.getFilterChainManager();

        if (!unauthorizedChainSpecified) {
            fcManager.createChain(unauthorizedUrlPattern, DefaultFilter.unauthorized.name());
        }
        if (!loginChainSpecified) {
            fcManager.createChain(loginUrlPattern, DefaultFilter.login.name());
        }
        if (!logoutChainSpecified) {
            fcManager.createChain(logoutUrlPattern, DefaultFilter.logout.name());
        }
        if (!registerChainSpecified) {
            fcManager.createChain(registerUrlPattern, DefaultFilter.register.name());
        }
        if (!verifyChainSpecified) {
            fcManager.createChain(verifyUrlPattern, DefaultFilter.verify.name());
        }
        if (!accessTokenChainSpecified) {
            fcManager.createChain(accessTokenUrlPattern, DefaultFilter.accessToken.name());
        }

        //register all specified chains:
        for (String pattern : patternChains.keySet()) {
            String chainDefinition = patternChains.get(pattern);
            resolver.getFilterChainManager().createChain(pattern, chainDefinition);
        }
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, final FilterChain chain)
        throws Exception {

        Assert.notNull(filterChainResolver,
                       "Filter has not yet been initialized. init(FilterConfig) must be called before use.");

        //ensure the Client and Application are conveniently available to all request filters/handlers:
        Client client = ClientResolver.INSTANCE.getClient(request.getServletContext());
        request.setAttribute(Client.class.getName(), client);

        Application application = ApplicationResolver.INSTANCE.getApplication(request.getServletContext());
        request.setAttribute(Application.class.getName(), application);

        //wrap:
        request = new StormpathHttpServletRequest(request, response);

        FilterChain target = filterChainResolver.getChain(request, response, chain);

        if (target == null) {
            target = chain;
        }

        //The account resolver filter always executes before any other configured filters in the chain:
        target = new ProxiedFilterChain(target, this.immediateExecutionFilters);

        //continue:
        target.doFilter(request, response);
    }
}
