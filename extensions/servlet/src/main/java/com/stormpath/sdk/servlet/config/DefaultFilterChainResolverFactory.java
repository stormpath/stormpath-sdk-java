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
package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.filter.DefaultFilter;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.Filters;
import com.stormpath.sdk.servlet.filter.PathMatchingFilterChainResolver;
import com.stormpath.sdk.servlet.filter.ProxiedFilterChain;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public class DefaultFilterChainResolverFactory implements Factory<FilterChainResolver>, ServletContextInitializable {

    public static final String ROUTE_CONFIG_NAME_PREFIX = "stormpath.web.uris.";

    private FilterChainResolver resolver;

    private ServletContext servletContext;

    @Override
    public void init(ServletContext servletContext) throws ServletException {
        this.servletContext = servletContext;
        try {
            this.resolver = createInstance();
        } catch (Exception e) {
            String msg = "Unable to create FilterChainResolver instance: " + e.getMessage();
            throw new ServletException(msg, e);
        }
    }

    protected ServletContext getServletContext() {
        return this.servletContext;
    }

    protected Config getConfig() {
        return (Config) getServletContext().getAttribute(Config.class.getName());
    }

    @Override
    public FilterChainResolver getInstance() {
        return this.resolver;
    }

    protected String cleanUri(String uri) {
        return UriCleaner.INSTANCE.clean(uri);
    }

    protected FilterChainResolver createInstance() throws ServletException {

        final PathMatchingFilterChainResolver resolver = new PathMatchingFilterChainResolver(getServletContext());

        //now register any configured chains:
        Config config = getConfig();

        //ensure the always-on AccountResolverFilter is available:
        Filter accountFilter = Filters.builder().setServletContext(getServletContext())
                                      .setName(Strings.uncapitalize(AccountResolverFilter.class.getSimpleName()))
                                      .setFilterClass(AccountResolverFilter.class).build();

        final List<Filter> immediateExecutionFilters = Arrays.asList(accountFilter);

        //Too much copy-and-paste. YUCK.
        //TODO: refactor this method to be more generic

        //Ensure handlers are registered:
        String loginUrl = config.getLoginUrl();
        String loginUrlPattern = cleanUri(loginUrl);
        boolean loginChainSpecified = false;

        String logoutUrl = config.getLogoutUrl();
        String logoutUrlPattern = cleanUri(logoutUrl);
        boolean logoutChainSpecified = false;

        String forgotUrl = config.getForgotPasswordUrl();
        String forgotUrlPattern = cleanUri(forgotUrl);
        boolean forgotChainSpecified = false;

        String changeUrl = config.getChangePasswordUrl();
        String changeUrlPattern = cleanUri(changeUrl);
        boolean changeChainSpecified = false;

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
                } else if (uriPattern.startsWith(forgotUrlPattern)) {
                    forgotChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.forgot.name();
                    if (!chainDefinition.contains(filterName)) {
                        chainDefinition += Strings.DEFAULT_DELIMITER_CHAR + filterName;
                    }
                } else if (uriPattern.startsWith(changeUrlPattern)) {
                    changeChainSpecified = true;

                    //did they specify the filter as a handler in the chain?  If not, append it:
                    String filterName = DefaultFilter.change.name();
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
        if (!forgotChainSpecified) {
            fcManager.createChain(forgotUrlPattern, DefaultFilter.forgot.name());
        }
        if (!changeChainSpecified) {
            fcManager.createChain(changeUrlPattern, DefaultFilter.change.name());
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

        //we wrap the above constructed resolver with one that will always guarantee the account filter executes
        //first before all others:

        return new FilterChainResolver() {

            @Override
            public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

                FilterChain target = resolver.getChain(request, response, chain);

                if (target == null) {
                    target = chain;
                }

                //The account resolver filter always executes before any other configured filters in the chain:
                target = new ProxiedFilterChain(target, immediateExecutionFilters);

                return target;
            }
        };
    }
}
