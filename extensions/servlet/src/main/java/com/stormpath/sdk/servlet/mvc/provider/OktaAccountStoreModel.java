package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.directory.Directory;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

/**
 */
public class OktaAccountStoreModel implements AccountStoreModel {

    private final Directory directory;
    private final OktaOAuthProviderModel providerModel;
    private final String authorizeUri;

    public OktaAccountStoreModel(Directory directory, OktaOAuthProviderModel provider, String authorizeBaseUri, String clientId) {
        this.directory = directory;
        this.providerModel = provider;
        if (authorizeBaseUri != null) {
            try {
                URIBuilder builder = new URIBuilder(authorizeBaseUri);
                builder.addParameter("response_type", "code");
                builder.addParameter("response_mode", "query");
                builder.addParameter("client_id", clientId);
                authorizeUri = builder.build().toString();

            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("authorizeBaseUri must be value URI", e);
            }
        } else {
            authorizeUri = null;
        }
    }

    @Override
    public String getHref() {
        return this.directory.getHref();
    }

    @Override
    public String getName() {
        return this.directory.getName();
    }

    @Override
    public String getAuthorizeUri() {
        return authorizeUri;
    }

    @Override
    public ProviderModel getProvider() {
        return this.providerModel;
    }

    public String getYes() {
        return "yes";
    }

}
