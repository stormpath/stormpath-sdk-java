package com.stormpath.sdk.saml;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.Resource;

import java.util.Map;

public interface SamlResponseRequest extends Resource {
    Account getAccount();
    SamlResponseRequest setAccount(Account account);
    RegisteredSamlServiceProvider getServiceProvider();
    SamlResponseRequest setServiceProvider(RegisteredSamlServiceProvider serviceProvider);
    String getRequestId();
    SamlResponseRequest setRequestId(String requestId);
}
