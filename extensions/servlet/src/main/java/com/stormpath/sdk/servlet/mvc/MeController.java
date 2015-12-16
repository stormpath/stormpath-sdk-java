package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 1.0.RC8
 */
public class MeController extends AbstractController {

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        if (AccountResolver.INSTANCE.hasAccount(request)) {
            Account account = AccountResolver.INSTANCE.getAccount(request);
            model.put("account", account);
        }

        return new DefaultViewModel("stormpathJsonView", model);
    }
}
