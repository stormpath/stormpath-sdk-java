package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WebHandler defines a basic interface that developers can use to implement their post/pre login/register handlers
 * <p/>
 * more info: https://github.com/stormpath/stormpath-framework-spec/blob/master/handlers.md
 *
 * @since 1.0.0
 */
public interface WebHandler {
    /**
     * handle is the only method devs need to implement their pre/post handlers, it receives the HTTP request and response
     *
     * @param request  The HTTP servlet request
     * @param response The HTTP servlet response
     * @param account  The account can be nil for example in the pre login handler
     * @return Return true to continue the normal flow, false to stop the current flow
     */
    boolean handle(HttpServletRequest request, HttpServletResponse response, Account account);
}
