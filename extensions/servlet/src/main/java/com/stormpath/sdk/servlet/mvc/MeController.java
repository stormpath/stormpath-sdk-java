package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountInfo;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.http.UserAgents;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 1.0.RC8
 */
public class MeController extends AbstractController {

    private boolean expandGroups;

    @Override
    public boolean isNotAllowIfAuthenticated() {
        return false;
    }

    public boolean isExpandGroups() {
        return expandGroups;
    }

    public void setExpandGroups(boolean expandGroups) {
        this.expandGroups = expandGroups;
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String,Object> model = new HashMap<String, Object>();

        if (AccountResolver.INSTANCE.hasAccount(request)) {
            Account account = AccountResolver.INSTANCE.getAccount(request);
            AccountInfo accountInfo = new AccountInfo(account);
            if (isExpandGroups()){
                accountInfo.setGroups(account);
            }
            model.put("account", accountInfo);
        }

        if (UserAgents.get(request).isJsonPreferred()) {
            return new DefaultViewModel("stormpath/me", model).setRedirect(false);
        }

        //otherwise HTML view:
        return new DefaultViewModel(getNextUri()).setRedirect(true);
    }
}
