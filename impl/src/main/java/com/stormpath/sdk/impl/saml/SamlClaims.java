package com.stormpath.sdk.impl.saml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stormpath.sdk.impl.provider.ProviderClaims;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SamlClaims extends ProviderClaims {

    public static final String ACCOUNT_STORE_HREF = "ash";

    public String getAccountStoreHref() {
        return getString(ACCOUNT_STORE_HREF);
    }

    public SamlClaims setAccountStoreHref(String accountStoreHref) {
        setValue(ACCOUNT_STORE_HREF, accountStoreHref);
        return this;
    }
}
