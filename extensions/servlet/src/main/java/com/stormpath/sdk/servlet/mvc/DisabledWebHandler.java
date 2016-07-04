package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default noop WebHandler use for pre/post login/register handlers
 *
 * @since 1.0.0
 */
public class DisabledWebHandler implements WebHandler {
    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
        return true;
    }
}
