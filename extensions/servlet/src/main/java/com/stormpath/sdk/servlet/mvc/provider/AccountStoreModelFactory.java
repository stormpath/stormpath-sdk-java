package com.stormpath.sdk.servlet.mvc.provider;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 1.0.RC8
 */
public interface AccountStoreModelFactory {

    List<AccountStoreModel> getAccountStores(HttpServletRequest request);
}
