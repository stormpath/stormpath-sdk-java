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
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @since 1.0.RC3
 */
//not an orderable filter - always executes immediately after the StormpathFilter but before other user-configured filters.
public class AccountResolverFilter extends HttpFilter {

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
    protected void onInit() throws Exception {
        Assert.notEmpty(resolvers, "resolvers cannot be null or empty.");
        Assert.hasText(oauthEndpointUri, "oauthEndpointUri cannot be null or empty.");
    }

    /**
     * Returns {@code false} if the current request is the oauth endpoint URI, otherwise {@code true}.  This is to
     * ensure that the oauth endpoint can properly handle errors in the client_credentials grant type
     *
     * @param request  the incoming servlet request
     * @param response the outbound servlet response
     * @return {@code false} if the current request is the oauth endpoint URI, otherwise {@code true}.
     * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/685">Issue 685</a>
     * @since 1.0.0
     */
    @Override
    protected boolean isEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return !request.getServletPath().contains(oauthEndpointUri);
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

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

        chain.doFilter(request, response);
    }
}
