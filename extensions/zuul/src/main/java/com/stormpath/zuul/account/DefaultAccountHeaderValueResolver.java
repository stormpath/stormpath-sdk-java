package com.stormpath.zuul.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.json.JsonFunction;
import com.stormpath.sdk.servlet.json.ResourceJsonFunction;
import com.stormpath.sdk.servlet.mvc.ResourceToMapConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.1.0
 */
public class DefaultAccountHeaderValueResolver implements Resolver<String> {

    private Function<Account, String> accountStringFactory;

    private AccountResolver accountResolver;

    public DefaultAccountHeaderValueResolver() {
        this.accountResolver = new DefaultAccountResolver();
        ResourceToMapConverter<Account> converter = new ResourceToMapConverter<>();
        converter.setIncludedFields(Collections.toSet("groups")); //represent this one collection by default
        this.accountStringFactory = new ResourceJsonFunction<>(converter, new JsonFunction<>());
    }

    public void setAccountStringFactory(Function<Account, String> accountStringFactory) {
        Assert.notNull(accountStringFactory, "accountStringFactory cannot be null.");
        this.accountStringFactory = accountStringFactory;
    }

    public void setAccountResolver(AccountResolver accountResolver) {
        Assert.notNull(accountResolver, "accountResolver cannot be null.");
        this.accountResolver = accountResolver;
    }

    @Override
    public String get(HttpServletRequest request, HttpServletResponse response) {
        Account account = accountResolver.getAccount(request);
        if (account != null) {
            return accountStringFactory.apply(account);
        }
        return null;
    }
}
