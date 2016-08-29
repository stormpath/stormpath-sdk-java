/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyCriteria;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenList;
import com.stormpath.sdk.oauth.RefreshTokenList;
import com.stormpath.sdk.provider.ProviderData;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

/**
 * Internal implementation class, not to be used by SDK users.
 *
 * @since 1.0.RC3
 */
public class ImmutableAccount implements Account {

    private static final String IMMUTABLE_MSG = "Immutable account references cannot be modified.";

    private final Account account;

    public ImmutableAccount(Account account) {
        Assert.notNull(account, "Account argument cannot be null.");
        this.account = account;
    }

    private static void immutable() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(IMMUTABLE_MSG);
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public Account setUsername(String username) {
        immutable();
        return null;
    }

    @Override
    public String getEmail() {
        return account.getEmail();
    }

    @Override
    public Account setEmail(String email) {
        immutable();
        return null;
    }

    @Override
    public Account setPassword(String password) {
        immutable();
        return null;
    }

    @Override
    public String getGivenName() {
        return account.getGivenName();
    }

    @Override
    public Account setGivenName(String givenName) {
        immutable();
        return null;
    }

    @Override
    public String getMiddleName() {
        return account.getMiddleName();
    }

    @Override
    public Account setMiddleName(String middleName) {
        immutable();
        return null;
    }

    @Override
    public String getSurname() {
        return account.getSurname();
    }

    @Override
    public Account setSurname(String surname) {
        immutable();
        return null;
    }

    @Override
    public String getFullName() {
        return account.getFullName();
    }

    @Override
    public AccountStatus getStatus() {
        return account.getStatus();
    }

    @Override
    public Account setStatus(AccountStatus status) {
        immutable();
        return null;
    }

    @Override
    public GroupList getGroups() {
        return account.getGroups();
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        return account.getGroups(queryParams);
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        return account.getGroups(criteria);
    }

    @Override
    public Directory getDirectory() {
        return account.getDirectory();
    }

    @Override
    public Tenant getTenant() {
        return account.getTenant();
    }

    @Override
    public GroupMembershipList getGroupMemberships() {
        return account.getGroupMemberships();
    }

    @Override
    public GroupMembership addGroup(Group group) {
        immutable();
        return null;
    }

    @Override
    public GroupMembership addGroup(String hrefOrName) {
        immutable();
        return null;
    }

    @Override
    public Account removeGroup(Group group) {
        immutable();
        return null;
    }

    @Override
    public Account removeGroup(String hrefOrName) {
        immutable();
        return null;
    }

    @Override
    public EmailVerificationToken getEmailVerificationToken() {
        return account.getEmailVerificationToken();
    }

    @Override
    public CustomData getCustomData() {
        return account.getCustomData();
    }

    @Override
    public Account saveWithResponseOptions(AccountOptions responseOptions) {
        immutable();
        return null;
    }

    @Override
    public boolean isMemberOfGroup(String hrefOrName) {
        return account.isMemberOfGroup(hrefOrName);
    }

    @Override
    public boolean isMemberOfGroup(Group group) {
        return account.isMemberOfGroup(group);
    }

    @Override
    public boolean isLinkedToAccount(String href) {
        return account.isLinkedToAccount(href);
    }

    @Override
    public boolean isLinkedToAccount(Account otherAccount) {
        return account.isLinkedToAccount(otherAccount);
    }

    @Override
    public ProviderData getProviderData() {
        return account.getProviderData();
    }

    @Override
    public ApiKeyList getApiKeys() {
        return account.getApiKeys();
    }

    @Override
    public ApiKeyList getApiKeys(Map<String, Object> queryParams) {
        return account.getApiKeys(queryParams);
    }

    @Override
    public ApiKeyList getApiKeys(ApiKeyCriteria criteria) {
        return account.getApiKeys(criteria);
    }

    @Override
    public ApiKey createApiKey() {
        immutable();
        return null;
    }

    @Override
    public ApiKey createApiKey(ApiKeyOptions options) {
        immutable();
        return null;
    }

    /** @since 1.0.RC4 */
    @Override
    public ApplicationList getApplications() {
        return account.getApplications();
    }

    /** @since 1.0.RC4 */
    @Override
    public ApplicationList getApplications(Map<String, Object> queryParams) {
        return account.getApplications(queryParams);
    }

    /** @since 1.0.RC4 */
    @Override
    public ApplicationList getApplications(ApplicationCriteria criteria) {
        return account.getApplications(criteria);
    }

    @Override
    public void delete() {
        immutable();
    }

    @Override
    public String getHref() {
        return account.getHref();
    }

    @Override
    public void save() {
        immutable();
    }

    /**
     * @since 1.0.RC4.6
     */
    @Override
    public Date getCreatedAt() {
        return account.getCreatedAt();
    }

    /**
     * @since 1.0.RC4.6
     */
    @Override
    public Date getModifiedAt() {
        return account.getModifiedAt();
    }

    /**
     * @since 1.0.RC7
     */
    @Override
    public AccessTokenList getAccessTokens() {
        return account.getAccessTokens();
    }

    /**
     * @since 1.0.RC7
     */
    @Override
    public RefreshTokenList getRefreshTokens() {
        return account.getRefreshTokens();
    }

    @Override
    public AccountList getLinkedAccounts() {
        return account.getLinkedAccounts();
    }

    @Override
    public AccountList getLinkedAccounts(Map<String, Object> queryParams) {
        return account.getLinkedAccounts(queryParams);
    }

    @Override
    public AccountList getLinkedAccounts(AccountCriteria criteria) {
        return account.getLinkedAccounts(criteria);
    }

    @Override
    public AccountLink link(Account otherAccount) {
        immutable();
        return null;
    }

    @Override
    public AccountLink link(String otherAccountHref) {
        immutable();
        return null;
    }

    @Override
    public AccountLink unlink(Account otherAccount) {
        immutable();
        return null;
    }

    @Override
    public AccountLink unlink(String otherAccountHref) {
        immutable();
        return null;
    }

    @Override
    public AccountLinkList getAccountLinks() {
        return account.getAccountLinks();
    }

    @Override
    public AccountLinkList getAccountLinks(Map<String, Object> queryParams) {
        return account.getAccountLinks(queryParams);
    }

    @Override
    public AccountLinkList getAccountLinks(AccountLinkCriteria criteria) {
        return account.getAccountLinks(criteria);
    }
}
