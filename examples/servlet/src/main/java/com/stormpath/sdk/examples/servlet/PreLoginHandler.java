package com.stormpath.sdk.examples.servlet;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public class PreLoginHandler implements WebHandler {
    private static final Logger log = LoggerFactory.getLogger(PreLoginHandler.class);

    @Override
    public boolean handle(HttpServletRequest request, HttpServletResponse response, Account account) {
        log.debug("----> PreLoginHandler");
        return true;
    }
}
