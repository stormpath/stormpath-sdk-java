package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.impl.resource.StringProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0
 */
public class DefaultAccountModelFactory implements AccountModelFactory {

    @Override
    public Map<String, Object> toMap(Account account) {
        Map<String, Object> accountMap = new LinkedHashMap<String, Object>();

        DefaultAccount defaultAccount = (DefaultAccount) account;
        accountMap.put("href", account.getHref());
        for (Property property : defaultAccount.getPropertyDescriptors().values()) {
            if (property instanceof StringProperty || property instanceof DateProperty || property instanceof StatusProperty) {
                if (!"password".equals(property.getName())) {
                    accountMap.put(property.getName(), defaultAccount.getProperty(property.getName()));
                }
            }
        }

        return accountMap;
    }
}
