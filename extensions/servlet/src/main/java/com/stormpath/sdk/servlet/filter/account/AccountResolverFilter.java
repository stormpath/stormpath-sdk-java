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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
//not an orderable filter - always executes immediately after the StormpathFilter but before other user-configured filters.
public class AccountResolverFilter extends HttpFilter {

    public static final String ACCOUNT_RESOLVER_LOCATIONS = "stormpath.web.account.resolvers";
    public static final String ACCOUNT_RESOLVER_PROPERTY_PREFIX = ACCOUNT_RESOLVER_LOCATIONS + ".";

    private List<Resolver<Account>> resolvers;
    private String oauthEndpointUri;

    public List<Resolver<Account>> getResolvers() {
        return resolvers;
    }

    public void setResolvers(List<Resolver<Account>> resolvers) {
        this.resolvers = resolvers;
    }

    public void setOauthEndpointUri(String oauthEndpointUri) {
        this.oauthEndpointUri = oauthEndpointUri;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInit() throws ServletException {

        if (!Collections.isEmpty(this.resolvers)) {
            //configured programatically (e.g. w/ Spring), so just return:
            return;
        }

        //not configured programatically - fall back to properties - based configuration

        Config config = getConfig();

        List<String> locations = null;
        String val = config.get(ACCOUNT_RESOLVER_LOCATIONS);
        if (Strings.hasText(val)) {
            String[] locs = Strings.split(val);
            locations = Arrays.asList(locs);
        }

        Assert.notEmpty(locations, "At least one " + ACCOUNT_RESOLVER_LOCATIONS + " must be specified.");
        assert locations != null;

        Map<String, Resolver> resolverMap = config.getInstances(ACCOUNT_RESOLVER_PROPERTY_PREFIX, Resolver.class);

        List<Resolver<Account>> resolvers = new ArrayList<Resolver<Account>>(resolverMap.size());

        for (String location : locations) {

            Resolver resolver = resolverMap.get(location);
            Assert.notNull(resolver, "There is no configured Account Resolver named " + location);

            Resolver<Account> accountResolver = (Resolver<Account>) resolver;
            resolvers.add(accountResolver);
        }

        resolvers = java.util.Collections.unmodifiableList(resolvers);
        setResolvers(resolvers);

        oauthEndpointUri = config.getAccessTokenUrl();
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws Exception {

        //since 1.0.0
        //https://github.com/stormpath/stormpath-sdk-java/issues/685 the oauth endpoint should be ignore in this filter
        //to properly handle errors in the client_credentials grant type scenario
        if (!request.getServletPath().contains(oauthEndpointUri)) {
            for (Resolver<Account> resolver : getResolvers()) {

                Account account = resolver.get(request, response);

                if (response.isCommitted()) {
                    //authentication problem - challenge response rendered, do not let the request continue:
                    return;
                }

                if (account != null && AccountStatus.ENABLED.equals(account.getStatus())) {

                    //store under both names - can be convenient depending on how it is accessed:
                    request.setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);
                    request.setAttribute("account", account);

                    //NOTE: the next two lines *must* execute after the above two request.setAttribute* calls
                    //this is because the request.getAuthType() implementation relies on the attribute being set:

                    //assert authType value is available as required by the Servlet API:
                    String authType = request.getAuthType();
                    Assert.hasText(authType, "Account Resolver must set a request authType value.");

                    break;
                }
            }
        }

        chain.doFilter(request, response);
    }
}
