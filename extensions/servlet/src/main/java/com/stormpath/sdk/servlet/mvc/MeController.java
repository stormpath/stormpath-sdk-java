package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
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
    private Set<String> accountProperties = new HashSet<String>(Arrays.asList("href", "username", "email", "givenName",
            "middleName", "surname", "fullName", "status", "createdAt", "modifiedAt", "emailVerificationToken"));

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
            AbstractResource abstractAccount = (AbstractResource)account;
            if (isExpandGroups()){
                accountProperties.add("groups");
            }
            model.put("account", getModel(abstractAccount, accountProperties));
        }

        if (UserAgents.get(request).isJsonPreferred()) {
            return new DefaultViewModel("stormpath/me", model).setRedirect(false);
        }

        //otherwise HTML view:
        return new DefaultViewModel(getNextUri()).setRedirect(true);
    }

    private Map<String, Object> getModel(AbstractResource abstractResource, Set<String> resourceProperties) {
        Map<String, Object> result = new HashMap<String, Object>();
        for(String propertyName: resourceProperties) {
            Object value = abstractResource.getProperty(propertyName);
            result.put(propertyName, value);
        }
        return result;
    }
}
