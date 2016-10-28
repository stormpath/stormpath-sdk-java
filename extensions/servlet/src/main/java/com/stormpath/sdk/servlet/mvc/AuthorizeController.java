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
import java.util.List;

/**
 * Controller for redirecting to the appropriate external authorization endpoint.
 *
 * @since 1.2.0
 */
public class AuthorizeController extends AbstractController {
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
        assertResponseType(request);
        Application application = applicationResolver.getApplication(request);
        String applicationCallbackUri = getApplicationCallbackUri(application, request);
        String dirHref = getAccountStoreHref(request);
        ApplicationAccountStoreMappingList accountStoreMappingList = application.getAccountStoreMappings();
        for (ApplicationAccountStoreMapping mapping : accountStoreMappingList) {
            AccountStore accountStore = mapping.getAccountStore();
            if (accountStore instanceof Directory && dirHref.equals(accountStore.getHref())) {
                String endpoint = providerAuthorizationEndpointResolver.getEndpoint(request, applicationCallbackUri, ((Directory) accountStore).getProvider());
                return new DefaultViewModel(endpoint).setRedirect(true);
            }
        }
        response.sendError(404);
        return null;
    }

    private String getApplicationCallbackUri(Application application, HttpServletRequest request) {
        List<String> authorizedCallbacks = application.getAuthorizedCallbackUris();
        Assert.isTrue(!authorizedCallbacks.isEmpty(),
                "Application must be configured with at least one authorized callback uri");
        String redirectUri = request.getParameter("redirect_uri");
        if (redirectUri != null) {
            Assert.isTrue(authorizedCallbacks.contains(redirectUri),
                    "Specified redirect_uri is not in the application's configured authorized callback uri's.");
        } else {
            redirectUri = authorizedCallbacks.get(0);
        }
        return redirectUri;
    }

    private void assertResponseType(HttpServletRequest request) {
        String responseType = request.getParameter("response_type");
        Assert.hasText(responseType, "Must specify response_type");
        Assert.isTrue(responseType.equals("stormpath_token"), "Invalid response_type.  Only stormpath_token supported.");
    }

    private String getAccountStoreHref(HttpServletRequest request) {
        return request.getParameter("account_store_href");
    }

    public void setProviderAuthorizationEndpointResolver(ProviderAuthorizationEndpointResolver providerAuthorizationEndpointResolver) {
        this.providerAuthorizationEndpointResolver = providerAuthorizationEndpointResolver;
    }
}
