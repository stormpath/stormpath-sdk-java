package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.mvc.provider.ProviderAuthorizationEndpointResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for redirecting to the appropriate external authorization endpoint.
 *
 * @since 1.2.0
 */
public class AuthorizeController extends AbstractController {
    private static final Pattern UID_PATTERN = Pattern.compile(".*/(.*)");
    private ProviderAuthorizationEndpointResolver providerAuthorizationEndpointResolver;

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return true;
    }

    @Override
    public void init() throws Exception {
        Assert.hasText(nextUri, "nextUri cannot be null or empty.");
        Assert.notNull(applicationResolver, "applicationResolver cannot be null.");
        Assert.notNull(providerAuthorizationEndpointResolver, "providerAuthorizationEndpointResolver cannot be null.");
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dirUid = getUid(request.getRequestURI());
        Application application = applicationResolver.getApplication(request);
        ApplicationAccountStoreMappingList accountStoreMappingList = application.getAccountStoreMappings();
        for (ApplicationAccountStoreMapping mapping : accountStoreMappingList) {
            AccountStore accountStore = mapping.getAccountStore();
            if (accountStore instanceof Directory && dirUid.equals(getUid(accountStore.getHref()))) {
                String endpoint = providerAuthorizationEndpointResolver.getEndpoint(request, ((Directory) accountStore).getProvider());
                return new DefaultViewModel(endpoint).setRedirect(true);
            }
        }
        response.sendError(404);
        return null;
    }

    private String getUid(String uri) {
        Matcher matcher = UID_PATTERN.matcher(uri);
        Assert.isTrue(matcher.matches(), String.format("uri %s must match pattern %s", uri, UID_PATTERN.pattern()));
        return matcher.group(1);
    }

    public void setProviderAuthorizationEndpointResolver(ProviderAuthorizationEndpointResolver providerAuthorizationEndpointResolver) {
        this.providerAuthorizationEndpointResolver = providerAuthorizationEndpointResolver;
    }
}
