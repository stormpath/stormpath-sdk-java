package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.http.UserAgents;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 1.0.RC8
 */
public class MeController extends AbstractController {

    private boolean expandGroups;

    @Override
    public boolean isNotAllowedIfAuthenticated() {
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
            AbstractResource abstractAccount = (AbstractResource)account;
            Map<String, Object> accountModel = getModel(abstractAccount);
            if (isExpandGroups()){
                DefaultAccount defaultAccount = (DefaultAccount)account;
                List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
                for (Group group: defaultAccount.getGroups()) {
                    Map<String, Object> groupModel = getModel((AbstractResource) group);
                    groups.add(groupModel);
                }
                accountModel.put("groups", groups);
            }
            model.put("account", accountModel);
        }

        if (UserAgents.get(request).isJsonPreferred()) {
            return new DefaultViewModel("stormpath/me", model).setRedirect(false);
        }

        //otherwise HTML view:
        return new DefaultViewModel(getNextUri()).setRedirect(true);
    }

    private Map<String, Object> getModel(AbstractResource abstractResource) {
        Map<String, Object> result = new HashMap<String, Object>();
        for(String propertyName: abstractResource.getPropertyNames()) {
            Object value = abstractResource.getProperty(propertyName);
            if (!(value instanceof Map || value instanceof Set)){
                result.put(propertyName, value);
            }
        }
        return result;
    }
}
