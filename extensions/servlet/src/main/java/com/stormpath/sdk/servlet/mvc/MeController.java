package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 1.0.RC8
 */
public class MeController extends AbstractController {

    private List<String> expands;
    private AccountModelFactory accountModelFactory;

    public MeController(List<String> expands) {
        this.expands = expands;

        this.accountModelFactory = new DefaultAccountModelFactory();
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Account account = AccountResolver.INSTANCE.getAccount(request);

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");

        return new DefaultViewModel("stormpathJsonView", java.util.Collections.singletonMap("account", accountModelFactory.toMap(account, expands)));
    }
}
