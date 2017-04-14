package com.stormpath.sdk.okta;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.client.Client;

/**
 *
 */
public interface ClientApiKeyResolver {


     ApiKey getClientApiKey(Client client, String baseHref, String applicationId);

}
