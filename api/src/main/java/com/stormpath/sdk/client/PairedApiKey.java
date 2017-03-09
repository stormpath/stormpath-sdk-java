package com.stormpath.sdk.client;

import com.stormpath.sdk.api.ApiKey;

/**
 * Created for internal implementation purposes only.  Please do not use.
 */
public interface PairedApiKey extends ApiKey {

    ApiKey getSecondaryApiKey();

    void setSecondaryApiKey(ApiKey secondary);
}
