package com.stormpath.sdk.group;

import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Status;
import com.stormpath.sdk.tenant.Tenant;

/**
 * @author Jeff Wysong
 *         Date: 6/13/12
 *         Time: 11:22 AM
 * @since 0.2
 */
public interface Group extends Resource {
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Status getStatus();

    void setStatus(Status status);

    Tenant getTenant();

    Directory getDirectory();

    AccountList getAccounts();


}
