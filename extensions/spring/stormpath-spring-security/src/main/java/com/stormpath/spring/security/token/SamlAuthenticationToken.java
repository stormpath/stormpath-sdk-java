package com.stormpath.spring.security.token;

import com.stormpath.sdk.account.Account;

public class SamlAuthenticationToken extends ThirdPartyAuthenticationToken {
    public SamlAuthenticationToken(Account account) {
        super(account);
    }
}
