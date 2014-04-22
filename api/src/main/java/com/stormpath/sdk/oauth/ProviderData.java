package com.stormpath.sdk.oauth;

import com.stormpath.sdk.resource.Resource;

import java.util.Date;

public interface ProviderData extends Resource {

    /**
     * Returns the ProviderData's created date.
     *
     * @return the ProviderData's created date.
     */
    Date getCreatedAt();

    /**
     * Returns the ProviderData's last modification date
     *
     * @return the ProviderData's last modification date
     */
    Date getModifiedAt();

    String getProviderId();

}
