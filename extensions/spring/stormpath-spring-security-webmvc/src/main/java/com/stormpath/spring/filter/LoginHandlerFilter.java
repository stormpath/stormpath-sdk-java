package com.stormpath.spring.filter;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.mvc.WebHandler;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public class LoginHandlerFilter extends HttpFilter {
    private WebHandler loginHandler;
    private String loginUri;

    public LoginHandlerFilter(WebHandler preLoginHandler, String loginUri) {
        this.loginHandler = preLoginHandler;
        this.loginUri = loginUri;
    }

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        if (loginHandler != null &&
                HttpMethod.POST.name().equals(request.getMethod()) &&
                loginUri.equals(request.getServletPath())) {
            if (!loginHandler.handle(request, response, AccountResolver.INSTANCE.getAccount(request))) {
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
