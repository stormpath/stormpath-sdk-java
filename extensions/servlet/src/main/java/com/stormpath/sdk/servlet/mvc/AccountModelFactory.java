package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;

import java.util.Map;

/**
 * @since 1.0.0
 */
public interface AccountModelFactory {
    Map<String, Object> toMap(Account account);
}
