package com.stormpath.sdk.oauth;

import com.stormpath.sdk.resource.Resource;

import java.util.Date;

public interface Provider extends Resource {

    /**
     * Returns the customData's created date.
     *
     * @return the customData's created date.
     */
    Date getCreatedAt();

    /**
     * Returns the customData's last modification date
     *
     * @return the customData's last modification date
     */
    Date getModifiedAt();

    String getProviderId();
}
