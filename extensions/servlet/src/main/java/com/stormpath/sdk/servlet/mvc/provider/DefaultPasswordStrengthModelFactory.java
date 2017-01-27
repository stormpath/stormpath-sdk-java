/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.AccountStoreVisitorAdapter;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.PasswordStrength;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.organization.OrganizationList;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Returns a {@link Map} of the PasswordPolicy's Strength for the default AccountStore mapped to the Application.
 * Returns null if there is no default AccountStore
 *
 * @since 1.5.0
 */
public class DefaultPasswordStrengthModelFactory implements PasswordStrengthModelFactory {

    public Map<String, Object> getPasswordPolicy(HttpServletRequest request) {

        String onk = request.getParameter("organizationNameKey");

        PasswordStrength passwordStrength;

        if (Strings.hasText(onk)) {
            passwordStrength = findPasswordStrengthByOrganization(request, onk);
        } else {
            passwordStrength = getApplicationPasswordStrength(request);
        }

        if (passwordStrength == null) {
            return null;
        }

        return convertPasswordStrengthToMap(passwordStrength);
    }

    private PasswordStrength getApplicationPasswordStrength(HttpServletRequest request) {
        AccountStore defaultAccountStore = ApplicationResolver.INSTANCE.getApplication(request).getDefaultAccountStore();

        if (defaultAccountStore == null) {
            return null;
        }

        final PasswordStrength[] passwordStrength = new PasswordStrength[1];
        defaultAccountStore.accept(new AccountStoreVisitor() {
            @Override
            public void visit(Group group) {
                passwordStrength[0] = group.getDirectory().getPasswordPolicy().getStrength();
            }

            @Override
            public void visit(Directory directory) {
                passwordStrength[0] = directory.getPasswordPolicy().getStrength();
            }

            @Override
            public void visit(Organization organization) {
                passwordStrength[0] = getOrganizationPasswordStrength(organization);
            }
        });

        return passwordStrength[0];
    }

    private PasswordStrength findPasswordStrengthByOrganization(HttpServletRequest request, String onk) {
        HashMap<String, Object> query = new HashMap<>();
        query.put("nameKey", onk);
        OrganizationList organizations = ClientResolver.INSTANCE.getClient(request).getOrganizations(query);

        if (organizations.getSize() != 1) {
            return null;
        }

        return getOrganizationPasswordStrength(organizations.single());
    }

    private PasswordStrength getOrganizationPasswordStrength(Organization organization) {
        AccountStore organizationDefaultAccountStore = organization.getDefaultAccountStore();

        if (organizationDefaultAccountStore == null) {
            return null;
        }

        final PasswordStrength[] passwordStrength = new PasswordStrength[1];
        organizationDefaultAccountStore.accept(new AccountStoreVisitorAdapter() {
            @Override
            public void visit(Group group) {
                passwordStrength[0] = group.getDirectory().getPasswordPolicy().getStrength();
            }

            @Override
            public void visit(Directory directory) {
                passwordStrength[0] = directory.getPasswordPolicy().getStrength();
            }
        });

        return passwordStrength[0];
    }

    private Map<String, Object> convertPasswordStrengthToMap(PasswordStrength passwordStrength) {
        AbstractResource abstractResource = (AbstractResource) passwordStrength;

        Map<String, Object> strength = new HashMap<>();

        for (String propertyName : abstractResource.getPropertyDescriptors().keySet()) {
            strength.put(propertyName, abstractResource.getProperty(propertyName));
        }

        return strength;
    }
}
