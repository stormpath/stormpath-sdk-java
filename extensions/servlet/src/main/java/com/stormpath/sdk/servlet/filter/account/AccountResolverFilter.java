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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

//not an orderable filter - always executes immediately after the StormpathFilter but before other user-configured filters.
public class AccountResolverFilter extends HttpFilter {

    public static final String ACCOUNT_RESOLVER_LOCATIONS = "stormpath.web.account.resolver.locations";
    public static final String ACCOUNT_RESOLVER_PROPERTY_PREFIX = "stormpath.servlet.filter.accountResolver.resolvers.";

    private List<Resolver<Account>> resolvers;

    @SuppressWarnings("unchecked")
    @Override
    protected void onInit() throws ServletException {

        Config config = getConfig();

        List<String> locations = null;
        String val = config.get(ACCOUNT_RESOLVER_LOCATIONS);
        if (Strings.hasText(val)) {
            String[] locs = Strings.split(val);
            locations = Arrays.asList(locs);
        }

        Assert.notEmpty(locations, "At least one " + ACCOUNT_RESOLVER_LOCATIONS +
                                           " location must be specified required.");
        assert locations != null;

        Map<String,Resolver> resolverMap = config.getInstances(ACCOUNT_RESOLVER_PROPERTY_PREFIX, Resolver.class);

        List<Resolver<Account>> resolvers = new ArrayList<Resolver<Account>>(resolverMap.size());

        for(String location : locations) {

            Resolver resolver = resolverMap.get(location);
            Assert.notNull(resolver, "There is no resolver named " + location);

            Resolver<Account> accountResolver = (Resolver<Account>)resolver;
            resolvers.add(accountResolver);
        }

        this.resolvers = Collections.unmodifiableList(resolvers);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        for(Resolver<Account> resolver : resolvers) {

            Account account = resolver.get(request, response);

            if (response.isCommitted()) {
                //authentication problem - challenge response rendered, do not let the request continue:
                return;
            }

            if (account != null) {
                //store under both names - can be convenient depending on how it is accessed:
                request.setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);
                request.setAttribute(Account.class.getName(), account);
                break;
            }
        }

        chain.doFilter(request, response);
    }
}
