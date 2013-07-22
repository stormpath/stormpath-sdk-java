package com.stormpath.sdk.directory;

import com.stormpath.sdk.resource.Resource;

/**
 * @since 0.9
 */
public interface AccountStore extends Resource {

    void accept(AccountStoreVisitor visitor);
}
