package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.Filters;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class AccountResolverFilterFactory extends FilterFactory<AccountResolverFilter> {

    public static final String ACCOUNT_RESOLVER_LOCATIONS = "stormpath.web.account.resolvers";
    public static final String ACCOUNT_RESOLVER_PROPERTY_PREFIX = ACCOUNT_RESOLVER_LOCATIONS + ".";

    @Override
    protected AccountResolverFilter createInstance(ServletContext servletContext, Config config) throws Exception {

        AccountResolverFilter filter = new AccountResolverFilter();
        filter.setOauthEndpointUri(config.getAccessTokenUrl());

        List<Resolver<Account>> resolvers = getResolvers(config);
        filter.setResolvers(resolvers);

        return (AccountResolverFilter)
            Filters.builder().setServletContext(servletContext)
            .setName(Strings.uncapitalize(AccountResolverFilter.class.getSimpleName()))
            .setFilter(filter)
            .build(); //ensures init is called on the filter
    }

    protected List<Resolver<Account>> getResolvers(Config config) throws ServletException {

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

        return java.util.Collections.unmodifiableList(resolvers);
    }
}
