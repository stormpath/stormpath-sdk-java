package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.directory.AccountCreationPolicy;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryOptions;
import com.stormpath.sdk.directory.DirectoryStatus;
import com.stormpath.sdk.directory.OktaPasswordPolicy;
import com.stormpath.sdk.directory.PasswordPolicy;
import com.stormpath.sdk.directory.OktaPasswordPolicyList;
import com.stormpath.sdk.directory.PasswordStrength;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.okta.OktaApiPaths;
import com.stormpath.sdk.impl.provider.DefaultOktaProvider;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.mail.EmailStatus;
import com.stormpath.sdk.mail.ModeledEmailTemplateList;
import com.stormpath.sdk.mail.UnmodeledEmailTemplateList;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.organization.OrganizationList;
import com.stormpath.sdk.provider.OktaProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.schema.Schema;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

public class OktaDirectory extends AbstractResource implements Directory {

    private final OktaProvider oktaProvider;

    public OktaDirectory(String clientId, InternalDataStore dataStore) {
        super(dataStore);
        this.oktaProvider = new DefaultOktaProvider(dataStore)
                .setClientId(clientId);
    }

    public OktaDirectory(String clientId, InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        this.oktaProvider = new DefaultOktaProvider(dataStore, properties)
                .setClientId(clientId);
    }

    @Override
    public String getHref() {
        return getDataStore().getBaseUrl();
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void accept(AccountStoreVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Date getCreatedAt() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public CustomData getCustomData() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Date getModifiedAt() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getName() {
        return "Okta";
    }

    @Override
    public Directory setName(String name) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getDescription() {
        return "Okta tenant.";
    }

    @Override
    public Directory setDescription(String description) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public DirectoryStatus getStatus() {
        return DirectoryStatus.ENABLED;
    }

    @Override
    public Directory setStatus(DirectoryStatus status) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Account createAccount(Account account) {
        Assert.notNull(account, "Account instance cannot be null.");
        CreateAccountRequest request = Accounts.newCreateRequestFor(account).build();
        return createAccount(request);
    }

    @Override
    public Account createAccount(Account account, boolean registrationWorkflowEnabled) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        String usersHref = getHref() + OktaApiPaths.USERS;
        final Account account = request.getAccount();
        return getDataStore().create(usersHref, account);
    }

    @Override
    public AccountList getAccounts() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GroupList getGroups() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Tenant getTenant() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Group createGroup(Group group) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Group createGroup(CreateGroupRequest request) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Provider getProvider() {
        return oktaProvider;
    }

    @Override
    public PasswordPolicy getPasswordPolicy() {
        String passwordPolicyHref = getHref() + OktaApiPaths.API_V1 + "policies?type=PASSWORD";
        OktaPasswordPolicyList policies = getDataStore().getResource(passwordPolicyHref, OktaPasswordPolicyList.class);
        OktaPasswordPolicy oktaPasswordPolicy = policies.single();
        return transformOktaPasswordPolicy(oktaPasswordPolicy);
    }

    @Override
    public AccountCreationPolicy getAccountCreationPolicy() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Directory saveWithResponseOptions(DirectoryOptions responseOptions) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationList getOrganizations() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationList getOrganizations(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationList getOrganizations(OrganizationCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public OrganizationAccountStoreMappingList getOrganizationAccountStoreMappings(OrganizationAccountStoreMappingCriteria criteria) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Schema getAccountSchema() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @SuppressWarnings("unchecked")
    private PasswordPolicy transformOktaPasswordPolicy(OktaPasswordPolicy oktaPasswordPolicy) {
        // ref: http://developer.okta.com/docs/api/resources/policy.html#GroupPasswordPolicy
        final Map<String, Object> strengthMap = (Map<String, Object>)
            ((Map<String, Object>)oktaPasswordPolicy.getSettings().get("password")).get("complexity");
        PasswordPolicy ret = new PasswordPolicy() {
            @Override
            public int getResetTokenTtlHours() {
                return 0;
            }

            @Override
            public PasswordPolicy setResetTokenTtlHours(int resetTokenTtl) {
                return null;
            }

            @Override
            public EmailStatus getResetEmailStatus() {
                return null;
            }

            @Override
            public PasswordPolicy setResetEmailStatus(EmailStatus status) {
                return null;
            }

            @Override
            public EmailStatus getResetSuccessEmailStatus() {
                return null;
            }

            @Override
            public PasswordPolicy setResetSuccessEmailStatus(EmailStatus status) {
                return null;
            }

            @Override
            public PasswordStrength getStrength() {
                PasswordStrength p =  new DefaultPasswordStrength(getDataStore());

                p.setMinLength((Integer) strengthMap.get("minLength"));
                p.setMinLowerCase((Integer) strengthMap.get("minLowerCase"));
                p.setMinUpperCase((Integer) strengthMap.get("minUpperCase"));
                p.setMinNumeric((Integer) strengthMap.get("minNumber"));
                p.setMinSymbol((Integer) strengthMap.get("minSymbol"));
                p.setMaxLength(1024);
                p.setMinDiacritic(0);
                p.setPreventReuse(0);

                return p;
            }

            @Override
            public ModeledEmailTemplateList getResetEmailTemplates() {
                return null;
            }

            @Override
            public UnmodeledEmailTemplateList getResetSuccessEmailTemplates() {
                return null;
            }

            @Override
            public String getHref() {
                return "local";
            }

            @Override
            public void save() {

            }
        };
        return ret;
    }
}
