package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.account.DefaultAccount;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.CollectionResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultAccountModelFactory implements AccountModelFactory {

    private static final Logger log = LoggerFactory.getLogger(DefaultAccountModelFactory.class);

    @Override
    public Map<String, Object> toMap(Account account, List<String> expands) {
        Assert.notNull(account, "account cannot be null");
        DefaultAccount defaultAccount = (DefaultAccount) account;

        Map<String, Object> accountMap = getResourceProperties(defaultAccount);

        for (String property : expands) {
            if (defaultAccount.getPropertyDescriptors().containsKey(property)) {
                try {
                    Method method = defaultAccount.getClass().getMethod("get" + Strings.capitalize(property));
                    Object propertyValue = method.invoke(account);

                    if (propertyValue instanceof CollectionResource) {
                        List<Map<String, Object>> resourcesMap = new ArrayList<>();

                        CollectionResource collectionResource = (CollectionResource) propertyValue;
                        Iterator iterator = collectionResource.iterator();
                        while (iterator.hasNext()) {
                            resourcesMap.add(getResourceProperties((AbstractResource) iterator.next()));
                        }

                        // Return "propertyName.items" instead of "propertyName" for expands
                        // https://github.com/stormpath/stormpath-sdk-java/issues/1044
                        Map<String, Object> items = new LinkedHashMap<>();
                        items.put("items", resourcesMap);

                        accountMap.put(property, items);
                    } else if (propertyValue instanceof AbstractResource) {
                        if ("customData".equals(property)) {
                            accountMap.put(property, account.getCustomData());
                        } else {
                            accountMap.put(property, getResourceProperties((AbstractResource) propertyValue));
                        }
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    log.error("Error expanding account property", e);
                }
            }
        }

        return accountMap;
    }

    public Map<String, Object> getResourceProperties(AbstractResource resource) {
        Map<String, Object> resourceProperties = new LinkedHashMap<String, Object>();

        resourceProperties.put("href", resource.getHref());
        resourceProperties.put("createdAt", resource.getProperty("createdAt"));
        resourceProperties.put("modifiedAt", resource.getProperty("modifiedAt"));
        for (Property property : resource.getPropertyDescriptors().values()) {
            if (property instanceof StringProperty || property instanceof DateProperty || property instanceof EnumProperty) {
                if (!"password".equals(property.getName())) {
                    resourceProperties.put(property.getName(), resource.getProperty(property.getName()));
                }
            }
        }

        return resourceProperties;
    }
}
