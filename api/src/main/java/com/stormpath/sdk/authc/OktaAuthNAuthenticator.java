package com.stormpath.sdk.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface OktaAuthNAuthenticator extends Resource {

    AuthenticationResult authenticate(AuthenticationRequest request);
    
    void assertValidAccessToken(String accessToken);

    ProviderAccountResult getAccount(ProviderAccountRequest providerAccountRequest);

    Account getAccountByToken(String accountToken);
}
