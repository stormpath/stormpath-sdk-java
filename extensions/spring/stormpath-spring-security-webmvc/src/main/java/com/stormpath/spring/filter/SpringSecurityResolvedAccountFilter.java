package com.stormpath.spring.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.spring.security.provider.StormpathUserDetails;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/450
 *
 * @since 1.0.RC.8.1
 **/
public class SpringSecurityResolvedAccountFilter extends HttpFilter implements InitializingBean {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws Exception {

        //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/605
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            Account account = AccountResolver.INSTANCE.getAccount(request);

            if (account != null) {
                Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

                boolean forceRefresh;

                if (currentAuthentication == null || !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof StormpathUserDetails)) {
                    forceRefresh = true;
                } else {
                    StormpathUserDetails userDetails = (StormpathUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    forceRefresh = !userDetails.getProperties().get("href").equals(account.getHref());
                }

                if (forceRefresh) {
                    Authentication authentication = new ProviderAuthenticationToken(account);
                    authentication = authenticationProvider.authenticate(authentication);
                    SecurityContextHolder.clearContext();
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(authenticationProvider != null, "AuthenticationProvider is required");
    }
}
