package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.account.EmailVerificationStatus;
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
import com.stormpath.sdk.impl.account.DefaultVerificationEmailRequest;
import com.stormpath.sdk.impl.application.OktaApplication;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.okta.OktaApiPaths;
import com.stormpath.sdk.impl.okta.OktaUserAccountConverter;
import com.stormpath.sdk.impl.provider.DefaultOktaProvider;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.DefaultVoidResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.mail.EmailStatus;
import com.stormpath.sdk.mail.ModeledEmailTemplateList;
import com.stormpath.sdk.mail.UnmodeledEmailTemplateList;
import com.stormpath.sdk.okta.OktaUserToApplicationMapping;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingCriteria;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.organization.OrganizationList;
import com.stormpath.sdk.provider.OktaProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.query.Options;
import com.stormpath.sdk.schema.Schema;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class OktaDirectory extends AbstractResource implements Directory {

    private final OktaProvider oktaProvider;
    private final OktaApplication oktaApplication;
    private boolean registrationWorkflowEnabled = false;

    public OktaDirectory(String clientId, OktaApplication oktaApplication, InternalDataStore dataStore) {
        super(dataStore);
        this.oktaApplication = oktaApplication;
        this.oktaProvider = new DefaultOktaProvider(dataStore)
                .setClientId(clientId);
    }

    public OktaDirectory(String clientId, OktaApplication oktaApplication, InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        this.oktaApplication = oktaApplication;
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
        return createAccount(account, isRegistrationWorkflowEnabled());
    }

    @Override
    public Account createAccount(Account account, boolean registrationWorkflowEnabled) {
        Assert.notNull(account, "Account instance cannot be null.");
        CreateAccountRequest request = Accounts.newCreateRequestFor(account).setRegistrationWorkflowEnabled(registrationWorkflowEnabled).build();
        return createAccount(request);
    }

    @Override
    public Account createAccount(CreateAccountRequest request) {
        String usersHref = getHref() + OktaApiPaths.USERS + "?activate=" + !request.isRegistrationWorkflowEnabled();
        final Account account = request.getAccount();
        account.getCustomData().put(OktaUserAccountConverter.RECOVERY_WORK_AROUND_KEY, UUID.randomUUID().toString());

        if (request.isRegistrationWorkflowEnabled()) {
            account.setStatus(AccountStatus.UNVERIFIED);
            account.setEmailVerificationStatus(EmailVerificationStatus.UNVERIFIED);
            account.getCustomData().put(OktaUserAccountConverter.STORMPATH_EMAIL_VERIFICATION_TOKEN, UUID.randomUUID().toString());
        }
        Account result = getDataStore().create(usersHref, account);

        if (request.isRegistrationWorkflowEnabled()) {

            oktaApplication.sendVerificationEmail(new DefaultVerificationEmailRequest(getDataStore()).setLogin(getUserUid(account)));
        }

        // add the new Account to the current application
        associateUser(account);

        return result;
    }

    protected void associateUser(Account account) {

        String groupId = getStringProperty(OktaApplication.APPLICATION_GROUP_ID);
        if (Strings.hasText(groupId)) {
            associateUserWithGroup(account, groupId);
        } else {
            associateUserWithApplication(account);
        }
    }

    protected void associateUserWithApplication(Account account) {
        // add the new Account to the current application
        String uid = account.getHref().substring(account.getHref().lastIndexOf('/')+1);
        String mappingHref = OktaApiPaths.apiPath("apps", oktaApplication.getId(), "users"); // "/api/v1/apps/{{appId}}/users"
        OktaUserToApplicationMapping mapping = getDataStore().instantiate(OktaUserToApplicationMapping.class)
                .setId(uid)
                .setScope("USER")
                .setUsername(account.getEmail());

        // create the mapping
        getDataStore().create(mappingHref, mapping);
    }

    protected void associateUserWithGroup(Account account, String groupId) {
        // add the new Account to the current application
        String uid = getUserUid(account);
        String mappingHref = OktaApiPaths.apiPath("groups", groupId, "users", uid); // "/api/v1/groups/{{gid}}/users/{{uid}}"

        // create the mapping
        getDataStore().save(new DefaultVoidResource(getDataStore(), null, mappingHref));
    }

    private String getUserUid(Account account) {
        String[] parts = account.getHref().split("/");
        return parts[parts.length-1];
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

        String passwordPolicyName = getStringProperty(OktaApplication.PASSWORD_POLICY_NAME);
        if (!Strings.hasText(passwordPolicyName)) {
            passwordPolicyName = "Default Policy";
        }

        OktaPasswordPolicy oktaPasswordPolicy = null;
        for (OktaPasswordPolicy tmpPolicy : policies) {
            if (passwordPolicyName.equals(tmpPolicy.getName())) {
                oktaPasswordPolicy = tmpPolicy;
                break;
            }
        }
        Assert.isTrue(oktaPasswordPolicy != null, "No password policy with name '"+ passwordPolicyName +"' found, you can set your password policy name using the configuration property: 'okta.password.policy.name'");
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

    public boolean isRegistrationWorkflowEnabled() {
        return registrationWorkflowEnabled;
    }

    public void setRegistrationWorkflowEnabled(boolean registrationWorkflowEnabled) {
        this.registrationWorkflowEnabled = registrationWorkflowEnabled;
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
