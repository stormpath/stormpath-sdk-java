package com.stormpath.sdk.impl.application.okta;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.okta.ApplicationCredentials;
import com.stormpath.sdk.application.okta.ClientApiKeyResolver;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.api.ClientApiKeyBuilder;

/**
 *
 */
public class OktaClientApiKeyResolver implements ClientApiKeyResolver {

    @Override
    public ApiKey getClientApiKey(Client client, String baseHref, String applicationId) {
        String applicationCredentialsHref = baseHref + "/internal/apps/" + applicationId + "/settings/clientcreds";
        ApplicationCredentials applicationCredentials = client.getResource(applicationCredentialsHref, ApplicationCredentials.class);

        return new ClientApiKeyBuilder()
                .setId(applicationCredentials.getClientId())
                .setSecret(applicationCredentials.getClientSecret())
                .build();
    }
}
