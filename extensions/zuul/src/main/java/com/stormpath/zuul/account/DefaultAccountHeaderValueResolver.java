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
 * A {@code Resolver} that locates the account associated with the current request using an
 * {@link AccountResolver}, converts the located account to a String, and returns that String to the caller.
 * <p>
 * If an account cannot be resolved (for example, the request is not authenticated or a user not remembered
 * from a previous authentication), {@code null} is returned to indicate no header value needs to be set.
 * </p>
 * <p>
 * If an account is located, the default configuration returns a JSON representation of the account.  A
 * different account String representation can be returned by configuring a different
 * {@link #setAccountStringFactory(Function) accountStringFactory}.
 * </p>
 *
 * @see #setAccountResolver(AccountResolver)
 * @see #setAccountStringFactory(Function)
 * @since 1.1.0
 */
public class DefaultAccountHeaderValueResolver implements Resolver<String> {

    private Function<Account, String> accountStringFactory;

    private AccountResolver accountResolver;

    /**
     * Default constructor that uses a {@link ResourceJsonFunction ResourceJsonFunction} to convert any discovered
     * account to a string.
     */
    public DefaultAccountHeaderValueResolver() {
        this.accountResolver = new DefaultAccountResolver();
        ResourceToMapConverter<Account> converter = new ResourceToMapConverter<>();
        converter.setIncludedFields(Collections.toSet("groups")); //represent this one collection by default
        this.accountStringFactory = new ResourceJsonFunction<>(converter, new JsonFunction<>());
    }

    /**
     * Sets the function used to convert a discovered {@link Account} to a String representation.
     * <p>Unless overridden, the default instance is a {@link ResourceJsonFunction}, which returns a JSON
     * representation of the account.</p>
     *
     * @param accountStringFactory the function used to convert a discovered {@link Account} to a String representation.
     */
    public void setAccountStringFactory(Function<Account, String> accountStringFactory) {
        Assert.notNull(accountStringFactory, "accountStringFactory cannot be null.");
        this.accountStringFactory = accountStringFactory;
    }

    /**
     * Sets the account resolver to use to look a request's associated account.  Unless overridden, the default
     * instance is a {@link DefaultAccountResolver}.  Once located, the account will be converted to a string
     * using the {@link #accountStringFactory}.
     *
     * @param accountResolver the account resolver to use to look a request's associated account.
     */
    public void setAccountResolver(AccountResolver accountResolver) {
        Assert.notNull(accountResolver, "accountResolver cannot be null.");
        this.accountResolver = accountResolver;
    }

    /**
     * Returns a string representation of the request's associated account or {@code null} if an account is not
     * available.
     * <p>
     * This method locates the account associated with the current request using the
     * {@link #accountResolver}, cnverts the located account to a String with the
     * {@link #accountStringFactory}, and returns the resulting String.
     * </p>
     * <p>
     * If an account cannot be resolved (for example, the request is not authenticated or a user not remembered
     * from a previous authentication), {@code null} is returned to indicate no header value needs to be set.
     * </p>
     *
     * @param request  the inbound request
     * @param response the outbound response
     * @return a string representation of the request's associated account or {@code null} if an account is not
     * available.
     */
    @Override
    public String get(HttpServletRequest request, HttpServletResponse response) {
        Account account = accountResolver.getAccount(request);
        if (account != null) {
            return accountStringFactory.apply(account);
        }
        return null;
    }
}
