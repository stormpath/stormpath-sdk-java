package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountLinkingPolicy;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.account.CreateAccountRequest;
import com.stormpath.sdk.account.EmailVerificationStatus;
import com.stormpath.sdk.account.PasswordResetToken;
import com.stormpath.sdk.account.VerificationEmailRequest;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.application.ApplicationStatus;
import com.stormpath.sdk.application.OAuthApplication;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.impl.client.DefaultClient;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.mail.DefaultEmailRequest;
import com.stormpath.sdk.impl.okta.OktaUserAccountConverter;
import com.stormpath.sdk.impl.tenant.TenantResolver;
import com.stormpath.sdk.mail.EmailRequest;
import com.stormpath.sdk.mail.EmailService;
import com.stormpath.sdk.impl.okta.OktaOAuthAuthenticator;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.okta.OktaActivateAccountResponse;
import com.stormpath.sdk.okta.OktaIdentityProviderList;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfigStatus;
import com.stormpath.sdk.application.webconfig.ChangePasswordConfig;
import com.stormpath.sdk.application.webconfig.ForgotPasswordConfig;
import com.stormpath.sdk.application.webconfig.LoginConfig;
import com.stormpath.sdk.application.webconfig.MeConfig;
import com.stormpath.sdk.application.webconfig.Oauth2Config;
import com.stormpath.sdk.application.webconfig.RegisterConfig;
import com.stormpath.sdk.application.webconfig.VerifyEmailConfig;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.DirectoryCriteria;
import com.stormpath.sdk.group.CreateGroupRequest;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteUrlBuilder;
import com.stormpath.sdk.impl.directory.DefaultDirectory;
import com.stormpath.sdk.impl.directory.OktaDirectory;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.okta.OktaApiPaths;
import com.stormpath.sdk.impl.okta.OktaApplicationAccountStoreMapping;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.oauth.IdSiteAuthenticator;
import com.stormpath.sdk.oauth.OAuthBearerRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathFactorChallengeGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthStormpathSocialGrantRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthTokenRevocator;
import com.stormpath.sdk.organization.OrganizationCriteria;
import com.stormpath.sdk.provider.OktaProvider;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.saml.SamlCallbackHandler;
import com.stormpath.sdk.saml.SamlIdpUrlBuilder;
import com.stormpath.sdk.saml.SamlPolicy;
import com.stormpath.sdk.tenant.Tenant;
import com.stormpath.sdk.tenant.TenantOptions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.compression.CompressionCodecs;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OktaApplication extends AbstractResource implements Application, OAuthApplication {

    public static final String AUTHORIZATION_SERVER_ID_KEY = "authorizationServerId";
    public static final String EMAIL_SERVICE_KEY = "emailService";
    public static final String REGISTRATION_WORKFLOW_KEY = "registrationWorkflowEnabled";
    public static final String CLIENT_KEY = "client";
    public static final String ALLOW_API_SECRET = "allowApiSecret";
    public static final String USER_API_QUERY_TEMPLATE = "userApiQueryTemplate";
    public static final String APPLICATION_ID = "applicationId";
    public static final String PASSWORD_POLICY_NAME = "passwordPolicyName";

    private final Logger log = LoggerFactory.getLogger(OktaApplication.class);

    private final OktaDirectory OKTA_TENANT_DIR;

    private final ApplicationAccountStoreMapping defaultAccountStoreMapping;

    private  String applicationId;
    private EmailService emailService;

    private OktaOAuthAuthenticator oAuthAuthenticator = null;
    private OktaAuthNAuthenticator authNAuthenticator = null;

    private String name = "My Application";

    public OktaApplication(String clientId, InternalDataStore dataStore) {
        super(dataStore, null);
        this.OKTA_TENANT_DIR = new OktaDirectory(clientId, this, dataStore);

        Map<String, Object> mappingProperties = new LinkedHashMap<>();
        mappingProperties.put("href", dataStore.getBaseUrl());
        mappingProperties.put("application", this);
        mappingProperties.put("accountStore", OKTA_TENANT_DIR);

        defaultAccountStoreMapping = new DefaultApplicationAccountStoreMapping(dataStore, mappingProperties) {
            @Override
            public AccountStore getAccountStore() {
                return OKTA_TENANT_DIR;
            }
        };
        defaultAccountStoreMapping.setAccountStore(OKTA_TENANT_DIR);

    }

    public Application configureWithProperties(Map<String, Object> properties) {
        setProperties(properties);
        this.applicationId = getStringProperty(APPLICATION_ID);
        String authServerId = getStringProperty(AUTHORIZATION_SERVER_ID_KEY);
        String userApiQueryTemplate = getStringProperty(USER_API_QUERY_TEMPLATE);
        boolean allowApiSecret = getBooleanProperty(ALLOW_API_SECRET);
        oAuthAuthenticator = new OktaOAuthAuthenticator(authServerId, allowApiSecret, userApiQueryTemplate, this, getDataStore());
        this.authNAuthenticator = oAuthAuthenticator.getOktaAuthNAuthenticator();
        emailService = (EmailService) getProperty(EMAIL_SERVICE_KEY);
        this.OKTA_TENANT_DIR.setRegistrationWorkflowEnabled(getBooleanProperty(REGISTRATION_WORKFLOW_KEY));
        this.OKTA_TENANT_DIR.setProperty(PASSWORD_POLICY_NAME, getStringProperty(PASSWORD_POLICY_NAME));

        Object client = properties.get(CLIENT_KEY);
        if (client instanceof DefaultClient) {
            ((DefaultClient) client).setTenantResolver(new TenantResolver() {
                @Override
                public Tenant getCurrentTenant() {
                    return getTenant();
                }

                @Override
                public Tenant getCurrentTenant(TenantOptions tenantOptions) {
                    return getTenant();
                }
            });
        }

        return this;
    }

    public String getId() {
        return applicationId;
    }

    @Override
    public String getHref() {
        return getDataStore().getBaseUrl();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Application setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Account createAccount(Account account) throws ResourceException {
        return OKTA_TENANT_DIR.createAccount(account);
    }

    @Override
    public Account createAccount(CreateAccountRequest request) throws ResourceException {
        return OKTA_TENANT_DIR.createAccount(request);
    }

    @Override
    public Tenant getTenant() {
        return new OktaTenant(this);
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email, AccountStore accountStore) throws ResourceException {
        throw new UnsupportedOperationException("sendPasswordResetEmail() method hasn't been implemented.");
    }

    @Override
    public PasswordResetToken sendPasswordResetEmail(String email) throws ResourceException {

        // make sure this email is valid first
        String userHref = OktaApiPaths.apiPath("users", email); // users endpoint works with uid and emails
        Account account = getDataStore().getResource(userHref, Account.class);

        String compactJwt =  Jwts.builder()
                .setSubject(email)
                .claim("tokenType", "reset")
                .claim("resetToken", UUID.randomUUID())
                .claim("userHref", account.getHref())
                .setExpiration(DateTime.now().plusHours(2).toDate())
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, getDataStore().getApiKey().getSecret())
                .compact();

        EmailRequest emailRequest = new DefaultEmailRequest()
                .setToAddress(email)
                .setToken(compactJwt);

        ensureEmailService().sendResetEmail(emailRequest);

        return null;

    }

    @Override
    public Account verifyPasswordResetToken(String token) {

        try {
            // parsing validate the JWT, so we only need to grab the href
            Jws<Claims> jwt = Jwts.parser()
                    .setSigningKey(getDataStore().getApiKey().getSecret())
                    .require("tokenType", "reset")
                    .parseClaimsJws(token);

            String userHref = jwt.getBody().get("userHref", String.class);
            return getDataStore().getResource(userHref, Account.class);
        }
        catch(JwtException e) {

            log.debug("Failed to parse JWT", e);
            Error error = new DefaultError()
                    .setCode(404)
                    .setStatus(404)
                    .setDeveloperMessage(e.getMessage())
                    .setMessage("Invalid Token");
            throw new ResourceException(error);
        }
    }

    @Override
    public Account resetPassword(String passwordResetToken, String newPassword) {

        Account account = verifyPasswordResetToken(passwordResetToken);
        account.setPassword(newPassword);
        getDataStore().save(account);

        return account;
    }

    @Override
    public void sendVerificationEmail(VerificationEmailRequest verificationEmailRequest) {

        String userHref = OktaApiPaths.apiPath("users", verificationEmailRequest.getLogin()); // users endpoint works with uid and emails
        Account account = getDataStore().getResource(userHref, Account.class);

        String compactJwt =  Jwts.builder()
                .setSubject(account.getEmail())
                .claim("tokenType", "verify")
                .claim("verifyToken", UUID.randomUUID())
                .claim("userHref", account.getHref())
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, getDataStore().getApiKey().getSecret())
                .compact();


        account.setEmailVerificationStatus(EmailVerificationStatus.UNVERIFIED);
        account.getCustomData().put(OktaUserAccountConverter.STORMPATH_EMAIL_VERIFICATION_TOKEN, compactJwt);

        account.save();

        EmailRequest emailRequest = new DefaultEmailRequest()
                .setToDisplayName(account.getFullName())
                .setToAddress(account.getEmail())
                .setToken(compactJwt);

        ensureEmailService().sendValidationEmail(emailRequest);
    }

    public Account verifyAccountEmail(String token) {

        // parsing validate the JWT, so we only need to grab the href
        Jws<Claims> jwt = Jwts.parser()
                .setSigningKey(getDataStore().getApiKey().getSecret())
                .require("tokenType", "verify")
                .parseClaimsJws(token);

        String userHref = jwt.getBody().get("userHref", String.class);
        String activateAccountHref = userHref + "/lifecycle/activate?sendEmail=false";
        OktaActivateAccountResponse activateAccountResponse = getDataStore().instantiate(OktaActivateAccountResponse.class);
        getDataStore().create(activateAccountHref, activateAccountResponse);

        Account account = getDataStore().getResource(userHref, Account.class);

        account.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
        account.setStatus(AccountStatus.ENABLED);
        account.getCustomData().put(OktaUserAccountConverter.STORMPATH_EMAIL_VERIFICATION_TOKEN, null);
        account.save();

        return account;
    }

    @Override
    public AuthenticationResult authenticateAccount(AuthenticationRequest request) throws ResourceException {
        return authNAuthenticator.authenticate(request);
    }

    @Override
    public ProviderAccountResult getAccount(ProviderAccountRequest request) {
        return authNAuthenticator.getAccount(request);
    }

    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings() {

        OktaIdentityProviderList oktaIdentityProviderList = getDataStore().getResource(OktaApiPaths.API_V1 + "idps", OktaIdentityProviderList.class);

        Collection<ApplicationAccountStoreMapping> mappingList = new ArrayList<>();
        mappingList.add(defaultAccountStoreMapping);

        // TODO: fix nested classes
        for(final OktaProvider provider : oktaIdentityProviderList.getIdentityProviders() ) {
            ApplicationAccountStoreMapping mapping = new OktaApplicationAccountStoreMapping(this.getDataStore());
            mapping.setApplication(this);
            mapping.setAccountStore(new AccountStore() {
                @Override
                public void accept(AccountStoreVisitor visitor) {
                    visitor.visit(new DefaultDirectory(getDataStore()) {
                        @Override
                        public Provider getProvider() {
                            return provider;
                        }
                    });
                }

                @Override
                public String getHref() {
                    return null;
                }
            });

            mappingList.add(mapping);
        }

        return new ApplicationAccountStoreMappingCollectionBackedList(mappingList);
    }

    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(Map<String, Object> queryParams) {
        return getAccountStoreMappings();
    }

    @Override
    public ApplicationAccountStoreMappingList getAccountStoreMappings(ApplicationAccountStoreMappingCriteria criteria) {
        return getAccountStoreMappings();
    }

    @Override
    public AccountStore getDefaultGroupStore() {
        return OKTA_TENANT_DIR;
    }

    @Override
    public OAuthClientCredentialsGrantRequestAuthenticator createClientCredentialsGrantAuthenticator() {
        return ensureOktaOAuthAuthenticator().createClientCredentialsGrantAuthenticator();
    }

    @Override
    public OAuthStormpathSocialGrantRequestAuthenticator createStormpathSocialGrantAuthenticator() {
        return ensureOktaOAuthAuthenticator().createStormpathSocialGrantAuthenticator();
    }

    @Override
    public OAuthStormpathFactorChallengeGrantRequestAuthenticator createStormpathFactorChallengeGrantAuthenticator() {
        return ensureOktaOAuthAuthenticator().createStormpathFactorChallengeGrantAuthenticator();
    }

    @Override
    public OAuthPasswordGrantRequestAuthenticator createPasswordGrantAuthenticator() {
        return ensureOktaOAuthAuthenticator().createPasswordGrantAuthenticator();
    }

    @Override
    public OAuthRefreshTokenRequestAuthenticator createRefreshGrantAuthenticator() {
        return ensureOktaOAuthAuthenticator().createRefreshGrantAuthenticator();
    }

    @Override
    public OAuthBearerRequestAuthenticator createJwtAuthenticator() {
        return ensureOktaOAuthAuthenticator().createJwtAuthenticator();
    }

    public IdSiteAuthenticator createIdSiteAuthenticator() {
        return ensureOktaOAuthAuthenticator().createIdSiteAuthenticator();
    }

    @Override
    public OAuthTokenRevocator createOAuthTokenRevocator() {
        return ensureOktaOAuthAuthenticator().createOAuthTokenRevocator();
    }

    private OktaOAuthAuthenticator ensureOktaOAuthAuthenticator() {
        Assert.notNull(this.oAuthAuthenticator);
        return oAuthAuthenticator;
    }

    private EmailService ensureEmailService() {
        Assert.notNull(this.emailService);
        return this.emailService;
    }

    @Override
    public AccountStore getDefaultAccountStore() {
        return OKTA_TENANT_DIR;
    }

    @Override
    public ApplicationWebConfig getWebConfig() {

        return new ApplicationWebConfig() {

            @Override
            public String getDomainName() {
                return "OktaDirectory-DomainName-Not-Used";
            }

            @Override
            public Oauth2Config getOAuth2() {
                return ensureOktaOAuthAuthenticator();
            }

            @Override
            public ApplicationWebConfigStatus getStatus() {
                return ApplicationWebConfigStatus.ENABLED;
            }

            @Override
            public LoginConfig getLogin() {
                return new LoginConfig() {
                    private boolean enabled = true;
                    @Override
                    public LoginConfig setEnabled(Boolean enabled) {
                        this.enabled = enabled;
                        return this;
                    }

                    @Override
                    public Boolean isEnabled() {
                        return enabled;
                    }
                };
            }

            @Override
            public void save() {
                throw new UnsupportedOperationException("save() method hasn't been implemented.");
            }

            @Override
            public String getHref() {
                throw new UnsupportedOperationException("getHref() method hasn't been implemented.");
            }

            @Override
            public Date getCreatedAt() {
                throw new UnsupportedOperationException("getCreatedAt() method hasn't been implemented.");
            }

            @Override
            public Date getModifiedAt() {
                throw new UnsupportedOperationException("getModifiedAt() method hasn't been implemented.");
            }

            @Override
            public String getDnsLabel() {
                throw new UnsupportedOperationException("getDnsLabel() method hasn't been implemented.");
            }

            @Override
            public ApplicationWebConfig setDnsLabel(String dnsLabel) {
                throw new UnsupportedOperationException("setDnsLabel() method hasn't been implemented.");
            }

            @Override
            public ApplicationWebConfig setStatus(ApplicationWebConfigStatus status) {
                throw new UnsupportedOperationException("setStatus() method hasn't been implemented.");
            }

            @Override
            public ApiKey getSigningApiKey() {
                throw new UnsupportedOperationException("getSigningApiKey() method hasn't been implemented.");
            }

            @Override
            public ApplicationWebConfig setSigningApiKey(ApiKey apiKey) {
                throw new UnsupportedOperationException("setSigningApiKey() method hasn't been implemented.");
            }

            @Override
            public RegisterConfig getRegister() {
                throw new UnsupportedOperationException("getRegister() method hasn't been implemented.");
            }

            @Override
            public VerifyEmailConfig getVerifyEmail() {
                throw new UnsupportedOperationException("getVerifyEmail() method hasn't been implemented.");
            }

            @Override
            public ForgotPasswordConfig getForgotPassword() {
                throw new UnsupportedOperationException("getForgotPassword() method hasn't been implemented.");
            }

            @Override
            public ChangePasswordConfig getChangePassword() {
                throw new UnsupportedOperationException("getChangePassword() method hasn't been implemented.");
            }

            @Override
            public MeConfig getMe() {
                throw new UnsupportedOperationException("getMe() method hasn't been implemented.");
            }

            @Override
            public Application getApplication() {
                throw new UnsupportedOperationException("getApplication() method hasn't been implemented.");
            }

            @Override
            public Tenant getTenant() {
                throw new UnsupportedOperationException("getTenant() method hasn't been implemented.");
            }
        };
    }

    @Override
    public AccountLinkingPolicy getAccountLinkingPolicy() {
        throw new UnsupportedOperationException("getAccountLinkingPolicy() method hasn't been implemented.");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("save() method hasn't been implemented.");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("delete() method hasn't been implemented.");
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        throw new UnsupportedOperationException("getPropertyDescriptors() method hasn't been implemented.");
    }

    @Override
    public Date getCreatedAt() {
        throw new UnsupportedOperationException("getCreatedAt() method hasn't been implemented.");
    }

    @Override
    public Date getModifiedAt() {
        throw new UnsupportedOperationException("getModifiedAt() method hasn't been implemented.");
    }

    @Override
    public CustomData getCustomData() {
        throw new UnsupportedOperationException("getCustomData() method hasn't been implemented.");
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("getDescription() method hasn't been implemented.");
    }

    @Override
    public Application setDescription(String description) {
        throw new UnsupportedOperationException("setDescription() method hasn't been implemented.");
    }

    @Override
    public ApplicationStatus getStatus() {
        throw new UnsupportedOperationException("getStatus() method hasn't been implemented.");
    }

    @Override
    public Application setStatus(ApplicationStatus status) {
        throw new UnsupportedOperationException("setStatus() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts() {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public AccountList getAccounts(AccountCriteria criteria) {
        throw new UnsupportedOperationException("getAccounts() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups() {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups(Map<String, Object> queryParams) {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public GroupList getGroups(GroupCriteria criteria) {
        throw new UnsupportedOperationException("getGroups() method hasn't been implemented.");
    }

    @Override
    public Group createGroup(Group group) throws ResourceException {
        throw new UnsupportedOperationException("createGroup() method hasn't been implemented.");
    }

    @Override
    public Group createGroup(CreateGroupRequest request) {
        throw new UnsupportedOperationException("createGroup() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping createAccountStoreMapping(ApplicationAccountStoreMapping mapping) throws ResourceException {
        throw new UnsupportedOperationException("createAccountStoreMapping() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(AccountStore accountStore) throws ResourceException {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApiKey getApiKey(String id) throws ResourceException, IllegalArgumentException {
        throw new UnsupportedOperationException("getApiKey() method hasn't been implemented.");
    }

    @Override
    public ApiKey getApiKey(String id, ApiKeyOptions options) throws ResourceException, IllegalArgumentException {
        throw new UnsupportedOperationException("getApiKey() method hasn't been implemented.");
    }

    @Override
    public IdSiteUrlBuilder newIdSiteUrlBuilder() {
        throw new UnsupportedOperationException("newIdSiteUrlBuilder() method hasn't been implemented.");
    }

    @Override
    public SamlIdpUrlBuilder newSamlIdpUrlBuilder() {
        throw new UnsupportedOperationException("newSamlIdpUrlBuilder() method hasn't been implemented.");
    }

    @Override
    public IdSiteCallbackHandler newIdSiteCallbackHandler(Object httpRequest) {
        throw new UnsupportedOperationException("newIdSiteCallbackHandler() method hasn't been implemented.");
    }

    @Override
    public SamlCallbackHandler newSamlCallbackHandler(Object httpRequest) {
        throw new UnsupportedOperationException("newSamlCallbackHandler() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(String hrefOrName) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(DirectoryCriteria criteria) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(OrganizationCriteria criteria) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public ApplicationAccountStoreMapping addAccountStore(GroupCriteria criteria) {
        throw new UnsupportedOperationException("addAccountStore() method hasn't been implemented.");
    }

    @Override
    public Application saveWithResponseOptions(ApplicationOptions responseOptions) {
        throw new UnsupportedOperationException("saveWithResponseOptions() method hasn't been implemented.");
    }

    @Override
    public OAuthPolicy getOAuthPolicy() {
        throw new UnsupportedOperationException("getOAuthPolicy() method hasn't been implemented.");
    }

    @Override
    public SamlPolicy getSamlPolicy() {
        throw new UnsupportedOperationException("getSamlPolicy() method hasn't been implemented.");
    }

    @Override
    public List<String> getAuthorizedCallbackUris() {
        throw new UnsupportedOperationException("getAuthorizedCallbackUris() method hasn't been implemented.");
    }

    @Override
    public Application setAuthorizedCallbackUris(List<String> authorizedCallbackUris) {
        throw new UnsupportedOperationException("setAuthorizedCallbackUris() method hasn't been implemented.");
    }

    @Override
    public Application addAuthorizedCallbackUri(String authorizedCallbackUri) {
        throw new UnsupportedOperationException("addAuthorizedCallbackUri() method hasn't been implemented.");
    }

    @Override
    public List<String> getAuthorizedOriginUris() {
        throw new UnsupportedOperationException("getAuthorizedOriginUris() method hasn't been implemented.");
    }

    @Override
    public Application setAuthorizedOriginUris(List<String> authorizedOriginUris) {
        throw new UnsupportedOperationException("setAuthorizedOriginUris() method hasn't been implemented.");
    }

    @Override
    public Application addAuthorizedOriginUri(String authorizedOriginUri) {
        throw new UnsupportedOperationException("addAuthorizedOriginUri() method hasn't been implemented.");
    }
    @Override
    public void setDefaultAccountStore(AccountStore accountStore) {
        throw new UnsupportedOperationException("setDefaultAccountStore() method hasn't been implemented.");
    }

    @Override
    public void setDefaultGroupStore(AccountStore accountStore) {
        throw new UnsupportedOperationException("setDefaultGroupStore() method hasn't been implemented.");
    }

    public static class ApplicationAccountStoreMappingCollectionBackedList implements ApplicationAccountStoreMappingList {

        private final Collection<ApplicationAccountStoreMapping> accountStoreMappings;

        public ApplicationAccountStoreMappingCollectionBackedList(Collection<ApplicationAccountStoreMapping> accountStoreMappings) {
            this.accountStoreMappings = accountStoreMappings;
        }

        @Override
        public String getHref() {
            return null;
        }

        @Override
        public int getOffset() {
            return 0;
        }

        @Override
        public int getLimit() {
            return getSize();
        }

        @Override
        public int getSize() {
            return accountStoreMappings.size();
        }

        @Override
        public ApplicationAccountStoreMapping single() {
            Iterator<ApplicationAccountStoreMapping> iterator = iterator();
            if (!iterator.hasNext()) {
                throw new IllegalStateException("This list is empty while it was expected to contain one (and only one) element.");
            }
            ApplicationAccountStoreMapping itemToReturn = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalStateException("Only a single resource was expected, but this list contains more than one item.");
            }
            return itemToReturn;
        }

        @Override
        public Iterator<ApplicationAccountStoreMapping> iterator() {
            return accountStoreMappings.iterator();
        }
    }
}
